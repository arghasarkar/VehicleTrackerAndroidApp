package uk.co.findmyride.track;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Argha
 */
public class GetHttpsRequest {

    // The https url to which the GET request will be sent
    String baseURL = "";
    // The query string will carry the data for the GET request
    String queryString = "";
    // The amount of time in ms the thread will sleep for
    int deferredAccess = 1000;


    public GetHttpsRequest(String url, String query) {
        // Constructor class with default DA time
        baseURL = url;
        queryString = query;
    }

    public GetHttpsRequest(String url, String query, int accessDelay){
        // Constructor class with custo DA time
        baseURL = url;
        queryString = query;
        deferredAccess = accessDelay;
    }


    public String sendRequest() throws MalformedURLException, UnknownHostException, InterruptedException {
        URL myUrl = new URL(baseURL + "?" + queryString);

        // Flag to show when data has been sent
        boolean dataSent = false;
        // Number of times sending data has failed
        int errNo = 0;

        while (!dataSent) {

            try {
                HttpsURLConnection con = (HttpsURLConnection)myUrl.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
                con.setDoOutput(true);
                con.setDoInput(true);

                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                output.close();

                StringBuilder webOut = new StringBuilder();
                DataInputStream input = new DataInputStream(con.getInputStream() );

                for (int c = input.read(); c != -1; c = input.read()) {
                    webOut.append((char) c);
                }
                input.close();

                dataSent = true;
                return webOut.toString();

            } catch (Exception ex) {
                //ex.printStackTrace(System.err);
                errNo++;
                System.out.println("Error times: " + errNo);
                dataSent = false;
                Thread.sleep(1000);
            }
        }


        return "";
    }

}
