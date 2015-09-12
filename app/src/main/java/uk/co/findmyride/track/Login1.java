package uk.co.findmyride.track;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;


import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.StrictMode;


import org.w3c.dom.Text;


public class Login1 extends ActionBarActivity {

    /*
    This is the start up activity where the users log in.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        // Enables strict mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Declaring the user text inputs: Email & Password
        final EditText txtEmail = (EditText) findViewById(R.id.editEmail);
        final EditText txtPassword = (EditText) findViewById(R.id.editPassword);
        // Gets the empty strings
        String userEmail = txtEmail.getText().toString();
        final String userPassword = txtPassword.getText().toString();

        // Prints details about the login status
        final TextView displayLoginStatus = (TextView) findViewById(R.id.labLoginStatus);

        // Declares the button
        Button butLogin = (Button) findViewById(R.id.butLogin);
        // Adds button click listener
        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Removes the virtual keyboard
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) { }

                String userEmail =  txtEmail.getText().toString();
                String userPassword = txtPassword.getText().toString();

                displayLoginStatus.setText("Trying to login..");

                String serverResponse = authenticateUser(userEmail, userPassword);

                if (serverResponse.equals("out")) {
                    displayLoginStatus.setText("Invalid email or password!");
                } else {
                    // Array containing the different lines of the response string sent back by the server
                    final String[] responseArray = serverResponse.toString().split("\n");

                    if (responseArray[0].equals("in;")) {
                        if (responseArray[1].equals("none;") == false) {
                            //displayLoginStatus.setText("");
                            //displayLoginStatus.setText("Devices Num: " + (responseArray.length - 1) + "\n");
                            String[] deviceDetails = new String[responseArray.length - 1];
                            for (int i = 1; i < responseArray.length; i++) {
                                deviceDetails[i-1] = responseArray[i];
                               // displayLoginStatus.setText(displayLoginStatus.getText() + responseArray[i] + "\n");
                            }
                            // Takes the user to the devices page from where they can select
                            // which device to choose to view the locations
                            goToDevices(responseArray.length - 1, deviceDetails);
                        } else {
                            // Tells the devices page that there are no registered devices
                            goToDevices(0, new String[0]);
                        }
                    }

                }

            }
        });
    }


    public void goToDevices(int deviceNum, String[] deviceDetails) {
        // Switches to the Devices activity
        Intent passInfo = new Intent(Login1.this, Devices.class);
        passInfo.putExtra("deviceNum", deviceNum);
        String deviceDetailsStr = "";

        for (int i = 0; i < deviceDetails.length; i++) {
            deviceDetailsStr += deviceDetails[i];
        }

        if (deviceNum > 0) {
            passInfo.putExtra("deviceDetails" , deviceDetailsStr);
        }
        startActivity(passInfo);
    }

    public void goToMap(int deviceNum) {
        // Switches to the MAP activity
        Intent passInfo = new Intent(Login1.this, MapsActivity.class);
        passInfo.putExtra("deviceNum", deviceNum);
        startActivity(passInfo);
    }

    private String authenticateUser(String userEmail, String userPassword) {
        // The response string from the server will be held here

        String response = "";
        // Sets up the URL and the query string
        String loginUrl = "https://findmyride.co.uk/project/app/authUser.php";
        /*String queryString = "email=" + userEmail + "&pass=" + userPassword;*/

        /* Sets up the information in an array in Key-Value format
            first line will contain the email
            second line will contain the password
            The field name (KEY) and the field value (VALUE) are separated by a comma (,)
         */
        String[] loginInfo = new String[2];
        loginInfo[0] = "email=," + userEmail;
        loginInfo[1] = "pass=," + userPassword;

        // Sets up the class for sending the web request
        /*GetHttpsRequest webRequester = new GetHttpsRequest(loginUrl, queryString);*/
        // Sets up the class for sending the HTTPS POST request
        PostHttpsRequest postReq = new PostHttpsRequest(loginUrl, loginInfo);

        try {
            response = postReq.sendRequest();
        } catch (Exception ex) {
            response = "ERROR\n";
            response += ex.getMessage();
        }

        return response;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
