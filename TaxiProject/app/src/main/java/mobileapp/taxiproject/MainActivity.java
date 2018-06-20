package mobileapp.taxiproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button cameraBtn;
    Button galleryBtn;
    Button resultBtn;
    ImageView imageIv;
    Boolean permissionBoolean = false;

    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;

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
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
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

        resultBtn.setOnClickListener(new View.OnClickListener() { //결과 화면으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
//                Uri takephoto = data.getData();
//                Log.d("uri", "uri " + data.getData());
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(takephoto, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                try {
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    Bitmap photo = BitmapFactory.decodeFile(picturePath);
//                    imageIv.setImageBitmap(photo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                break;

            case GALLERY_REQUEST_CODE:
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                try {
//                    String picturePath = cursor.getString(columnIndex);
//                    cursor.close();
//                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//                    imageIv.setImageBitmap(bitmap);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Uri uri = data.getData();
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
                .setRationaleMessage("'Taxi 맞니?'를 실행하기 위해서는 위치 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .setGotoSettingButton(true)
                .check();

        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        galleryBtn = (Button) findViewById(R.id.galleryBtn);
        resultBtn = (Button) findViewById(R.id.resultBtn);
        imageIv = (ImageView) findViewById(R.id.imageIv);

    }
}
