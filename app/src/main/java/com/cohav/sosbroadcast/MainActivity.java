package com.cohav.sosbroadcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.animator.nothing,R.animator.slide_out);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        finish();
        overridePendingTransition(R.animator.nothing,R.animator.slide_out);
        return true;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start here
        //toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        toolbar.setTitle("Set Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //add contact
        final FloatingActionButton myButton = (FloatingActionButton) findViewById(R.id.addContact);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.animator.slide_in,R.animator.nothing);

            }
        });

        userData = getPreferences(MODE_PRIVATE);
        editor = userData.edit();
        gson = new Gson();

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
        Contact c1 = new Contact(number,name);
        String objectString;
        if(contactList==null){
            contactList = new ArrayList<>();
        }
        if(!IsExist(c1)){
            contactList.add(c1);
            objectString = gson.toJson(contactList);
            editor.putString("contactList",objectString);
            editor.commit();
            myAdapter.addItemsToList(c1);
            myAdapter.notifyDataSetChanged();
        }
        else{
            //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            CoordinatorLayout crdLayOut = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
            Snackbar snackbar = Snackbar.make(crdLayOut,"Contact is already exist !",Snackbar.LENGTH_LONG);//ERROR HERE
            snackbar.show();
        }


    }
//check if contact exist
    public boolean IsExist(Contact c1){
        for (int i =0;i<contactList.size();i++){
            if(c1.getNumber().equals(contactList.get(i).getNumber())){
                return true;//exist
            }
        }
        return false;
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


