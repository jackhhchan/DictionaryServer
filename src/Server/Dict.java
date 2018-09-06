package Server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;



public class Dict{

    private static Map<String, String> dictionary = new HashMap<String, String>();
    private static int requests = 0;        // Indicate to users to stop clicking a button many times.


    protected static void Load(String path) throws Exception{
        // Load dictionary file into hashmap.
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(path));
            JSONObject jsonObj = (JSONObject) obj;

            for (Object key: jsonObj.keySet()){
                String word = (String) key;
                String definition = (String) jsonObj.get(key);
                dictionary.put(word, definition);
            }
        }
        catch(FileNotFoundException fnfe){
            throw new FileNotFoundException("Unable to find dictionary file at " + path);
        }
        catch(IOException ioe){
            throw new IOException("Unable to load file using JSONParser.");
        }
        catch(ParseException pe){
            throw new Exception("Unable to parse file using JSONParser.");
        }

    }



    public synchronized String queryMeaning (String word){

        //Check if word is in dictionary;
        word = word.toUpperCase();
        String meaning = dictionary.get(word);

        // Fun little message.
        if (meaning == null){
            meaning = "Word not found in dictionary.";
            requests++;
            if (requests >= 10){
                return "Please stop it.";
            }
        }

        requests = 0;
        return meaning;         // Returns the meaning of the query.
    }

    public synchronized String addWord (String word, String meaning){
        String message;
        // Check if word is in dictionary.
        word = word.toUpperCase();
        if (dictionary.get(word) != null){
            requests++;
            if (requests >= 10){
                return "Please stop it.";
            }
            message = "Word already exist in dictionary.";
            return message;
        }

        // Adds word to dictionary.
        dictionary.put(word, meaning);
        message = word.toLowerCase() + " successfully added to dictionary.";
        requests = 0;
        return message;
    }

    public synchronized String removeWord (String word){
        String message;
        word = word.toUpperCase();
        // Check if word is in dictionary.
        if (dictionary.get(word) == null){
            requests++;
            if (requests >= 10){
                return "Please stop it.";
            }
            message = "Word not found in dictionary.";
            return message;
        }

        // Removes word from dictionary.
        dictionary.remove(word);
        message = word.toLowerCase() + " successfully removed from dictionary.";
        requests = 0;
        return message;
    }

}