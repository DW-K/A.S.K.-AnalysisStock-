package com.gachon.ask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gachon.ask.base.BaseActivity;
import com.gachon.ask.databinding.ActivitySignupBinding;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends BaseActivity<ActivitySignupBinding> {

    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private EditText etNickname;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword_check;
    private Button btnSignup;
    private Button btnGoLogin;

    @Override
    protected ActivitySignupBinding getBinding() {
        return ActivitySignupBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etNickname = findViewById(R.id.et_nickname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPassword_check = findViewById(R.id.et_password_check);
        btnSignup = findViewById(R.id.btn_signup);
        btnGoLogin = findViewById(R.id.btn_go_login);

        // Initialize Firebase Auth
        mAuth = Auth.getFirebaseAuthInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginActivity();
            }
        });
    }

    private void signUp(){
        String nickname = etNickname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordCheck = etPassword_check.getText().toString();

        if (nickname.length()>0 && email.length()>0 && password.length()>0 && passwordCheck.length()>0) {

            if (password.equals(passwordCheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    // DB 생성 작업
                                    createNewUserDatabase(task.getResult().getUser(), nickname);
                                    // 유저 닉네임 가져오는 테스트

                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                        Log.d(TAG, "파이어베이스 연동 실패: "+task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                startToast("비밀번호가 일치하지 않습니다.");
            }
        }else {
            startToast("이메일 또는 비밀번호를 입력해 주세요.");
        }
    }

    /**
     * 새로운 사용자의 DB 정보를 생성한다
     * @author Taehyun Park
     */
    private void createNewUserDatabase(FirebaseUser user, String userNickName) {
        // 새 유저 정보 작성
        Firestore.writeNewUser(user.getUid(), user.getEmail(), userNickName,
                0,0,0,0, 0,0,0,0,null,null,null)
                .addOnCompleteListener(documentTask -> {
                    // 성공했다면
                    if(documentTask.isSuccessful()) {
                        startToast("회원가입에 성공하였습니다.");
                    }
                    // 실패했다면
                    else {
                        // 에러 메시지 띄우고 로그아웃
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity(){
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
    }


}
