package Server;

/*
DictionaryServer.java: An implementation of the Dictionary Server.
Contains:
    * Sockets
 */

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.net.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;


public class DictionaryServer{

    // Mutators
    public Socket getClientSocket() {
        return clientSocket;
    }
    protected void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    // instance variables: clientSockets.
    private Socket clientSocket;
    protected Dict dict = new Dict();
    private DataInputStream in;
    private DataOutputStream out;


    public void execute(){
        // Execution of thread.
        String clientPort = "N/A'";

        try{
            clientPort = String.format("%d", clientSocket.getPort());

            obtainDataStreams();

            // Welcome message to client.
            welcome();

            // Read and parse input data from client.
            String regex = "\\|";
            String[] inputs = in.readUTF().split(regex);
            String result = parseInput(inputs);

            // Send results to client.
            out.writeUTF(""+result);

            // Close datastreams and socket: Thread per request architecture.
            close();
        }
        catch(IllegalArgumentException iae){
            System.out.println("Client did not enter a valid input.");
        }
        catch(EOFException eofe){
            System.out.println("Unable to read client input through input stream.");
        }
        catch(IOException ioe){
            System.out.println(ioe);
        }

    }


    /*
    Helper Functions
        * parseInput(inputs)        -- parse input from client and determine which action to take.
        * obtainDataStreams()       -- get datastreams from client.
        * close()                   -- close datastreams and socket.
     */

    // Input parser, to return add, remove or query.
    private String parseInput (String[] inputs) throws IllegalArgumentException{
        // Server check for input.
        if (inputs.length > 3 | inputs.length < 2){
            throw new IllegalArgumentException("Input to server is invalid.");
        }

        //Parse client inputs.
        char operation = inputs[0].charAt(0);
        String word = inputs[1];
        String meaning = "";
        if (inputs.length == 3){
            meaning = inputs[2];
        }

        String result = "No operation found.";
        switch (operation){
            case 'q':
                result = dict.queryMeaning(word);
                break;
            case 'a':
                result = dict.addWord(word, meaning);
                break;
            case 'r':
                result = dict.removeWord(word);
                break;
        }

        return result;
    }

    private void obtainDataStreams() throws IOException{
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        }catch(IOException ioe){
            throw new IOException("Unable to get streams from client.");
        }
    }

    private void close() throws IOException{
        try{
            in.close();
            out.close();
            clientSocket.close();
        }catch(IOException ioe){
            throw new IOException("Unable to close streams to client.");
        }
    }

    private void welcome() throws IOException{
        try{
            out.writeUTF("Dictionary: Hi there!");
            out.flush();
        }catch(IOException ioe){
            throw new IOException("Unable to send welcome message.");
        }
    }

}