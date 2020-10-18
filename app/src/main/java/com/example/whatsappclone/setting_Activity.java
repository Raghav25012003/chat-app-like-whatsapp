package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URL;
import java.security.Permission;
import java.util.HashMap;

public class setting_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText name , description;
    Button update;
    DatabaseReference RootRef,Reftophotolink;
    FirebaseAuth mauth;
    ImageView profile_photo;
    String profilephotourl=null;
    StorageReference Reftophoto;
    public String username,descriptionofstatus;


    public void updateprofile(View view){
        username = name.getText().toString();
        descriptionofstatus = description.getText().toString();
        if (username.isEmpty()){
            name.setError("Please Enter a Name!");
        }else if (descriptionofstatus.isEmpty()){
            description.setError("Please enter a Status");
        }else{
            RootRef.child("Users").child(mauth.getCurrentUser().getUid()).child("name").setValue(username);
            RootRef.child("Users").child(mauth.getCurrentUser().getUid()).child("description").setValue(descriptionofstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(setting_Activity.this, "Profile is updated", Toast.LENGTH_SHORT).show();
                    Intent movetomenupage = new Intent(setting_Activity.this, menupage.class);
                    startActivity(movetomenupage);
                    finish();
                }
            });
            /*RootRef.child("Users").child(mauth.getCurrentUser().getUid()).setValue(profiledeatils)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()){
                                Toast.makeText(setting_Activity.this, "Profile is updated", Toast.LENGTH_SHORT).show();
                                Intent movetomenupage = new Intent(getApplicationContext(),menupage.class);
                                startActivity(movetomenupage);
                                finish();
                            }
                            else {
                                Toast.makeText(setting_Activity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
        setTitle("Raghav's Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        update = findViewById(R.id.updateprofilebutton);
        mauth = FirebaseAuth.getInstance();
        profile_photo = findViewById(R.id.profile_photo);
        profile_photo.setOnClickListener(this);
        Reftophoto = FirebaseStorage.getInstance().getReference().child("User Photo");
        RootRef = FirebaseDatabase.getInstance().getReference();
        Reftophotolink = RootRef.child("Users").child(mauth.getCurrentUser().getUid()).child("profilePhoto");
                checkfornewuser();
    }

    private void checkfornewuser() {
        RootRef.child("Users").child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("name").exists()){
                    name.setText(snapshot.child("name").getValue().toString());
                    description.setText(snapshot.child("description").getValue().toString());

                }
                if (snapshot.child("profilePhoto").exists()){
                    String urlofprofilephoto = snapshot.child("profilePhoto").getValue().toString();
                    Picasso.get().load(urlofprofilephoto).into(profile_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.profile_photo){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                movetogallery();
            }
            else {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            movetogallery();
        }
    }

    private void movetogallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();

                final StorageReference Reftothisuser = Reftophoto.child(mauth.getCurrentUser().getUid()+".jpg");
                Reftothisuser.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Reftothisuser.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String urlofimage = task.getResult().toString();
                                    profilephotourl = urlofimage;
                                    Reftophotolink.setValue(urlofimage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(setting_Activity.this, "Profile Photo Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}