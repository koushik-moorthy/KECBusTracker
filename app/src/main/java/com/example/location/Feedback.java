package com.example.location;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Feedback extends AppCompatActivity {
    EditText name, mail, txtContent;
    String name1, mail1, txtContent1;
    private long mTime=0;
    ProgressDialog progressDialog;
    DatabaseReference reff;
    long maxid=0;
    MemberFeedback member;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        name = findViewById(R.id.userName);
        mail = findViewById(R.id.email);
        txtContent = findViewById(R.id.feedback);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.nameerror);
        member=new MemberFeedback();
        reff=FirebaseDatabase.getInstance().getReference().child("Member Feedback");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    maxid = dataSnapshot.getChildrenCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendDetails(View view) {
        progressDialog = new ProgressDialog(Feedback.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        if (SystemClock.elapsedRealtime() - mTime < 1000) {
            return;
        }
        mTime = SystemClock.elapsedRealtime();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null == activeNetwork) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            name1 = name.getText().toString().trim();
            mail1 = mail.getText().toString().trim();
            txtContent1 = txtContent.getText().toString().trim();
            if (name1.equals("") || mail1.equals("") || txtContent1.equals("")) {
                Toast.makeText(Feedback.this, "Contents Empty!", Toast.LENGTH_SHORT).show();
            } else {
                if (awesomeValidation.validate()) {
                    member.setName1(name1);
                    member.setEmail(mail1);
                    member.setTxt(txtContent1);
                    reff.child(String.valueOf(maxid + 1)).setValue(member);
                    Toast.makeText(Feedback.this, "Thank You for Your Valuable Feedback!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    Toast.makeText(Feedback.this, "Error In the form Fields!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
