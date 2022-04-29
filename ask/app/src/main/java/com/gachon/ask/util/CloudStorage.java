package com.gachon.ask.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CloudStorage {

    /**
     * Profile 이미지 저장 폴더 Reference
     * Profile 이미지는 user/[User Id] 내에 저장되어야 함
     */
    public static StorageReference profileRef = getStorageInstance().getReference().child("user");

    /**
     * Storage Instance를 가져온다
     * @author Taehyun Park
     * @return FirebaseStorage Instance
     */
    public static FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    /**
     * 사용자 Profile Image 업로드
     * @author Taehyun Park
     * @param userId 사용자 Firebase ID
     * @param bitmap Image Bitmap
     * @return Task<UploadTask.TaskSnapshot> (업로드 Task)
     */
    public static Task<UploadTask.TaskSnapshot> uploadProfileImg(String userId, Bitmap bitmap) {
        StorageReference userProfileRef = profileRef.child(userId + "/profile.jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        return userProfileRef.putBytes(data);
    }

    /**
     * URL을 통해 이미지를 불러온다
     * @param URL 이미지 URL
     * @return Task<byte[]></byte[]>
     */
    public static Task<byte[]> getImageFromURL(String URL) {
        StorageReference profileReference = getStorageInstance().getReferenceFromUrl(URL);
        return profileReference.getBytes(1500000);
    }
}
