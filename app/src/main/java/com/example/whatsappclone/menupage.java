package com.example.whatsappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;

public class menupage extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_resource,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.logout){
            mAuth.signOut();
            Toast.makeText(this, "Logout complete!", Toast.LENGTH_SHORT).show();
            Intent mainintent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainintent);
            finish();
        }
        if (item.getItemId()==R.id.setting){
            movetosettingactivity();
        }
        if (item.getItemId()==R.id.creategroup){
            AlertDialog.Builder groupnamealertbox = new AlertDialog.Builder(this);
            final EditText groupname = new EditText(this);
            groupname.setHint("Enter Group name");
            groupnamealertbox.setTitle("Enter group name")
                    .setView(groupname)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RoofRef.child("GroupName").child(groupname.getText().toString()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                Toast.makeText(menupage.this, "Group Created!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(menupage.this, "Error! , Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).show();
        }
        if (item.getItemId()==R.id.findfriends){
            Intent movetofindfriendactivity = new Intent(getApplicationContext(),FindfriendActivity.class);
            startActivity(movetofindfriendactivity);
        }
        return true;
    }


    private void movetosettingactivity() {
        Intent setting_Intent = new Intent(getApplicationContext(),setting_Activity.class);
        startActivity(setting_Intent);
    }

    FirebaseAuth mAuth;
    DatabaseReference RoofRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menupage);
        mAuth = FirebaseAuth.getInstance();
        RoofRef = FirebaseDatabase.getInstance().getReference();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Raghav's Chat App");
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        AppBarLayout appBarLayout = findViewById(R.id.appbarlayout);

        if (mAuth.getCurrentUser()==null){
            Intent movetomainactivity = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(movetomainactivity);
            finish();
        }else {
            checkfornewuser();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Work in progress. #NoobRaghav", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void checkfornewuser() {
        RoofRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("name").exists()){
                    Intent movetosettingactivity = new Intent(getApplicationContext(),setting_Activity.class);
                    startActivity(movetosettingactivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkfornewuser();
    }
}