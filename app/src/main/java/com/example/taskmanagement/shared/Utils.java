package com.example.taskmanagement.shared;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class Utils {

    public static String getFileExtension( Uri uri , ContentResolver contentResolver ){
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
