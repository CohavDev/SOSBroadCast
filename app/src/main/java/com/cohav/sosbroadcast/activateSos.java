package com.cohav.sosbroadcast;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class activateSos extends AppCompatActivity {
    private List<Contact> contactList = null;
    private SharedPreferences userData;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private TypeToken<ArrayList<Contact>> token =  new TypeToken<ArrayList<Contact>>(){};
    private LocationManager mng;
    private LocationListener locationListener;
    private String msg;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.slide_in,R.animator.nothing);
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();
        GetRefList();//update the list

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_send);
        //toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        toolbar.setTitle("Activate SOS");
        setSupportActionBar(toolbar);


        //start here
        GetRefList();
        final ImageButton myBtn = (ImageButton) findViewById(R.id.ActivateBtn);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Intent gpsOptionsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }
                else{
                    //gps is enabled.

                    GetLoc();
                }
                //scan and send
                if(contactList == null||contactList.size()==0){

                    //send message to SetUp screen(Main activity).
                    Intent intent = new Intent (activateSos.this,MainActivity.class);
                    intent.putExtra("noContact","Please Add SOS Contacts First");
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_in,R.animator.nothing);

                }
                else{
                    //send broadcast to every contact.
                    if(msg!=null){
                        ScanArrayList();
                        //show msg
                    }
                    //msg == null
                    else{
                        //could'nt get location before trying to send sms.
                        //delaying sending the sms.
                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                //action to do
                                if(msg==null){
                                    handler.postDelayed(this,1000);
                                }
                            }
                        };
                        handler.postDelayed(runnable,1000);
                        ScanArrayList();
                    }

                }


            }
        });

    }

    public void SendSOSbroadCast(String phoneNumber,String msgText) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber,null,msgText,null,null);
        } catch(Exception e) {
            Toast.makeText(activateSos.this, "ERROR while sending sms", Toast.LENGTH_SHORT).show();

        }

    }
    public void ScanArrayList(){
        for (int i = 0; i<contactList.size();i++){
            SendSOSbroadCast(contactList.get(i).getNumber(),contactList.get(i).getName()+", אני צריך עזרה"+"\n"+msg+"\n זוהי בדיקה בלבד ולא קריאת עזרה אמיתית.");
        }
        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
        Snackbar snackbar = Snackbar.make(constraintLayout,"ההודעה נשלחה בהצלחה !",Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    public void GetRefList(){
        //start here
        userData = PreferenceManager.getDefaultSharedPreferences(this);
        editor = userData.edit();
        gson = new Gson();
        if(userData.contains("contactList")){
            this.contactList =gson.fromJson(userData.getString("contactList",""),token.getType());
        }
    }
    public void GetLoc(){

         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                msg="http://maps.google.com/?q=";
                msg+=""+location.getLatitude()+",";
                msg+=""+location.getLongitude()+"";
                Toast.makeText(activateSos.this,location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        mng = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            mng.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000, 0, locationListener);

        }
        catch (SecurityException e){
            Toast.makeText(activateSos.this,"Security exception",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mng !=null){
            //remove location updates.
            mng.removeUpdates(locationListener);
        }

    }
}