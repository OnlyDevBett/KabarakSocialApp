package com.kabarakuniversityforumApp.General_Issues.Commenting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
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

public class ImagePickerComment extends AppCompatActivity {
    private ImageView mImageView,mBack;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private EditText mComment;
    private FloatingActionButton floatingActionButton;
    private String user_id,document,documentId;
    private Uri uri;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_comment);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = auth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        mImageView = (ImageView)findViewById(R.id.image_picked_comment_picked);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.floatSendComment);
        mBack = (ImageView)findViewById(R.id.image_back_comment_image);
        mComment = (EditText)findViewById(R.id.edt_comment_image);

        document = getIntent().getExtras().getString("document");
        documentId = getIntent().getExtras().getString("documentID");

        launchingCroper();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mComment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){
                    mComment.setError("Enter comment...");
                }else {
                    commentingWith(comment);
                }
            }
        });


    }

    private void launchingCroper(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ImagePickerComment.this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri  = result.getUri();
                mImageView.setImageURI(uri);
                if (uri==null){
                    onBackPressed();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void commentingWith(final String comment){

        String randomText = UUID.randomUUID().toString();
        final StorageReference reference = storageReference.child("Comment Images").child(randomText+".jpg");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //sending the values to firebase db
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("user_id",user_id);
                        objectMap.put("comment",comment);
                        objectMap.put("imageComment",uri.toString());
                        objectMap.put("timeStamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("AllPosts").document(document).collection("Posts").document(documentId)
                                .collection("Comments").add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(ImagePickerComment.this, "Sent", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }else {
                                    Toast.makeText(ImagePickerComment.this, "Comment not sent something went wrong.\nNetwork error try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

    }
}
