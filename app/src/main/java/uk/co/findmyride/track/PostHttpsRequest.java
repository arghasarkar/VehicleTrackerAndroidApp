package uk.co.findmyride.track;

/**
 * Created by Argha
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class PostHttpsRequest {

    // The https url to which the GET request will be sent
    String baseURL = "";
    // The query string will carry the data for the GET request
    String[] keyValuePair;
    // The amount of time in ms the thread will sleep for
    int deferredAccess = 1000;


    public PostHttpsRequest(String url, String[] kVPair) {
        // Constructor class with default DA time
        baseURL = url;
        keyValuePair = kVPair;
    }

    public PostHttpsRequest(String url, String[] kVPair, int accessDelay){
        // Constructor class with custo DA time
        baseURL = url;
        keyValuePair = kVPair;
        deferredAccess = accessDelay;
    }


    public String sendRequest() throws MalformedURLException, UnknownHostException, InterruptedException {
        /*
        Sends the post request to the server.
        The URL is an argument in the constructor class as a string.
        The payload data parameter are sent via a POST request so therefore come in a key-value pair.
        Multiple pairs can be sent with the use of a String array.
        Each string element in the array is a key value pair.
        The string is separated by a comma (,).
        On the left hand side of the comma is the string containing the key.
        On the right hand side of the comma is the string containing the value.
        EG: Name = "Argha Sarkar".

        String[] name = {"Name", "Argha Sarkar"};
        String URL = "https://findmyride.co.uk/some.php";
        PostHttpsRequest post = new PostHttpsRequest(URL, name);
        post.sendRequest();

         */
        URL myUrl = new URL(baseURL);

        // Flag to show when data has been sent
        boolean dataSent = false;
        // Number of times sending data has failed
        int errNo = 0;

        // This external will continue looping until the data has been sent successfully.
        // It is used to mitigate against circumstances like no internet service.
        while (!dataSent) {

            try {
                // Separates the key and value pair.
                String[] curKVPair = keyValuePair[0].split(",");
                // ENCODING THE INFORMATION TO BE TRANSMITTED INTO A POST STRING
                String postInfo = curKVPair[0] + URLEncoder.encode(curKVPair[1], "UTF-8");

                for (int i = 1; i < keyValuePair.length; i++) {
                    // ENCODING THE INFORMATION TO BE TRANSMITTED INTO A POST STRING
                    postInfo += "&";
                    curKVPair = keyValuePair[i].split(",");
                    postInfo += curKVPair[0] + URLEncoder.encode(curKVPair[1], "UTF-8");
                }

                HttpsURLConnection con = (HttpsURLConnection) myUrl.openConnection();
                // Sets up the request method which is Post
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
                con.setDoOutput(true);
                con.setDoInput(true);

                // Opens the data output stream.
                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                // Sends the encoded stream
                output.writeBytes(postInfo);
                // Closes the socket
                output.close();

                // Prepares for receiving the reply
                StringBuilder webOut = new StringBuilder();
                DataInputStream input = new DataInputStream(con.getInputStream() );

                // Keeps reading as long as the next input character is valid.
                for (int c = input.read(); c != -1; c = input.read()) {
                    // Adds the last character to the string
                    webOut.append((char) c);
                }
                input.close();
                // Data sent flag is set to true
                dataSent = true;
                // Returns the string
                return webOut.toString();

            } catch (Exception ex) {
                // Increases contention and keeps going in a loop until a request is received.
                // Enables the system to work until internet connection is achieved.
                // Enables the system to work if the server is down.
                errNo++;                                     // Counts the number of loops
                System.out.println("Error times: " + errNo); // Prints out the number of loops
                dataSent = false;                            // Resets the disconnection loop
                Thread.sleep(1000);                          // Provides delay for server being up
            }
        }

        return "";
    }


}
