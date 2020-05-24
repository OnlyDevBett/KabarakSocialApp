package com.kabarakuniversityforumApp.General_Issues.Commenting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabarakuniversityforumApp.General_Issues.General;
import com.kabarakuniversityforumApp.ProfileIssues.ProfilePicture;
import com.kabarakuniversityforumApp.ProfileIssues.Settings;
import com.kabarakuniversityforumApp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private Context context;
    private List<CommentList> commentLists;

    public CommentAdapter(List<CommentList> commentLists) {
        this.commentLists = commentLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_itec, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String document = commentLists.get(position).getDocument();
        final String user_id = commentLists.get(position).getUser_id();
        final String status = commentLists.get(position).getStatus();
        final String comment = commentLists.get(position).getComment();
        String imageUrl = commentLists.get(position).getImageComment();

        if (imageUrl!=null){
            holder.imageView.setVisibility(View.VISIBLE);
        }

        if (document.equals("StaffPosts")){
            holder.mSMS.setVisibility(View.VISIBLE);
        }else {
            holder.mSMS.setVisibility(View.INVISIBLE);
        }

       holder.mSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, General.class);
                i.putExtra("user_id",user_id);
                v.getContext().startActivity(i);
            }
        });


        holder.setingDetails(user_id,comment,imageUrl, status );

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        final String imageUrl = task.getResult().getString("imageUrl");
                        final String username = task.getResult().getString("ProfileName");
                        final String status = task.getResult().getString("status");

                        holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                final Dialog builder = new Dialog(context);
                                builder.setContentView(R.layout.profile_dialog);
                                final TextView userName = (TextView)builder.findViewById(R.id.tv_username_profile_dialog);
                                ImageView profilePic = (ImageView)builder.findViewById(R.id.image_profile_dialog);
                                ImageView info = (ImageView)builder.findViewById(R.id.image_info_dialog);

                                RequestOptions requestOptions1 = new RequestOptions();
                                requestOptions1.centerCrop();
                                Glide.with(context).applyDefaultRequestOptions(requestOptions1).load(imageUrl).into(profilePic);
                                userName.setText(username);
                                profilePic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent i = new Intent(context,ProfilePicture.class);
                                        i.putExtra("imageUrl",imageUrl);
                                        i.putExtra("activity",username);
                                        v.getContext().startActivity(i);
                                        builder.dismiss();
                                    }
                                });info.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public int getItemCount() {
        return commentLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private CircleImageView mCircleImageView;
        private TextView mUsername,mComment,mSMS;
        private ImageView imageView;
        private LinearLayout mLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mLinearLayout = (LinearLayout)view.findViewById(R.id.linearUsername);
            imageView = (ImageView)view.findViewById(R.id.image_commented);
            mCircleImageView = (CircleImageView)view.findViewById(R.id.image_profile_commenter);
            mSMS = (TextView)view.findViewById(R.id.tv_sms_person);
        }

        public void setingDetails(String userId, final String user_id, String comment, String imageUrlPCommented){

            imageView = (ImageView)view.findViewById(R.id.image_commented);
            mUsername = (TextView)view.findViewById(R.id.tv_username_profile_comment);
            mComment = (TextView)view.findViewById(R.id.tv_comment_actual);
            mCircleImageView = (CircleImageView)view.findViewById(R.id.image_profile_commenter);

            //getting commenter profile
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().exists()){
                            final String imageUrl = task.getResult().getString("imageUrl");
                            final String username = task.getResult().getString("ProfileName");
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.centerCrop();
                            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mCircleImageView);
                            mUsername.setText(username);
                        }
                    }
                }
            });

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageUrlPCommented).into(imageView);
            mComment.setText(comment);



        }
    }
}
