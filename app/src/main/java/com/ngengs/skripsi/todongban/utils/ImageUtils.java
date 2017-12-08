/*==============================================================================
 Copyright (c) 2017 Rizky Kharisma (@ngengs)


 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 =============================================================================*/

package com.ngengs.skripsi.todongban.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.ngengs.skripsi.todongban.BuildConfig;
import com.ngengs.skripsi.todongban.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static Bitmap getThumbnail(@NonNull ContentResolver contentResolver,
                                      @NonNull String path)
            throws Exception {

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                              new String[]{MediaStore.MediaColumns._ID},
                                              MediaStore.MediaColumns.DATA + "=?",
                                              new String[]{path},
                                              null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id,
                                                             MediaStore.Images.Thumbnails.MICRO_KIND,
                                                             null);
        }

        if (cursor != null) cursor.close();
        return null;

    }

    public static Intent openGallery() {
        return new Intent(Intent.ACTION_PICK,
                          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public static Intent openCamera(@NonNull Context context, @NonNull File image) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(context,
                                                  BuildConfig.APPLICATION_ID + ".provider",
                                                  image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        return intent;
    }

    public static Object[] handleImageGallery(@NonNull Context context, @NonNull Intent data)
            throws Exception {
        Object[] imageResult = null;
        Uri selectedImage = data.getData();
        imageResult = handleImageGallery(context, selectedImage);
        return imageResult;
    }

    public static Object[] handleImageGallery(@NonNull Context context, @NonNull Uri selectedImage)
            throws Exception {
        Bitmap bitmapImage = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                                                           filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            bitmapImage = getThumbnail(context.getContentResolver(), imgDecodableString);

            return new Object[]{imgDecodableString, bitmapImage};
        } else {
            return null;
        }
    }

    public static Bitmap handleImageUri(@NonNull Context context, @NonNull Uri selectedImage)
            throws Exception {
        return getThumbnail(context.getContentResolver(), selectedImage.toString());
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        String appDirectoryName = context.getString(R.string.appNameShort);
        File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);
        //noinspection ResultOfMethodCallIgnored
        imageRoot.mkdir();

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                imageRoot      /* directory */
        );
    }

    public static Bitmap handleImageCamera(@NonNull String imagePath, int imageSize) {
        Log.d(TAG, "handleImageCamera: " + imagePath);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / imageSize, photoH / imageSize);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    /**
     * Notify the picture to the Galerry
     *
     * @param context
     *         Context of activity
     * @param picturePath
     *         Location of picture
     */
    public static void notifyGallery(Context context, String picturePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(picturePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void deleteFailedImage(String picutrePath) {
        File file = new File(picutrePath);
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }
}
