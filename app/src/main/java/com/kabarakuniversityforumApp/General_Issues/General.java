package com.kabarakuniversityforumApp.General_Issues;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
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
import java.util.Objects;

import javax.annotation.Nullable;

public class General extends Fragment {
    private View view;
    private PostAdapter postAdapter;
    private List<PostModelAdapter> postModelAdapters;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth auth;
    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_general, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        postModelAdapters = new ArrayList<>();
        postAdapter = new PostAdapter(postModelAdapters);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_gen);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.post_list_gen);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(postAdapter);

        queryGeneralMethod();

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void queryGeneralMethod() {
        if (auth.getCurrentUser() !=null){

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean aBoolean  = !recyclerView.canScrollVertically(1);

                    if (aBoolean){
                        loadMorePosts();
                    }
                }
            });

            Query firstQuery = firebaseFirestore.collection("AllPosts").document("GeneralPosts").collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
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

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadMorePosts(){
        Query secondQuery = firebaseFirestore.collection("AllPosts").document("GeneralPosts").collection("Posts")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);
        secondQuery.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                assert queryDocumentSnapshots != null;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
