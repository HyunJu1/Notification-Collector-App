package com.example.hyunju.notification_collector.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    private FileUtils() {}

    static final String TAG = "FileUtils";
    private static final boolean DEBUG = true;

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";

    public static String getExtension(String uri) {
        if(uri == null) return null;

        int dot = uri.lastIndexOf(".");
        if(dot >= 0) return uri.substring(dot);
        else return "";
    }

    public static boolean isLocal(String url) {
        if(url != null && !url.startsWith("http://") && !url.startsWith("https://")) return true;
        return false;
    }

    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    public static Uri getUri(File file) {
        if(file != null) return Uri.fromFile(file);
        return null;
    }

    public static File getPathWithoutFilename(File file) {
        if(file != null) {
            if(file.isDirectory()) {
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if(pathwithoutname.endsWith("/")) pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);

                return new File(pathwithoutname);
            }
        }
        return null;
    }

    public static String getMimeType(File file) {
        String extension = getExtension(file.getName());

        if(extension.length() > 0) return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    public static String getMimeType(Context context, Uri uri) {
        File file = new File(getPath(context, uri));
        return getMimeType(file);
    }

    public static String getPath(final Context context, final Uri uri) {
        if(DEBUG) Log.e("gg", "authority : " + uri.getAuthority() + ", fragment : " + uri.getFragment() + ", port : " + uri.getPort()
         + ", query: " + uri.getQuery() + ", schema: " + uri.getScheme() + ", host: " + uri.getHost()
         + ", segment : " + uri.getPathSegments().toString());

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            if(isLocalStorageDocument(uri)) return DocumentsContract.getDocumentId(uri);
            if(isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if(isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri;

                try {
                     contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } catch(NumberFormatException e) {
                     contentUri = Uri.parse("content://downloads/public_downloads" + id);
                }

                return contentUri.getPath();
//                return getDataColumn(context, contentUri, null, null);
            } else if(isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if(isGoogleDriveUri(uri)) {
                Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String name = (returnCursor.getString(nameIndex));
                String size = (Long.toString(returnCursor.getLong(sizeIndex)));
                File file = new File(context.getCacheDir(), name);
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    int read = 0;
                    int maxBufferSize = 1 * 1024 * 1024;
                    int bytesAvailable = inputStream.available();

                    //int bufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    final byte[] buffers = new byte[bufferSize];
                    while ((read = inputStream.read(buffers)) != -1) {
                        outputStream.write(buffers, 0, read);
                    }
                    Log.e("File Size", "Size " + file.length());
                    inputStream.close();
                    outputStream.close();
                    Log.e("File Path", "Path " + file.getPath());
                    Log.e("File Size", "Size " + file.length());
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
                return file.getPath();
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
//
    }



//    public static boolean isLocalStorageDocument(Uri uri) {
//        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
//    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) ||
                "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static Intent createGetContentIntent() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }
}
