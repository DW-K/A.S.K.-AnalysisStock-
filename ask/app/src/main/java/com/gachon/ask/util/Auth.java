package com.gachon.ask.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.UnicodeSetIterator;

import com.gachon.ask.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gachon.ask.R;

public class Auth {

    /**
     * FirebaseAuth Instance를 얻어온다
     * @return FirebaseAuth Instance
     * @author Taehyun Park
     */
    public static FirebaseAuth getFirebaseAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser() {
        return getFirebaseAuthInstance().getCurrentUser();
    }

    /**
     * Google Sign-In Client 객체를 얻어온다
     * @param context Context
     * @return GoogleSignInClient
     * @author Taehyun Park
     */
    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

    /**
     * 로그아웃 처리 후 LoginActivity로 이동
     *
     * @param activity 로그아웃을 하는 Activity
     * @author Taehyun Park
     */
    public static void signOut(Activity activity) {
        if (getCurrentUser() != null) {
            getFirebaseAuthInstance().signOut();
            getGoogleSignInClient(activity).signOut();
        }

        moveToLogin(activity);
    }

    /**
     * 모든 이전 작업들을 지우고 LoginActivity로 이동
     *
     * @param activity 작업하는 Activity
     */
    public static void moveToLogin(Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(loginIntent);
        activity.finishAndRemoveTask();
    }

}
