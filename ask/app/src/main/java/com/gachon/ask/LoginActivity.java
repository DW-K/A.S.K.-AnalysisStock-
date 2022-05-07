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

        // Request Code가 RC_SIGN_IN과 일치한다면
        if (requestCode == RC_SIGN_IN) {
            // 넘어온 정보로 로그인
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // 로그인 Result 가져옴
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(this.getLocalClassName(), "firebaseAuthWithGoogle:" + account.getId());

                // 이메일 정보 로드
                String emailAddress = account.getEmail();

                // 이메일 정상이면
                if(emailAddress != null) {
                    firebaseAuthWithGoogle(account);
                }
                // 이메일 정보가 정상이 아니면
                else {
                    // 토스트 띄우고 구글 로그아웃 처리
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
                        if(Auth.getCurrentUser().isEmailVerified()){ // 현재 회원가입 한 계정의 이메일 인증이 제대로 되어야 로그인이 가능! , (지울 예정)
                            Log.d(TAG, "이메일 인증 성공");
                            startToast("Success to login.");
                            startMainActivity();  // MainActivity로 이동
                            finish();
                        }else{
                            Log.d(TAG, "이메일 인증 실패");
                            startToast("이메일 인증 실패, 다시 회원가입 해주세요 :(");
                            deleteUserDatabase(); // Fire-store에서 유저 삭제
                            Auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() { // Authentification 에서도 유저 삭제
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "메일 인증 못한 유저 삭제 완료");
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
     * Google Login 정보를 Firebase Auth로 넘겨서 로그인한다
     * @author Taehyun Park
     * @return Void
     * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        // Credential 정보로 Firebase Auth
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            // Task 성공이라면
            if (task.isSuccessful()) {
                Log.d(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:success");
                AdditionalUserInfo additionalUserInfo = task.getResult().getAdditionalUserInfo();

                // User 정보가 정상이고, 추가 UserInfo도 잘 가져와졌다면
                if(task.getResult().getUser() != null && additionalUserInfo != null) {
                    // 신규 유저라면
                    if (additionalUserInfo.isNewUser()) {
                        Log.d("LoginActivity", "New User Detected");

                        // DB 생성 작업
                        createNewUserDatabase(task.getResult().getUser());
                    }
                    // 아니라면
                    else {
                        Log.d("LoginActivity", "Already Registered User");

                        // MainActivity로
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
                // 하나라도 Null이라면
            } else {
                // 토스트 띄우고 로그아웃
                Log.w(LoginActivity.this.getLocalClassName(), "firebaseAuthWithGoogle:failure", task.getException());
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                mAuth.signOut();
                mGoogleSignInClient.signOut();
            }
        });
    }

    /**
     * 구글 로그인 용도
     * 새로운 사용자의 DB 정보를 생성한다
     * @author Taehyun Park
     */
    private void createNewUserDatabase(FirebaseUser user) {
        // 새 유저 정보 작성
        Firestore.writeNewUser(user.getUid(), "무소속", user.getEmail(), user.getDisplayName(),
                0,0,0,0, 0,0,0,0,null,null,null)
                .addOnCompleteListener(documentTask -> {
                    // 성공했다면
                    if(documentTask.isSuccessful()) {
                        Log.d(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:success");
                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_LONG).show();

                        // MainActivity로
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    // 실패했다면
                    else {
                        Log.w(LoginActivity.this.getLocalClassName(), "createNewUserDatabase:failure", documentTask.getException());
                        // 에러 메시지 띄우고 로그아웃
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
     * 새로운 사용자의 DB 정보를 삭제한다
     * @author Taehyun Park
     */
    private void deleteUserDatabase(){
        Firestore.deleteUser(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "firestore user 성공적으로 삭제");
                }else{
                    Log.d(TAG, "firestore user 삭제 실패");
                }
            }
        });
    }
}
