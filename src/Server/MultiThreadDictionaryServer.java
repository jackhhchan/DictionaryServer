package Server;
import java.net.*;
import java.io.*;



public class MultiThreadDictionaryServer extends DictionaryServer implements Runnable{
    public void run() {
        // thread runs execute from inherited method.
        execute();
    }


    public static void main(String[] args){

        // Check arguments.
        if (args.length != 2) {
            throw new IllegalArgumentException("Please register a port number, then enter dictionary path " +
                    "file in arguments.");
        }

        // parse arguments
        String path = args[1];

        try {
            int port = Integer.parseInt(args[0]);           // set port number for the server.


            // Load dictionary from path.
            Dict.Load(path);


            // Create main server socket.
            ServerSocket serverSocket = new ServerSocket(port);
            int i = 0; // counter for number of requests.
            while (true) {

                // Listen for client's connection request and create new socket when connected.
                System.out.println("Server listening on port " + port + "...");
                Socket clientSocket = serverSocket.accept();
                i++;
                System.out.println("Request " + i + " connected from port " + clientSocket.getPort());

                // Create new thread assign new client socket to thread.
                MultiThreadDictionaryServer server = new MultiThreadDictionaryServer();
                server.setClientSocket(clientSocket);
                // Start the thread.
                System.out.println("Starting new thread...");
                Thread serverThread = new Thread(server);       // New thread instantiated.
                serverThread.start();
            }
        }catch (NumberFormatException nfe){
            System.out.println("Unable to parse port argument as integer, " +
                    "please register an integer for the port number.");
        }catch (IOException ioe){
            System.out.println(ioe);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}