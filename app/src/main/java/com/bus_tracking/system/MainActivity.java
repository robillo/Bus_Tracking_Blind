package com.bus_tracking.system;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {


    SharedPreferences prefrences;

    ListView listView;
    Adapter1 adapter;
    LocationManager locationManager;
    FirebaseAuth auth;
    NavigationView navigationView;
    EditText txtSource, txtDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        context = this;
        prefrences = PreferenceManager.getDefaultSharedPreferences(this);
        listView = findViewById(R.id.list);
        adapter = new Adapter1(this, R.layout.custum_list, lstBusNo1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: " + position);
                Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                intent.putExtra("userid", lstDriverID1.get(position));
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTosUrl("https://superapp.example.com/terms-of-service.html")
                            .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),


                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                    ))
                            .build(),
                    RC_SIGN_IN);
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constant.DB);
        txtSource = findViewById(R.id.txtSource);
        txtDestination = findViewById(R.id.txtDestination);
        myRef.child(Constant.bus_details).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstBusNo.clear();
                lstLatitude.clear();
                lstLongitude.clear();
                lstRoutes.clear();
                lstDriverID.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        lstDriverID.add(dataSnapshot1.child("driver_id").getValue().toString());
                        lstRoutes.add(dataSnapshot1.child("route").getValue().toString());
                        lstBusNo.add(dataSnapshot1.child("bus_no").getValue().toString());
                        lstSource.add(dataSnapshot1.child("source").getValue().toString());
                        lstDestination.add(dataSnapshot1.child("destination").getValue().toString());
                        lstLongitude.add(dataSnapshot1.child("longitude").getValue(Long.class));
                        lstLatitude.add(dataSnapshot1.child("latitude").getValue(Long.class));
                    }catch (Exception e){
                        e.printStackTrace();
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
        speakRead("enter source");
    }

    ArrayList<Long> lstLatitude = new ArrayList<>();
    ArrayList<Long> lstLongitude = new ArrayList<>();
    ArrayList<String> lstBusNo = new ArrayList<>();
    ArrayList<String> lstBusNo1 = new ArrayList<>();
    ArrayList<String> lstDistance = new ArrayList<>();
    ArrayList<String> lstRoutes = new ArrayList<>();
    ArrayList<String> lstRoutes1 = new ArrayList<>();
    ArrayList<String> lstDriverID = new ArrayList<>();
    ArrayList<String> lstDriverID1 = new ArrayList<>();
    ArrayList<String> lstSource = new ArrayList<>();
    ArrayList<String> lstDestination = new ArrayList<>();


    FirebaseDatabase database;
    DatabaseReference myRef;
    String source = "", destination = "";

    public void viewRoutes(View v) {
        getRoutes();
    }

    public void getRoutes() {
        source = txtSource.getText().toString().toLowerCase().trim();
        destination = txtDestination.getText().toString().toLowerCase().trim();
        if (source.length() > 0) {
            if (destination.length() > 0) {
                long current_lat = (long) Double.parseDouble(prefrences.getString("latitude", "0.0"));
                long current_lon = (long) Double.parseDouble(prefrences.getString("longitude", "0.0"));
                int count = 0;
                lstBusNo1.clear();
                lstRoutes1.clear();
                lstDriverID1.clear();
                lstDistance.clear();

                while (count < lstBusNo.size()) {
                    Log.i(TAG, "viewRoutes: " + lstSource.get(count).toLowerCase());
                    Log.i(TAG, "viewRoutes: " + lstDestination.get(count).toLowerCase());
                    Log.i(TAG, "viewRoutes: " + source);
                    Log.i(TAG, "viewRoutes: " + destination);

                    if ((lstSource.get(count).toLowerCase().contains(source.toLowerCase()) && lstDestination.get(count).toLowerCase().contains(destination.toLowerCase()))

                            ) {
                        long given_lat = lstLatitude.get(count);
                        long given_lon = lstLongitude.get(count);
                        Log.i(TAG, "viewRoutes: curr loc" + current_lat + "," + current_lon);
                        Log.i(TAG, "viewRoutes: given loc" + given_lat + "," + given_lon);
                        double distance = distance(current_lat, given_lat, current_lon, given_lon, Constant.altitude, 0);
                        Log.i(TAG, "viewRoutes: distance" + distance);
                        if (distance <= 1000000) {
                            lstBusNo1.add(lstBusNo.get(count));
                            lstRoutes1.add(lstRoutes.get(count));
                            lstDriverID1.add(lstDriverID.get(count));
                            lstDistance.add(Double.toString(distance));
                            adapter.notifyDataSetChanged();
                        }
                    }
                    count++;
                }
                if (lstDriverID1.size() == 0) {
                    speakRead("You have " + lstDriverID1.size() + " Enter source");
                } else {
                    speakRead("You have " + lstDriverID1.size() + " results. Say 1 to get details of first result");
                }

            } else {
                Toast.makeText(context, "Destination cannot be null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Source cannot be null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: " + location.getLatitude() + "," + location.getLongitude() + "altitude=" + location.getAltitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        prefrences.edit().putString("latitude", Double.toString(latitude)).putString("longitude", Double.toString(longitude)).apply();
        if (prefrences.getBoolean("bus_details", false)) {
            Log.i(TAG, "onLocationChanged: updating loc");
            if (prefrences.getString("utype", "").equalsIgnoreCase("driver")) {
                myRef.child(Constant.bus_details).child(prefrences.getString("userid", "")).child("latitude").setValue(latitude);
                myRef.child(Constant.bus_details).child(prefrences.getString("userid", "")).child("longitude").setValue(longitude);
            }
        }

    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */


    Context context;


    String token_id;

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            checkRegID();
            requestLocation();
        }
        if (auth.getCurrentUser() != null) {
            navigationView.getMenu().findItem(R.id.nav_sign_in).setTitle(auth.getCurrentUser().getDisplayName());
            View hView = navigationView.getHeaderView(0);
            ImageView nav_user = (ImageView) hView.findViewById(R.id.imageView);
            TextView txtEmail = (TextView) hView.findViewById(R.id.txtEmail);
            TextView txtName = (TextView) hView.findViewById(R.id.txtName);
            txtEmail.setText(prefrences.getString("email", ""));
            txtName.setText(prefrences.getString("name", ""));
            try {

                Picasso.with(this).load(prefrences.getString("image", "")).into(nav_user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED)) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0,
                            1, this);
                } else if (locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, 0,
                            1, this);
                } else {


                    Toast.makeText(getApplicationContext(), "Enable Location", Toast.LENGTH_LONG).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }

        }
    }

    public void checkRegID() {
        if (playServicesAvailable()) {

            Log.i(TAG, "onCreate: " + token_id);
            token_id = FirebaseInstanceId.getInstance().getToken();
            if (auth.getCurrentUser() != null && prefrences.getBoolean("reg_status", false)) {
                if (prefrences.getString("utype", "").equalsIgnoreCase("user")) {
                    User user = new User(auth.getCurrentUser().getDisplayName(), auth.getUid(), auth.getCurrentUser().getPhotoUrl().toString(), prefrences.getString("phone", ""), auth.getCurrentUser().getEmail(), prefrences.getString("utype", null), token_id);
                    myRef.child(Constant.users).child(auth.getUid()).setValue(user);
                    prefrences.edit().putString("token_id", token_id).commit();
                } else {//name, userid, url_image, phone, email, vehicle, strUtype,  null
                    Driver user = new Driver(auth.getCurrentUser().getDisplayName(), auth.getUid(), auth.getCurrentUser().getPhotoUrl().toString(), prefrences.getString("phone", ""),
                            auth.getCurrentUser().getEmail(), prefrences.getString("vehicle", null), prefrences.getString("utype", ""), token_id);
                    myRef.child(Constant.drivers).child(auth.getUid()).setValue(user);
                    prefrences.edit().putString("token_id", token_id).commit();

                }


            }
            Log.i(TAG, "onCreate: " + token_id);

        } else {
            Log.i(TAG, "sendNotificationToUser: no play services");
            // ... log error, or handle gracefully
        }
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean playServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int RC_SIGN_IN = 123;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_in) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                item.setTitle(auth.getCurrentUser().getDisplayName());


                if (prefrences.getString("utype", "").equalsIgnoreCase("user")) {
                    Intent intent1 = new Intent(getBaseContext(), SignedInActivityUser.class);
                    startActivity(intent1);
                } else {//name, userid, url_image, phone, email, vehicle, strUtype,  null
                    Intent intent1 = new Intent(getBaseContext(), SignedInActivityDriver.class);
                    startActivity(intent1);

                }
            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()


                                .setTosUrl("https://superapp.example.com/terms-of-service.html")
                                .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                                .setAvailableProviders(
                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),


                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                        ))
                                .build(),
                        RC_SIGN_IN);
                // not signed in
            }
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                prefrences.edit().clear().commit();
                finish();
                Toast.makeText(context, "Logout success", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_admin) {
//            Intent intent1 = new Intent(getBaseContext(), MarkAttendence.class);
//
//            startActivity(intent1);
            Intent intent1 = new Intent(getBaseContext(), AdminPanel.class);

            startActivity(intent1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    String path;
    String TAG = "MAINACTIVITY";

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                Intent intent1 = new Intent(getBaseContext(), SignedInActivityUser.class);

                startActivity(intent1);

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
        } else if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {

            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String value = result.get(0);
            if (value.contains("ok") || value.contains("okay")) {
                if (!(source.length() > 0)) {
                    source = input;
                    txtSource.setText(source);
                    speakRead("Enter destination");
                } else if (!(destination.length() > 0)) {

                    destination = input;
                    txtDestination.setText(destination);
                    speakRead("Say go to get results");
                } else if (input.equalsIgnoreCase("go")) {
                    if (source.length() > 0) {
                        if (destination.length() > 0) {
                            getRoutes();
                        } else {
                            speakRead("please enter destination");
                        }
                    } else {
                        speakRead("please enter source");
                    }
                } else {
                    try {
                        if (input.equalsIgnoreCase("1") || input.equalsIgnoreCase("one")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(0));
                            intent.putExtra("distance", lstBusNo1.get(0));
                            intent.putExtra("bus_no", lstBusNo1.get(0));
                            startActivity(intent);
                        } else if (input.equalsIgnoreCase("2") || input.equalsIgnoreCase("two")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(1));
                            startActivity(intent);
                        } else if (input.equalsIgnoreCase("3") || input.equalsIgnoreCase("three")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(2));
                            startActivity(intent);
                        } else if (input.equalsIgnoreCase("4") || input.equalsIgnoreCase("four")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(3));
                            startActivity(intent);
                        } else if (input.equalsIgnoreCase("5") || input.equalsIgnoreCase("five")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(4));
                            startActivity(intent);
                        } else if (input.equalsIgnoreCase("6") || input.equalsIgnoreCase("six")) {
                            Intent intent = new Intent(getBaseContext(), ViewProfile.class);
                            intent.putExtra("userid", lstDriverID1.get(5));
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        speakRead("Invalid results");
                    }
                }


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


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void showSnackbar(final int id) {
        Toast.makeText(getBaseContext(), id, Toast.LENGTH_SHORT).show();


    }

    Location location = null;

    public void requestLocation() {
        Log.i(TAG, "requestLocation: requesting location");

        try {
            LocationManager locationManager;
            String contex = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(contex);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, false);
            int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

            if (rc == PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(provider);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                //seconds and meter
                locationManager.requestLocationUpdates(provider, 0, 0,
                        locationListener);
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            updateWithNewLongitude(location);
        }

        @Override
        public void onProviderDisabled(String provider) {

            //updateWithNewLongitude(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    double latitude = 0.0, longitude = 0.0;

    private void updateWithNewLongitude(Location location) {

        // myLocationText = (TextView) findViewById(R.id.myLocationText);
        if (location != null) {
            latitude = location.getLongitude();
            longitude = location.getLongitude();

            //  Toast.makeText(context, "Current Loc="+latitude+","+longitude, Toast.LENGTH_SHORT).show();

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


    public void tapHere(View v) {
        if(!(source.length()>0)){
            speakRead("Enter Source");
        }
        else
        if(!(destination.length()>0)){
            speakRead("Enter Destination");
        }else if(!(lstDriverID1.size()>0)){
        speakRead("Say go to proceed");
        }else {
            speakRead("Say Something");
        }

    }
}
