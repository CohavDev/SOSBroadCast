package com.cohav.sosbroadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private MyAdapter myAdapter;
    private RecyclerView mRecyclerView;
    private SharedPreferences userData;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<Contact> contactList;
    private TypeToken<ArrayList<Contact>> token =  new TypeToken<ArrayList<Contact>>(){};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start here

        //add contact
        final ImageButton myButton = (ImageButton) findViewById(R.id.addContact);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        final Button myBtn2 = (Button)findViewById(R.id.deleteRef);
        myBtn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
              //  userData = getPreferences(MODE_PRIVATE);
              //  editor = userData.edit();
              //  editor.clear();
             //   editor.commit();
              //  contactList = null;
               // myAdapter.Clear();
                //myAdapter.notifyDataSetChanged();
                Intent intent = new Intent(MainActivity.this,activateSos.class);
                intent.putExtra("CONTACT_LIST",(Serializable) GetRefList());
                startActivity(intent);
                finish();
            }
        });
        //settings
        //final ImageButton myButton2 = (ImageButton) findViewById(R.id.settingsButton);
        //myButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //Intent intent = new Intent(this,activateSos.class);
                //startActivity(intent);
          //  }
        //});
        //initialize
        userData = getPreferences(MODE_PRIVATE);
        editor = userData.edit();
        gson = new Gson();
       //editor.clear();
       // editor.commit();
        if(userData.contains("contactList")) {
            contactList = GetRefList();
        }
        else{
            contactList = new ArrayList<>();
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        myAdapter = new MyAdapter(this.contactList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setClickListener(this);

        //put values --->  prepareContactData

        prepareContactData();
    }

    public void prepareContactData() {

        if(contactList!=null){
            myAdapter.notifyDataSetChanged();

        }
    }

    //show & add contact
    public void AddContactToList(String name, String number) {
        contactList = GetRefList();
        Contact c1 = new Contact(name,number);
        String objectString;
        if(contactList==null){
            contactList = new ArrayList<>();
        }
        contactList.add(c1);
        objectString = gson.toJson(contactList);
        editor.putString("contactList",objectString);
        editor.commit();
        myAdapter.addItemsToList(c1);
        myAdapter.notifyDataSetChanged();

    }

    //main
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);
                        String name = c.getString(1);
                        AddContactToList(name, number);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    @Override
    public void recyclerViewListClicked(View view, final int position) {
        //on click
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                RemoveFromListShared(position);
                Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_LONG).show();
                myAdapter.RemoveItemsFromList(position);
                myAdapter.notifyDataSetChanged();
                return true;
            }
        });
        popupMenu.show();
    }
    //gets the contacts list that is saved in the sharedPreferences
    public List<Contact> GetRefList(){
        List<Contact> getList = null;
        if(userData.contains("contactList")){
            getList = gson.fromJson(userData.getString("contactList",""),token.getType());
        }
        return getList;
    }
    //remove from list
    public void RemoveFromListShared(int pos){
        List<Contact>list = GetRefList();//gets the list
        if(list!=null){
            list.remove(pos);
            String objectStr = gson.toJson(list);
            editor.putString("contactList",objectStr);
            editor.commit();

        }

    }
}


