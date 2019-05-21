package com.bus_tracking.system;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class SignedInActivityDriver extends AppCompatActivity {
    String name, email, phone, url_image = null, providerId, userid, strUtype, vehicle;
    ImageView imgUser;
    EditText txtPhone, txtVehicle;

    SharedPreferences sharedPreferences;
    ImageView imgQRCode;
    FloatingActionButton fab;
    Context context;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth auth;
    String TAG = "DB";
    String user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in_driver);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgUser = (ImageView) findViewById(R.id.imgUser);


        txtVehicle = (EditText) findViewById(R.id.txtVehicle);
        txtPhone = (EditText) findViewById(R.id.txtPhone);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constant.DB);
        userid = auth.getUid();
        name = auth.getCurrentUser().getDisplayName();
        email = auth.getCurrentUser().getEmail();
        url_image = auth.getCurrentUser().getPhotoUrl().toString();


        myRef.child(Constant.drivers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String userid = (String) dataSnapshot1.child("userid").getValue();
                    if (userid.equals(auth.getUid())) {
                        name = (String) dataSnapshot1.child("name").getValue();
                        phone = (String) dataSnapshot1.child("phone").getValue();
                        email = (String) dataSnapshot1.child("email").getValue();
                        url_image = (String) dataSnapshot1.child("url").getValue();

                        user_type = (String) dataSnapshot1.child("utype").getValue();
                        vehicle = (String) dataSnapshot1.child("vehicle").getValue();
                        count++;
                        break;

                    }


                }
                if (count > 0) {
                    try {
                        txtPhone.setText(phone);
                        txtVehicle.setText(vehicle);



                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Picasso.with(this).load(url_image).into(imgUser);
        fab = (FloatingActionButton) findViewById(R.id.fab);


    }



    public void save(View v) {

        phone = txtPhone.getText().toString();
        strUtype = "Driver";
        vehicle = txtVehicle.getText().toString();

        if (!TextUtils.isEmpty(vehicle)) {

                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(strUtype)) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(Constant.DB);

                        //String name, String userid, String url, String phone, String email, String dept_name, String strUtype, String password)
                        Driver user = new Driver(name, userid, url_image, phone, email, vehicle, strUtype,  null);
                        myRef.child(Constant.drivers).child(userid).setValue(user);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userid", userid)
                                .putString("name", name)
                                .putString("utype", strUtype)
                                .putString("email", email)

                                .putString("vehicle", vehicle)
                                .putString("image", url_image)
                                .putString("phone", phone)
                                .putBoolean("reg_status", true)
                                .apply();
                        Intent intent=new Intent(getBaseContext(),MainActivity.class);
                        startActivity(intent);
finish();
                        Toast.makeText(this, "Account Creation success", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Please check strUtype", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please check phone no.", Toast.LENGTH_SHORT).show();
                }



        } else {
            Toast.makeText(this, "Vehicle No. cannot be blank!", Toast.LENGTH_SHORT).show();
        }

    }

}
