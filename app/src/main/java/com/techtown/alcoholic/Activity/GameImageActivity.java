package com.techtown.alcoholic.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.techtown.alcoholic.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;


//1. 사진찍고 돌아오면 내 화면에 보이기
//2. 맞았습니다. 틀렸습니다 보이기
//3.


public class GameImageActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "GameLog";
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    ImageView imageMyPic;
    Button btnTakePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_image);

        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();

        //FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //startCamera();
        imageMyPic = findViewById(R.id.pictureView);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);
    }


    File camera_file;
    public static final String FILE_NAME = "profile.jpg";
    public File getCameraFile() {
        File storageDir = getApplicationContext().getFilesDir();
        return new File(storageDir, FILE_NAME);
    }
    public void startCamera() {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //카메라는 파일이 저장될 위치를 명시하고, fileProvider에 authority를 명시해야한다.
            // 1. Manifest의 authority확인 2. Manifest안에 Provider에 존재하는 meta-data확인
            // 3. meta-data의 저장경로 확인(cache에 저장할것인지 EXTERNAL에 저장할 것인지)
            // file객체의 이름은 서버에서 처리할 것이기 때문에 똑같이 처리해도 된다.

            if (camera_file == null) {
                camera_file = getCameraFile();
            }
            Uri photoUri = FileProvider.getUriForFile(this, "org.techtown.alcoholic.fileprovider", camera_file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);

    }

    Bitmap bitmapPicture;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            //사진 이미지가 커서 업로드 되지 않는다면 주석처리한 코드로 설정
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//            Bitmap bitmap = BitmapFactory.decodeFile(camera_file.getAbsolutePath(), options);

            bitmapPicture = BitmapFactory.decodeFile(camera_file.getAbsolutePath());
            imageMyPic.setImageBitmap(bitmapPicture);
            //sendStringImg(bitmapPicture);

        }

    }
    String encodedImage;
    public void sendStringImg(Bitmap bitmapPicture){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d(TAG, "onActivityResult: CAMERA 이미지 String 확인 :" + encodedImage);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTakePicture:
                    startCamera();
                break;

        }
    }

}
