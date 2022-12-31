package com.techbirdssolutions.sendmasagetoall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.techbirdssolutions.sendmasagetoall.Controller.ContactController;
import com.techbirdssolutions.sendmasagetoall.been.Contact;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private Cursor cursor;
    private ListView listView;
    private Button button;
    private EditText etmessage;
    private TextView tvsendto,tvsuccess,tverror,tvstatus;
    public static int success = 0;
    public static int error = 0;
    public static int current=0;
    public static int all=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        etmessage = findViewById(R.id.etmessage);
        tvsendto = findViewById(R.id.tvsendto);
        tvsuccess = findViewById(R.id.tvsuccess);
        tverror = findViewById(R.id.tverror);
        tvstatus = findViewById(R.id.tvstatus);

        // set OnClickListener() to the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling of getContacts()
                requestContactPermission();
            }
        });

    }

    public ArrayList<PendingIntent> getSMSIntent(Contact contact){
        ArrayList<PendingIntent> list = new ArrayList<>();
        String name = contact.getName();
        for(int i=1;i<=contact.getParts();i++){
            String SENT = "SMS_SENT_"+contact.getNumberCheck()+"_"+i;
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    MainActivity.current++;
                    tvsendto.setText("Send Message To "+contact.getName());
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS sent to "+name,
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.success++;
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure to "+name,
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.error++;
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service to "+name,
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.error++;
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU to "+name,
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.error++;
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off to "+name,
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.error++;
                            break;
                    }
                    tverror.setText("Fail Count:"+(int)(error/contact.getParts()));
                    tvsuccess.setText("Success Count:"+(int)(success/contact.getParts()));
                    tvstatus.setText("Status: Sending "+(int)(current/contact.getParts())+"/"+all);
                    if(MainActivity.current==MainActivity.all){
                        tvstatus.setText("Status: Sending Completed "+(int)(current/contact.getParts())+"/"+all);
                        etmessage.setEnabled(true);
                        button.setEnabled(true);
                    }
                }
            }, new IntentFilter(SENT));

            list.add(sentPI);
        }


        return list;
    }

    public void getContacts() {
        etmessage.setEnabled(false);
        button.setEnabled(false);
        ArrayList<Contact> contacts = new ContactController().getContactList(this);
        MainActivity.all = contacts.size();
        MainActivity.current=0;
        MainActivity.success = 0;
        MainActivity.error = 0;
        tverror.setText("Fail Count:"+error);
        tvsuccess.setText("Success Count:"+success);
        tvstatus.setText("Status: Sending SMS"+current+"/"+all);
        tvsendto.setText("Send Message To None");
        String msg = etmessage.getText().toString();
        for ( Contact contact:contacts ) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(msg);
                contact.setParts(parts.size());
                smsManager.sendMultipartTextMessage(contact.getPhoneNumber(), null, parts, getSMSIntent(contact), null);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                MainActivity.current++;
                MainActivity.error++;
                tverror.setText("Fail Count:"+error);

            }

        }
        if(MainActivity.current==MainActivity.all){
            etmessage.setEnabled(true);
            button.setEnabled(true);
        }



    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                requestSendSMSPermission();
            }
        } else {
            requestSendSMSPermission();
        }
    }

    public void requestSendSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.SEND_SMS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("END SMS permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to SEND SMS.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.SEND_SMS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}