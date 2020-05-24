package com.kabarakuniversityforumApp.General_Issues;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kabarakuniversityforumApp.AccountCreation.LoginActivity;
import com.kabarakuniversityforumApp.General_Issues.NotificationsIn.CountDrawable;
import com.kabarakuniversityforumApp.General_Issues.NotificationsIn.NotificationsIn;
import com.kabarakuniversityforumApp.ProfileIssues.JoinigProfile;
import com.kabarakuniversityforumApp.ProfileIssues.Settings;
import com.kabarakuniversityforumApp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThePanel extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView mProfilePic;
    private FloatingActionButton mFloatingActionButton;
    private TextView mProfileName;
    private String user_id;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_panel);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        user_id = auth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mProfileName = (TextView)findViewById(R.id.tv_my_profilename) ;
        mProfilePic = (CircleImageView)findViewById(R.id.image_profile_main);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.floatPosting);
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThePanel.this, Settings.class);
                intent.putExtra("user_idpassed","no_nothing");
                startActivity(intent);
            }
        });
        firebaseFirestore.collection("Users").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("CheckResult")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String profileName = task.getResult().getString("ProfileName");
                        String imageUrl = task.getResult().getString("imageUrl");

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.centerCrop();
                        Glide.with(ThePanel.this).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(mProfilePic);
                        mProfileName.setText(profileName);
                    }
                }
            }
        });
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        firebaseFirestore.collection("Users").document(user_id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (Objects.requireNonNull(task.getResult()).exists()){
                                String rights = task.getResult().getString("rights");
                                String rank = task.getResult().getString("rank");

                                assert rank != null;
                                assert rights != null;
                                if (rank.equals("student") && rights.equals("not_admin")){
                                    tabLayout.addTab(tabLayout.newTab().setText("General"));
                                    tabLayout.addTab(tabLayout.newTab().setText("Events"));
                                    tabLayout.addTab(tabLayout.newTab().setText("Chats"));
                                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                                    final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                                    final PagerAdapter adapter = new PagerAdapter
                                            (getSupportFragmentManager(), tabLayout.getTabCount());
                                    viewPager.setAdapter(adapter);
                                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            viewPager.setCurrentItem(tab.getPosition());
                                        }
                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {
                                        }
                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {
                                        }
                                    });

                                }
                                else
                                    {
                                    tabLayout.addTab(tabLayout.newTab().setText("General"));
                                    tabLayout.addTab(tabLayout.newTab().setText("Events"));
                                    tabLayout.addTab(tabLayout.newTab().setText("Chats"));
                                    tabLayout.addTab(tabLayout.newTab().setText("Staff"));
                                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                                    final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                                    final PagerAdapter adapter = new PagerAdapter
                                            (getSupportFragmentManager(), tabLayout.getTabCount());
                                    viewPager.setAdapter(adapter);
                                    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            viewPager.setCurrentItem(tab.getPosition());
                                        }
                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {
                                        }
                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {
                                        }
                                    });

                                }
                            }else {
                                tabLayout.addTab(tabLayout.newTab().setText("General"));
                                tabLayout.addTab(tabLayout.newTab().setText("Events"));
                                tabLayout.addTab(tabLayout.newTab().setText("Chats"));
                                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                                final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                                final PagerAdapter adapter = new PagerAdapter
                                        (getSupportFragmentManager(), tabLayout.getTabCount());
                                viewPager.setAdapter(adapter);
                                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        viewPager.setCurrentItem(tab.getPosition());
                                    }
                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {
                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });

                            }
                        }
                        else {
                            tabLayout.addTab(tabLayout.newTab().setText("General"));
                            tabLayout.addTab(tabLayout.newTab().setText("Events"));
                            tabLayout.addTab(tabLayout.newTab().setText("Chats"));
                            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                            final PagerAdapter adapter = new PagerAdapter
                                    (getSupportFragmentManager(), tabLayout.getTabCount());
                            viewPager.setAdapter(adapter);
                            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    viewPager.setCurrentItem(tab.getPosition());
                                }
                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {
                                }
                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                }
                            });

                        }
                    }
                });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThePanel.this,Posting.class));
            }
        });
    }
    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        firebaseFirestore.collection("Users").document(user_id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String rights = task.getResult().getString("rights");
                            String rank = task.getResult().getString("rank");
                            getMenuInflater().inflate(R.menu.main_menu, menu);
                            MenuItem addstaff = menu.findItem(R.id.menu_lecturer);
                            MenuItem addAdmin = menu.findItem(R.id.menu_admin);
                            gettingNotificationCount(menu);

                            assert rank != null;
                            assert rights != null;
                            if (rank.equals("student") && rights.equals("not_admin"))
                            {
                                addAdmin.setVisible(false);
                                addstaff.setVisible(false);
                            }
                            if (rank.equals("staff") && rights.equals("not_admin")){
                                addAdmin.setVisible(false);
                                addstaff.setVisible(true);
                            }
                            if (rights.equals("admin")){
                                addAdmin.setVisible(true);
                                addAdmin.setVisible(true);
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ThePanel.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return true;


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.menu_join){
            startActivity(new Intent(ThePanel.this, JoinigProfile.class));
        }
        if (id == R.id.menu_notifications){
            startActivity(new Intent(ThePanel.this, NotificationsIn.class));
        }
        if (id== R.id.menu_lecturer){
           AlertDialog.Builder builder =  new AlertDialog.Builder(ThePanel.this);
                    builder.setTitle("Adding Staff");
                    builder.setMessage("Please enter official's Id Number");

            final EditText edtId = new EditText(ThePanel.this);
            edtId.setHint("Enter ID Number");
            builder.setView(edtId);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String idNumber  = edtId.getText().toString().trim();
                    if (idNumber==null){
                        Toast.makeText(ThePanel.this, "Enter valid id number", Toast.LENGTH_SHORT).show();
                    }else {
                        new AlertDialog.Builder(ThePanel.this)
                                .setMessage("Are you sure this is the correct Id Number: "+edtId.getText()+"?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        querry(idNumber);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        if (id == R.id.menu_admin){
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this);
                    builder.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET);
            builder.setTitle("Admin Privileges");
            builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
            builder.setCancelable(false);
            builder.setCornerRadius(10);
            builder.setHeaderView(R.layout.admin);
            builder.setTextColor(getResources().getColor(R.color.Black));
            builder .setMessage("Enter ID number bellow");
            final EditText footer = new EditText(ThePanel.this);
            footer.setPadding(10,10,10,10);
            footer.setHint("ID Number");
            footer.setHighlightColor(getResources().getColor(R.color.lightgray02));
            builder .setFooterView(footer);

            builder .addButton("Add" +
                            "", Color.parseColor("#8A4122"), Color.parseColor("#32CD32"), CFAlertDialog.CFAlertActionStyle.POSITIVE,
                            CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    final String idnumber = footer.getText().toString().trim();
                                    new AlertDialog.Builder(ThePanel.this)
                                            .setMessage("Are you sure this is the correct Id Number: "+footer.getText()+"?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    querryAdmin(idnumber);
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            });
            builder .addButton("Cancel", Color.parseColor("#ffffff"), Color.parseColor("#8A4122"), CFAlertDialog.CFAlertActionStyle.NEGATIVE,
                            CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
// Show the alert
            builder.show();
        }
        if (id == R.id.menu_logout){
            // Create Alert using Builder
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Logging Out")
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setCancelable(false)
                    .setCornerRadius(10)
                    .setHeaderView(R.layout.logout)
                    .setTextColor(getResources().getColor(R.color.Black))
                    .setMessage("Are you sure you want to logout from your account?")
                    .addButton("Yes", Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), CFAlertDialog.CFAlertActionStyle.POSITIVE,
                            CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    auth.signOut();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                }
                            })
                    .addButton("No", Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), CFAlertDialog.CFAlertActionStyle.NEGATIVE,
                            CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
// Show the alert
            builder.show();
        }
        if (id == R.id.menu_settings){
            Intent intent = new Intent(ThePanel.this, Settings.class);
            intent.putExtra("user_idpassed","no_nothing");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        gettingNotificationCount(menu);
        return super.onPrepareOptionsMenu(menu);
    }
    public void setCount(Context context, String count,Menu defaultMenu) {
        MenuItem menuItem = defaultMenu.findItem(R.id.menu_notifications);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        CountDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    private void gettingNotificationCount(final Menu menu){
        CollectionReference status = firebaseFirestore.collection("Users").document(auth.getCurrentUser().getUid()).collection("Notifications");

        Query query = status.whereEqualTo("status","unread");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (!queryDocumentSnapshots.isEmpty()){
                    int notificationCount = queryDocumentSnapshots.size();
                    setCount(ThePanel.this,""+notificationCount,menu);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void querry(final String id_number){
        firebaseFirestore.collection("Users").addSnapshotListener(ThePanel.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){

                    if (documentChange.getType()== DocumentChange.Type.ADDED){

                        final String documentId = documentChange.getDocument().getId();

                        CollectionReference reference = firebaseFirestore.collection("Users").document(documentId).collection("OtherDetails");

                        reference.whereEqualTo("id",id_number).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (!queryDocumentSnapshots.isEmpty()){
                                    for (DocumentChange documentChange1: queryDocumentSnapshots.getDocumentChanges()){
                                        if (documentChange1.getType()== DocumentChange.Type.ADDED){
                                            String Document = documentChange1.getDocument().getId();
                                            updateDetails(Document);
                                        }
                                    }
                                    updateDetails(documentId);
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private void querryAdmin(final String id_number){
        firebaseFirestore.collection("Users").addSnapshotListener(ThePanel.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){

                    if (documentChange.getType()== DocumentChange.Type.ADDED){

                        final String documentId = documentChange.getDocument().getId();

                        firebaseFirestore.collection("Users").document(documentId).collection("OtherDetails").document(documentId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){

                                            if (task.getResult().exists()){
                                                String id = task.getResult().getString("id");

                                                if (id_number.equals(id)){
                                                    adminAddition(documentId);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void updateDetails(String doc){

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("rank","staff");
        firebaseFirestore.collection("Users").document(doc).update(objectMap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ThePanel.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }
    private void adminAddition(String doc){

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("rights","admin");
        firebaseFirestore.collection("Users").document(doc).update(objectMap).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ThePanel.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }
}
