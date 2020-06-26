package com.example.location;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogreg extends AppCompatActivity {
    EditText busNo;
    DatabaseReference reff;
    ProgressDialog progressDialog;
    private int count=0;
    private long mTime=0;
    private String parent="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logreg);
        busNo = (EditText) findViewById(R.id.busnum);
    }
    public void checkBus(View view) {
        progressDialog=new ProgressDialog(UserLogreg.this);
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
        final String busnumber = busNo.getText().toString().trim();
        reff = FirebaseDatabase.getInstance().getReference().child("User");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("bus-" + busnumber)){
                        Toast.makeText(UserLogreg.this, "Tracking Successful!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent transfer = new Intent(UserLogreg.this, RetrieveMapActivity.class);
                        transfer.putExtra("busnumber", busnumber);
                        startActivity(transfer);
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(UserLogreg.this,"Please Enter Valid Bus Number",Toast.LENGTH_SHORT).show();
                }
            }

             @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

/*
        if(reff.child(busnumber).equals(null))
        {
            Toast.makeText(UserLogreg.this,"True"+busnumber,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(UserLogreg.this, "False", Toast.LENGTH_SHORT).show();
        }

        /*Intent transfer = new Intent(UserLogreg.this, RetrieveMapActivity.class);
        transfer.putExtra("busnumber", busnumber);
        startActivity(transfer);

           /* if(busnumber.equals("")) {
                Toast.makeText(UserLogreg.this,"Please Enter Bus number",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(dataSnapshot .hasChild(busnumber))
                {
                    Intent transfer = new Intent(UserLogreg.this, RetrieveMapActivity.class);
                    transfer.putExtra("busnumber", busnumber);
                    startActivity(transfer);
                }
                else
                {
                    Toast.makeText(UserLogreg.this,"Enter Valid bus number!",Toast.LENGTH_SHORT).show();
                }
                /*if(dataSnapshot.exists())
                {

                    count = (int) dataSnapshot.getChildrenCount();
                    parent=dataSnapshot.getKey();
                    Toast.makeText(UserLogreg.this, "parent:"+parent, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(UserLogreg.this, "Error", Toast.LENGTH_SHORT).show();
                }*/


            }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}

