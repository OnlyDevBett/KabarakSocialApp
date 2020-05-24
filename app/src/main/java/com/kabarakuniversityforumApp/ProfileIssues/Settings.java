package com.kabarakuniversityforumApp.ProfileIssues;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabarakuniversityforumApp.AccountCreation.MainActivity;
import com.kabarakuniversityforumApp.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView mProfileImage;
    private Button mDeletion;
    private TextView mFaceBook,mTwitter,mInsta,mPersonalDetails,mUsername,mStatus,mAccountDeletion;
    private String user_id,user_idPassed;
    private ProgressDialog progressDialog;
    private LinearLayout mLinearLayout;
    private Toolbar toolbar;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        mProfileImage = (CircleImageView)findViewById(R.id.profile_image_settings);
        mLinearLayout = (LinearLayout)findViewById(R.id.linearProfile);
        mDeletion = (Button)findViewById(R.id.btn_deletion);
        mFaceBook = (TextView)findViewById(R.id.tv_facebook_details);
        mTwitter = (TextView)findViewById(R.id.tv_twitter_details);
        mInsta = (TextView)findViewById(R.id.tv_instagram_details);
        mPersonalDetails = (TextView)findViewById(R.id.tv_personal_details);
        mUsername =(TextView)findViewById(R.id.tv_username_settings);
        mAccountDeletion = (TextView)findViewById(R.id.tv_accountDeletion);
        mBack = (ImageView)findViewById(R.id.image_back_settings);
        mStatus  = (TextView)findViewById(R.id.tv_status);

        user_idPassed = getIntent().getExtras().getString("user_idpassed");

        if (user_idPassed.equals("no_nothing")){
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            String profileName = task.getResult().getString("ProfileName");
                            String status = task.getResult().getString("status");
                            String imageUrl = task.getResult().getString("imageUrl");
                            String insta = task.getResult().getString("insta");
                            String facebook = task.getResult().getString("facebook");
                            String twitter = task.getResult().getString("twitter");

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop();

                            Glide.with(Settings.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfileImage);
                            mUsername.setText(profileName);
                            mStatus.setText(status);
                            mFaceBook.setText(facebook);
                            mTwitter.setText(twitter);
                            mInsta.setText(insta);
                        }
                    }
                }
            });

            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            firebaseFirestore.collection("Users").document(user_id).collection("OtherDetails").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){

                            String fname = task.getResult().getString("fname");
                            String lname = task.getResult().getString("lname");
                            String sname = task.getResult().getString("sname");
                            String school = task.getResult().getString("school");
                            String id = task.getResult().getString("id");
                            String phone = task.getResult().getString("phone");

                            mPersonalDetails.setText("First Name: "+fname+"\nSecond Name: "+sname+"\nLast Name: "+lname+"\nID Number: "+id+"\nPhone Number: "+phone+"\nSchool: "+school);
                        }
                    }
                }
            });
        }else {
            mDeletion.setVisibility(View.GONE);
            mLinearLayout.setEnabled(false);
            mPersonalDetails.setEnabled(false);
            mAccountDeletion.setVisibility(View.GONE);
            mTwitter.setEnabled(false);
            mInsta.setEnabled(false);
            mFaceBook.setEnabled(false);



            firebaseFirestore.collection("Users").document(user_idPassed).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            String profileName = task.getResult().getString("ProfileName");
                            String status = task.getResult().getString("status");
                            String imageUrl = task.getResult().getString("imageUrl");
                            String insta = task.getResult().getString("insta");
                            String facebook = task.getResult().getString("facebook");
                            String twitter = task.getResult().getString("twitter");

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop();

                            Glide.with(Settings.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfileImage);
                            mUsername.setText(profileName);
                            mStatus.setText(status);
                            mFaceBook.setText(facebook);
                            mTwitter.setText(twitter);
                            mInsta.setText(insta);
                        }
                    }
                }
            });


            firebaseFirestore.collection("Users").document(user_idPassed).collection("OtherDetails").document(user_idPassed).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){

                            final String fname = task.getResult().getString("fname");
                            final String lname = task.getResult().getString("lname");
                            final String sname = task.getResult().getString("sname");
                            final String school = task.getResult().getString("school");
                            final String id = task.getResult().getString("id");
                            final String phone = task.getResult().getString("phone");


                            //getting current user rights to set phone number visible
                            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult().exists()){
                                            final String rights = task.getResult().getString("rights");

                                            if (rights.equals("admin")){

                                                mPersonalDetails.setText("First Name: "+fname+"\nSecond Name: "+sname+"\nLast Name: "+lname+"\nID Number: "+id+"\nPhone Number: "+phone+"\nSchool: "+school);

                                            }else {

                                                mPersonalDetails.setText("First Name: "+fname+"\nSecond Name: "+sname+"\nLast Name: "+lname+"\nID Number: "+id+"\nSchool: "+school);

                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });

        }
        mFaceBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toChange = "facebook";
                String hint = "Enter your facebook username";
                String title = "Facebook Username";
                String error = mFaceBook.getText().toString().trim();

                openDialog(title,toChange,hint,error);
            }
        });
        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toChange = "twitter";
                String hint = "Enter your twitter username";
                String title = "Twitter Username";
                String error = mTwitter.getText().toString().trim();

                openDialog(title,toChange,hint,error);

            }
        });

        mInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toChange = "insta";
                String hint = "Enter your instagram username";
                String title = "Instagram Username";
                String error = mInsta.getText().toString().trim();

                openDialog(title,toChange,hint,error);

            }
        });

        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this,Profile.class));

            }
        });

        mDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setTitle("Account Deletion..")
                        .setMessage("Are you sure you want to delete your account?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(Settings.this);
                                progressDialog.setTitle("Account Deletion..");
                                progressDialog.setMessage("Deleting your account please wait...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                firebaseFirestore.collection("Users").document(user_id).delete().addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    auth.getCurrentUser().delete();
                                                    finish();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    KabarakUniversity.getInstance().clearApplicationData();
                                                    Toast.makeText(Settings.this, "Account deleted..!", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(Settings.this, "Something went wrong try again...", Toast.LENGTH_SHORT).show();
                                                }
                                                progressDialog.dismiss();
                                            }
                                        }
                                );
                            }
                        })
                        .show();
            }
        });

        mPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this,JoinigProfile.class));
            }
        });

    }

    private void openDialog(String title, final String toChnge, String hint, final String initialStringItem){
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle(title);
        final EditText edtToChange = new EditText(Settings.this);
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
                            Toast.makeText(Settings.this, "Successful updated.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(Settings.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }
        );

    }
}
