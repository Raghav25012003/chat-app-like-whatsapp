package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email,password;
    Button gobutton;
    Boolean check=true;
    TextView change,registerwithphone;
    private DatabaseReference Refdata;
    private FirebaseAuth mAuth;

    public void movetomenupage(){
        Intent intent = new Intent(getApplicationContext(),menupage.class);
        startActivity(intent);
        finish();
    }
    public void gobutton(View view){
        if (TextUtils.isEmpty(password.getText().toString().trim())){
            password.setError("Please Enter a password!");
        }else if (TextUtils.isEmpty(email.getText().toString().trim())){
            email.setError("Please enter a name");
        }else{
            if (check){
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String UserID =mAuth.getCurrentUser().getUid();
                                    Refdata.child("Users").child(UserID).setValue("");
                                    // Sign in success, update UI with the signed-in user's information
                                    movetomenupage();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    movetomenupage();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(MainActivity.this, "Noob strong password rakh!", Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(MainActivity.this, "Wrong Credentials. Check it again", Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(MainActivity.this, "This email ID is already in use ", Toast.LENGTH_SHORT).show();
                                    } catch(Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                // ...
                            }
                        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email= findViewById(R.id.email);
        password = findViewById(R.id.password);
        gobutton = findViewById(R.id.gobutton);
        registerwithphone = findViewById(R.id.registerwithphone);
        change = findViewById(R.id.change);
        change.setOnClickListener(this);
        registerwithphone.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        Refdata = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            movetomenupage();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.change){
            if (check){
                gobutton.setText("Login");
                change.setText("Create a new account");
                check=false;
            }
            else {
                gobutton.setText("Signup");
                change.setText("Already Registered?");
                check=true;
            }
        }
        if (view.getId() == R.id.registerwithphone){
            Intent movetoregisterwithphonepage = new Intent(getApplicationContext(),Registerwithphone.class);
            startActivity(movetoregisterwithphonepage);
            finish();
        }

    }
}