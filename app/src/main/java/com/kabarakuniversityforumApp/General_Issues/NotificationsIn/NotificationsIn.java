package com.kabarakuniversityforumApp.General_Issues.NotificationsIn;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kabarakuniversityforumApp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NotificationsIn extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mRecyclerView;
    private List<NotificationList> notificationLists;
    private NotificationAdapter notificationAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;
    private ImageView mBack;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_in);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        notificationLists = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationLists);

        mBack = (ImageView)findViewById(R.id.image_back_notification);
        coordinatorLayout =(CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mRecyclerView = (RecyclerView)findViewById(R.id.post_list_notification);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_notification);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationsIn.this));
        mRecyclerView.setAdapter(notificationAdapter);

        if (auth.getCurrentUser() !=null){

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean aBoolean  = !recyclerView.canScrollVertically(1);

                    if (aBoolean){
                        loadMorePosts();
                    }
                }
            });

            Query firstQuery =firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").orderBy("timeStamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(NotificationsIn.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    if (!queryDocumentSnapshots.isEmpty()){

                        if (isFirstPageFirstLoad){
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);

                        }

                        for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                String documentId = documentChange.getDocument().getId();
                                NotificationList notificationList = documentChange.getDocument().toObject(NotificationList.class).withId(documentId);

                                if (isFirstPageFirstLoad){
                                    notificationLists.add(notificationList);
                                }else {
                                    notificationLists.add(0,notificationList);
                                }
                                notificationAdapter.notifyDataSetChanged();
                            }
                        }

                        isFirstPageFirstLoad =false;
                    }
                }
            });

        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                notificationAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(notificationAdapter);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        enableSwipeToDeleteAndUndo();

    }


    private void loadMorePosts(){
        Query secondQuery =firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications").orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        secondQuery.addSnapshotListener(NotificationsIn.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);

                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            String documentId = documentChange.getDocument().getId();
                            NotificationList notificationList = documentChange.getDocument().toObject(NotificationList.class).withId(documentId);
                            notificationLists.add(notificationList);
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final NotificationList item = notificationAdapter.getData().get(position);

                notificationAdapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Notification removed", Snackbar.LENGTH_LONG);


                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
    }

}
