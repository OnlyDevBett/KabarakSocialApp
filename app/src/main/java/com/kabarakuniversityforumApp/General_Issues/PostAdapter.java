package com.kabarakuniversityforumApp.General_Issues;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cnrylmz.zionfiledownloader.DownloadFile;
import com.cnrylmz.zionfiledownloader.FILE_TYPE;
import com.cnrylmz.zionfiledownloader.ZionDownloadFactory;
import com.cnrylmz.zionfiledownloader.ZionDownloadListener;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kabarakuniversityforumApp.General_Issues.Commenting.Commenting;
import com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.FirebaseNotiCallBack;
import com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.FirebaseNotificationHelper;
import com.kabarakuniversityforumApp.ProfileIssues.ProfilePicture;
import com.kabarakuniversityforumApp.ProfileIssues.Settings;
import com.kabarakuniversityforumApp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.Constants.KEY_TEXT;
import static com.kabarakuniversityforumApp.Notifications.FirebaseNotificationActionHelper.Constants.KEY_TITLE;


@SuppressWarnings("ALL")
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements FirebaseNotiCallBack {
    private Context context;
    private List<PostModelAdapter> postModelAdapters;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private Context activity;

    public PostAdapter(List<PostModelAdapter> postModelAdapters) {
        this.postModelAdapters = postModelAdapters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        context = parent.getContext().getApplicationContext();
        activity = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String user_id = postModelAdapters.get(position).getUser_id();
        final String imageUrl = postModelAdapters.get(position).getImageUrl();
        final String topic = postModelAdapters.get(position).getTopic();
        final String title = postModelAdapters.get(position).getTitle();
        final String post = postModelAdapters.get(position).getPost();
        final String documentId = postModelAdapters.get(position).PostId;
        final String document = postModelAdapters.get(position).getDocument();
        final Date posted = postModelAdapters.get(position).getTimeStamp();
        final String file = postModelAdapters.get(position).getFile();
        final String actulafilestring = postModelAdapters.get(position).getActulafilestring();
        final String collection = postModelAdapters.get(position).getCollection();

        String current_user_id = auth.getCurrentUser().getUid();

        //setting the delete button visibility
        if (user_id.equals(current_user_id)){
            holder.mPostDelete.setVisibility(View.VISIBLE);
        }else {
            firebaseFirestore.collection("Users").document(current_user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                if (task.getResult().exists()){
                                    String rights = task.getResult().getString("rights");
                                    if (rights.equals("admin")){
                                        holder.mPostDelete.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
        }//setting visibility ends here
        //deleting/Droping a post
        final String finalCollection = collection;
        holder.mPostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        // Create Alert using Builder
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(activity)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setTitle("Post Deletion")
                        .setTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setCornerRadius(10)
                        .setHeaderView(R.layout.deleting)
                        .setTextColor(activity.getResources().getColor(R.color.Black))
                        .setMessage("Are you sure you want to delete this post\n"+title+"?")
                        .addButton("Yes", Color.parseColor("#FFFFFF"), Color.parseColor("#32CD32"), CFAlertDialog.CFAlertActionStyle.POSITIVE,
                                CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        final ProgressDialog  progressDialog= new ProgressDialog(activity);
                                        progressDialog.setMessage("Deleting please wait...");
                                        progressDialog.setCancelable(false);
                                        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
                                        progressDialog.show();
                                        firebaseFirestore.collection("AllPosts").document(document).collection(finalCollection).document(documentId)
                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                                                removeItem(position);
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Something went wrong try again letter", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });

                                        dialog.dismiss();


                                    }
                                })
                        .addButton("No", Color.parseColor("#FFFFFF"), Color.parseColor("#cd0d1a"), CFAlertDialog.CFAlertActionStyle.NEGATIVE,
                                CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });
// Show the alert
                builder.show();
            }
        });

        if (!file.equals("NoFile")){
            holder.mDocFile.setVisibility(View.VISIBLE);


            /*String extension = actulafilestring.substring(actulafilestring.lastIndexOf("."));//getting the file extension
            if (extension.equals(".mp4") || extension.equals(".miv"))
            {
                holder.mDocFile.setText("Download video file");
            }else if (extension.equals(".pdf") || extension.equals(".doxc") || extension.equals(".doc"))
            {
                //holder.mDoc.setVisibility(View.VISIBLE);
                holder.mDocFile.setText("Download document file");
            }*/
            //downloading the file
            holder.mDocFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String file_type = null;
                    final String extension = actulafilestring.substring(actulafilestring.lastIndexOf("."));

                    if (extension.equals(".jpg") || extension.equals(".png"))
                    {
                        file_type = "IMAGE";
                    }else if (extension.equals(".mp4") || extension.equals(".miv"))
                    {
                        file_type = "VIDEO";
                    }else if (extension.equals(".pdf") || extension.equals(".doxc") || extension.equals(".doc"))
                    {
                        file_type = "PDF";
                    }
                    ZionDownloadFactory factory = new ZionDownloadFactory(context, file, post);
                    DownloadFile downloadFile = factory.downloadFile(FILE_TYPE.valueOf(file_type));
                    downloadFile.start(new ZionDownloadListener() {
                        @Override
                        public void OnSuccess(String dataPath) {

                            Toast.makeText(context, "Download complete:\n"+dataPath, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void OnFailed(String message) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                       @Override
                        public void OnPaused(String message) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        }
                        @Override
                        public void OnPending(String message) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void OnBusy() {
                        }
                    });
                }
            });
        }
        //setting visibility
        if (imageUrl!=null){
            holder.mImagePosted.setVisibility(View.VISIBLE);
            holder.mPostedPost.setVisibility(View.GONE);
        }else {
            holder.mActualPost.setVisibility(View.GONE);
        }
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()){
                                final String imageUrlPoster = task.getResult().getString("imageUrl");
                                final String username = task.getResult().getString("ProfileName");

                                holder.settingDetails(imageUrlPoster,username,imageUrl,topic,title,post,post,document,documentId,posted, collection);
                                holder.mRepliesLinear.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(context, Commenting.class);
                                        i.putExtra("poster",imageUrlPoster);
                                        i.putExtra("postedimage",imageUrl);
                                        i.putExtra("topic",topic);
                                        i.putExtra("title",title);
                                        i.putExtra("post",post);
                                        i.putExtra("username",username);
                                        i.putExtra("documentId",documentId);
                                        i.putExtra("document",document);
                                        i.putExtra("collection", collection);
                                        v.getContext().startActivity(i);
                                    }
                                });
                                holder.mPoster.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onClick(View v) {
                                        final Dialog builder = new Dialog(activity);
                                        builder.setContentView(R.layout.profile_dialog);
                                        final TextView userName = (TextView)builder.findViewById(R.id.tv_username_profile_dialog);
                                        ImageView profilePic = (ImageView)builder.findViewById(R.id.image_profile_dialog);
                                        ImageView info = (ImageView)builder.findViewById(R.id.image_info_dialog);

                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions.centerCrop();
                                        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrlPoster).into(profilePic);
                                        userName.setText(username);
                                        profilePic.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                Intent i = new Intent(context,ProfilePicture.class);
                                                i.putExtra("imageUrl",imageUrlPoster);
                                                i.putExtra("activity",username);
                                                v.getContext().startActivity(i);
                                                builder.dismiss();
                                            }
                                        });
                                        info.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(context,Settings.class);
                                                i.putExtra("user_idpassed",user_id);
                                                v.getContext().startActivity(i);
                                                builder.dismiss();
                                            }
                                        });

                                        builder.show();
                                    }
                                });

                            }
                        }
                    }
                });

        holder.mImagePosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProfilePicture.class);
                i.putExtra("activity","adapter");
                i.putExtra("imageUrl",imageUrl);
                v.getContext().startActivity(i);

            }
        });

        holder.mLikesLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                        .collection("Likes").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()){

                                firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                                        .collection("Likes").document(auth.getCurrentUser().getUid()).delete();
                            }else {
                                Map<String , Object>objectMap = new HashMap<>();
                                objectMap.put("timeStamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                                        .collection("Likes").document(auth.getCurrentUser().getUid()).set(objectMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid())
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        if (Objects.requireNonNull(task.getResult()).exists()){
                                                            final String name = task.getResult().getString("ProfileName");
                                                            holder.sendingNotification(user_id,title,name);
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    }
                });

            }
        });


        firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                .collection("Likes").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                if (documentSnapshot.exists()){
                    holder.mLikesImage.setImageDrawable(context.getDrawable(R.drawable.ic_action_like));

                }else {
                    holder.mLikesImage.setImageDrawable(context.getDrawable(R.drawable.ic_action_unlike));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postModelAdapters.size();
    }

    public void removeItem(int position) {
        postModelAdapters.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void success(String s) {

    }

    @Override
    public void fail(String s) {

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mPoster;
        private TextView mTopic,mTitle,mLikes,mReplies,mTime,mUsername,mDocFile;
        private ReadMoreTextView mActualPost,mPostedPost;
        private LinearLayout mLikesLinear,mRepliesLinear;
        private ImageView mLikesImage,mImagePosted,mPostDelete;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            mLikesLinear = (LinearLayout)view.findViewById(R.id.linearLikes);
            mRepliesLinear = (LinearLayout)view.findViewById(R.id.linearComments);
            mImagePosted = (ImageView)view.findViewById(R.id.image_posted_item);
            mActualPost = (ReadMoreTextView) view.findViewById(R.id.tv_atual_post);
            mLikesImage = (ImageView)view.findViewById(R.id.imageLikes);
            mPostedPost = (ReadMoreTextView) view.findViewById(R.id.tv_post_posted_item);
            mImagePosted = (ImageView)view.findViewById(R.id.image_posted_item);
            mPoster= (CircleImageView)view.findViewById(R.id.profileImagePoster);
            mTime = (TextView)view.findViewById(R.id.tv_timePosted);
            mTopic = (TextView)view.findViewById(R.id.tv_postTopic);
            mTitle = (TextView)view.findViewById(R.id.tv_postTitle);
            mDocFile = (TextView)view.findViewById(R.id.tv_document);
            mPostDelete = (ImageView)view.findViewById(R.id.image_post_delete);
        }

        @SuppressLint("CheckResult")
        public void settingDetails(String imageUrlPoster, String username, String imageUrl, String topic, String title, String actualpost, String post, String document, String documentId, Date posted, String collection){

            mPoster= (CircleImageView)view.findViewById(R.id.profileImagePoster);
            mTopic = (TextView)view.findViewById(R.id.tv_postTopic);
            mTitle = (TextView)view.findViewById(R.id.tv_postTitle);
            mActualPost = (ReadMoreTextView) view.findViewById(R.id.tv_atual_post);
            mPostedPost = (ReadMoreTextView) view.findViewById(R.id.tv_post_posted_item);
            mImagePosted = (ImageView)view.findViewById(R.id.image_posted_item);
            mReplies  =(TextView)view.findViewById(R.id.tv_comments);
            mLikes  =(TextView)view.findViewById(R.id.tv_likes);
            mTime = (TextView)view.findViewById(R.id.tv_timePosted);
            mUsername = (TextView)view.findViewById(R.id.tv_username_post_item);
            mDocFile = (TextView)view.findViewById(R.id.tv_document);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrlPoster).into(mPoster);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mImagePosted);

            mTitle.setText(title);
            mTopic.setText(topic);
            mActualPost.setText(actualpost);
            mPostedPost.setText(post);
            mUsername.setText(username);

            Query  query = firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                    .collection("Comments");
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            int comments = queryDocumentSnapshots.size();
                            if (comments==1){

                                mReplies.setText(""+comments+" Comment");
                            }else {
                                mReplies.setText(""+comments+" Comments");

                            }
                        }
                    });

            Query  query1 = firebaseFirestore.collection("AllPosts").document(document).collection(collection).document(documentId)
                    .collection("Likes");
            query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int likes = queryDocumentSnapshots.size();
                    mLikes.setText(""+likes+" Like(s)");
                }
            });
            //getting and setting the current time of the post
            Date c = Calendar.getInstance().getTime();
            //setting date to milisecs

            long currenttime = c.getTime();
            long diff = currenttime-posted.getTime();

            long seconds = diff/1000;
            long min = seconds/60;
            long hrs = min/60;
            long day = hrs/24;
            long week = day/7;
            long month = week/4;
            long year = month/12;


            if (seconds<60 && seconds>=0){
                mTime.setText(seconds+" Seconds ago");
            }else {

            }
            if (min>=1 && min<60){
                mTime.setText(min+" Minutes ago");
            }
            if (hrs>=1 && hrs<24){
                mTime.setText(hrs+" Hour(s) ago");
            }
            if (day>=1 && day<7){
                mTime.setText(day+" Day(s) ago");
            }
            if (week>=1 && week<4){
                mTime.setText(week+" Week(s) ago");
            }
            if (month>=1 && month<12){
                mTime.setText(month+" Month(s) ago");
            }
            if (year>=1){
                mTime.setText(year+" Year(s) ago");
            }

        }
        public void sendingNotification(final String user_id, final String posttitle, final String name){
            firebaseFirestore.collection("Users").document(user_id).collection("OtherDetails")
                    .document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (Objects.requireNonNull(task.getResult()).exists()){
                            final String devicetoken = task.getResult().getString("devicetoken");

                            //sending to db
                            Map<String,Object> objectMap = new HashMap<>();
                            objectMap.put("message",name+" liked your "+posttitle+" post");
                            objectMap.put("status","unread");
                            objectMap.put("user_id",auth.getCurrentUser().getUid());
                            objectMap.put("timeStamp",FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Users").document(user_id).collection("Notifications").add(objectMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        FirebaseNotificationHelper.initialize(context.getString(R.string.server_key))
                                                .defaultJson(false, getJsonBody("Kabarak University",name+" liked your "+posttitle+" post"))
                                                .receiverFirebaseToken(devicetoken)
                                                .send();

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

    }
}
