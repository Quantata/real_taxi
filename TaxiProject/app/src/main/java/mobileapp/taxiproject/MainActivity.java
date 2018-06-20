package mobileapp.taxiproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
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
                startActivity(intent);
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

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageIv.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case GALLERY_REQUEST_CODE:
                uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageIv.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void init() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() { //권한이 모두 허용되고나서 실행됨
                permissionBoolean = true;
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) { //요청한 권한 중에서 거부당한 권한 목록 리턴
                permissionBoolean = false;
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("'ARE U TAXI?'를 실행하기 위해서는 카메라, 갤러리, 저장소 권한이 필요합니다.")
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
