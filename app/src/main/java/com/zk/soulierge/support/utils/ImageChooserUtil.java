package com.zk.soulierge.support.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.zk.soulierge.R;
import com.zk.soulierge.support.base.CoreApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/* Usage:

     1. declare provider in manifest

     <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="${applicationId}.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/image_path" />
        </provider>

     2. write permission and feature in manifest

     <uses-permission android:name="android.permission.CAMERA" />

     <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

     3. xml file for provider resource

     <paths>
        <files-path name="captured_image" path="Images/" />
     </paths>

     4. calling code

        ImageChooserUtil.openChooserDialog(coreFragment,"fileName");

     4. add in requesting activity

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case ImageChooserUtil.PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    ImageChooserUtil.openChooserDialog(getCoreFragment(), getViewModel().cvPhotoName);
                }
                break;
            case ImageChooserUtil.REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    ImageChooserUtil.startCameraIntent(getCoreFragment(), getViewModel().cvPhotoName);
                }
                break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

      5. Add onActivityResult

       @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case ImageChooserUtil.REQUEST_GALLERY:
                case ImageChooserUtil.REQUEST_CAMERA:
                    if (resultCode == RESULT_OK) {
                        new ImageChooserUtil.SaveImageTask(activity,
                                data,
                                requestCode,
                                String.valueOf(goodsFileName),
                                new ImageChooserUtil.SaveImageTask.FileSaveListener() {
                                    @Override
                                    public void fileSaved(File file) {

                                    }
                                }).execute();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

      */

public class ImageChooserUtil {

    public static final int REQUEST_GALLERY = 1235;
    public static final int REQUEST_CAMERA = 1234;
    public static final int PERMISSION_CAMERA = 1236;
    public static final int PERMISSION_WRITE_STORAGE = 1237;
    private static final String IMAGE_DIRECTORY = "Images";
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = ".fileprovider";
    private static String FILE_EXTENSION = ".jpg";
    public static int REQUEST_CODE;

    /**
     * @param fileName keep file name in field. this will be required when getting permission.
     */
    public static void openChooserDialog(final Activity activity, final String fileName, final int requestcode) {
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.lbl_choose_image)
                    .setPositiveButton(R.string.lbl_camera, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.CAMERA)) {
                                startCameraIntent(activity, fileName);
                            } else {
                                PermissionUtil.getPermission(activity,
                                        PermissionUtil.Permissions.CAMERA,
                                        PERMISSION_CAMERA+requestcode,
                                        PermissionUtil.PermissionMessage.CAMERA,
                                        null);
                            }
                        }
                    })
                    .setNegativeButton(R.string.lbl_gallery, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startGalleryIntent(activity, requestcode);
                        }
                    })
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            builder.show();
        } else {
            PermissionUtil.getPermission(activity,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE+requestcode,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    /**
     * @param fileName keep file name in field. this will be required when getting permission.
     */
    public static void openChooserDialog(final Fragment fragment, final String fileName, final int requestCode) {
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            REQUEST_CODE = requestCode;
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

            builder.setTitle(R.string.lbl_choose_image)
                    .setPositiveButton(R.string.lbl_camera, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.CAMERA)) {
                                startCameraIntent(fragment, fileName, requestCode + 1);
                            } else {
                                PermissionUtil.getPermission(fragment,
                                        PermissionUtil.Permissions.CAMERA,
                                       requestCode == 0 ? PERMISSION_CAMERA : requestCode,
                                        PermissionUtil.PermissionMessage.CAMERA,
                                        null);
                            }
                        }
                    })
                    .setNegativeButton(R.string.lbl_gallery, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startGalleryIntent(fragment, requestCode);
                        }
                    })
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            builder.show();
        } else {
            PermissionUtil.getPermission(fragment,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    /*requestCode == 0 ? */PERMISSION_WRITE_STORAGE /*: requestCode*/,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private static void startGalleryIntent(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode == 0 ? REQUEST_GALLERY : requestCode);
    }

    private static void startGalleryIntent(Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode == 0 ? REQUEST_GALLERY : requestCode);
    }

    public static void startCameraIntent(Activity activity, String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);

        if (!path.exists()) path.mkdirs();

        File image = new File(path, fileName + FILE_EXTENSION);

        Uri imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + CAPTURE_IMAGE_FILE_PROVIDER, image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(activity.getContentResolver(), "A photo", imageUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        activity.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static void startCameraIntent(Fragment fragment, String fileName, int requestCode) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);

        if (!path.exists()) path.mkdirs();

        File image = new File(path, fileName + FILE_EXTENSION);

        Uri imageUri = FileProvider.getUriForFile(fragment.getContext(), fragment.getContext().getPackageName() + CAPTURE_IMAGE_FILE_PROVIDER, image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(fragment.getContext().getContentResolver(), "A photo", imageUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    fragment.getContext().getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                fragment.getContext().grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        fragment.startActivityForResult(intent, requestCode == 0 ? REQUEST_CAMERA : requestCode);
    }

    public static File getGalleryImageFile(Intent data, ContentResolver resolver, String fileName) {
        if (data != null) {
            try {
                return saveImageToStorage(
                        MediaStore.Images.Media.getBitmap(resolver, data.getData()),
                        IMAGE_DIRECTORY,
                        fileName);
            } catch (IOException e) {
//                AppLog.log(false, "ImageChooserUtil " + "getGalleryImageFile: ", e);
                return null;
            }
        } else {
            return null;
        }
    }

    public static File getCameraImageFile(String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);
        if (!path.exists()) path.mkdirs();
        return new File(path, fileName + FILE_EXTENSION);
    }

    private static File saveImageToStorage(Bitmap finalBitmap, String path, String imageName) {
        String root = StorageUtils.createInternalDirectory();
        File myDir = new File(root + "/" + path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, imageName + FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
//            AppLog.log(false, "ImageChooserUtil " + "saveImageToStorage: ", e);
            return file;
        }
    }

    public static class SaveImageTask extends AsyncTask<Void, Void, File> {

        private Intent data;
        private int requestCode;
        private String fileName;
        private FileSaveListener listener;
        private boolean isCamera;

        public SaveImageTask(Intent data,
                             int requestCode,
                             String fileName,
                             boolean isCamera,
                             FileSaveListener listener) {
            this.data = data;
            this.requestCode = requestCode;
            this.fileName = fileName;
            this.isCamera = isCamera;
            this.listener = listener;
        }

        @Override
        protected File doInBackground(Void... pObjects) {
            File file = null;

            if (!isCamera) {
                file = ImageChooserUtil
                        .getGalleryImageFile(data,
                                CoreApp.INSTANCE.getContentResolver()
                                , String.valueOf(fileName));
            } else if (isCamera) {
                file = ImageChooserUtil.getCameraImageFile(String.valueOf(fileName));
            }

            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            if (listener != null)
                listener.fileSaved(file);
            super.onPostExecute(file);
        }

        public interface FileSaveListener {
            void fileSaved(File file);
        }
    }
}
