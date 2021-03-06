package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gachon.ask.base.BaseActivity;
import com.gachon.ask.databinding.ActivityLoginBinding;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import static com.gachon.ask.util.Util.RC_SIGN_IN;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Button btnSign;
    private Button btnLogin;
    private SignInButton btnGoogleLogin;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected ActivityLoginBinding getBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSign = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        // Google Login Client
        mGoogleSignInClient = Auth.getGoogleSignInClient(this);

        // Firebase Auth Instance
        mAuth = Auth.getFirebaseAuthInstance();

        // SignUp button btn_signup
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request Login Window
                login();
            }
        });

        // Google Login Button
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request Login Window
                Intent signInIntent = mGoogleSignInClient.getSignInIntent(); //RC_SIGN_IN
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Request Code??? RC_SIGN_IN??? ???????????????
        if (requestCode == RC_SIGN_IN) {
            // ????????? ????????? ?????????
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // ????????? Result ?????????
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(this.getLocalClassName(), "firebaseAuthWithGoogle:" + account.getId());

                // ????????? ?????? ??????
                String emailAddress = account.getEmail();

                // ????????? ????????????
                if(emailAddress != null) {
                    firebaseAuthWithGoogle(account);
                }
                // ????????? ????????? ????????? ?????????
                else {
                    // ????????? ????????? ?????? ???????????? ??????
                    Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                    mGoogleSignInClient.signOut();
                }
            } catch (ApiException e) {
                Log.w(this.getClass().getSimpleName(), "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void login(){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(email.length() > 0 && password.length() > 0 && !email.equals("") && !password.equals("")){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        if(Auth.getCurrentUser().isEmailVerified()){ // ?????? ???????????? ??? ????????? ????????? ????????? ????????? ????????? ???????????? ??????! , (?????? ??????)
                            Log.d(TAG, "????????? ?????? ??????");
                            startToast("Success to login.");
                            startMainActivity();  // MainActivity??? ??????
                            finish();
                        }else{
                            Log.d(TAG, "????????? ?????? ??????");
                            startToast("????????? ?????? ??????, ?????? ???????????? ???????????? :(");
                            deleteUserDatabase(); // Fire-store?????? ?????? ??????
                            Auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() { // Authentification ????????? ?????? ??????
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "?????? ?????? ?????? ?????? ?????? ??????");
                                }
                            });
                        }
                    }else{
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        if(task.getException() != null){
                            startToast(task.getException().toString());
                        }
                    }
                }
            });
        }
    }

    /**
     * Google Login ????????? Firebase Auth??? ????????? ???????????????
     * @author Taehyun Park
     * @return Void
     * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        // Credential ????????? Firebase Auth
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            // Task ???????????????
            if (task.isSuccessful()) {
                Log.d(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:success");
                AdditionalUserInfo additionalUserInfo = task.getResult().getAdditionalUserInfo();

                // User ????????? ????????????, ?????? UserInfo??? ??? ??????????????????
                if(task.getResult().getUser() != null && additionalUserInfo != null) {
                    // ?????? ????????????
                    if (additionalUserInfo.isNewUser()) {
                        Log.d("LoginActivity", "New User Detected");

                        // DB ?????? ??????
                        createNewUserDatabase(task.getResult().getUser());
                    }
                    // ????????????
                    else {
                        Log.d("LoginActivity", "Already Registered User");

                        // MainActivity???
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
                // ???????????? Null?????????
            } else {
                // ????????? ????????? ????????????
                Log.w(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:failure", task.getException());
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                mAuth.signOut();
                mGoogleSignInClient.signOut();
            }
        });
    }

    /**
     * ?????? ????????? ??????
     * ????????? ???????????? DB ????????? ????????????
     * @author Taehyun Park
     */
    private void createNewUserDatabase(FirebaseUser user) {
        // ??? ?????? ?????? ??????
        Firestore.writeNewUser(user.getUid(), "?????????", user.getEmail(), user.getDisplayName(),
                0,0,0,0, 0,0,0,0,null,null,null)
                .addOnCompleteListener(documentTask -> {
                    // ???????????????
                    if(documentTask.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:success");
                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

                        // MainActivity???
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    // ???????????????
                    else {
                        Log.w(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:failure", documentTask.getException());
                        // ?????? ????????? ????????? ????????????
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        mGoogleSignInClient.signOut();
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startMainActivity(){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * ????????? ???????????? DB ????????? ????????????
     * @author Taehyun Park
     */
    private void deleteUserDatabase(){
        Firestore.deleteUser(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "firestore user ??????????????? ??????");
                }else{
                    Log.d(TAG, "firestore user ?????? ??????");
                }
            }
        });
    }
}
