package com.bus_tracking.system;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationActivity extends AppCompatActivity {
    TextView txtDetails;
    Context context;
    SharedPreferences preferences;
    Adapter1 adapter1;
    ListView list;
    ArrayList<String> lstFiles = new ArrayList<>();
    ArrayList<String> lstURL = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference myRef;
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = findViewById(R.id.list);
        context = this;
        adapter1 = new Adapter1(context, R.layout.custum_list, lstFiles);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

list.setAdapter(adapter1);
list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Log.i(TAG, "onClick: file"+lstURL.get(position));
new DownloadTask(context,lstURL.get(position));

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:


                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to download?").setPositiveButton("Yes!!",
                dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
});
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constant.DB);
        Query query = myRef.child("upload");
        ;
        Log.i(TAG, "sendNotification: query=" + query);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lstFiles.clear();
                lstURL.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String notice = (String) dataSnapshot1.child("notice").getValue();
                    String url = (String) dataSnapshot1.child("url").getValue();
                    String rdate = (String) dataSnapshot1.child("rdate").getValue();

                    lstFiles.add(notice + "\nDated: " + rdate);
                    lstURL.add(url);


                }
                if (lstFiles.size() > 0) {
                    adapter1.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
