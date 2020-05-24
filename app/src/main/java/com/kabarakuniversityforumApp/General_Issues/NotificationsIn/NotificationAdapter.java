package com.kabarakuniversityforumApp.General_Issues.NotificationsIn;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabarakuniversityforumApp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationList> notificationLists;
    private Context context;
    private Context activity;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;


    public NotificationAdapter(List<NotificationList> notificationLists) {
        this.notificationLists = notificationLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        context = parent.getContext().getApplicationContext();
        activity = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String user_id = notificationLists.get(position).getUser_id();
        String message = notificationLists.get(position).getMessage();
        String status = notificationLists.get(position).getStatus();
        final String notificationId = notificationLists.get(position).PostId;

        //checking status if deleted to remove permanetly
        if (status.equals("deleted")){
            firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").document(notificationId)
                    .delete();
        }

        holder.settingDetails(message);



        if (!status.equals("unread")){
            holder.mLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting color to white
                holder.mLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));

                //updating staus
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("status","read");
                firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").document(notificationId)
                        .update(objectMap);

            }
        });


    }

    @Override
    public int getItemCount() {
        return notificationLists.size();
    }

    public void removeItem(int position) {
        String notificationId= notificationLists.get(position).PostId;
        //updating status
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("status","deleted");
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").document(notificationId)
                .update(objectMap);
        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();

        notificationLists.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationList item, int position) {
        String notificationId= notificationLists.get(position).PostId;
        //updating status
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("status","read");
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").document(notificationId)
                .update(objectMap);
        Toast.makeText(context, "Restored", Toast.LENGTH_SHORT).show();
        notificationLists.add(position, item);
        notifyItemInserted(position);
    }

    public List<NotificationList> getData() {
        return notificationLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView mMessage;
        private LinearLayout mLinearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mLinearLayout = (LinearLayout)view.findViewById(R.id.layout_noti);
            mMessage = (TextView)view.findViewById(R.id.tv_notification_message);

        }

        public void settingDetails(String message){
            mMessage = (TextView)view.findViewById(R.id.tv_notification_message);
            mMessage.setText(message);
        }
    }
}
