package MultiThreadedChatApp;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;        // required for List and Scanner
import org.json.simple.*;  // required for JSON encoding and decoding
import java.util.Map;
import java.util.HashMap;

public class MessageBoardServer {

    static class Clock {
        private long t;

        public Clock() { t = 0; }

        // tick the clock and return the current time
        public synchronized long tick() { return ++t; }
    }


    static class ClientHandler extends Thread {
        // shared message board
        private static List<Message> board = new ArrayList<Message>();
        private static File file = new File("Messages.txt");
        private static String path = file.getAbsolutePath();

        private static boolean Subscribed;


        // shared logical clock
        private static Clock clock = new Clock();

        // number of messages that were read by this client already
        private int read;

        // login name; null if not set
        private String login;

        private static Map<String, List<String>> channels = new HashMap<>();

        private Socket client;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            client = socket;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            read = 0;
            Subscribed = false;
        }
        public void MessageWriter(String message) throws IOException {
            // write message to the file
            FileWriter fw = new FileWriter(path, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter writer = new PrintWriter(bw);
            writer.println(message);
            writer.flush();
            writer.close();
        }
        public List<Message> GetMesseges() throws IOException {
            List<Message> board = new ArrayList<>();
            String[] lines;
            List<String> listOfStrings = new ArrayList<String>();
            // load data from file
            BufferedReader bf = new BufferedReader(new FileReader(path));
            // read entire line as string
            String line = bf.readLine();
            // checking for end of file
            while (line != null) {
                listOfStrings.add(line);
                line = bf.readLine();
            }
            // closing buffer reader object
            bf.close();
            // storing the data in arraylist to array
            lines = listOfStrings.toArray(new String[0]);
            if (lines != null) {
            for (String lin : lines) {
                String[] values = lin.split(",");

                board.add(new Message(values[0], values[1], Long.parseLong(values[2])));
             }
            }
            return board;
        }

        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // tick the clock and record the current time stamp
                    long ts = clock.tick();

                    // logging request (+ login if possible) to server console
                    if (login != null)
                        System.out.println(login + ": " + inputLine);
                    else
                        System.out.println(inputLine);

                    // parse request, then try to deserialize JSON
                    Object json = JSONValue.parse(inputLine);
                    Request req;

                    // login request? Must not be logged in already
                    if (login == null &&
                            (req = LoginRequest.fromJSON(json)) != null) {
                        // set login name
                        login = ((LoginRequest)req).getName();
                        // response acknowledging the login request
                        out.println(new SuccessResponse());
                        continue;
                    }

                    //creating channel Must  be logged in already
                    if (login != null && (req = OpenRequest.fromJSON(json)) != null) {
                        // set login name
                        List<String> subs = new ArrayList<>();
                        String channelName = ((OpenRequest) req).GetChannelName();
                        String identity= ((OpenRequest) req).GetIdentity();
                        // response acknowledging the login request


                        // create a channel for the user and subscribe him
                        synchronized (ClientHandler.class) {
                            subs.add(identity);
                            channels.put(channelName,subs);
                            Subscribed=true;
                        }
                        out.println(new SuccessResponse());
                        continue;
                    }

                    //publish message on channel
                    if (login != null &&  channels != null && Subscribed && (req = PublishRequest.fromJSON(json)) != null) {
                        String message = ((PublishRequest) req).getMessage().getBody();

                        synchronized (ClientHandler.class) {
                            // add message with login and timestamp
                            MessageWriter(message+","+login+","+ts);
                        }
                        // response: list of unread messages
                        out.println(new SuccessResponse());

                        continue;
                    }
                    // Subscribe request? Must be logged in and available channel
                    if (login != null && channels != null && (req = SubscribeRequest.fromJSON(json)) != null) {
                        List<String> subs = new ArrayList<>();
                        String channelName = (((SubscribeRequest) req).getChannelName());
                        String identity = ((SubscribeRequest) req).getIdentity();

                        if(channels.containsKey(channelName)){
                            synchronized (ClientHandler.class) {
                                subs = channels.get(channelName);
                                subs.add(identity);
                                Subscribed=true;

                            }
                            out.println(new SuccessResponse());
                        }
                        else {
                            out.println(new ErrorResponse("Channel Not founded"));
                        }

                        continue;
                    }



                    // Unsubscribe request? Must be logged in and available channel
                    if (login != null && channels != null && Subscribed && (req = UnsubscribeRequest.fromJSON(json)) != null) {
                        String channelName = (((UnsubscribeRequest) req).GetChannelName());
                        String identity = ((UnsubscribeRequest) req).GetIdentity();
                        List<String> subs = new ArrayList<>();

                        if(channels.containsKey(channelName)){
                            synchronized (ClientHandler.class) {
                                subs=channels.get(channelName);
                                subs.remove(identity);
                                Subscribed=false;
                            }
                        }
                        else {
                            out.println(new ErrorResponse("Channel Not founded"));
                        }

                        continue;
                    }

                    // Get request? Must be logged in
                    if (login != null && channels != null && Subscribed && (req = GetRequest.fromJSON(json)) != null) {
                        String channelName = (((GetRequest) req).GetChannelName());
                        List<Message> msgs =new ArrayList<>();
                        List<String> subs = new ArrayList<>();

                        // synchronized access to the shared message board
                        synchronized (ClientHandler.class) {
                            subs = channels.get(channelName);
                            board=GetMesseges();
                            for (Message m:board) {
                               if(subs.contains(m.getAuthor())){
                                    msgs.add(m);
                               }
                             }
                            // adjust read counter
                            // response: list of unread messages
                            out.println(new MessageListResponse(msgs));
                        }
                        continue;
                    }

                    // quit request? Must be logged in; no response
                    if (login != null && QuitRequest.fromJSON(json) != null) {
                        in.close();
                        out.close();
                        return;
                    }
                    out.println(new ErrorResponse("ILLEGAL REQUEST (you must have been logged in and subscribed channel to be able to \"publish\",\"unsubscribe\" and \"get\"\n" +
                            "And you cannot open a channel when you are subscribed in a diffrent channel!)"));
                }
        } catch (IOException e) {
                System.out.println("Exception while connected");
                System.out.println(e.getMessage());
            }

        }
    }


    public static void main(String[] args) {


        int portNumber = 1235;

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler cl = new ClientHandler(clientSocket);
                cl.start();
                // cl.join();
            }
        } catch (IOException e) {
            System.out.println("Exception listening for connection on port " +
                    portNumber);
            System.out.println(e.getMessage());
        } //catch (InterruptedException e) {
        // throw new RuntimeException(e);
        // }
    }

}
