package com.example.doctorsj.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doctorsj.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ForkJoinPool;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 200;

    private static final String TAG = SetupActivity.class.getSimpleName();

    EditText name , contact;
    TextView email , address , time_text;
    CircleImageView setup_image;
    FrameLayout change_picture;
    CoordinatorLayout root_layout;
    RelativeLayout bottom_sheet_root;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    ImageView camera , gallery;
    File photofile;
    String currentpicturepath;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.CAMERA};
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    String date , filename;
    StorageReference storageReference;
    UploadTask uploadTask;
    ImageView change_address;
    FusedLocationProviderClient fusedLocationProviderClient;
    final ArrayList<String> searchparams = new ArrayList<>();
    Button save , time_spinner , choose_timer;
    TimePicker pref_timepicker;
    String convertedtime;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SetupActivity.this);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Picture.Please Wait.");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this , Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(permissions, 500);

            } else {

                ActivityCompat.requestPermissions(SetupActivity.this, permissions, 500);
            }

        }

        root_layout = (CoordinatorLayout) findViewById(R.id.root_layout);
        bottom_sheet_root = root_layout.findViewById(R.id.bottom_sheet_root);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_root);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        camera = (ImageView) bottom_sheet_root.findViewById(R.id.camera);
        gallery = (ImageView) bottom_sheet_root.findViewById(R.id.gallery);
        address = (TextView) findViewById(R.id.address);
        email = (TextView) findViewById(R.id.email);
        contact = (EditText) findViewById(R.id.contact);
        change_address = (ImageView) findViewById(R.id.button_change_address);
        time_spinner = (Button) findViewById(R.id.time_spinner);
        save = (Button) findViewById(R.id.save);
        time_text = (TextView) findViewById(R.id.time_text);

        time_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SetupActivity.this)
                        .setCancelable(true)
                        .setView(R.layout.layout_time_picker)
                        .setTitle(R.string.choose_your_time_dialogue);

                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();


                pref_timepicker = (TimePicker) alertDialog1.findViewById(R.id.pref_timepicker);
                pref_timepicker.setIs24HourView(true);
                choose_timer = (Button) alertDialog1.findViewById(R.id.choose_time);



                choose_timer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        time_text.setText(pref_timepicker.getCurrentHour() + ":" + pref_timepicker.getCurrentMinute());
                        convertedtime = String.valueOf((pref_timepicker.getCurrentHour()*60) + pref_timepicker.getCurrentMinute());

                    }
                });




            }
        });

        if (sharedPreferences.getBoolean("isPhoneLogin" , false)) {

            contact.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        } else if (sharedPreferences.getBoolean("isEmailLogin" , false)) {

            email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveToDatabase();
            }
        });

        change_address.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                searchparams.clear();

                fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY , new CancellationTokenSource().getToken())
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                final double latitude = location.getLatitude();
                                final double longitude = location.getLongitude();

                                if (Geocoder.isPresent()) {

                                    Toast.makeText(SetupActivity.this, "Geocoder Present.", Toast.LENGTH_SHORT).show();

                                    Geocoder geocoder = new Geocoder(SetupActivity.this);


                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(latitude , longitude , 5);
                                        String fulladdress = addresses.get(0).getAddressLine(0);
                                        address.setText(fulladdress);
                                        Log.d(TAG, "Address: " + fulladdress);

                                        SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        HashSet hashSet = new HashSet();


                                        editor.putFloat("latitude" , (float) latitude);
                                        editor.putFloat("longitude" , (float) longitude);
                                        editor.putString("fulladdress" , address.getText().toString());

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

                                        editor.putStringSet("searchparams" , hashSet);
                                        editor.commit();

                                    } catch (IOException e) {
                                        Log.d(TAG, "onSuccess: " + e.getMessage());
                                    }

                                }



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });


        name = (EditText) findViewById(R.id.name);
        contact = (EditText) findViewById(R.id.contact);

        email = (TextView) findViewById(R.id.email);
        address = (TextView) findViewById(R.id.address);

        setup_image = (CircleImageView) findViewById(R.id.setup_imageview);
        change_picture = (FrameLayout) findViewById(R.id.change_picture);


        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchCameraIntent();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent , "Select Image") , GALLERY_CODE);



            }
        });



    }

    public void dispatchCameraIntent () {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            photofile = createImageFile();

        }

        if (photofile != null) {

            Uri photouri = FileProvider.getUriForFile(SetupActivity.this , "com.example.doctorsj.fileprovider" , photofile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT , photouri);
            startActivityForResult(intent , CAMERA_CODE);
        }
    }

    public File createImageFile () {

        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timestamp = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
        String filename = "JPEG_" + timestamp + "_";
        File final_file = null;
        try {
            final_file = File.createTempFile(filename , ".jpg" , file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentpicturepath = final_file.getPath();
        return final_file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch(requestCode) {

                case CAMERA_CODE:


                    Glide.with(SetupActivity.this)
                            .load(currentpicturepath)
                            .placeholder(R.drawable.ic_user)
                            .into(setup_image);

                    date = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
                    filename = date + ".jpg";

                    progressDialog.show();

                    storageReference = storage.getReference().child("ProfilePic/" + filename);
                    uploadTask = storageReference.putFile(Uri.fromFile(new File(currentpicturepath)));
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(SetupActivity.this,getString(R.string.upload_dialogue) , Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setProgress((int) progress);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(SetupActivity.this, getString(R.string.upload_failed_dialogue), Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                        }
                    });

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                            if (!task.isSuccessful()) {

                                progressDialog.cancel();
                                throw task.getException();
                            }

                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            Uri uri = task.getResult();
                            progressDialog.cancel();
                            setPhotoPath(uri.toString());

                        }
                    });

                    break;

                case GALLERY_CODE:

                    Uri selected_image_uri = data.getData();
                    Glide.with(SetupActivity.this)
                            .load(selected_image_uri)
                            .placeholder(R.drawable.ic_user)
                            .into(setup_image);

                    progressDialog.setMessage("Please Wait. This may take some time.");
                    progressDialog.show();



                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selected_image_uri);
                        date = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());

                        filename = date + ".jpg";
                        storageReference = storage.getReference("ProfilePic/" + filename);
                        uploadTask = storageReference.putStream(inputStream);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                Log.d(TAG, "onProgress: " + progress + "%");
                                progressDialog.setProgress((int) progress);

                            }
                        });
                        Task<Uri> uri = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {

                                    progressDialog.cancel();

                                    throw task.getException();
                                }

                                progressDialog.cancel();

                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {

                                    Uri dowloadUri = task.getResult();
                                    progressDialog.cancel();
                                    setPhotoPath(dowloadUri.toString());

                                    Log.d(TAG, "onComplete: " + dowloadUri.toString());

                                }

                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 500) {

            if (grantResults.length>0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {


                }
            }
        }
    }


    public void saveToDatabase() {

        progressDialog.setMessage("Saving Details.Please wait..");
        progressDialog.show();

        if (currentpicturepath != null && !currentpicturepath.equals("")) {

            Map map = new HashMap();
            map.put("name" , name.getText().toString());
            map.put("contact" , contact.getText().toString());
            map.put("email" , email.getText().toString());
            map.put("profilepic" , currentpicturepath);
            map.put("fulladdress" , address.getText().toString());
            map.put("searchparams" , searchparams);
            map.put("preftime" , convertedtime);

            SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name" , name.getText().toString());
            editor.putString("contact" , contact.getText().toString());
            editor.putString("email" , email.getText().toString());
            editor.putString("profilepic" , currentpicturepath);
            editor.putString("fulladdress", address.getText().toString());
            editor.putString("preftimetime" , convertedtime);

            editor.commit();



            FirebaseFirestore.getInstance().collection("Doctors")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(SetupActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                            Intent intent = new Intent(SetupActivity.this , MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: " + e.getMessage());
                    progressDialog.cancel();

                }
            });


        } else {


            Map map = new HashMap();
            map.put("name" , name.getText().toString());
            map.put("contact" , contact.getText().toString());
            map.put("email" , email.getText().toString());
            map.put("profilepic" , "");
            map.put("fulladdress" , address.getText().toString());
            map.put("preftime" , convertedtime);


            SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name" , name.getText().toString());
            editor.putString("contact" , contact.getText().toString());
            editor.putString("email" , email.getText().toString());
            editor.putString("profilepic" , "");
            editor.putString("fulladdress", address.getText().toString());
            editor.putString("preftime" , convertedtime);

            editor.commit();



            FirebaseFirestore.getInstance().collection("Doctors")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(SetupActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();

                            Intent intent = new Intent(SetupActivity.this , MapsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: " + e.getMessage());
                    progressDialog.cancel();

                }
            });


        }

    }

    public void setPhotoPath(String photoPath) {

        this.currentpicturepath = photoPath;
    }

}