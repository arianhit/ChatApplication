package MultiThreadedChatApp;

import java.io.*;
import java.net.*;
import java.util.*;        // required for Scanner
import org.json.simple.*;  // required for JSON encoding and decoding

    public class MessageBoardClient {
        private static String nameOfClient;
        private static String channel;

        private static Boolean subscribed;

    public static void main(String[] args) throws IOException {

        System.out.println("***************  Welcome to the SHU massager  ****************:");
        System.out.println("For using the massager you need to login first :");
        System.out.println("Please login by writing login space an your name like(login jack)");
        String hostName = "localhost";
        int portNumber = 1235;

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Parse user and build request
                Request req;
                Scanner sc = new Scanner(userInput);
                Client client = new Client();
                //login
                try {
                    switch (sc.next().toLowerCase()) {
                        case "login":

                            nameOfClient = sc.next();
                            boolean validLogin =  client.login(nameOfClient);
                            if(validLogin){
                                req = new LoginRequest(nameOfClient);
                                LoginRequest name = (LoginRequest) req;
                                String n = name.getName();
                                System.out.println("welcome "+n+" ");
                                System.out.println("For using the massager please subscribe your friend's channel or open a channel:");
                                System.out.println("\t\t\t1.Open (Enter:open {name}) to open a channel");
                                System.out.println("\t\t\t2.Subscribe (Enter:Subscribe {name}) to connect the channel");
                                System.out.println("\t\t\t3.Unsubscribe (Enter:Unsubscribe {name}) to disconnect the channel");
                                System.out.println("\t\t\t4.publish (Enter:publish (your massage) to send massage");
                                System.out.println("\t\t\t2.Get request (Enter:Get) to read all massages (you can see publish massages only once)");
                                System.out.println("\t\t\t3.quit to quit");

                                new SuccessResponse();
                            }
                            else {
                                System.exit(0);
                                req = new QuitRequest();
                            }
                            break;
                        case "open":

                                channel = sc.next();
                                req = new OpenRequest(nameOfClient, channel);
                                new SuccessResponse();
                                subscribed=true;
                                break;
                        case "publish":

                                req = new PublishRequest(nameOfClient, new Message(sc.skip(" ").nextLine(), nameOfClient, 0));
                                new SuccessResponse();
                                break;
                        case "subscribe":
                                channel = sc.skip(" ").nextLine();
                                req = new SubscribeRequest(nameOfClient, channel);
                                System.out.println("subscribed to "+channel);
                                new SuccessResponse();
                                subscribed=true;
                                break;

                        case "unsubscribe":
                                channel = sc.skip(" ").nextLine();
                                req = new UnsubscribeRequest(nameOfClient, channel);
                                new SuccessResponse();
                                subscribed=false;
                                break;
                        case "get":
                            if(subscribed){
                                req = new GetRequest(channel);
                            }
                            else {
                                req=null;
                                System.out.println("You must subscribed to a channel first");
                            }
                                break;
                        case "quit":
                                req = new QuitRequest();
                                break;
                        default:
                            System.out.println("ILLEGAL COMMAND");
                            continue;
                    }
                } catch (NoSuchElementException e) {
                    System.out.println(e);
                    continue;
                }

                // Send request to server
                out.println(req);

                // Read server response; terminate if null (i.e. server quit)
                String serverResponse;
                if ((serverResponse = in.readLine()) == null) {
                    System.out.println("Thank you for using the massager "+nameOfClient);
                    //out put all messages in text file
                    break;
                }
                // Parse JSON response, then try to deserialize JSON
                Object json = JSONValue.parse(serverResponse);
                Response resp;

                // Try to deserialize a success response
                if (SuccessResponse.fromJSON(json) != null)
                    continue;

                // Try to deserialize a list of messages
                if ((resp = MessageListResponse.fromJSON(json)) != null){
                    for (Message m : ((MessageListResponse)resp).getMessages())
                        System.out.println(m.toString());
                    continue;
                }

                // Try to deserialize an error response
                if ((resp = ErrorResponse.fromJSON(json)) != null) {
                    System.out.println(((ErrorResponse)resp).getError());
                    continue;
                }

                // Not any known response
                System.out.println("PANIC: " + serverResponse +
                        " parsed as " + json);
                break;
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            //out put messages
            System.exit(1);
        }
    }
}