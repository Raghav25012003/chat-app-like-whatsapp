package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindfriendActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference Reftodatabase ,ReftoRequests;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        setTitle("Find Friends");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        Reftodatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ReftoRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options  = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(Reftodatabase,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,FindfriendsViewholder> adapter = new FirebaseRecyclerAdapter<Contacts, FindfriendsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindfriendsViewholder holder, final int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.UserStatus.setText(model.getDescription());
                Picasso.get().load(model.getProfilePhoto()).placeholder(R.drawable.profilephoto).into(holder.userPhoto);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String selected_User_Uid = getRef(position).getKey();
                        Intent movetoprofileofuser = new Intent(getApplicationContext(),selectedUserProfile.class);
                        movetoprofileofuser.putExtra("selected_user_uid",selected_User_Uid);
                        startActivity(movetoprofileofuser);
                    }
                });
            }

            @NonNull
            @Override
            public FindfriendsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdesignlayout,parent,false);
                FindfriendsViewholder findfriendsViewholder = new FindfriendsViewholder(view);
                return findfriendsViewholder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FindfriendsViewholder extends RecyclerView.ViewHolder{
        TextView userName,UserStatus;
        CircleImageView userPhoto;
        ImageView online_design;
        public FindfriendsViewholder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_namedesign);
            UserStatus = itemView.findViewById(R.id.user_statusdesign);
            userPhoto = itemView.findViewById(R.id.profile_photoindesign);
            online_design = itemView.findViewById(R.id.user_onlinedesign);
        }
    }
}