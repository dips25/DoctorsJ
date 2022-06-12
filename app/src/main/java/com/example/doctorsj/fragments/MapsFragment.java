package com.example.doctorsj.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.doctorsj.PatientsService;
import com.example.doctorsj.R;

import com.example.doctorsj.activities.MapsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Context c;
    private static final String TAG = MapsFragment.class.getSimpleName();
    Double latitude , longitude;
    TextView full_address;
    BottomNavigationView bottomNavigationView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapsFragment.this);
        }

        full_address = (TextView) view.findViewById(R.id.full_address);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {


                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude , longitude);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 16));

                    if (Geocoder.isPresent()) {

                        Geocoder geocoder = new Geocoder(getActivity());


                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 5);
                            String fulladdress = addresses.get(0).getAddressLine(0);
                            full_address.setText(fulladdress);


                            final List<String> searchparams = new ArrayList<>();
                            HashSet hashSet = new HashSet();


                            String[] splitted = fulladdress.trim().split(", ");



                            for (String s : splitted) {

                                if (s.contains(addresses.get(0).getPostalCode())) {

                                    int index = s.indexOf(addresses.get(0).getPostalCode());
                                    int length = s.length();
                                    searchparams.add(s.substring(0 , index-1).toLowerCase());
                                    searchparams.add(s.substring(index , length-1));
                                    hashSet.add(s.substring(0 , index-1).toLowerCase());
                                    hashSet.add(s.substring(0 , index-1).toLowerCase());
                                    continue;
                                }

                                searchparams.add(s.toLowerCase());
                                hashSet.add(s.toLowerCase());


                            }

                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("fulladdress" , fulladdress);
                            editor.commit();

                            Map map = new HashMap();
                            map.put("latitude" , latitude);
                            map.put("longitude" , longitude);
                            map.put("fulladdress" , fulladdress);
                            map.put("searchparams" , searchparams);

                            if (PatientsService.isRunning) {

                                map.put("isenabled" , true);

                            } else {

                                map.put("isenabled" , false);
                            }

                            FirebaseFirestore.getInstance().collection("Doctors")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(map , SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "onFailure: " + e.getMessage());

                                }
                            });

                        } catch (IOException e) {
                            Log.d(TAG, "onSuccess: " + e.getMessage());
                        }


                    }

                }
            });




            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {

                    LatLng latLng = mMap.getCameraPosition().target;
                    latitude = Double.valueOf(String.format("%.6f" , latLng.latitude));
                    longitude = Double.valueOf(String.format("%.6f" , latLng.longitude));


                    Toast.makeText(c, "Latitude" + latitude + "\n" + "Longitude:" + longitude, Toast.LENGTH_SHORT).show();


                    if (Geocoder.isPresent()) {

                        if (latitude > 1.0 && longitude > 1.0) {

                            Geocoder geocoder = new Geocoder(getActivity());

                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 5);
                                full_address.setText(addresses.get(0).getAddressLine(0));


                            } catch (IOException e) {
                                Log.d(TAG, "onSuccess: " + e.getMessage());
                            }

                        }

                    }

                }
            });

        }

    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        c = context;
    }





}