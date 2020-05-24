package com.kabarakuniversityforumApp.AccountCreation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kabarakuniversityforumApp.General_Issues.ThePanel;
import com.kabarakuniversityforumApp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreation extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private String user_id;
    private CircleImageView mProfilePic;
    private Uri ImageUri;
    private Button  mProceed;
    private StorageReference storageReference;
    private TextInputEditText mProfileName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        mProceed = (Button)findViewById(R.id.btn_continue_profile);
        mProfilePic = (CircleImageView)findViewById(R.id.image_profile);
        mProfileName = (TextInputEditText)findViewById(R.id.edt_profile_name);


            //picking image
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ProfileCreation.this);

            }
        });

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileName = mProfileName.getText().toString().trim();
                if (TextUtils.isEmpty(profileName)){
                    mProfileName.setError("Enter profile name");
                }else
                {
                    accountCreation(user_id,profileName);
                }
            }
        });
    }

    private void accountCreation(final String userId, final String profileName)
    {
        if (ImageUri == null){
            Toast.makeText(this, "Pick image to proceed", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("Please wait uploading your profile details");
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            String randomName = UUID.randomUUID().toString();
            final StorageReference reference = storageReference.child("ProfileImages").child(randomName+".jpg");
            reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //uploading all the details to database now
                            uploading(profileName,uri.toString(),userId);


                        }
                    });
                }
            });
        }
    }

    private void uploading(String profileName,String uri,String userId){
        String devicetoken = FirebaseInstanceId.getInstance().getToken();

        Map<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("ProfileName",profileName);
        stringStringHashMap.put("imageUrl",uri);
        stringStringHashMap.put("status","Proud to be part of Kabarak Community");
        stringStringHashMap.put("facebook","not yet");
        stringStringHashMap.put("insta","not yet");
        stringStringHashMap.put("twitter","not yet");
        stringStringHashMap.put("devicetoken",devicetoken);
        stringStringHashMap.put("rights","not_admin");
        stringStringHashMap.put("rank","student");

        firebaseFirestore.collection("Users").document(userId).set(stringStringHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            //open the main panel
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileCreation.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user_id",user_id);
                            editor.putString("rights","not_admin");
                            editor.putString("rank","student");
                            editor.apply();
                            startActivity(new Intent(ProfileCreation.this, ThePanel.class));
                            finish();
                        }else {
                            Toast.makeText(ProfileCreation.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageUri  = result.getUri();
                mProfilePic.setImageURI(ImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void updatingSharePref(){
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()){
                                String rights = task.getResult().getString("rights");
                                String rank = task.getResult().getString("rank");
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileCreation.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("rights",rights);
                                editor.putString("rank",rank);
                                editor.apply();

                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = preferences.getString("user_id", null);

        if (userid!= null){
            startActivity(new Intent(ProfileCreation.this, ThePanel.class));
            finish();
            updatingSharePref();
        }

    }
}
