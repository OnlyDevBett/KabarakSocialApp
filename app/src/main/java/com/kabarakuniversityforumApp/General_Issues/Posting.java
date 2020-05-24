package com.kabarakuniversityforumApp.General_Issues;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.FirebaseNotiCallBack;
import com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.FirebaseNotificationHelper;
import com.kabarakuniversityforumApp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import static com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.Constants.KEY_TEXT;
import static com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.Constants.KEY_TITLE;

@SuppressWarnings("ALL")
public class Posting extends AppCompatActivity implements FirebaseNotiCallBack {
    private static final int PICKFILE_RESULT_CODE = 0;
    private static int RESULT_CODE = 0;
    private EditText mPost,mTitle,mTopic;
    private Button mPostBtn,mSpecifySchool,mDocPicking;
    private ImageView mPickedImage;
    private LinearLayout mLinearLayout, eventspecific;
    private FirebaseAuth auth;
    private LinearLayout mLinearSchool;
    private Spinner Spin_School;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String document,user_id;
    private RadioButton mGen,mEvent,mStaff;
    private Uri imageUri,docUri;
    private Toolbar toolbar;
    private TextView mDetailsEvent,mFilePath;
    private ImageView mBack;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = (Toolbar)findViewById(R.id.toolbar_joining);
        setSupportActionBar(toolbar);

        mPost = (EditText)findViewById(R.id.edt_whats_on_yourmind);
        Spin_School = (Spinner)findViewById(R.id.sp_school_posting);
        mTitle = (EditText)findViewById(R.id.edt_topic);
        mSpecifySchool = (Button)findViewById(R.id.btn_specify_school);
        mTopic = (EditText)findViewById(R.id.edt_topic_topic);
        mPostBtn = (Button)findViewById(R.id.btn_post_new);
        mFilePath = (TextView)findViewById(R.id.tv_file_path);
        mPickedImage = (ImageView)findViewById(R.id.image_picked);
        mGen = (RadioButton)findViewById(R.id.radio_general);
        mStaff = (RadioButton)findViewById(R.id.radio_staff);
        mEvent = (RadioButton)findViewById(R.id.radio_event);
        mDocPicking = (Button)findViewById(R.id.btn_document_picking);
        mLinearLayout = (LinearLayout)findViewById(R.id.linearPicture);
        mLinearSchool = (LinearLayout)findViewById(R.id.linearschool);

        mDocPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RESULT_CODE = 1;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try{

                    startActivityForResult(
                            Intent.createChooser(intent,"Select a file to upload"),PICKFILE_RESULT_CODE
                    );
                }catch (android.content.ActivityNotFoundException e){
                    // CustomToastView.makeText(NoticeBoard.this, Toast.LENGTH_SHORT,CustomToastView.SUCCESS, "Please install a file manager..",true).show();
                }
            }
        });

        //picking image
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(Posting.this);
            }
        });

        mEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSpecifySchool.setVisibility(View.VISIBLE);

                if (!mSpecifySchool.getText().equals("Don't Specify School")){
                    mLinearSchool.setVisibility(View.VISIBLE);
                    mSpecifySchool.setText("Don't Specify the School");

                }else {

                    mLinearSchool.setVisibility(View.GONE);
                    mSpecifySchool.setText("Which School?");

                }
            }
        });

        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the document value
                if (mGen.isChecked()){
                    document = "GeneralPosts";
                }
                if (mStaff.isChecked()){
                    document = "StaffPosts";
                }
                if (mEvent.isChecked()){
                    document = "EventPosts";
                }

                final String post = mPost.getText().toString().trim();
                final String title = mTitle.getText().toString().trim();
                final String topic = mTopic.getText().toString().trim();
                final String Spinner_School = Spin_School.getSelectedItem().toString();

                if (TextUtils.isEmpty(post)){
                    Toast.makeText(Posting.this, "Enter the post...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(title)){
                    Toast.makeText(Posting.this, "Enter post title...", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(topic)){
                    Toast.makeText(Posting.this, "Enter post topic...", Toast.LENGTH_SHORT).show();
                }else if (!mStaff.isChecked() && !mGen.isChecked() && !mEvent.isChecked()){
                    Toast.makeText(Posting.this, "Select post category...", Toast.LENGTH_SHORT).show();
                }else {
                    new AlertDialog.Builder(Posting.this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to proceed with the upload?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProgressDialog progressDialog = new ProgressDialog(Posting.this);
                                    progressDialog.setTitle("Uploading");
                                    progressDialog.setMessage("Please wait uploading your post.");
                                    progressDialog.show();
                                    progressDialog.setCancelable(false);

                                    if (imageUri!=null){
                                        String randomName = UUID.randomUUID().toString();
                                        final StorageReference reference = storageReference.child("ImagePosted").child(document).child(randomName+".jpg");
                                        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        if (docUri!=null){
                                                            final StorageReference reference = storageReference.child("FilePosted").child(document).child(mFilePath.getText().toString().trim());
                                                            reference.putFile(docUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            uploadingWith(title,post,uri.toString(),document,topic,Spinner_School,Spin_School,uri.toString());
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }else {
                                                            String docfile = "NoFile";
                                                            uploadingWith(title,post,uri.toString(),document,topic,Spinner_School,Spin_School,docfile);
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }else {
                                        if (docUri!=null){
                                        final StorageReference reference = storageReference.child("FilePosted").child(document).child(mFilePath.getText().toString().trim());
                                        reference.putFile(docUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        uploadingWithOut(title,post,document,topic,Spinner_School,Spin_School,uri.toString());
                                                    }
                                                });
                                            }
                                        });
                                    }else {
                                        String docfile = "NoFile";
                                            uploadingWithOut(title,post,document,topic,Spinner_School,Spin_School ,docfile);
                                    }
                                    }
                                }
                            })
                            .setNeutralButton("Cancel Post", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            })
                            .show();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri  = result.getUri();
                mPickedImage.setImageURI(imageUri);
                mPickedImage.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

            switch (requestCode) {
                case PICKFILE_RESULT_CODE:
                    if (resultCode == RESULT_OK && requestCode == PICKFILE_RESULT_CODE && data != null && data.getData() != null) {
                        docUri = data.getData();
                         docUri = data.getData();
                        File file = new File(docUri.getPath());//create path from uri
                        final String[] split = ":".split(file.getPath());//split the path.
                        String filePath = split[0];
                        mFilePath.setText(filePath);
                    }
                    break;

            }
super.onActivityResult(requestCode, resultCode, data);
  }


    private void uploadingWith(final String title, final String post, String imageUrl, String document, String topic, final String spinner_school, Spinner spin_school, String docfile) {

    String collection = null;

        if (spin_school.getSelectedItemPosition() == 0){
            collection = "Posts";
        }else {
            if (spin_school.getSelectedItemPosition() != 0){
                collection = spin_school.getSelectedItem().toString();
            }
        }

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("post",post);
        objectMap.put("title",title);
        objectMap.put("topic",topic);
        objectMap.put("imageUrl",imageUrl);
        objectMap.put("user_id",user_id);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());
        objectMap.put("document",document);
        objectMap.put("collection",collection);
        objectMap.put("file",docfile);
        objectMap.put("actulafilestring",mFilePath.getText().toString().trim());

        if (spin_school.getSelectedItemPosition()==0)
            firebaseFirestore.collection("AllPosts").document(document).collection(collection).add(objectMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Posting.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                sendingNotification("Kabarak University", post);
                                onBackPressed();
                            } else {
                                Toast.makeText(Posting.this, "Something went wrong check your network", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        else
            {
           if (spin_school.getSelectedItemPosition()!=0){
                firebaseFirestore.collection("AllPosts").document(document).collection(spinner_school).add(objectMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Posting.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                    sendingNotificationSchool("Kabarak Event",post,spinner_school);
                                    onBackPressed();
                                }else {
                                    Toast.makeText(Posting.this, "Something went wrong check your network", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }

        }
    }

    private void uploadingWithOut(final String title, final String post, String document, String topic, final String spinner_school, Spinner spin_school, String docfile) {

    String collection = null;

        if (spin_school.getSelectedItemPosition() == 0){
            collection = "Posts";
        }else {
            if (spin_school.getSelectedItemPosition() != 0){
                collection = spin_school.getSelectedItem().toString();
        }
        }
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("post", post);
        objectMap.put("title", title);
        objectMap.put("topic", topic);
        objectMap.put("user_id", user_id);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());
        objectMap.put("document", document);
        objectMap.put("file", docfile);
        objectMap.put("collection",collection);
        objectMap.put("actulafilestring", mFilePath.getText().toString().trim());

        if (spin_school.getSelectedItemPosition() == 0) {
            firebaseFirestore.collection("AllPosts").document(document).collection(collection).add(objectMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Posting.this, ThePanel.class));

                                Toast.makeText(Posting.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                sendingNotification("Kabarak University", post);
                                onBackPressed();
                            } else {
                                Toast.makeText(Posting.this, "Something went wrong check your network", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            if (spin_school.getSelectedItemPosition() != 0) {
                firebaseFirestore.collection("AllPosts").document(document).collection(spinner_school).add(objectMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Posting.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                    sendingNotificationSchool("Kabarak Event", post, spinner_school);
                                    Toast.makeText(Posting.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(Posting.this, "Something went wrong check your network", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }

        }
    }

    private void sendingNotification(final String title, final String message){


        firebaseFirestore.collection("Users").addSnapshotListener(Posting.this,new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                final String devicetoken = doc.getDocument().getString("devicetoken");
                                String school = doc.getDocument().getString("school");
                                FirebaseNotificationHelper.initialize(getString(R.string.server_key))
                                        .defaultJson(false, getJsonBody(title,message))
                                        .setCallBack(Posting.this)
                                        .receiverFirebaseToken(devicetoken)
                                        .send();
                            }
                        }
                    }
                });

    }
   private void sendingNotificationSchool(final String title, final String message, final String spinner_school){

        firebaseFirestore.collection("Users").addSnapshotListener(Posting.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String user_ids = doc.getDocument().getId();

                        firebaseFirestore.collection("Users").document(user_ids).collection("OtherDetails").whereEqualTo("school",spinner_school).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                for (DocumentChange doc1 : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc1.getType() == DocumentChange.Type.ADDED) {

                                        if (doc1.getType() == DocumentChange.Type.ADDED) {
                                            final String devicetoken = doc1.getDocument().getString("devicetoken");
                                            FirebaseNotificationHelper.initialize(getString(R.string.server_key))
                                                    .defaultJson(false, getJsonBody(title,message))
                                                    .setCallBack(Posting.this)
                                                    .receiverFirebaseToken(devicetoken)
                                                    .send();
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

    private String getJsonBody(String title,String message) {

        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put(KEY_TITLE,title );
            jsonObjectData.put(KEY_TEXT, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObjectData.toString();
    }

    @Override
    public void success(String s) {

    }

    @Override
    public void fail(String s) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkingStaffRights(user_id);
    }

    private void checkingStaffRights(String UserId){
        firebaseFirestore.collection("Users").document(UserId).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                String staff = task.getResult().getString("rank");
                                if (!staff.equals("staff")){
                                    mStaff.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
    }

}