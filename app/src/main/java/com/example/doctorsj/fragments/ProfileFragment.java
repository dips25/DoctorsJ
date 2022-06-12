package com.example.doctorsj.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doctorsj.PatientsService;
import com.example.doctorsj.R;
import com.example.doctorsj.activities.LoginActivity;
import com.example.doctorsj.activities.SetupActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.GeneratedMessageLite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final int CAMERA_CODE = 100;
    private static final int GALLERY_CODE = 200;


    TextView text_change_location;
    SwitchCompat toggle_switchh;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    public static boolean isEnabled;
    TextView profile_name , profile_email , profile_contact , profile_address , profile_location;
    SharedPreferences sharedPreferences;
    Button profile_edit , profile_logout;
    ImageView change_location;
    CircleImageView profile_screen_image;
    Button dialog_camera , dialog_gallery;

    File photofile;
    String currentpicturepath;
    LinearLayout save_profile_image;

    ProgressDialog progressDialog;
    Uri selected_image_uri;

    EditText profile_edit_name , profile_edit_contact , profile_edit_email;
    Button save_name_contact;

    BottomNavigationView bottomNavigationView;



    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseApp.initializeApp(getContext());

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        progressDialog = new ProgressDialog(getContext());
        sharedPreferences = getContext().getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        profile_name = (TextView) view.findViewById(R.id.profile_name);
        profile_email = (TextView) view.findViewById(R.id.profile_email);
        profile_contact = (TextView) view.findViewById(R.id.profile_contact);
        profile_edit = (Button) view.findViewById(R.id.profile_edit);
        profile_logout = (Button) view.findViewById(R.id.profile_logout);
        toggle_switchh = (SwitchCompat) view.findViewById(R.id.toggle_switch);
        profile_location = (TextView) view.findViewById(R.id.profile_location);
        change_location = (ImageView) view.findViewById(R.id.change_location);
        profile_screen_image = (CircleImageView) view.findViewById(R.id.profile_screen_image);
        save_profile_image = (LinearLayout) view.findViewById(R.id.save_profile_image);
        profile_edit = (Button) view.findViewById(R.id.profile_edit);
        save_profile_image.setVisibility(View.GONE);

        profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.edit_profile_dialog);
                dialog.show();

                profile_edit_name = (EditText) dialog.findViewById(R.id.edit_profile_name);
                profile_edit_contact = (EditText) dialog.findViewById(R.id.edit_profile_contact);
                profile_edit_email = (EditText) dialog.findViewById(R.id.edit_profile_email);
                save_name_contact = (Button) dialog.findViewById(R.id.save_name_contact);

                profile_edit_name.setText(sharedPreferences.getString("name" , ""));
                profile_edit_contact.setText(sharedPreferences.getString("contact" , ""));
                profile_edit_email.setText(sharedPreferences.getString("email" , ""));


                save_name_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       final String name = profile_edit_name.getText().toString();
                       final String contact = profile_edit_contact.getText().toString();
                       final String email = profile_edit_email.getText().toString();

                        if (!name.equals("") && name != null) {

                            Map map = new HashMap();
                            map.put("name" , name);
                            map.put("contact" , contact);

                            FirebaseFirestore.getInstance()
                                    .collection("Doctors")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(map , SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("name" , name);
                                            editor.putString("contact" , contact);
                                            editor.apply();

                                            profile_name.setText(name);
                                            profile_contact.setText(contact);
                                            profile_email.setText(email);
                                            dialog.cancel();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                        } else {

                            Toast.makeText(getContext(), getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        profile_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name" , "");
                editor.putString("contact" , "");
                editor.putString("email" , "");
                editor.putString("profilepic" , "");
                editor.putString("fulladdress" , "");

                if (sharedPreferences.getBoolean("isPhoneLogin" , false)) {

                    editor.putBoolean("isPhoneLogin" , false);

                } else if (sharedPreferences.getBoolean("isEmailLogin" , false)) {

                    editor.putBoolean("isEmailLogin" , false);
                }

                editor.commit();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext() , LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        save_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected_image_uri != null) {

                    saveProfileImage(selected_image_uri);
                } else {

                    Toast.makeText(getContext(), getString(R.string.cant_upload), Toast.LENGTH_SHORT).show();
                }
            }
        });




        Glide.with(getContext()).load(sharedPreferences.getString("profilepic" , ""))
                .placeholder(R.drawable.ic_user)
                .into(profile_screen_image);

        profile_name.setText(sharedPreferences.getString("name" , ""));
        profile_email.setText(sharedPreferences.getString("email" , ""));
        profile_contact.setText(sharedPreferences.getString("contact" , ""));
        profile_location.setText(sharedPreferences.getString("fulladdress" , ""));

        profile_screen_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.camera_gallery_dialog);
                dialog.setCancelable(true);
                dialog.show();

                dialog_camera = (Button) dialog.findViewById(R.id.dialog_camera);
                dialog_gallery = (Button) dialog.findViewById(R.id.dialog_gallery);

                dialog_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dispatchCameraIntent();
                    }
                });

                dialog_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_PICK , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent , "Select Image") , GALLERY_CODE);



                    }
                });

            }
        });



        if (PatientsService.isRunning) {

            toggle_switchh.setChecked(true);

        } else {

            toggle_switchh.setChecked(false);
        }

        if (currentpicturepath != null) {


        }


        toggle_switchh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    Intent intent1 = new Intent(getContext() , PatientsService.class);
                    ContextCompat.startForegroundService(getContext() , intent1);

                    FirebaseFirestore.getInstance().collection("Doctors")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("isenabled" , true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "Toggle Enabled Error: " + e.getMessage());

                        }
                    });


                } else {

                    Intent intent1 = new Intent(getContext() , PatientsService.class);
                    getContext().stopService(intent1);

                    FirebaseFirestore.getInstance().collection("Doctors")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("isenabled" , false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "OnEnabledStop: " + e.getMessage());

                        }
                    });

                }

            }
        });



        change_location.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {


                profile_location.setText(R.string.getting_location);

                if (Geocoder.isPresent()) {

                    final Geocoder geocoder = new Geocoder(getContext());

                    CancellationTokenSource cts = new CancellationTokenSource();

                    fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY , cts.getToken())
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {

                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude() , location.getLongitude() , 5);
                                    } catch (IOException e) {
                                        Log.d(TAG, "Error: " + e.getMessage());
                                    }
                                    String fulladdress = addresses.get(0).getAddressLine(0);
                                    profile_location.setText(fulladdress);

                                    final List<String> searchparams = new ArrayList<>();

                                    String[] splitted = fulladdress.trim().split(", ");
                                    String[] postalsplit = new String[3];


                                    for (String s : splitted) {

                                        if (s.contains(addresses.get(0).getPostalCode())) {

                                            int index = s.indexOf(addresses.get(0).getPostalCode());
                                            int length = s.length();
                                            searchparams.add(s.substring(0 , index-1).toLowerCase());
                                            searchparams.add(s.substring(index , length-1));
                                            continue;
                                        }

                                        searchparams.add(s.toLowerCase());

                                    }

                                    if (addresses.get(0).getLocality() != null) {

                                        searchparams.add(addresses.get(0).getLocality().toLowerCase());

                                    }
                                    if (addresses.get(0).getSubLocality() != null) {

                                        searchparams.add(addresses.get(0).getSubLocality().toLowerCase());

                                    }

                                    if (addresses.get(0).getAdminArea() != null) {

                                        searchparams.add(addresses.get(0).getAdminArea().toLowerCase());

                                    }
                                    if (addresses.get(0).getSubAdminArea() != null) {

                                        searchparams.add(addresses.get(0).getSubAdminArea().toLowerCase());

                                    }
                                    if (addresses.get(0).getPostalCode() != null) {

                                        searchparams.add(addresses.get(0).getPostalCode().toLowerCase());

                                    }
                                    if (addresses.get(0).getCountryName() != null) {

                                        searchparams.add(addresses.get(0).getCountryName().toLowerCase());

                                    }


                                    Map map = new HashMap();
                                    map.put("fulladdress" , fulladdress);
                                    map.put("searchparams" , searchparams);

                                    FirebaseFirestore.getInstance()
                                            .collection("Doctors")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(map , SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {



                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });





                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            }
        });
        return view;
    }

    public void dispatchCameraIntent () {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {

            photofile = createImageFile();

        }

        if (photofile != null) {

            Uri photouri = FileProvider.getUriForFile(getContext() , "com.example.doctorsj.fileprovider" , photofile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT , photouri);
            startActivityForResult(intent , CAMERA_CODE);
        }
    }

    public File createImageFile () {

        File file = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        save_profile_image.setVisibility(View.VISIBLE);

        if (resultCode == RESULT_OK) {

            switch(requestCode) {

                case CAMERA_CODE:

                    Glide.with(getContext())
                            .load(currentpicturepath)
                            .placeholder(R.drawable.ic_user)
                            .into(profile_screen_image);

                    selected_image_uri = Uri.fromFile(new File(currentpicturepath));


                    break;

                case GALLERY_CODE:

                    selected_image_uri = data.getData();
                    Glide.with(getContext())
                            .load(selected_image_uri)
                            .placeholder(R.drawable.ic_user)
                            .into(profile_screen_image);


                    break;


            }
        }
    }

    public void saveProfileImage(Uri imguri) {

        progressDialog.setMessage("Please Wait. This may take some time.");
        progressDialog.show();

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(imguri);
            String date = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());

            String filename = date + ".jpg";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePic/" + filename);
            UploadTask uploadTask = storageReference.putStream(inputStream);
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

                        Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();

                        Uri dowloadUri = task.getResult();
                        progressDialog.cancel();
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("profilepic" , dowloadUri.toString());
                        editor.apply();

                        FirebaseFirestore.getInstance()
                                .collection("Doctors")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .update("profilepic" , dowloadUri.toString());

                        Log.d(TAG, "onComplete: " + dowloadUri.toString());

                    }

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}