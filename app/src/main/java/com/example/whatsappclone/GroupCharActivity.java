package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupCharActivity extends AppCompatActivity {
    private ImageButton sendbutton;
    private EditText usermessageInput;
    private ScrollView scrollView;
    private TextView textviewformessages;
    String groupname,UserUid, CurrentUsername,currenttime,currentdate,usermessage,groupmessagekey;
    DatabaseReference UsersRef,GroupRef,groupmessageref;
    FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_char);
        Intent intent = getIntent();
        groupname = intent.getStringExtra("groupName");
        setTitle(groupname);
        Initializefields();
        getnameofUser();
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addmessagetodatabase();
                usermessageInput.setText("");
                scrollView.fullScroll(View.FOCUS_DOWN);

            }
        });
    }

    private void addmessagetodatabase() {
        if (TextUtils.isEmpty(usermessageInput.getText().toString().trim())){
            usermessageInput.setError("Please Enter a Message!");
        }
        else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatforday = new SimpleDateFormat("MMM dd, yyyy");
            currentdate = formatforday.format(calendar.getTime());
            Calendar calendarfortime = Calendar.getInstance();
            SimpleDateFormat formatfortime = new SimpleDateFormat("hh:mm a");
            currenttime = formatfortime.format(calendarfortime.getTime());
            usermessage = usermessageInput.getText().toString();
            groupmessagekey= GroupRef.push().getKey();
            groupmessageref = GroupRef.child(groupmessagekey);
            HashMap<String,Object> messageinfo = new HashMap<>();
            messageinfo.put("name",CurrentUsername);
            messageinfo.put("message",usermessageInput.getText().toString());
            messageinfo.put("time",currenttime);
            messageinfo.put("date",currentdate);
            groupmessageref.updateChildren(messageinfo);

        }
    }

    private void getnameofUser() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CurrentUsername = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Initializefields() {
        myAuth = FirebaseAuth.getInstance();
        UserUid = myAuth.getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UserUid);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("GroupName").child(groupname);
        sendbutton = findViewById(R.id.groupchatsendbutton);
        usermessageInput = findViewById(R.id.groupchatmessage);
        scrollView = findViewById(R.id.scrollviewforgroupchat);
        textviewformessages = findViewById(R.id.textviewformessages);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    getmessageandshowtouser(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                    getmessageandshowtouser(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getmessageandshowtouser(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String date =(String) ((DataSnapshot)iterator.next()).getValue();
            String message =(String) ((DataSnapshot)iterator.next()).getValue();
            String name =(String) ((DataSnapshot)iterator.next()).getValue();
            String time =(String) ((DataSnapshot)iterator.next()).getValue();

            textviewformessages.append(name+":\n"+message+"\n"+date+"    " + time+"\n\n\n");
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
    }
}