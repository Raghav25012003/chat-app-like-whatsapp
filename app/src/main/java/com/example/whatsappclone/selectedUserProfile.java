package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class selectedUserProfile extends AppCompatActivity {
    TextView selected_user_name ,selected_user_status;
    ImageView selected_user_photo;
    Button requestOrCancel_Button,cancel_request_button;
    String selected_user_uid,current_user_uid,relationbetweenUsers;
    FirebaseAuth mauth;
    DatabaseReference RefOFselectedUser,Reftorequests,ReftoContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user_profile);
        Intent intent = getIntent();
        selected_user_uid = intent.getStringExtra("selected_user_uid");
        selected_user_name = findViewById(R.id.selected_user_name);
        selected_user_photo = findViewById(R.id.selected_user_photo);
        selected_user_status = findViewById(R.id.selected_user_status);
        requestOrCancel_Button = findViewById(R.id.requestOrCancel_Button);
        cancel_request_button = findViewById(R.id.cancel_request_button);
        mauth = FirebaseAuth.getInstance();
        current_user_uid = mauth.getCurrentUser().getUid();
        RefOFselectedUser = FirebaseDatabase.getInstance().getReference().child("Users").child(selected_user_uid);
        Reftorequests = FirebaseDatabase.getInstance().getReference().child("Requests");
        ReftoContacts = FirebaseDatabase.getInstance().getReference().child("Contacts");
        relationbetweenUsers = "new";
        getSelectedUserInfo();

    }
    public void cancelRequest(View view){
        deleteRequest();
    }

    private void deleteRequest() {
        Reftorequests.child(current_user_uid).child(selected_user_uid).child("request_state").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Reftorequests.child(selected_user_uid).child(current_user_uid).child("request_state").setValue("cancel").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        requestOrCancel_Button.setEnabled(true);
                        requestOrCancel_Button.setText("Send friend request");
                        relationbetweenUsers="new";
                    }
                });
            }
        });
        ReftoContacts.child(current_user_uid).child(selected_user_uid).child("relation").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ReftoContacts.child(selected_user_uid).child(current_user_uid).child("relation").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
        cancel_request_button.setVisibility(View.INVISIBLE);
    }

    private void getSelectedUserInfo() {
        RefOFselectedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    selected_user_name.setText(snapshot.child("name").getValue().toString());
                    selected_user_status.setText(snapshot.child("description").getValue().toString());
                    if (snapshot.child("profilePhoto").exists()){
                        Picasso.get().load(snapshot.child("profilePhoto").getValue().toString()).placeholder(R.drawable.profilephoto).into(selected_user_photo);
                    }
                    checkforrelationstatus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkforrelationstatus() {

        if (current_user_uid.equals(selected_user_uid)){
            requestOrCancel_Button.setVisibility(View.INVISIBLE);
        }
        else {
            Reftorequests.child(current_user_uid).child(selected_user_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.hasChild("request_state")){
                        String request_state = snapshot.child("request_state").getValue().toString();
                        if (request_state.equals("sent")){
                            requestOrCancel_Button.setText("Cancel request");
                            relationbetweenUsers="sent";
                        }
                        if (request_state.equals("received")){
                            requestOrCancel_Button.setText("Accept Request");
                            relationbetweenUsers="received";
                            cancel_request_button.setVisibility(View.VISIBLE);
                        }
                        if (request_state.equals("Friends")){
                            requestOrCancel_Button.setText("Remove from Friend");
                            relationbetweenUsers="Friends";
                        }
                        if (request_state.equals("cancel")){
                            requestOrCancel_Button.setText("Send friend request");
                            relationbetweenUsers="new";
                            cancel_request_button.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            requestOrCancel_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestOrCancel_Button.setEnabled(false);
                        sendRequest();

                }
            });
        }
    }

    private void sendRequest() {
        if (relationbetweenUsers=="new"){
            Reftorequests.child(current_user_uid).child(selected_user_uid).child("request_state").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Reftorequests.child(selected_user_uid).child(current_user_uid).child("request_state").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(selectedUserProfile.this, "Request Send!", Toast.LENGTH_SHORT).show();
                            requestOrCancel_Button.setEnabled(true);
                            requestOrCancel_Button.setText("Cancel request");
                            relationbetweenUsers="sent";
                        }
                    });
                }
            });
        }
        if (relationbetweenUsers=="sent"){
               deleteRequest();
        }
        if (relationbetweenUsers=="received"){
            ReftoContacts.child(current_user_uid).child(selected_user_uid).child("relation").setValue("Friends").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ReftoContacts.child(selected_user_uid).child(current_user_uid).child("relation").setValue("Friends").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                            }
                        }
                    });
                }
            });
            Reftorequests.child(current_user_uid).child(selected_user_uid).child("request_state").setValue("Friends").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Reftorequests.child(selected_user_uid).child(current_user_uid).child("request_state").setValue("Friends").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(selectedUserProfile.this, "Added as Friend", Toast.LENGTH_SHORT).show();
                                requestOrCancel_Button.setText("Remove from friend");
                                cancel_request_button.setVisibility(View.INVISIBLE);
                                relationbetweenUsers ="Friends";
                                requestOrCancel_Button.setEnabled(true);
                            }
                        }
                    });
                }
            });
        }
        if (relationbetweenUsers=="Friends"){
            deleteRequest();
            cancel_request_button.setVisibility(View.INVISIBLE);
        }
    }
}