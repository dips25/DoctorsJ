package com.example.doctorsj.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doctorsj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    EditText create_user_email , create_user_password , create_user_confirm_password;
    Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        create_user_email = (EditText) findViewById(R.id.create_user_email);
        create_user_password = (EditText) findViewById(R.id.create_user_password);
        create_user_confirm_password = (EditText) findViewById(R.id.create_user_confirm_password);
        sign_up = (Button) findViewById(R.id.sign_up);



        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = create_user_email.getText().toString();
                String password = create_user_password.getText().toString();
                String cnf_password = create_user_confirm_password.getText().toString();



                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(SignUpActivity.this, "Enter Email.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(password)) {

                    Toast.makeText(SignUpActivity.this, "Enter Password.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(cnf_password)) {

                    Toast.makeText(SignUpActivity.this, "Confirm Password.", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {

                    Toast.makeText(SignUpActivity.this, "Enter Email and Password.", Toast.LENGTH_SHORT).show();



                } else if (!password.equals(cnf_password)){

                    Toast.makeText(SignUpActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();


                } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(cnf_password)) {

                    Toast.makeText(SignUpActivity.this, "Enter Credentials.", Toast.LENGTH_SHORT).show();

                } else {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Intent intent = new Intent(SignUpActivity.this , LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                                Toast.makeText(SignUpActivity.this, getString(R.string.verification_link_dialogue), Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(SignUpActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    } else {

                                        Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });


    }
}