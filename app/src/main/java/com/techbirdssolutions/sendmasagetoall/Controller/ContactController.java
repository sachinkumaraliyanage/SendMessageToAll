package com.techbirdssolutions.sendmasagetoall.Controller;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.ContactsContract;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.techbirdssolutions.sendmasagetoall.been.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ContactController {
    ArrayList<Contact> contactList = new ArrayList<Contact>();
    private final Boolean isTest=false;
    private static final Set<String> testNumber = new HashSet<String>(Arrays.asList(
            new String[] {"773920012","762889397"}
    ));
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    public ArrayList<Contact> getContactList(Context context) {
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number,numberCheck;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    numberCheck = number.replace("+94", "");
                    numberCheck = number.replace("+", "");
                    numberCheck = numberCheck.charAt(0)=='0'?numberCheck.substring(1):numberCheck;
                    System.out.println("sachin:"+number);
                    if (!mobileNoSet.contains(numberCheck) && numberCheck.length()>8) {
                        if(isTest==true){
                            if(testNumber.contains(numberCheck)){
                                contactList.add(new Contact(name, number,numberCheck));
                            }
                        }else{
                            contactList.add(new Contact(name, number,numberCheck));
                        }
                        mobileNoSet.add(numberCheck);
                        Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                + " No = " + number);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                cursor.close();
            }
            return contactList;
        }
        return null;
    }
}
