package com.kabarakuniversityforumApp.ProfileIssues;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kabarakuniversityforumApp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView mProfileImage;
    private FloatingActionButton mFloatEdit;
    private TextView mUsername,mStatus,mPhone;
    private String user_id,imageUrl;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private StorageReference storageReference;
    private Toolbar toolbar;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = (Toolbar)findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        mUsername = (TextView)findViewById(R.id.tv_username_profile);
        mStatus = (TextView)findViewById(R.id.tv_status_profile);
        mPhone = (TextView)findViewById(R.id.tv_phone_profile);
        mFloatEdit = (FloatingActionButton)findViewById(R.id.floatPicEdit);
        mBack = (ImageView)findViewById(R.id.image_back_profile);
        mProfileImage = (CircleImageView)findViewById(R.id.profile_image_profile);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateDetails();

        //looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // this will run in the main thread
                firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String profileName = task.getResult().getString("ProfileName");
                                String status = task.getResult().getString("status");
                                imageUrl = task.getResult().getString("imageUrl");

                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();

                                Glide.with(Profile.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfileImage);
                                mUsername.setText(profileName);
                                mStatus.setText(status);
                                mPhone.setText(auth.getCurrentUser().getPhoneNumber());
                            }
                        }
                    }
                });
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this,ProfilePicture.class);
                i.putExtra("imageUrl",imageUrl);
                i.putExtra("activity","joining");
                startActivity(i);
            }
        });

        mFloatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(Profile.this);
            }
        });

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toChange = "ProfileName";
                String hint = "Enter New Username";
                String title = "New Username";
                String error = mUsername.getText().toString().trim();

                openDialog(title,toChange,hint,error);

            }
        });

        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toChange = "status";
                String hint = "Enter New Status";
                String title = "New Status";
                String error = mStatus.getText().toString().trim();

                openDialog(title,toChange,hint,error);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mProfileImage.setImageURI(imageUri);
                picUpdate(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void picUpdate(Uri imgUri)
    {
            progressDialog.setMessage("Updating...");
            progressDialog.show();
            String randomName = UUID.randomUUID().toString();
            final StorageReference reference = storageReference.child("ProfileImages").child(randomName+".jpg");
            reference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //uploading all the details to database now
                            uploading(uri.toString());


                        }
                    });
                }
            });

    }

    private void uploading(String uri){
        Map<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("imageUrl",uri);

        firebaseFirestore.collection("Users").document(user_id).update(stringStringHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(Profile.this, "Profile updated", Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }



    private void openDialog(String title, final String toChnge, String hint, final String initialStringItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle(title);
        final EditText edtToChange = new EditText(Profile.this);
        edtToChange.setHint(hint);
        edtToChange.setText(initialStringItem);
        builder.setView(edtToChange);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String changed = edtToChange.getText().toString().trim();
                if (changed!=null){
                    progressDialog.setMessage("Updating...");
                    progressDialog.show();
                    updating(toChnge, changed);
                }

            }
        });
        builder.show();


    }

    private void updating(String toChange,String changed){

        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put(toChange,changed);

        firebaseFirestore.collection("Users").document(user_id).update(objectMap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Profile.this, "Successfully updated.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }
        );

    }

    private void updateDetails(){
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String profileName = task.getResult().getString("ProfileName");
                        String status = task.getResult().getString("status");
                        imageUrl = task.getResult().getString("imageUrl");

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.centerCrop();

                        Glide.with(Profile.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfileImage);
                        mUsername.setText(profileName);
                        mStatus.setText(status);
                        mPhone.setText(auth.getCurrentUser().getEmail());
                    }
                }
            }
        });
    }
}
