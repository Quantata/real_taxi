package mobileapp.taxiproject;

import android.Manifest;
import android.app.Activity;
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
import com.bumptech.glide.request.RequestOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobileapp.taxiproject.Application.ApplicationController;
import mobileapp.taxiproject.Network.NetworkService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    ImageButton cameraBtn;
    ImageButton galleryBtn;
    Button detectBtn;
    ImageView imageIv;

    NetworkService service;

    Boolean permissionBoolean = false;
    String mCurrentPhotoPath;

    final int REQ_CODE_SELECT_IMAGE = 100;
    //private final int CAMERA_CODE = 1111;
    String imgUrl = "";
    Uri data;


    Boolean getPermissionBoolean = false;
    private final int CAMERA_REQUEST_CODE = 1;
    Uri uri;
    //    String getmCurrentPhotoPath;
    String galleryPath;
    String cameraPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        init();

        service = ApplicationController.getInstance().getNetworkService();

//        detectBtn = (Button) findViewById(R.id.detectBtn);
//        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
//        imageIv = (ImageView) findViewById(R.id.imageIv);
//        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);


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
                .setRationaleMessage("'ARE U TAXI?'를 실행하기 위해서는 카메라, 갤러리, 저장소 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한]에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .setGotoSettingButton(true)
                .check();


        cameraBtn.setOnClickListener(new View.OnClickListener() {
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


        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestBody auth_key = RequestBody.create(MediaType.parse("multipart/form-data"), "YWFhYWE6YmJiYmI=");
                MultipartBody.Part body;

                if (imgUrl == "") {
                    body = null;
                } else {
                    // 이미지 리사이징

                    Log.d("Log", "이미지 리사이징");
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    InputStream in = null; // here, you need to get your context.
                    try {
                        in = getContentResolver().openInputStream(data);
                        Log.d("Log", "in : " + data);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Log", "in : error");
                    }

                    // 이미지 압축
                    Bitmap bitmap = BitmapFactory.decodeStream(in, null, options); // InputStream 으로부터 Bitmap 생성
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    Log.d("Log", "bitmap : " + bitmap);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    // 압축 옵션( JPEG, PNG ) , 품질 설정 ( 0 - 100까지의 int형 ), 압축된 바이트 배열을 담을 스트림
                    RequestBody photoBody = RequestBody.create(MediaType.parse("user_image/jpg"), baos.toByteArray());

                    File photo = new File(imgUrl); // 가져온 파일의 이름 가져옴
                    Log.d("Log", "photo : " + photo);

                    // MultipartBody.Part 실제 파일의 이름을 보내기 위해 사용
                    body = MultipartBody.Part.createFormData("user_image", photo.getName(), photoBody);
                    Log.d("Log", "body : " + body);
                }

                retrofit2.Call<Lpr_result> lpr_resultCall = service.getTexiResult(auth_key, body);
                lpr_resultCall.enqueue(new Callback<Lpr_result>() {
                    @Override
                    public void onResponse(Call<Lpr_result> call, Response<Lpr_result> response) {
                        Log.d("Log", "nonono");
                        if (response.isSuccessful()) {
                            Log.d("Log", "nonono2222");
                            Log.d("Log", response.body().lpr_result.plate_num);

                            Toast.makeText(getApplicationContext(), response.body().lpr_result.plate_num, Toast.LENGTH_SHORT).show();

                            String first_text = response.body().lpr_result.plate_num.substring(0, 2);
                            if (first_text.equals("서울")) {
                                int num = Integer.parseInt(response.body().lpr_result.plate_num.substring(2, 4));
                                String text = response.body().lpr_result.plate_num.substring(4, 5);

                                if (num >= 0 && num <= 69) {
                                    if (text.equals("아") || text.equals("바") || text.equals("사") || text.equals("자")) {
                                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                        intent.putExtra("isTaxi", "ok");
                                        intent.putExtra("galleryPath", galleryPath);
                                        intent.putExtra("cameraPath", cameraPath);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                        intent.putExtra("isTaxi", "no");
                                        intent.putExtra("galleryPath", galleryPath);
                                        intent.putExtra("cameraPath", cameraPath);
                                        startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putExtra("isTaxi", "no");
                                    intent.putExtra("galleryPath", galleryPath);
                                    intent.putExtra("cameraPath", cameraPath);
                                    startActivity(intent);
                                }
                            } else {
                                int num = Integer.parseInt(response.body().lpr_result.plate_num.substring(0, 2));
                                String text = response.body().lpr_result.plate_num.substring(2, 3);
                                if (num >= 0 && num <= 69) {
                                    if (text.equals("아") || text.equals("바") || text.equals("사") || text.equals("자")) {
                                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                        intent.putExtra("isTaxi", "ok");
                                        Log.d("Log", "이미지 테스트" + galleryPath);
                                        intent.putExtra("galleryPath", galleryPath);
                                        intent.putExtra("cameraPath", cameraPath);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                        intent.putExtra("isTaxi", "no");

                                        Log.d("Log", "이미지 테스트" + galleryPath);
                                        intent.putExtra("galleryPath", galleryPath);
                                        intent.putExtra("cameraPath", cameraPath);
                                        startActivity(intent);

                                    }
                                } else {
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putExtra("isTaxi", "no");

                                    Log.d("Log", "이미지 테스트" + galleryPath);
                                    intent.putExtra("galleryPath", galleryPath);
                                    intent.putExtra("cameraPath", cameraPath);
                                    startActivity(intent);
                                }
                            }


                        }


                    }

                    @Override
                    public void onFailure(Call<Lpr_result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }


    // 선택된 이미지 가져오기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SELECT_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {
                try {

                    Log.d("Log", "갤러리 들어옴");
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());
                    this.data = data.getData();

                    Log.d("Log", "갤러리에서 this.data : " + this.data);
                    galleryPath = getPath(this.data);

                    Glide.with(MainActivity.this)
                            .load(this.data)
                            .apply(new RequestOptions()
                                    .centerCrop())//.circleCrop()
                            .into(imageIv);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                imgUrl = "";
            }
        } else {

            try {

                Log.d("Log", "카메라 들어옴..!!!!");
                Bitmap bitmap;
                cameraPath = mCurrentPhotoPath;
                    bitmap = BitmapFactory.decodeFile(cameraPath);
                Log.d("Log", "mcurrent : " + mCurrentPhotoPath);

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ExifInterface exif = new ExifInterface(String.valueOf(cameraPath));
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    bitmap = rotate(bitmap, exifDegree);
                    imageIv.setImageBitmap(bitmap);
//                this.data = Uri.parse(cameraPath);
                this.data = uri;
                imgUrl = cameraPath;


                Log.d("Log", "imgUrl : " + imgUrl);

                Log.d("Log", "data : " + this.data);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    // 선택된 이미지 파일명 가져오기
    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        imgUrl = imgPath;
        Log.d("Log", "imgUrl : " + imgUrl);
        return imgName;
    }


    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TaxiProject_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TaxiProject/" + imageFileName);

        File directory_TaxiProject = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TaxiProject");
        if (!directory_TaxiProject.exists())
            directory_TaxiProject.mkdir();

//        imageUri = Uri.fromFile(storageDir);
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

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap  비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
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