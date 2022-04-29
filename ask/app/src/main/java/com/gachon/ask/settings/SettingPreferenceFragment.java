package com.gachon.ask.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.gachon.ask.R;
import com.gachon.ask.util.Auth;
import com.gachon.ask.util.CloudStorage;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;


public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // 프로필 정보 수정 (사진)
        Preference changeInfoImagePref = findPreference("change_info_image");
        changeInfoImagePref.setSummary(prefs.getString("change_info_image", ""));
        changeInfoImagePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ImagePicker.Companion.with(SettingPreferenceFragment.this)
                        .crop()
                        .galleryMimeTypes(new String[]{"image/png", "image/jpg", "image/jpeg"})
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
                return true;
            }
        });


        // 프로필 닉네임 변경
        EditTextPreference changeInfoNickPref = findPreference("change_info_nickname");
        changeInfoNickPref.setSummary(prefs.getString("change_info_nickname", "")); // 저장해둔 닉네임 캐시 값 표시
        changeInfoNickPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue instanceof String) {
                    changeInfoNickPref.setSummary("현재 닉네임 : "+ (String) newValue);
                    Firestore.updateProfileNickName(Auth.getCurrentUser().getUid(), (String) newValue).addOnCompleteListener(documentTask -> {
                        // 성공했다면
                        if(documentTask.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.change_nickname_success, Toast.LENGTH_LONG).show();
                        }
                        // 실패했다면
                        else {
                            // 에러 토스트
                            Toast.makeText(getContext(), R.string.change_nickname_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    changeInfoNickPref.setSummary("");
                }
                return true;
            }
        });

        // 로그아웃 이벤트 리스너
        Preference myPref_logout = (Preference) findPreference("log_out");
        myPref_logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.setting_logout);
                builder.setMessage(R.string.setting_logout_msg);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Auth.signOut(getActivity());
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.create().show();
                return true;
            }
        });

        // 어플 정보
        Preference checkInfoDevelopers = findPreference("check_info_developers");
        checkInfoDevelopers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("개발자 정보"); // 제목
                builder.setMessage("강두원 : https://github.com/DW-K \n" +
                        "김소정 : https://github.com/Sojeong-Kim0915 \n" +
                        "노민하 : https://github.com/MinaRoh \n" +
                        "박태현 : https://github.com/Taehyuny \n");
                builder.setPositiveButton("확인",null);
                builder.create().show();
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            Bitmap fileBitmap = BitmapFactory.decodeFile(ImagePicker.Companion.getFilePath(data));
            CloudStorage.uploadProfileImg(Auth.getCurrentUser().getUid(),fileBitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task1) {
                    if(task1.isSuccessful()) {
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
                                                        Toast.makeText(getContext(), R.string.change_image_success, Toast.LENGTH_LONG).show();
                                                    }
                                                    // 실패했다면
                                                    else {
                                                        // 에러 토스트
                                                        Toast.makeText(getContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }else{
                                                // 에러 토스트
                                                Toast.makeText(getContext(), R.string.change_image_error, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else {
                                    Log.d(this.getClass().getSimpleName(), "Profile Image NULL");
                                }
                            }
                        });
                    }
                    else {
                        // fail
                    }
                }
            });
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }


}