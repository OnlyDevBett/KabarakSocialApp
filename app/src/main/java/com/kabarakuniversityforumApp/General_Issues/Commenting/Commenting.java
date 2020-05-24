package com.kabarakuniversityforumApp.General_Issues.Commenting;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kabarakuniversityforumApp.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class Commenting extends AppCompatActivity {
    private CircleImageView mPoster,mPicked;
    private TextView mTopic,mTitle,mLikes,mReplies,mUsername, status;
    private ReadMoreTextView mActualPost,mPostedPost;
    private LinearLayout mLikesLinear,mRepliesLinear;
    private ImageView mLikesImage,mImagePosted,mPicImage;
    private FloatingActionButton mFloateRmove;
    private Button mSend;
    private EditText mComment;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String user_id,poster,post,imageUrl,topic,title,documentId,document,username, mStatus, collection;
    private RequestOptions requestOptions;
    private List<CommentList>  commentLists;
    private CommentAdapter commentAdapter;
    private Uri imageUri;
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commenting);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        requestOptions = new RequestOptions();
        requestOptions.centerCrop();


        mImagePosted = (ImageView)findViewById(R.id.image_posted_item1);
        mLinearLayout = (LinearLayout)findViewById(R.id.linearLikesComments);
        mActualPost = (ReadMoreTextView) findViewById(R.id.tv_atual_post1);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_comments);
        commentLists = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentLists);
        mPoster= (CircleImageView)findViewById(R.id.profileImagePoster1);
        mTopic = (TextView)findViewById(R.id.tv_postTopic1);
        mUsername = (TextView)findViewById(R.id.tv_username_commenting);
        mTitle = (TextView)findViewById(R.id.tv_postTitle1);
        mPostedPost = (ReadMoreTextView) findViewById(R.id.tv_post_posted_item1);
        mSend = (Button)findViewById(R.id.btn_senf_reply);
        mComment = (EditText)findViewById(R.id.edt_comment);
        mPicImage = (ImageView)findViewById(R.id.image_pic_picture_comment);
        status = (TextView) findViewById(R.id.statusEdt);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(Commenting.this));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(commentAdapter);
        mLinearLayout.setVisibility(View.GONE);


        //getting values passed
        poster = getIntent().getExtras().getString("poster");
        imageUrl = getIntent().getExtras().getString("postedimage");
        title = getIntent().getExtras().getString("title");
        topic = getIntent().getExtras().getString("topic");
        post = getIntent().getExtras().getString("post");
        documentId = getIntent().getExtras().getString("documentId");
        document = getIntent().getExtras().getString("document");
        username = getIntent().getExtras().getString("username");
        collection = getIntent().getExtras().getString("collection");
        mStatus = getIntent().getExtras().getString("status");

        if (imageUrl!=null){
            mImagePosted.setVisibility(View.VISIBLE);
            mPostedPost.setVisibility(View.GONE);
        }else {
            mActualPost.setVisibility(View.GONE);
        }


        //setting them values
        Glide.with(Commenting.this).applyDefaultRequestOptions(requestOptions).load(poster).into(mPoster);
        Glide.with(Commenting.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mImagePosted);
        mPostedPost.setText(post);
        mActualPost.setText(post);
        mTitle.setText(title);
        mTopic.setText(topic);
        mUsername.setText(username);
        status.setText(mStatus);

        //picking image to comment
        mPicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Commenting.this,ImagePickerComment.class);
                intent.putExtra("document",document);
                intent.putExtra("documentID",documentId);
                startActivity(intent);
            }
        });

        //sending comment
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mComment.getText().toString().trim();
                //checking if comment is empty
                if (TextUtils.isEmpty(comment)){
                    mComment.setError("Enter comment...");
                }else {
                    if (imageUri!=null){

                        commentingWith(comment);
                        mComment.setText("");
                        imageUri=null;

                    }else {
                        commentWithout(comment);
                        mComment.setText("");
                        imageUri=null;
                    }
                }
            }
        });

        Query query = firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                .collection("Comments").orderBy("timeStamp", Query.Direction.ASCENDING);
        query.addSnapshotListener(Commenting.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String documentId = documentChange.getDocument().getId();
                        CommentList commentList = documentChange.getDocument().toObject(CommentList.class).withId(documentId);
                        commentLists.add(commentList);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void commentingWith(final String comment){

        String randomText = UUID.randomUUID().toString();
        final StorageReference reference = storageReference.child("Comment Images").child(randomText+".jpg");
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //sending the values to firebase db
                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put("user_id",user_id);
                        objectMap.put("comment",comment);
                        objectMap.put("document",document);
                        objectMap.put("imageComment",uri.toString());
                        objectMap.put("timeStamp", FieldValue.serverTimestamp());

                        firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                                .collection("Comments").add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(Commenting.this, "Sent", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(Commenting.this, "Comment not sent something went wrong.\nNetwork error try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });

    }

    private void commentWithout(String comment){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("user_id",user_id);
        objectMap.put("document",document);
        objectMap.put("comment",comment);
        objectMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                .collection("Comments").add(objectMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){

                    Toast.makeText(Commenting.this, "Sent", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Commenting.this, "Comment not sent something went wrong.\nNetwork error try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
