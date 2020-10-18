package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Registerwithphone extends AppCompatActivity implements View.OnClickListener {

    EditText phonenumber,varificationcode;
    Button verificationcodesendbutton,verifyphonenumber;
    TextView registerwithemail,changenumber;
    private FirebaseAuth mAuth;
    String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;


    public void sendvarificationcode(View view){
        if (TextUtils.isEmpty(phonenumber.getText().toString().trim())){
            phonenumber.setError("Noob tere Baap ne Empty No. launch karwaya tha kya!!");
        }
        else {

            String phoneNumber = phonenumber.getText().toString();
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    Registerwithphone.this,               // Activity (for callback binding)
                    callbacks);        // OnVerificationStateChangedCallbacks

        }
    }
    public void verifyphonenumber(View view){
        if (TextUtils.isEmpty(varificationcode.getText().toString().trim())){
            varificationcode.setError("Noob Code to enter kar!");
        }
        else {
            phonenumber.setVisibility(View.INVISIBLE);
            registerwithemail.setVisibility(View.INVISIBLE);
            verificationcodesendbutton.setVisibility(View.INVISIBLE);
            varificationcode.setVisibility(View.VISIBLE);
            verifyphonenumber.setVisibility(View.VISIBLE);
            changenumber.setVisibility(View.VISIBLE);

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, varificationcode.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerwithphone);
        initializefields();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Registerwithphone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                phonenumber.setVisibility(View.VISIBLE);
                registerwithemail.setVisibility(View.VISIBLE);
                verificationcodesendbutton.setVisibility(View.VISIBLE);
                varificationcode.setVisibility(View.INVISIBLE);
                verifyphonenumber.setVisibility(View.INVISIBLE);
                changenumber.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                phonenumber.setVisibility(View.INVISIBLE);
                registerwithemail.setVisibility(View.INVISIBLE);
                verificationcodesendbutton.setVisibility(View.INVISIBLE);
                varificationcode.setVisibility(View.VISIBLE);
                verifyphonenumber.setVisibility(View.VISIBLE);
                changenumber.setVisibility(View.VISIBLE);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void initializefields() {
        mAuth = FirebaseAuth.getInstance();
        varificationcode = findViewById(R.id.varificationcode);
        phonenumber = findViewById(R.id.phonenumber);
        verificationcodesendbutton = findViewById(R.id.verificationcodesendbutton);
        verifyphonenumber = findViewById(R.id.verifyphonenumber);
        registerwithemail = findViewById(R.id.registerwithemail);
        registerwithemail.setOnClickListener(this);
        changenumber = findViewById(R.id.changenumber);
        changenumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerwithemail){
            Intent movetomainactivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(movetomainactivity);
            finish();
        }
        if (view.getId() == R.id.changenumber){
            phonenumber.setVisibility(View.VISIBLE);
            registerwithemail.setVisibility(View.VISIBLE);
            verificationcodesendbutton.setVisibility(View.VISIBLE);
            varificationcode.setVisibility(View.INVISIBLE);
            verifyphonenumber.setVisibility(View.INVISIBLE);
            changenumber.setVisibility(View.INVISIBLE);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendusertomainactivity();

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            Toast.makeText(Registerwithphone.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendusertomainactivity() {
        Intent movetomainactivity = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(movetomainactivity);
        finish();
    }

}