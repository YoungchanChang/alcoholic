package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.techtown.alcoholic.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


//1. 사진찍고 돌아오면 내 화면에 보이기
//2. 맞았습니다. 틀렸습니다 보이기
//3.


public class GameImageActivity extends AppCompatActivity implements View.OnClickListener, AutoPermissionsListener {
    private static final String TAG = "GameLog";
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    ImageView imageMyPic;
    ImageView imageRight;

    TextView textSentence;
    Button btnTakePicture;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_image);
        AutoPermissions.Companion.loadAllPermissions(this, 101);

        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();

        //FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        //startCamera();
        imageMyPic = findViewById(R.id.pictureView);
        imageRight = findViewById(R.id.imageRight);
        textSentence = findViewById(R.id.textSentence);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);

    }



    public static final String FILE_NAME = "profile.jpg";


    public void takePicture() {
        if (file == null) {
            file = createFile();
        }

        Uri fileUri = FileProvider.getUriForFile(this,"org.techtown.alcoholic.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }

    private File createFile() {
        String filename = "capture.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);

        return outFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            if (bitmap != null) {
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;
                switch (orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                imageMyPic.setImageBitmap(rotatedBitmap);

                showProgressDialogue(this, "사진을 확인중입니다.");
                Log.d(TAG, "BeforeFirebase");

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(rotatedBitmap);
                FirebaseVisionCloudImageLabelerOptions options2 =
                     new FirebaseVisionCloudImageLabelerOptions.Builder()
                         .setConfidenceThreshold(0.7f)
                         .build();
                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                     .getCloudImageLabeler(options2);
                labeler.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                Log.d(TAG, "AfterFirebase" + labels);
                                for (FirebaseVisionImageLabel label: labels) {
                                    String text = label.getText();
                                    String entityId = label.getEntityId();
                                    float confidence = label.getConfidence();
                                    Log.d(TAG, "LabelImage "+text+" AND "+entityId+" AND "+confidence);
                                    if(text.equals("Cup")){
                                        Log.d(TAG, "SameImage");
                                        imageRight.setImageDrawable(getResources().getDrawable(R.drawable.check, getApplicationContext().getTheme()));
                                        textSentence.setText("사진이 일치합니다. 다른 사용자를 기다립니다.");
                                        btnTakePicture.setVisibility(View.INVISIBLE);
                                        dismissProgressDialogue();
                                        break;
                                    }else{
                                        Log.d(TAG, "DifferentImage");
                                        imageRight.setImageDrawable(getResources().getDrawable(R.drawable.wrong, getApplicationContext().getTheme()));
                                        textSentence.setText("사진이 일치하지 않습니다.");
                                        btnTakePicture.setText("다시 찍기");
                                        dismissProgressDialogue();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "FailureFirebase" + e);
                            }
                        });
            }
        }
    }
    ProgressDialog progressDialog;
    /**
     * 파일이 업로드 되는 동안 다이얼로그 창을 띄우고, 없애기 위한 메소드.
     */
    public void showProgressDialogue(Activity context, String dialougeText){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(dialougeText);
        progressDialog.show();
    }
    public void dismissProgressDialogue(){
        progressDialog.dismiss();
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTakePicture:
                    takePicture();
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
        //Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }
}
