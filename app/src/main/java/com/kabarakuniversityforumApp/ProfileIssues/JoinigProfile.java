package com.kabarakuniversityforumApp.ProfileIssues;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kabarakuniversityforumApp.R;

import java.util.HashMap;
import java.util.Map;

public class JoinigProfile extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Button mSubmit,mEdit;
    private TextInputEditText mFname,mLname,mSname,mEmail,mId;
    private Spinner mSchool;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private CheckBox mTerms;
    private String user_id;
    private boolean exist = false;
    private int position;
    private Toolbar toolbar;
    private ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinig_profile);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        progressDialog  = new ProgressDialog(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar_joining);
        setSupportActionBar(toolbar);
        mSubmit = (Button)findViewById(R.id.btn_submit_details);
        mEdit = (Button)findViewById(R.id.btn_edit_details);
        mFname = (TextInputEditText)findViewById(R.id.edt_fname);
        mLname = (TextInputEditText)findViewById(R.id.edt_lname);
        mSname = (TextInputEditText)findViewById(R.id.edt_sname);
        mId = (TextInputEditText)findViewById(R.id.edt_idnumber);
        mEmail = (TextInputEditText)findViewById(R.id.edt_phone);
        mBack = (ImageView)findViewById(R.id.image_back_joining);
        mSchool = (Spinner)findViewById(R.id.sp_school_posting);
        mTerms = (CheckBox)findViewById(R.id.check_terms);
        mSchool.setSelection(0);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseFirestore.collection("Users").document(user_id).collection("OtherDetails").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String fname1 = task.getResult().getString("fname");
                        String lname1 = task.getResult().getString("lname");
                        String sname1 = task.getResult().getString("sname");
                        String id1 = task.getResult().getString("id");
                        String school = task.getResult().getString("school");
                        mEdit.setVisibility(View.VISIBLE);
                        mSubmit.setText("Submit");
                        mFname.setText(fname1);
                        mSname.setText(sname1);
                        mLname.setText(lname1);
                        mId.setText(id1);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(JoinigProfile.this, R.array.schools, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSchool.setAdapter(adapter);
                        if (school != null) {
                            int spinnerPosition = adapter.getPosition(school);
                            mSchool.setSelection(spinnerPosition);
                        }
                        mFname.setEnabled(false);
                        mLname.setEnabled(false);
                        mSchool.setEnabled(false);
                        mSname.setEnabled(false);
                        mId.setEnabled(false);
                        mTerms.setChecked(true);
                        mTerms.setEnabled(false);
                        mSubmit.setText("EDIT DETAILS");
                        mSubmit.setVisibility(View.GONE);
                        exist = true;
                    }
                }
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFname.setEnabled(true);
                mLname.setEnabled(true);
                mSchool.setEnabled(true);
                mSname.setEnabled(true);
                mId.setEnabled(true);
                mTerms.setChecked(true);
                mTerms.setEnabled(false);
                mSubmit.setText("EDIT DETAILS");
                mSubmit.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.GONE);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname  = mFname.getText().toString().trim();
                final String sname  = mSname.getText().toString().trim();
                final String lname  = mLname.getText().toString().trim();
                final String id  = mId.getText().toString().trim();
                final String email  = auth.getCurrentUser().getEmail();
                final String school  = mSchool.getSelectedItem().toString().trim();

                mEmail.setText(email);

                //checking all them details if entered

                if (mSchool.getSelectedItemPosition() == 0){
                    Toast.makeText(JoinigProfile.this, "Select School", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(fname)){
                    Toast.makeText(JoinigProfile.this, "Enter your first name..!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(lname)){
                    Toast.makeText(JoinigProfile.this, "Enter your last name..!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(sname)){
                    Toast.makeText(JoinigProfile.this, "Enter your second name..!", Toast.LENGTH_SHORT).show();
                }else  if (TextUtils.isEmpty(id)){
                    Toast.makeText(JoinigProfile.this, "Enter your id number..!", Toast.LENGTH_SHORT).show();
                }else if (!mTerms.isChecked()){
                    Toast.makeText(JoinigProfile.this, "Accept terms and conditions to proceed..!", Toast.LENGTH_SHORT).show();
                }else {
                    if (fname.equals(lname) || sname.equals(fname) || sname.equals(lname)){
                        Toast.makeText(JoinigProfile.this, "Names cannot be the same..!", Toast.LENGTH_SHORT).show();
                    }else if (mId.length()>8){
                        Toast.makeText(JoinigProfile.this, "Enter a valid id number..!", Toast.LENGTH_SHORT).show();
                    }else {

                        new AlertDialog.Builder(JoinigProfile.this)
                                .setTitle("Confirming..")
                                .setMessage("Are you sure you want to proceed with the details provided?")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (exist == false){

                                            progressDialog.setTitle("Submitting...");
                                            progressDialog.setMessage("Please wait details are being uploaded.");
                                            progressDialog.show();
                                            registration(fname,lname,sname,email,id,school,user_id);
                                        }else {

                                            progressDialog.setTitle("Submitting...");
                                            progressDialog.setMessage("Please wait details are being uploaded.");
                                            progressDialog.show();
                                            registrationUpdate(fname,lname,sname,email,id,school,user_id);

                                        }
                                    }
                                })
                                .show();
                    }
                }
            }
        });

    }

    private void registration(String fname,String lname,String sname,String phone,String id, String school,String user_Id ){


        String devicetoken = FirebaseInstanceId.getInstance().getToken();
        //ObjectData
        Map<String, Object> registration = new HashMap<>();
        registration.put("fname",fname);
        registration.put("lname",lname);
        registration.put("sname",sname);
        registration.put("email",phone);
        registration.put("id",id);
        registration.put("school",school);
        registration.put("timeStamp", FieldValue.serverTimestamp());
        registration.put("devicetoken",devicetoken);

        //uploading the dataset to firebase
        firebaseFirestore.collection("Users").document(user_Id).collection("OtherDetails").document(user_Id).set(registration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(JoinigProfile.this, "Details Successfully submitted", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }else {
                            Toast.makeText(JoinigProfile.this, "Something went wrong..!"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void registrationUpdate(String fname, String lname, String sname, String phone, String id, String school, final String user_Id ){


        final String devicetoken = FirebaseInstanceId.getInstance().getToken();
        //ObjectData
        Map<String, Object> registration = new HashMap<>();
        registration.put("fname",fname);
        registration.put("lname",lname);
        registration.put("sname",sname);
        registration.put("email",phone);
        registration.put("id",id);
        registration.put("school",school);
        registration.put("timeStamp", FieldValue.serverTimestamp());
        registration.put("devicetoken",devicetoken);

        //uploading the dataset to firebase
        firebaseFirestore.collection("Users").document(user_Id).collection("OtherDetails").document(user_Id).update(registration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(JoinigProfile.this, "Details Successfully edited", Toast.LENGTH_SHORT).show();
                            updateDeviceTokenBoth(devicetoken,user_Id);
                            onBackPressed();
                        }else {
                            Toast.makeText(JoinigProfile.this, "Something went wrong..!"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateDeviceTokenBoth(String devicetoken,String user_Id){
        //ObjectData
        Map<String, Object> registration = new HashMap<>();
        registration.put("devicetoken",devicetoken);
        firebaseFirestore.collection("Users").document(user_Id).update(registration);


    }

}
