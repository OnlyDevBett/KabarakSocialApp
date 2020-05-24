package com.kabarakuniversityforumApp.General_Issues;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class KabaUniversity extends Fragment {


    private View view;
    private PostAdapter postAdapter;
    private List<PostModelAdapter> postModelAdapters;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_kaba_university, container, false);


        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        postModelAdapters = new ArrayList<>();
        postAdapter = new PostAdapter(postModelAdapters);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_gov);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.post_list_gov);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(postAdapter);

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

            Query firstQuery = firebaseFirestore.collection("AllPosts").document("StaffPosts").collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    if (!queryDocumentSnapshots.isEmpty()){

                        if (isFirstPageFirstLoad){
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);

                        }


                        for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                String documentId = documentChange.getDocument().getId();
                                PostModelAdapter postModelAdapter = documentChange.getDocument().toObject(PostModelAdapter.class).withId(documentId);

                                if (isFirstPageFirstLoad){
                                    postModelAdapters.add(postModelAdapter);
                                }else {
                                    postModelAdapters.add(0,postModelAdapter);
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }

                        isFirstPageFirstLoad =false;
                    }
                }
            });

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                postAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(postAdapter);
            }
        });


        return view;
    }
    private void loadMorePosts(){
        Query secondQuery = firebaseFirestore.collection("AllPosts").document("StaffPosts").collection("Posts")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        secondQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() -1);

                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            String documentId = documentChange.getDocument().getId();
                            PostModelAdapter postModelAdapter = documentChange.getDocument().toObject(PostModelAdapter.class).withId(documentId);
                            postModelAdapters.add(postModelAdapter);
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }
        });
    }
}
