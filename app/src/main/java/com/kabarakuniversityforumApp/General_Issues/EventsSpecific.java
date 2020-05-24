package com.kabarakuniversityforumApp.General_Issues;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kabarakuniversityforumApp.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class EventsSpecific extends AppCompatActivity {
    private PostAdapter postAdapter;
    private List<PostModelAdapter> postModelAdapters;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String school_s;
    private Toolbar toolbar;
    private ImageView mBack;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_specific);
        firebaseFirestore = FirebaseFirestore.getInstance();
        postModelAdapters = new ArrayList<>();
        postAdapter = new PostAdapter(postModelAdapters);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_evnt_all);
        mTitle = (TextView)findViewById(R.id.tv_post_title);

        toolbar = (Toolbar)findViewById(R.id.toolbar_post_title);
        setSupportActionBar(toolbar);
        mBack = (ImageView)findViewById(R.id.image_back_post_title);

        mRecyclerView = (RecyclerView)findViewById(R.id.post_list_evnt_all);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(postAdapter);


        school_s = getIntent().getExtras().getString("school");

            mTitle.setText(school_s+" Events");
            Query query = firebaseFirestore.collection("AllPosts").document("EventPosts").collection(school_s).orderBy("timeStamp", Query.Direction.DESCENDING);
            query.addSnapshotListener(EventsSpecific.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            String documentId = documentChange.getDocument().getId();
                            PostModelAdapter postModelAdapter = documentChange.getDocument().toObject(PostModelAdapter.class).withId(documentId);
                            postModelAdapters.add(postModelAdapter);
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                postAdapter.notifyDataSetChanged();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

          }

}
