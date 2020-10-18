package com.example.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link requestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class requestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerviewforrequest;
    private DatabaseReference ReftoRequest,ReftoUsers;
    private FirebaseAuth mauth;
    private String current_user_uid;
    View requestView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public requestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment requestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static requestsFragment newInstance(String param1, String param2) {
        requestsFragment fragment = new requestsFragment();
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
        // Inflate the layout for this fragment
        requestView = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerviewforrequest = requestView.findViewById(R.id.recyclerviewforrequest);
        recyclerviewforrequest.setLayoutManager(new LinearLayoutManager(getContext()));
        mauth = FirebaseAuth.getInstance();
        current_user_uid = mauth.getCurrentUser().getUid();
        ReftoUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        ReftoRequest = FirebaseDatabase.getInstance().getReference().child("Requests");
        return requestView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ReftoRequest,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,ViewholderofRequestClass> adapter = new FirebaseRecyclerAdapter<Contacts, ViewholderofRequestClass>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewholderofRequestClass holder, int position, @NonNull Contacts model) {
                holder.userName.setText("raghav");
                holder.userStatus.setText("i am pro");

            }

            @NonNull
            @Override
            public ViewholderofRequestClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestsdesignlayout,parent,false);
                ViewholderofRequestClass returnit = new ViewholderofRequestClass(view);
                return returnit;
            }
        };
        recyclerviewforrequest.setAdapter(adapter);
        adapter.startListening();
    }
    private class ViewholderofRequestClass extends RecyclerView.ViewHolder{
        TextView userName , userStatus ;
        CircleImageView userPhoto ;
        Button acceptButton , rejectButton ;
        public ViewholderofRequestClass(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_namerequestdesign);
            userStatus = itemView.findViewById(R.id.user_statusrequestdesign);
            userPhoto = itemView.findViewById(R.id.profile_photorequestdesign);
        }
    }
}