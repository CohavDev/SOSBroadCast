package com.cohav.sosbroadcast;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class activateSos extends AppCompatActivity {
    private List<Contact> contactList;
    private SharedPreferences userData;
    private SharedPreferences.Editor editor;
    private TypeToken<ArrayList<Contact>> token = new TypeToken<ArrayList<Contact>>() {
    };
    private Gson gson;
    private String phoneNumber = "0528726439";
    private String msgText = "Hey Mor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_send);

        //start here
        final ImageButton myBtn = (ImageButton) findViewById(R.id.ActivateBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contactList = gson.fromJson(userData.getString("contactList",""),token.getType());
                Intent gpsStat = new Intent("android.location.GPS_ENABLE_CHANGE");
                gpsStat.putExtra("enabled",true);
                sendBroadcast(gpsStat);
                //send sms
                //SendSOSbroadCast(contactList);
            }
        });


      //  LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);

    }

    public void SendSOSbroadCast(List contactList) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber,null,msgText,null,null);
            Toast.makeText(activateSos.this, "SMS SENT", Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
            Toast.makeText(activateSos.this, "ERROR", Toast.LENGTH_SHORT).show();

        }

    }
}
