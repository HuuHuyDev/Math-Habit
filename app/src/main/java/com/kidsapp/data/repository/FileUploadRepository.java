package com.kidsapp.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Repository để upload file (ảnh/video)
 * 
 * NOTE: Upload endpoints riêng đã bị xóa từ BE.
 * Sử dụng TaskRepository.submitTaskProofWithFile() để upload minh chứng task.
 * File này chỉ còn helper methods để convert URI to File.
 */
public class FileUploadRepository {
    
    private static final String TAG = "FileUploadRepository";
    
    private Context context;
    
    public FileUploadRepository(Context context) {
        this.context = context;
    }
    
    /**
     * Convert URI to File
     * Sử dụng method này để chuẩn bị file trước khi upload
     */
    public File getFileFromUri(Uri uri, String filename) {
        try {
            File tempFile = new File(context.getCacheDir(), filename);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            
            if (inputStream == null) {
                return null;
            }
            
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            
            return tempFile;
            
        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to File", e);
            return null;
        }
    }
    
    /**
     * Get file extension from URI
     */
    public String getFileExtension(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                return ".jpg";
            } else if (mimeType.startsWith("video/")) {
                return ".mp4";
            }
        }
        return "";
    }
    
    /**
     * Check if URI is image
     */
    public boolean isImage(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    /**
     * Check if URI is video
     */
    public boolean isVideo(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("video/");
    }
    
    public interface UploadCallback {
        void onSuccess(String url, String type);
        void onError(String error);
    }
}
