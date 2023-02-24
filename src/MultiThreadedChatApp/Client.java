package MultiThreadedChatApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private String username;
    private String password;

    public Client(){}
    public Client(String username,String password){
        this.password=password;
        this.username=username;
    }


    public String GetClientUserName(){return this.username;}
    public String GetClientPassword(){return this.password;}

    public boolean login(String username) throws IOException {
        List<Client> clients = new ArrayList<>();
        int numberOfLginTrials=0;
        File myObj = new File("ClientDB.txt");
        String path = myObj.getAbsolutePath();
        Scanner myReader = new Scanner(myObj);
        Scanner sc = new Scanner(System.in);
        String[] lines;
        String userPassword;
        boolean validLogin=false;
        while (numberOfLginTrials < 4)
        {
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
                //get the path of staffDB.csv file
            for (String lin:lines) {
                String[] values = lin.split(",");

                clients.add(new Client(values[0], values[1]));
            }

                while (true)
                {
                    //ask for the password
                    System.out.println("Enter your password :");
                    userPassword = sc.next();
                    //check the password that user cannot enter null value or bigger that 20 chars
                    if (userPassword != null && userPassword.length() < 20)
                    {
                        break;
                    }
                    else
                    {
                        //Out put the error in a format of error
                        System.out.println("Please Enter a password in correct format (it should not be more than 20 charecters)");
                    }
                }

            for (Client c:clients) {
                if (username.toLowerCase().equals(c.GetClientUserName()) && userPassword.equals(c.GetClientPassword()))
                {
                    validLogin = true;
                    break;
                }
            }

            //if login was valid
            if (validLogin)
            {
                //freindly message that user logged in
                System.out.println("You have successfully logged in " + username + "!!!\n\n\n");;
                //return the current staff that logged in
                return true;
            }
            //if login was not valid
            else if (!validLogin)
            {

                switch (numberOfLginTrials)
                {
                    //first try
                    case 0:
                        System.out.println("****************ACCES DENIED WRONG USERNAME OR PASSWORD************** \nTRY AGAIN\nYOU HAVE JUST ");
                        System.out.println("*3* ");
                        System.out.print("ATTEMPTS\n");
                        numberOfLginTrials++;
                        break;
                    //second try
                    case 1:

                        System.out.println("****************ACCES DENIED WRONG USERNAME OR PASSWORD************** \nTRY AGAIN\nYOU HAVE JUST ");
                        System.out.println("*2* ");
                        System.out.print("ATTEMPTS\n");
                        numberOfLginTrials++;
                        break;
                    //third and last try
                    case 2:
                        System.out.println("****************ACCES DENIED WRONG USERNAME OR PASSWORD************** \nTRY AGAIN\nTHIS IS YOUR ");
                        System.out.println(" LAST ATTEMPT ");
                        System.out.print("BE CARFUL!\n");
                        numberOfLginTrials++;
                        break;
                    //kick user out of the software with freindly text
                    case 3:
                        System.out.println("[ERROR]:****************ACCES DENIED WRONG USERNAME OR PASSWORD************** \nYOU HAD 3 ATTEMPTS AND ");
                        System.out.println("YOU ARE NOT ALLOW LOGIN AGAIN\n");
                        numberOfLginTrials++;
                        return validLogin;
                }
            }
        }
        return validLogin;
    }
}
