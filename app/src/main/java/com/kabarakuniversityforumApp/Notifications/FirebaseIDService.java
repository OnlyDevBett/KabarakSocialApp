package com.kabarakuniversityforumApp.Notifications;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    public static final String FIREBASE_TOKEN = "token";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPrefUtil.getInstance(this).put(FIREBASE_TOKEN,refreshedToken);
      //  Toast.makeText(this, refreshedToken, Toast.LENGTH_SHORT).show();

    }
}
