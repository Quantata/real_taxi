package mobileapp.taxiproject;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton cameraBtn;
    ImageButton galleryBtn;
    Button detectBtn;
    ImageView imageIv;
    Boolean permissionBoolean = false;

    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;

    Uri uri;
    String mCurrentPhotoPath;
    String galleryPath;
    String cameraPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        init();

        cameraBtn.setOnClickListener(new View.OnClickListener() { //카메라 버튼을 클릭했을 때
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File pictureFile = null;
                try {
                    pictureFile = createImageFile();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "pictureFile 에러", Toast.LENGTH_LONG).show();
                }

                if (pictureFile != null) {
                    uri = FileProvider.getUriForFile(MainActivity.this, "mobileapp.taxiproject.fileprovider", pictureFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() { //갤러리 버튼을 클릭했을 때
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() { //결과 화면으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("uri", uri);
                intent.putExtra("cameraPath", cameraPath);
                intent.putExtra("galleryPath", galleryPath);
                startActivity(intent);
                finish();
            }
        });
    }

    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TaxiProject_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TaxiProject/" + imageFileName);

        File directory_TaxiProject = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TaxiProject");
        if (!directory_TaxiProject.exists())
            directory_TaxiProject.mkdir();

        mCurrentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                try {
                    cameraPath = mCurrentPhotoPath;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=2;
                    Bitmap bitmap = BitmapFactory.decodeFile(cameraPath, options);
                    ExifInterface exif = new ExifInterface(String.valueOf(cameraPath));
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    bitmap = rotate(bitmap, exifDegree);
                    Glide.with(MainActivity.this).load(bitmap).into(imageIv);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case GALLERY_REQUEST_CODE:
                uri = data.getData();
                galleryPath = getPath(uri);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=2;
                    Bitmap bitmap = BitmapFactory.decodeFile(galleryPath, options);
                    ExifInterface exif = new ExifInterface(galleryPath);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    bitmap = rotate(bitmap, exifDegree);
                    Glide.with(MainActivity.this).load(bitmap).into(imageIv);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


    public void init() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() { //권한이 모두 허용되고나서 실행됨
                permissionBoolean = true;
                //Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) { //요청한 권한 중에서 거부당한 권한 목록 리턴
                permissionBoolean = false;
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("ARE U TAXI?를 실행하기 위해서는 카메라, 갤러리, 저장소 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .setGotoSettingButton(true)
                .check();

        detectBtn = (Button) findViewById(R.id.detectBtn);
        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        imageIv = (ImageView) findViewById(R.id.imageIv);
    }


}