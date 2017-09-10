package com.deucate.kartik.cityrideclient;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId = "", code = "";
    private static int ACTIVITY_STATE = 0;
    private FirebaseAuth mAuth;

    Button mOkButton;
    EditText mFirstNameEt, mLastNameEt, mPhoneNumberEt;

    LinearLayout mNameLayout,  mPhoneNumberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mReference = mDatabase.getReference().child("UserData").child(uid);

        mOkButton = (Button) findViewById(R.id.registerOkBtn);
        mFirstNameEt = (EditText) findViewById(R.id.registerFirstName);
        mLastNameEt = (EditText) findViewById(R.id.registerLastName);
        mPhoneNumberEt = (EditText) findViewById(R.id.registerPhoneNumber);

        mNameLayout = (LinearLayout) findViewById(R.id.nameLinearLayout);
        mPhoneNumberLayout = (LinearLayout) findViewById(R.id.phoneNumberLinearLayout);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ACTIVITY_STATE == 4) {
                    String otp = mPhoneNumberEt.getText().toString();
                    cheackOTP(mVerificationId, otp);
                    return;
                }

                String phoneNumber = mPhoneNumberEt.getText().toString();
                //   startPhoneNumberVerification(phoneNumber);

                String fName = mFirstNameEt.getText().toString();
                String lName = mLastNameEt.getText().toString();
                String fullName = fName + " " + lName;
                pushDataOnDatabase(fullName,  phoneNumber);


            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(RegisterActivity.this, "Complate All", Toast.LENGTH_SHORT).show();
                parshIntent();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberEt.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(RegisterActivity.this, "Quota exceeded", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mPhoneNumberEt.setHint("OTP");
                mOkButton.setText("Verify");
                ACTIVITY_STATE = 4;
                mVerificationId = s;
                mPhoneNumberEt.setText("");

            }
        };

    }

    private void cheackOTP(String verificationId, String otp) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.getCurrentUser().updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    parshIntent();
                }
            }
        });

    }

    private void parshIntent() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    private void pushDataOnDatabase(String fullName,  String phoneNumber) {

        mReference.child("FullName").setValue(fullName);
        mReference.child("PhoneNumber").setValue(phoneNumber);

        parshIntent();

        mNameLayout.setVisibility(View.INVISIBLE);
        mPhoneNumberLayout.setVisibility(View.INVISIBLE);

        ACTIVITY_STATE = 4;

    }
}

