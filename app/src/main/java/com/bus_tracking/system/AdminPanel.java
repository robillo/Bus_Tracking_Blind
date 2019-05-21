package com.bus_tracking.system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class AdminPanel extends AppCompatActivity {

    ArrayList<String> lstUsers = new ArrayList<>();
    FirebaseAuth auth;
    String TAG = "ADMIN";

    EditText txtBusNo, txtSource, txtDestination, txtFare,txtRoute;
    Context context;
    StorageReference mStorageReference;
Button btnSaveDetails;
SharedPreferences prefrences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtBusNo = findViewById(R.id.txtBusNo);
        txtSource = findViewById(R.id.txtSource);
        txtDestination = findViewById(R.id.txtDestination);
        txtFare = findViewById(R.id.txtFare);
        txtRoute = findViewById(R.id.txtRoute);
        btnSaveDetails = findViewById(R.id.btnSaveDetails);
        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constant.DB);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        prefrences = PreferenceManager.getDefaultSharedPreferences(this);
//        myRef.child(Constant.bus_details).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                int count = 0;
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//
//                    String userid = (String) dataSnapshot1.child("userid").getValue();
//                    if (userid.equals(auth.getUid())) {
//                        name = (String) dataSnapshot1.child("name").getValue();
//                        phone = (String) dataSnapshot1.child("phone").getValue();
//                        email = (String) dataSnapshot1.child("email").getValue();
//                        url_image = (String) dataSnapshot1.child("url").getValue();
//
//                        user_type = (String) dataSnapshot1.child("utype").getValue();
//                        vehicle = (String) dataSnapshot1.child("vehicle").getValue();
//                        count++;
//                        break;
//
//                    }
//
//
//                }
//                if (count > 0) {
//                    try {
//                        txtPhone.setText(phone);
//                        txtVehicle.setText(vehicle);
//
//
//
//                    } catch (Exception e) {
//
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        txtBusNo.setText(prefrences.getString("vehicle",""));
        txtBusNo.setEnabled(false);
    }



    String bus_no, source, destination, fare,route;

    public void saveDetails(View v) {
        bus_no = txtBusNo.getText().toString().toLowerCase();
        source = txtSource.getText().toString().toLowerCase();
        destination = txtDestination.getText().toString();
        fare = txtFare.getText().toString();
        route = txtRoute.getText().toString().toLowerCase();
        if (bus_no.length() > 0) {
            if (source.length() > 0) {
                if (destination.length() > 0) {
                    if (route.length() > 0) {
                        btnSaveDetails.setEnabled(false);
                        BusDetails busDetails=new BusDetails(bus_no,source,destination,fare,prefrences.getString("userid",""),route,0,0);
                        myRef.child(Constant.bus_details).child(prefrences.getString("userid","")).setValue(busDetails);
                        txtBusNo.setText("");
                        txtFare.setText("");
                        txtDestination.setText("");
                        txtSource.setText("");
                        txtRoute.setText("");
                        btnSaveDetails.setEnabled(true);
                        prefrences.edit().putBoolean("bus_details",true).commit();
                        Toast.makeText(context, "Details saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Please enter route details.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter destination.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please enter source", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Please enter bus no.", Toast.LENGTH_SHORT).show();
        }
    }

    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog dialog;

//    public void sendNotification(View v) {
//        final String notice = txtNotice.getText().toString();
//        if (notice.length() > 0) {
//            dialog = ProgressDialog.show(context, "Please wait..", "Uploading notice..", true, true);
//            StorageReference sRef = mStorageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
//            sRef.putFile(uri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            dialog.dismiss();
//
//                            Upload upload = new Upload(notice, taskSnapshot.getDownloadUrl().toString(), Constant.getDate());
//                            myRef.child("upload").child(myRef.push().getKey()).setValue(upload);
//                            int count = 0;
//                            while (count < lstUsers.size()) {
//
//                                String arr[] = {lstUsers.get(count)};
//                                SENDFCM sendfcm = new SENDFCM();
//                                sendfcm.execute(arr);
//                                count++;
//                            }
//
//                            Toast.makeText(AdminPanel.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            //dialog.setMessage("upload done="+progress);
//                        }
//                    });
//
//        } else {
//            Toast.makeText(this, "Please set subject", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    class SENDFCM extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            FireBase fb = new FireBase();
//            Log.i(TAG, "doInBackground: " + params[0]);
//
//            JSONObject jsonObject = new JSONObject();
//            try {
//
//                jsonObject.put("title", "New Notice Uploaded");
//
//                jsonObject.put("msg", "New Notice has been uploaded please check notice board");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            return fb.send(params[0], jsonObject.toString());
//        }
//
//        @Override
//        protected void onPostExecute(final String aVoid) {
//            super.onPostExecute(aVoid);
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        JSONObject jObj = new JSONObject(aVoid);
//                        if (jObj.getInt("success") == 1) {
//                            Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            Toast.makeText(context, "Failed to send notification", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//
//        }
//    }
//
//    public void selectFile(View v) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/pdf");
//        startActivityForResult(intent, 1212);
//    }
//
//    String path = null;
//    Uri uri;
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 1212:
//                if (resultCode == RESULT_OK) {
//                    // Get the Uri of the selected file
//                    uri = data.getData();
//                    String uriString = uri.toString();
//                    File myFile = new File(uriString);
//                    path = myFile.getAbsolutePath();
//
//
//                    Log.i("MA", "onActivityResult: " + path);
//
//
//                    String displayName = null;
//
//                    if (uriString.startsWith("content://")) {
//                        Cursor cursor = null;
//                        try {
//                            cursor = context.getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                    } else if (uriString.startsWith("file://")) {
//                        displayName = myFile.getName();
//                    }
//                    txtFile.setText(displayName);
//                /*   path= getRealPathFromURI(uri);
//                    Log.i("MA", "onActivityResult: "+path);*/
//
//                    try {
//                        path = PathUtils.getPath(context, uri);
//                        Log.i("MA", "onActivityResult: " + path);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}
