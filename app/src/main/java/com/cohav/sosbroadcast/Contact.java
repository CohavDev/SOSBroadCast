package com.cohav.sosbroadcast;

import java.util.ArrayList;
import java.util.List;

public class Contact {

    private String number;
    private String name;
    private static List<Contact> contactList = new ArrayList<>();
    private static int size = 0;
    public Contact(String number,String name){
        this.name=name;
        this.number=number;
    }
    public static void AddContact(Contact contact){
        contactList.add(contact);
        size++;
    }
    public static List<Contact> getContactList(){
        return contactList;
    }
    public String getNumber(){
        return this.number;
    }
    public String getName(){
        return this.name;
    }
    public static int getSize(){
        return size;
    }
    public static void setSize(int num){
        size=num;
    }
    public static void DeleteFromList(int position){
        contactList.remove(position);
    }
    public void setNumber(String number){
        this.number=number;
    }
    public void setName(String name){
        this.name=name;
    }
}
