package com.example.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link usersfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class usersfragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View contactsview;
    RecyclerView recyclerViewforcontacts;
    DatabaseReference ReftoContacts,ReftoUsers;
    private String current_user_uid;
    private FirebaseAuth mauth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public usersfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment usersfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static usersfragment newInstance(String param1, String param2) {
        usersfragment fragment = new usersfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactsview =inflater.inflate(R.layout.fragment_usersfragment, container, false);
        // Inflate the layout for this fragment
        recyclerViewforcontacts = contactsview.findViewById(R.id.recyclerViewofcontacts);
        recyclerViewforcontacts.setLayoutManager(new LinearLayoutManager(getContext()));
        mauth = FirebaseAuth.getInstance();
        current_user_uid = mauth.getCurrentUser().getUid();
        ReftoContacts = FirebaseDatabase.getInstance().getReference().child("Contacts").child(current_user_uid);
        ReftoUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        return contactsview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ReftoContacts,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,FirebaseViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FirebaseViewHolder holder, int position, @NonNull Contacts model) {
                String friend = getRef(position).getKey();
                ReftoUsers.child(friend).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String name = snapshot.child("name").getValue().toString();
                            String description = snapshot.child("description").getValue().toString();
                            String photo = snapshot.child("profilePhoto").getValue().toString();
                            holder.userName.setText(name);
                            holder.UserStatus.setText(description);
                            Picasso.get().load(photo).placeholder(R.drawable.profilephoto).into(holder.userPhoto);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersdesignlayout,parent,false);
                FirebaseViewHolder firebaseViewHolder = new FirebaseViewHolder(view);
                return firebaseViewHolder;
            }
        };
        recyclerViewforcontacts.setAdapter(adapter);
        adapter.startListening();
    }
    private class FirebaseViewHolder extends RecyclerView.ViewHolder{
        TextView userName,UserStatus;
        CircleImageView userPhoto;
        ImageView online_design;
        public FirebaseViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_namedesign);
            UserStatus = itemView.findViewById(R.id.user_statusdesign);
            userPhoto = itemView.findViewById(R.id.profile_photoindesign);
            online_design = itemView.findViewById(R.id.user_onlinedesign);
        }
    }
}