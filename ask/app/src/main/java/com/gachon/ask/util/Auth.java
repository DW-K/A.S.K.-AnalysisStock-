package com.gachon.ask.util;

import android.content.Context;
import android.icu.text.UnicodeSetIterator;

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
     * @author Minjae Seon
     */
    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

}
