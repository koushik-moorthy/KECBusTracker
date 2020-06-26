package com.example.location;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverLogreg extends AppCompatActivity {
    EditText busNo,Password;
    DatabaseReference reff,reff1;
    private long mTime=0;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    public static final String prefrences="mypref";
    public static final String busnum="numbus";
    public static final String pass="wordpass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_logreg);
        busNo=(EditText)findViewById(R.id.busno);
        Password=(EditText)findViewById(R.id.pass);
        Button button=(Button)findViewById(R.id.button);
        sharedPreferences = getSharedPreferences(prefrences,Context.MODE_PRIVATE);
        if(sharedPreferences.contains(busnum) && sharedPreferences.contains(pass))
        {
            String busnumbe2 = sharedPreferences.getString(busnum,"");
            Intent transfer = new Intent(DriverLogreg.this, MapsActivity.class);
            transfer.putExtra("busnumber", busnumbe2);
            startActivity(transfer);
        }
    }

    public void checkDriver(View view) {
        progressDialog=new ProgressDialog(DriverLogreg.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        if(SystemClock.elapsedRealtime() -mTime < 1000)
        {
            return;
        }
        mTime =SystemClock.elapsedRealtime();
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        ConnectivityManager manager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=manager.getActiveNetworkInfo();
        if(null==activeNetwork){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else {
//            Toast.makeText(DriverLogreg.this, "...Loading...", Toast.LENGTH_SHORT).show();

            final String busnumber = busNo.getText().toString().trim();
            final String password = Password.getText().toString().trim();

            if (busnumber.equals("") || password.equals("")) {
                Toast.makeText(DriverLogreg.this, "Please Enter Valid Username or Password!", Toast.LENGTH_SHORT).show();
            } else {reff = FirebaseDatabase.getInstance().getReference().child("User");
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("bus-" + busnumber)) {
                            reff1 = FirebaseDatabase.getInstance().getReference().child("User").child("bus-" + busnumber);
                            reff1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String busnumber1 = dataSnapshot.child("no").getValue().toString().trim();
                                    String password1 = dataSnapshot.child("password").getValue().toString().trim();
                                    if (busnumber.equals(busnumber1)) {
                                        if (busnumber.equals(busnumber1) && password.equals(password1)) {
                                            SharedPreferences.Editor editor=sharedPreferences.edit();
                                            editor.putString(busnum,busnumber);
                                            editor.putString(pass,password);
                                            editor.commit();
                                            Toast.makeText(DriverLogreg.this, "Login Succesful!", Toast.LENGTH_SHORT).show();
                                            Intent transfer = new Intent(DriverLogreg.this, MapsActivity.class);
                                            transfer.putExtra("busnumber", busnumber1);
                                            startActivity(transfer);
                                        }
                                        else
                                            {
                                            Toast.makeText(DriverLogreg.this, "UserName or Password Incorrect!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            }
                                    } else {
                                        Toast.makeText(DriverLogreg.this, "Bus number not Valid!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            Toast.makeText(DriverLogreg.this, "Bus number not Valid!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }
}