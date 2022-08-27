package com.pbharti.r64sniffer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.installations.FirebaseInstallations;

public class MainActivity extends Activity {
    private static final String TAG = "r64sniffer";
    EditText mUsername, mPassword;
    Button mLoginBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    public static DatabaseReference mDatabase;
    public static String AppContext = null;
    public static String UID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            AppContext = task.getResult();
                        }
                    }
                });
        if (user == null) {
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String email = mUsername.getText().toString().trim();
                    final String password = mPassword.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        mUsername.setError("Email is Required.");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        mPassword.setError("Password is Required.");
                        return;
                    }
                    if (password.length() < 6) {
                        mPassword.setError("Password Must be at least 6 Characters");
                        return;
                    }
                    UID = mUsername.getText().toString().trim().replace(".", "");
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDatabase.child("DeviceVerification").child(UID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            String data = String.valueOf(task.getResult().getValue());
                                            Log.d(TAG,"DAY" + data);
                                            if (data.equals(TAG)) {
                                                mDatabase.child("DeviceVerification").child(UID).setValue(AppContext);
                                                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                                finish();
                                            } else {
                                                fAuth.signOut();
                                                Toast.makeText(MainActivity.this,
                                                        "Sign in Failed\n" +
                                                                "You are already Signed in Another Device.\n" +
                                                                "Contact Vendor to Resolve This Issue.", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                            //progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(MainActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        } else {
            startActivity(new Intent(getApplicationContext(), g.class));
            finish();
        }
    }
}