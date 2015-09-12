package uk.co.findmyride.track;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class Devices extends ActionBarActivity {
    /*
    This is the activity which lets the user select the device
     */

    // Will contain the string with the details
    String deviceDetailsStr = "";
    // Splits it into an array with each element containing each device detail
    String[] deviceDetailsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Executed when the devices activity loads up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        // Enables strict mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Number of devices text label.
        TextView displayDeviceNum = (TextView) findViewById(R.id.labDeviceNum);
        // Gets the devices list text label.
        TextView displayDevicesList = (TextView) findViewById(R.id.labDeviceName);

        // Sets the preset test data for the spinner
        final Spinner listDevices = (Spinner) findViewById(R.id.listDevices);

        // Sets the button to view the map
        final Button butViewMap = (Button) findViewById(R.id.butViewMap);

        // Gets all the extras using a bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // This is to avoid runtime exceptions
            // Gets the number of devices
            int deviceNum = extras.getInt("deviceNum");
            if (deviceNum < 1) {
                // Executed if the user does not own devices
                displayDeviceNum.setText("Please register online!");
                // Sets the device display list as invisible
                displayDevicesList.setVisibility(View.INVISIBLE);
                listDevices.setVisibility(View.INVISIBLE);
                butViewMap.setVisibility(View.INVISIBLE);
            } else {
                displayDeviceNum.setText("Devices owned: " + deviceNum + ".");
                //--------------------------------------------------------DYNAMIC DROPDOWN MENU-----------
                // Creats an arrayList of the devices owned by the user
                ArrayList<String> deviceList = new ArrayList<String>();
                // Will contain the string with the details
                deviceDetailsStr = extras.getString("deviceDetails");
                deviceDetailsArray = deviceDetailsStr.split(";");

                // Sets the items in the array list
                for (int i = 0; i < deviceDetailsArray.length; i++) {
                    String[] curDeviceArray = deviceDetailsArray[i].split(",");
                    if (curDeviceArray.length > 1) {
                        String curDeviceDetail = curDeviceArray[0] + ") " + curDeviceArray[1];
                        deviceList.add(curDeviceDetail);
                    }
                }

                // SETS ALL THE DEVICES ON THE SPINNER (DROP DOWN MENU)
                ArrayAdapter<String> dynamicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deviceList);
                listDevices.setAdapter(dynamicAdapter);

                // CREATING A BUTTON ON CLICK LISTENER
                butViewMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send the selected device's location to the map
                      // butViewMap.setText("Selected index: " + listDevices.getSelectedItemPosition());
                        prepareGoToMap(listDevices.getSelectedItemPosition());
                    }
                });

            }
        }
    }

    public void goToMap(String deviceName, String deviceTime, double deviceLat, double deviceLng, double deviceSpeed) {
        // Goes to the map activity to display the location
        Intent passInfo = new Intent(Devices.this, MapsActivity.class);
        // These are all the info that will be needed to display the marker correctly
        passInfo.putExtra("deviceName", deviceName);
        passInfo.putExtra("deviceTime", deviceTime);
        passInfo.putExtra("deviceLat", deviceLat);
        passInfo.putExtra("deviceLng", deviceLng);
        passInfo.putExtra("deviceSpeed", deviceSpeed);
        startActivity(passInfo);
    }

    public void prepareGoToMap(int deviceIndex) {
        // The selected device String
        String curSelectedDevice = deviceDetailsArray[deviceIndex].replace('\n', '\0');
        String[] curSelectedDeviceArray = curSelectedDevice.split(",");
        if (curSelectedDeviceArray.length < 3) {
            String dName = curSelectedDeviceArray[1];
            String dTime = "No Location.";
            double dLat = 0.0;
            double dLng = 0.0;
            double dSpeed = 0.0;
            goToMap(dName, dTime, dLat, dLng, dSpeed);
        } else {
            String dName = curSelectedDeviceArray[1];
            String dTime = curSelectedDeviceArray[3];
            double dLat = Double.valueOf(curSelectedDeviceArray[5]);
            double dLng = Double.valueOf(curSelectedDeviceArray[6]);
            double dSpeed = Double.valueOf(curSelectedDeviceArray[4]);
            goToMap(dName, dTime, dLat, dLng, dSpeed);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}