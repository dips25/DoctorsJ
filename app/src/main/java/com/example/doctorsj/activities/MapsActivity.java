package com.example.doctorsj.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.doctorsj.R;
import com.example.doctorsj.fragments.MapsFragment;
import com.example.doctorsj.fragments.PaymentsFragment;
import com.example.doctorsj.fragments.PendingFragment;
import com.example.doctorsj.fragments.ProfileFragment;
import com.example.doctorsj.fragments.RequestFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.doctorsj.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapsActivity extends AppCompatActivity {

    Intent intent;
    private GoogleMap mMap;
    private BottomNavigationView bottomNavigationView;
    private ActivityMapsBinding binding;
    private static final String TAG = MapsActivity.class.getSimpleName();
    FusedLocationProviderClient fusedLocationProviderClient;
    Bundle bundle;
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bundle = getIntent().getExtras();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {


                    case R.id.home:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new MapsFragment())
                                .commit();

                        break;

                    case R.id.request:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new RequestFragment())
                                .commit();
                        break;

                    case R.id.pending:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new PendingFragment())
                                .commit();
                        break;

                    case R.id.history:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new PaymentsFragment())
                                .commit();

                        break;

                    case R.id.profile:

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame , new ProfileFragment())
                                .commit();
                        break;

                }
                return true;
            }
        });


        if (bundle != null ) {


                getSupportFragmentManager().beginTransaction().replace(R.id.frame , new PendingFragment()).commit();


        } else {

               getSupportFragmentManager().beginTransaction().replace(R.id.frame , new MapsFragment()).commit();

        }

    }



    @Override
    protected void onStart() {
        super.onStart();

            checkGPS();

    }

    private void checkGPS() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showDialog();


        }
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setCancelable(false);
        builder.setMessage("App requires Location Services to be enabled.Do you want to enable it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}