package com.bus_tracking.system;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ViewProfile extends AppCompatActivity {
    ImageView imgUser;
    TextView txtDetails;
    String name, email, phone, url_image = null, providerId, userid, strUtype, vehicle;
    ;
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
String distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imgUser = findViewById(R.id.imgUser);
        txtDetails = findViewById(R.id.txtDetails);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constant.DB);
        userid = getIntent().getStringExtra("userid");
        distance = getIntent().getStringExtra("distance");
        Log.i(TAG, "onCreate: userid"+userid);

        myRef.child(Constant.drivers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String userid1 = (String) dataSnapshot1.child("userid").getValue();
                    if (userid1.equals(userid)) {
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
                        txtDetails.setText("Name:" + name + "\nEmail ID: " + email + "\nVehicle No." + vehicle + "\nPhone No." + phone);

                        Picasso.with(ViewProfile.this).load(url_image).into(imgUser);
                        speakRead("Bus Number. "+vehicle+" Distance ="+distance+" meters.");
                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

    }

    public void call(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phone));
            startActivity(callIntent);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    1);
        }

    }

    private TextToSpeech t1;

    public void speakRead(final String speak) {
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                t1.speak(speak,
                        TextToSpeech.QUEUE_FLUSH, null);

            }
        }.start();

        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

                promptSpeechInput();
            }
        }.start();
    }

    private final int REQ_CODE_SPEECH_INPUT = 100;
    String input;

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String value = result.get(0);
                    if (value.contains("ok") || value.contains("okay")) {
                        if (input.equalsIgnoreCase("call")) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phone));
                                startActivity(callIntent);

                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{
                                                Manifest.permission.CALL_PHONE
                                        },
                                        1);
                            }
                        }

//                        finish();
                    } else if (value.contains("no")) {
                        promptSpeechInput();
                    } else if (value.length() > 0) {
                        input = value;

                        t1.speak("You said " + input + " Say Ok to Continue or No To Retry", TextToSpeech.QUEUE_FLUSH, null);
                        new CountDownTimer(6000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onFinish() {
                                // TODO Auto-generated method stub

                                promptSpeechInput();
                            }
                        }.start();
                    }

                    Log.i("speak", "value=" + value);

                }
                break;

            }

        }
    }

    public void tapHere(View v) {
        speakRead("To Call Say call");
    }
}
