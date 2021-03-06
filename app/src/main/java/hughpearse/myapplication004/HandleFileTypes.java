package hughpearse.myapplication004;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class HandleFileTypes extends AppCompatActivity {

    private static final String TAG = "TTS-HandleFileTypes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_extract);
        Bundle bundle = this.getIntent().getExtras();
        Uri uri = bundle.getParcelable("fileUri");
        Log.i(TAG, "Handling file: " + uri.toString());

        String path = uri.getPath();
        String fileName = getPath(getApplicationContext(), uri);
        Log.i(TAG, "fileName: " + fileName);
        int dot = fileName.lastIndexOf(".");
        String extension = fileName.substring(dot + 1);
        Log.i(TAG, "Extension: " + extension);

        ArrayList<String> extractedText = new ArrayList<String>();

        if(extension.toLowerCase().contains("txt")){
            Log.i(TAG, "Launching txt extractor");
            ExtractFromTxt extractor = new ExtractFromTxt();
            extractedText = extractor.extract(fileName);
        } else if (extension.toLowerCase().contains("pdf")) {

        }

        Intent intent = getIntent();
        intent.putStringArrayListExtra("sentences", extractedText);
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * Handle different storage locations in Android
     *
     * References
     * https://stackoverflow.com/questions/36128077/android-opening-a-file-with-action-get-content-results-into-different-uris
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            Log.i(TAG, "Getting path for DocumentProvider");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.i(TAG, "Getting path for ExternalStorageDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.i(TAG, "docId: " + docId);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Log.i(TAG, "Getting path for primary volume");
                    String path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    Log.i(TAG, path);
                    return path;
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.i(TAG, "Getting path for DownloadsProvider");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                Log.i(TAG, "contentUri: " + contentUri);
                String dataColumn = getDataColumn(context, contentUri, null, null);
                Log.i(TAG, "dataColumn: " + dataColumn);
                return dataColumn;
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                Log.i(TAG, "Getting path for MediaProvider");
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
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "Getting path for MediaStore");
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "Getting path for File");
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
