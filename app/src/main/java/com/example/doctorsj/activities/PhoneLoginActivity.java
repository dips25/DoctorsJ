package com.example.doctorsj.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctorsj.MyOTPBroadcastReceiver;
import com.example.doctorsj.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private static final String TAG = PhoneLoginActivity.class.getSimpleName();

    FirebaseAuth mAuth;
    EditText phone_number_login;
    Button verify;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    MyOTPBroadcastReceiver myOTPBroadcastReceiver;

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        phone_number_login = (EditText) findViewById(R.id.phone_number_edit_text);
        verify = (Button) findViewById(R.id.phone_number_verify);

        registerreceiver();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendOtp(phone_number_login.getText().toString());


            }
        });

    }

    public void sendOtp(String phonenumber) {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.d(TAG, "onVerificationFailed:"  + e.getMessage());

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);



                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        String number = "+91"+phonenumber;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(PhoneLoginActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(PhoneLoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            String id = user.getUid();

                            FirebaseFirestore.getInstance().collection("Doctors")
                                    .document(id)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            if (!documentSnapshot.exists()) {

                                                SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isPhoneLogin" , true);
                                                editor.commit();
                                                editor.apply();

                                                Intent intent = new Intent(PhoneLoginActivity.this, SetupActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);

                                            } else {

                                                SharedPreferences sharedPreferences = getSharedPreferences("profiledetails" , MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isPhoneLogin" , true);
                                                editor.putString("name" , documentSnapshot.getString("name"));
                                                editor.putString("contact" , documentSnapshot.getString("contact"));
                                                editor.putString("email" , documentSnapshot.getString("email"));
                                                editor.commit();
                                                editor.apply();


                                                Intent intent = new Intent(PhoneLoginActivity.this, MapsActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }

                                        }
                                    });

                        } else {

                            Toast.makeText(PhoneLoginActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    public void registerreceiver() {

        myOTPBroadcastReceiver = new MyOTPBroadcastReceiver();
        registerReceiver(myOTPBroadcastReceiver , new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        myOTPBroadcastReceiver.setotplistener(new MyOTPBroadcastReceiver.Getotplistener() {
            @Override
            public void getotp(String otp) {

                Toast.makeText(PhoneLoginActivity.this, "OTP Received", Toast.LENGTH_SHORT).show();

                Dialog dialog = new Dialog(PhoneLoginActivity.this);
                dialog.setContentView(R.layout.phone_otp_dialog);
                dialog.show();

                EditText et1 = (EditText) dialog.findViewById(R.id.et1);
                EditText et2 = (EditText) dialog.findViewById(R.id.et2);
                EditText et3 = (EditText) dialog.findViewById(R.id.et3);
                EditText et4 = (EditText) dialog.findViewById(R.id.et4);
                EditText et5 = (EditText) dialog.findViewById(R.id.et5);
                EditText et6 = (EditText) dialog.findViewById(R.id.et6);



                et1.setText(String.valueOf(Character.getNumericValue(otp.charAt(0))));
                et2.setText(String.valueOf(Character.getNumericValue(otp.charAt(1))));
                et3.setText(String.valueOf(Character.getNumericValue(otp.charAt(2))));
                et4.setText(String.valueOf(Character.getNumericValue(otp.charAt(3))));
                et5.setText(String.valueOf(Character.getNumericValue(otp.charAt(4))));
                et6.setText(String.valueOf(Character.getNumericValue(otp.charAt(5))));


            }

            @Override
            public void timeout() {

                Toast.makeText(PhoneLoginActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myOTPBroadcastReceiver);
    }
}