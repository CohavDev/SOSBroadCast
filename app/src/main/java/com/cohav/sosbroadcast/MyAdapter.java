package com.cohav.sosbroadcast;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Contact> contactList;
    private static RecyclerViewClickListener itemListener;
    private int pos;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, number;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            number = (TextView) view.findViewById(R.id.number);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(Build.VERSION.SDK_INT>=22) {
                pos = this.getLayoutPosition();
                itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
            }
            else{
                itemListener.recyclerViewListClicked(view, this.getPosition());
                pos = this.getPosition();
            }
        }

    }

    public void setClickListener(RecyclerViewClickListener itemListener){
        this.itemListener = itemListener;
    }

    public MyAdapter(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        if(contactList!=null) {
            return contactList.size();
        }
        return 0;

    }

    public void addItemsToList(Contact c1){
        this.contactList.add(c1);
    }
    public void Clear(){
        this.contactList=new ArrayList<>();
    }
    public void RemoveItemsFromList(int pos){
        this.contactList.remove(pos);
    }

}