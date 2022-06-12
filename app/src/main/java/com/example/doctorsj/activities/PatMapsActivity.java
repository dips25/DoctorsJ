package com.example.doctorsj.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class PatMapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMyLocationButtonClickListener
, View.OnClickListener{

    private GoogleMap mMap;
    Double latitude;
    Double longitude;
    TextView change_location_full_address;
    Button change_location_button;
    Intent intent;

    TextView pat_maps_name , pat_maps_full_address , pat_maps_date , pat_maps_time;
    CircleImageView pat_maps_profile_image;
    Button pat_maps_accept , pat_maps_decline;

    String id;
    String name;
    String fulladdress;
    String profileimage;
    String date;
    String time;
    String status;
    String timestamp;
    int convertedtime;



    private static final String TAG = PatMapsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_maps);

        intent = getIntent();


        id = intent.getStringExtra("id");
        latitude = intent.getDoubleExtra("latitude" , 0.0);
        longitude = intent.getDoubleExtra("longitude" , 0.0);
        name = intent.getStringExtra("name");
        fulladdress = intent.getStringExtra("fulladdress");
        profileimage = intent.getStringExtra("profileimage");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        convertedtime = intent.getIntExtra("convertedtime" , 0);
        timestamp = intent.getStringExtra("timestamp");
        status = intent.getStringExtra("status");
        name = intent.getStringExtra("name");

        String timest = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            timest = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        pat_maps_profile_image = (CircleImageView) findViewById(R.id.pat_map_profile_image);
        pat_maps_name = (TextView) findViewById(R.id.pat_map_name);
        pat_maps_full_address = (TextView) findViewById(R.id.pat_map_full_address);
        pat_maps_accept = (Button) findViewById(R.id.pat_map_accept);
        pat_maps_decline = (Button) findViewById(R.id.pat_map_decline);
        pat_maps_date = (TextView) findViewById(R.id.pat_map_date);
        pat_maps_time = (TextView) findViewById(R.id.pat_map_time);

        pat_maps_time.setText(timest);

        pat_maps_name.setText(name);
        pat_maps_full_address.setText(fulladdress);
        pat_maps_date.setText(date);

        Glide.with(PatMapsActivity.this)
                .load(profileimage)
                .placeholder(R.drawable.ic_user)
                .into(pat_maps_profile_image);

       // toggleButtonState();

        if (status.equalsIgnoreCase("accepted")) {

            pat_maps_accept.setVisibility(View.GONE);

        } else {

            pat_maps_accept.setVisibility(View.VISIBLE);
        }


        pat_maps_accept.setOnClickListener(this);
        pat_maps_decline.setOnClickListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void toggleButtonState() {

        FirebaseFirestore.getInstance().collection("Doctors")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Patients")
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            return;
                        }

                        if (value.exists()) {

                            Patients patients = value.toObject(Patients.class);

                            if (patients.getStatus().equalsIgnoreCase("pending")) {

                                pat_maps_accept.setVisibility(View.VISIBLE);

                            } else {

                                pat_maps_decline.setVisibility(View.GONE);

                            }

                        }
                    }
                });


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));
        mMap.setMyLocationEnabled(true);

        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMyLocationButtonClick() {

        LatLng latLng = new LatLng(latitude, longitude);
       // mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));
        return false;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.pat_map_accept:

                final Map map = new HashMap();

                SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);

                map.put("id" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("name" , sharedPreferences.getString("name" , ""));
                map.put("contact" , sharedPreferences.getString("contact" , ""));
                map.put("profilepic" , sharedPreferences.getString("profilepic" , ""));
                map.put("fulladdress" , sharedPreferences.getString("fulladdress" , ""));
                map.put("status" , "accepted");
                map.put("latitude" , sharedPreferences.getFloat("latitude" , 0));
                map.put("longitude" , sharedPreferences.getFloat("longitude" , 0));
                map.put("date" , date);
                map.put("time" , time);
                map.put("convertedtime" , convertedtime);
                map.put("timestamp" , timestamp);



                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Patients")
                        .document(id)
                        .update("status" , "accepted")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                FirebaseFirestore.getInstance().collection("Patients")
                                        .document(id)
                                        .collection("Doctors")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                pat_maps_accept.setVisibility(View.GONE);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "onFailure: " + e.getMessage());

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDoc: " + e.getMessage());

                    }
                });

                break;

            case R.id.pat_map_decline:

                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Patients")
                        .document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelDoc: "+  e.getMessage());

                    }
                });

                FirebaseFirestore.getInstance().collection("Patients")
                        .document(id)
                        .collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelPat: " + e.getMessage());

                    }
                });


                break;

        }
    }
}