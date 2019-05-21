package com.bus_tracking.system;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

public class EntryActivity extends AppCompatActivity {
    SharedPreferences prefrences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefrences = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefrences.getBoolean("reg_status", false)) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_entry);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.UK);
                    }
                }
            });
        }
    }

    public void driver(View v){
        utype="driver";
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                ))
                        .build(),
                RC_SIGN_IN);
    }

    public void user(View v){
        utype="user";
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                ))
                        .build(),
                RC_SIGN_IN);
    }
    private TextToSpeech t1;

    public void speakRead(final String speak, String read) {
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
    String utype;

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

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()


                                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
                                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                                        .setAvailableProviders(
                                                Arrays.asList(


                                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                                ))
                                        .build(),
                                RC_SIGN_IN);
//                        Intent i=new Intent(getBaseContext(),EmailIDRegScreen.class);
//                        i.putExtra("name", email);
//                        startActivity(i);
                        finish();
                    } else if (value.contains("no")) {
                        promptSpeechInput();
                    } else if (value.length() > 0) {
                        utype = value;

                        t1.speak("You said " + utype + " Say Ok to Continue or No To Retry", TextToSpeech.QUEUE_FLUSH, null);
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
            case RC_SIGN_IN:
                if (requestCode == RC_SIGN_IN) {
                    IdpResponse response = IdpResponse.fromResultIntent(data);

                    // Successfully signed in
                    if (resultCode == RESULT_OK) {
                        if (utype.equalsIgnoreCase("user")) {
                            Intent intent1 = new Intent(getBaseContext(), SignedInActivityUser.class);
                            startActivity(intent1);
finish();
                        } else {
                            Intent intent1 = new Intent(getBaseContext(), SignedInActivityDriver.class);
                            startActivity(intent1);
finish();
                        }

                        return;
                    } else {
                        // Sign in failed
                        if (response == null) {
                            // User pressed back button
                            showSnackbar(R.string.sign_in_cancelled);
                            return;
                        }

                        if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                            showSnackbar(R.string.no_internet_connection);
                            return;
                        }

                        if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                            showSnackbar(R.string.unknown_error);
                            return;
                        }
                    }

                    showSnackbar(R.string.unknown_sign_in_response);
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }

        }
    }

    public void tapHere(View v) {
        speakRead("Email ID Please", null);
    }

    private static final int RC_SIGN_IN = 123;
    String TAG = "Entry";


    public void showSnackbar(final int id) {
        Toast.makeText(getBaseContext(), id, Toast.LENGTH_SHORT).show();


    }
}
