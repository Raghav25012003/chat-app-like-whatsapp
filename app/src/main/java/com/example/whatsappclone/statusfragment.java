package com.example.whatsappclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link statusfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class statusfragment extends Fragment {

    ListView listofgroupname;
    private View groupfragmentview;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList= new ArrayList<>();
    private DatabaseReference RootRef;
    private FirebaseAuth MyAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public statusfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment statusfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static statusfragment newInstance(String param1, String param2) {
        statusfragment fragment = new statusfragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupfragmentview = inflater.inflate(R.layout.fragment_statusfragment, container, false);
        initializefields();
        updatelistview();
        // Inflate the layout for this fragment
        return groupfragmentview;

    }

    private void updatelistview() {
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializefields() {
        MyAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference().child("GroupName");
        listofgroupname = groupfragmentview.findViewById(R.id.listofgroupname);
        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        listofgroupname.setAdapter(arrayAdapter);
        listofgroupname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent movetogroupchatactivity = new Intent(getContext(),GroupCharActivity.class);
                movetogroupchatactivity.putExtra("groupName",arrayList.get(i));
                startActivity(movetogroupchatactivity);
            }
        });
    }
}