package com.kabarakuniversityforumApp.General_Issues;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment {


    private View view;
    private PostAdapter postAdapter;
    private List<PostModelAdapter> postModelAdapters;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private String mSchool;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth auth;
    private String user_id;
    private Button mSchool_btn;
    private ProgressDialog progressDialog;
    private DocumentSnapshot lastVisible;
    private boolean isFirstPageFirstLoad = true;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_events, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        user_id = auth.getCurrentUser().getUid();
        postModelAdapters = new ArrayList<>();
        postAdapter = new PostAdapter(postModelAdapters);
        mSchool_btn = (Button) view.findViewById(R.id.btn_school_event);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_evnt);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.post_list_evnt);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseFirestore.collection("Users").document(user_id).collection("OtherDetails").document(user_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (Objects.requireNonNull(task.getResult()).exists()){
                        mSchool = task.getResult().getString("school");
                    }
                }
            }
        });
        mRecyclerView.setAdapter(postAdapter);


        final Handler timerHandler;
        timerHandler = new Handler();

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Here you can update your adapter data
                postAdapter.notifyDataSetChanged();
                timerHandler.postDelayed(this, 1000); //run every second
            }
        };

        timerHandler.postDelayed(timerRunnable, 1000);

       mSchool_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext());
               builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
               builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
               builder.setTitle("Select School to view its events");
               builder.setSingleChoiceItems(new String[]{"Pick School","Law", "Business", "Education", "Computer Science", "Theology", "Medicine", "Music", "Post Graduate"}, 0, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int index) {
                       if (index ==0 ){
                           mSchool="Pick School";
                       }
                       if (index==1){
                           mSchool ="Law";
                       }
                       if (index==2){
                           mSchool ="Business";
                       }
                       if (index==3){
                           mSchool ="Education";
                       }
                       if (index==4){
                           mSchool ="Computer Science";
                       }
                       if (index==5){
                           mSchool ="Theology";
                       }
                       if (index==6){
                           mSchool ="Medicine";
                       }
                       if (index==7){
                           mSchool ="Music";
                       }
                       if (index==8){
                           mSchool ="Post Graduate";
                       }

                       Intent intent = new Intent(getContext(), EventsSpecific.class);
                       intent.putExtra("school", mSchool);
                       startActivity(intent);
                       dialogInterface.dismiss();
                   }
               });
               builder.show();
           }
       });


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

            Query firstQuery = firebaseFirestore.collection("AllPosts").document("EventPosts").collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!Objects.requireNonNull(queryDocumentSnapshots).isEmpty()){

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
        Query secondQuery =  firebaseFirestore.collection("AllPosts").document("EventPosts").collection("Posts")
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
