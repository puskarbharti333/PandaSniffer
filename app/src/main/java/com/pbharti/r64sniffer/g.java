package com.pbharti.r64sniffer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.Log;
import com.pbharti.r64sniffer.window.AppConnectionsWindow;

import java.util.Objects;

import static com.pbharti.r64sniffer.MainActivity.AppContext;
import static com.pbharti.r64sniffer.MainActivity.mDatabase;

public class g extends Activity {
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Start();
    }
    public void Start(){
            progressBar.setVisibility(View.VISIBLE);
            mDatabase.child("DeviceVerification").child(Objects.requireNonNull(user.getEmail()).trim().replace(".", "")).get()
                    .addOnSuccessListener(
                            new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    String data = String.valueOf(dataSnapshot.getValue());
                                    if (data.equals(AppContext)) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                    } else {
                                        fAuth.signOut();
                                        Toast.makeText(g.this,
                                                "Sign in Failed\n" +
                                                        " Contact Vendor to Resolve This Issue.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        if (TrafficMgr.getInstance().isEnable()) {
                                            TrafficMgr.getInstance().stop();
                                        }
                                    }
                                    finish();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                if (count < 3){
                                    Toast.makeText(g.this,"Connecting...", Toast.LENGTH_SHORT).show();
                                    count++;
                                    Start();
                                }
                                else{
                                    Toast.makeText(g.this,
                                            "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                        }
                    });
    }
}