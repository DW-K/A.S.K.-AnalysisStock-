package com.gachon.ask;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gachon.ask.base.BaseActivity;
import com.gachon.ask.databinding.ActivitySignupBinding;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.CloudStorage;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.Stock;
import com.gachon.ask.util.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SignUpActivity extends BaseActivity<ActivitySignupBinding> {
    private GoogleSignInClient mGoogleSignInClient;
    private static final int PICK_FROM_ALBUM = 1;
    private int isVerification = 0;
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private File tempFile;

    private EditText etNickname;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPassword_check;
    private Button btnSignup;
    private Button btnGoLogin;
    private Button btnGallery;
    private Button btnCamera;
    private Button btnEmailRight;

    private ImageView imageView;

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
        btnGallery = findViewById(R.id.btn_gallery);
        btnCamera = findViewById(R.id.btn_camera);
        btnEmailRight = findViewById(R.id.btn_email_right);

        imageView = findViewById(R.id.imageView_signup);
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        // Initialize Firebase Auth
        mAuth = Auth.getFirebaseAuthInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageView.getDrawable() == null || etNickname.getText().toString().equals("") || etEmail.getText().toString().equals("") ||
                        etPassword.getText().toString().equals("") || etPassword_check.getText().toString().equals("")){
                       // 회원가입 필드 중 하나라도 채워져 있지 않다면
                    startToast("모든 정보를 입력해주세요!");
                }else{ // 모든 정보가 입력되었다면 회원가입 진행
                    if(btnEmailRight.getText().toString().equals("○")){
                        signUp();
                    }else{
                        startToast("이메일 형식 확인을 해주세요!");
                    }
                }
            }
        });

        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginActivity();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlbum();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 0);
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG,"onTextChanged TEST!");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때 조치
                Log.d(TAG,"afterTextChanged TEST!");
                // 입력난에 변화가 있을 시 조치
                Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
                // 이메일 형식 확인
                if(pattern.matcher(etEmail.getText().toString()).matches()){
                    //이메일 맞음!
                    // startToast("올바른 형식입니다!");
                    btnEmailRight.setText("○");
                } else {
                    //이메일 아님!
                    // startToast("이메일 형식이 올바르지 않습니다!");
                    btnEmailRight.setText("X");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에 조치
            }
        });

        btnEmailRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이메일 발송 버튼 눌렀을 때, 이메일 링크를 클릭하여 인증을 완료해주세요!
                if(etEmail.getText().toString().equals("") || etEmail.getText().toString().equals(null)){
                    startToast("이메일을 입력해주세요!");
                }else{
                    Log.d(TAG,"이메일 필드 값 확인 : "+etEmail.getText().toString());
                }
            }
        });


        tedPermission();
    }

    private void signUp(){
        String nickname = etNickname.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordCheck = etPassword_check.getText().toString();
        String userGroup = "";

        boolean isGachonMail = Pattern.matches("^[a-zA-Z0-9._%+-]+@gachon.ac.kr$", email); // 가천대학교
        boolean isGatholicMail = Pattern.matches("^[a-zA-Z0-9._%+-]+@csj.ac.kr$", email); // 가톨릭상지대학교

        if (isGachonMail == true){
            userGroup = "가천대학교";
        }else if (isGatholicMail == true){
            userGroup = "가톨릭상지대학교";
        }else{
            userGroup = "무소속";
        }

        if (nickname.length()>0 && email.length()>0 && password.length()>0 && passwordCheck.length()>0) {

            if (password.equals(passwordCheck)) {
                final String user_group = userGroup;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    // 메일 전송
                                    emailSend();
                                    // DB 생성 작업
                                    createNewUserDatabase(task.getResult().getUser(), nickname, user_group);
                                    // 사진 업로드
                                    uploadCloudStorage();
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
    private void createNewUserDatabase(FirebaseUser user, String userNickName, String userGroup) {
        // 새 유저 정보 작성
        Firestore.writeNewUser(user.getUid(), userGroup, user.getEmail(), userNickName,
                0,0,0,0, 0,0,0,0,new ArrayList<Stock>(),null,null)
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

    /**
     * 회원가입 시 Cloud Storage에 이미지 업로드, FireStore Database에는 별도의 uri 업데이트 필요함!
     * @author Taehyun Park
     */
    private void uploadCloudStorage(){
        ImageView imageView = findViewById(R.id.imageView_signup);
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        /*
        if(imageView.getDrawable() == null){
            // 이미지를 올리지 않을 경우
            startToast("이미지를 선택하지 않으셨습니다!");
        }else{
            Bitmap bitmap = ((GlideBitmapDrawable) imageView.getDrawable()).getBitmap();
            CloudStorage.uploadProfileImg(Auth.getCurrentUser().getUid(),bitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task1) {
                    if (task1.isSuccessful()) {
                        startToast("프로필 이미지 업로드에 성공하였습니다.");
                        setProfileImage(); // firebase user collection의 이미지 URL필드 업데이트
                    } else {
                        startToast("프로필 이미지 업로드에 실패하였습니다.");
                    }
                }
            });
        }*/

        if(imageView.getDrawable() != null) { // 이미지를 선택했다면 CloudStorage에 저장
            Bitmap bitmap = ((GlideBitmapDrawable) imageView.getDrawable()).getBitmap();
            CloudStorage.uploadProfileImg(Auth.getCurrentUser().getUid(), bitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> task1) {
                    if (task1.isSuccessful()) {
                        startToast("프로필 이미지 업로드에 성공하였습니다.");
                        setProfileImage(); // firebase user collection의 이미지 URL필드 업데이트
                    } else {
                        startToast("프로필 이미지 업로드에 실패하였습니다.");
                    }
                }
            });
        }
    }

    private void setProfileImage(){
        Firestore.getUserData(Auth.getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                User user = task2.getResult().toObject(User.class);
                if(user != null) {
                    CloudStorage.profileRef.child(Auth.getCurrentUser().getUid() + "/profile.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task3) {
                            if(task3.isSuccessful()){
                                Firestore.updateProfileImage(Auth.getCurrentUser().getUid(),task3.getResult().toString()).addOnCompleteListener(documentTask ->{
                                    // 성공했다면
                                    if(documentTask.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), R.string.change_image_success, Toast.LENGTH_LONG).show();
                                    }
                                    // 실패했다면
                                    else {
                                        // 에러 토스트
                                        Toast.makeText(getApplicationContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                // 에러 토스트
                                Toast.makeText(getApplicationContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Log.d(this.getClass().getSimpleName(), "Profile Image NULL");
                }
            }
        });
    }

    // 회원가입 시 프로필 사진 관련 interaction
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) { // 갤러리를 호출한다면
            Uri photoUri = data.getData();
            Cursor cursor = null;
            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            try {
                setImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == 0){ // 카메라 클릭 시
            /*
            ImageView imageView = findViewById(R.id.imageView_signup);

            // Bundle로 데이터를 입력
            Bundle extras = data.getExtras();
            // Bitmap으로 컨버전
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // 이미지뷰에 Bitmap으로 이미지를 입력
            imageView.setImageBitmap(imageBitmap);*/
            startToast("준비중입니다.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startLoginActivity(){
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
    }

    // 이미지 선택하면 회원가입 프로필 이미지 뷰에 보여주는 함수
    private void setImage() throws Exception {
        ImageView imageView = findViewById(R.id.imageView_signup);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        // Uri uri = getImageUri(this,originalBm);
        // String imagePath = uri.getPath();

        // Glide 라이브러리를 통해 이미지 회전 문제 해결
        Glide.with(getApplicationContext())
                .load(tempFile.getAbsolutePath())
                .into(imageView);
    }

    private void emailSend(){
        Auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "이메일 전송 성공");
                    Toast.makeText(getApplicationContext(),"로그인을 위한 인증 이메일이 전송되었습니다. 확인해주세요!",Toast.LENGTH_LONG).show();
                }else{
                    Log.d(TAG, "이메일 전송 실패: "+task.getException().toString());
                    //startToast("이메일 전송을 실패하였습니다!");
                }
            }
        });
    }

    // 갤러리 접근 권한 요청 함수
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 비트맵->Uri 추출 함수
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
