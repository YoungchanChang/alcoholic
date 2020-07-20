package com.techtown.alcoholic.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.techtown.alcoholic.SingleToneSocket;
import com.techtown.alcoholic.SocketSendThread;
import com.techtown.alcoholic.TimerThread;

import org.json.JSONException;
import org.json.JSONObject;

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
    LinearLayout linearLank;
    TextView textSentence,rankOne,rankTwo,rankThree;
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
        rankOne= findViewById(R.id.rankOne);
        rankTwo= findViewById(R.id.rankTwo);
        rankThree= findViewById(R.id.rankThree);
        linearLank =findViewById(R.id.LinearRank);
        imageMyPic = findViewById(R.id.pictureView);
        imageRight = findViewById(R.id.imageRight);
        textSentence = findViewById(R.id.textSentence);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);



        //값 초기화
        startTime = System.currentTimeMillis();
        Log.d(TAG, "StartTime "+ startTime);
        handler = getHandler();
        timerThread = new TimerThread(timeLimit, handler);
        timerThread.start();
        textTimeLeft = findViewById(R.id.textTimeLeft);
        textTimeLeft.setText(timeLimit+"초 남았습니다");
        socketSendThread = socketSendThread.getInstance(getString(R.string.server_ip), SingleToneSocket.getInstance());
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

                                        //조건이 만족한다면, 서버에 시간초 정보를 보낸다.
                                        //시간초 관련 뷰는 invisible처리한다.
                                        //그리고 10초 완료됬을 시에 데이터를 보내지 않게 해야한다.
                                        endTime = System.currentTimeMillis()- startTime;
                                        isOver = true;
                                        textTimeLeft.setVisibility(View.INVISIBLE);
                                        //TODO
                                        //데이터를 서버에 보내야 한다.
                                        String request = "gameResult:"+endTime;
                                        socketSendThread.sendData(request);
                                        Log.d(TAG, "endTime " + endTime);
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






    //0.
    //1. 게임 시간이 0이 되면 시간초 관련 데이터를 서버에 보내
    //2. 특정 조건을 완료하면 데이터를 서버에 보내
    //2-1. 완료했으면 시간초가 멈춰야되. (멈추는 것 처럼 보여야되)
    //내가 찍으면 시간초 관련된 뷰가 invisible처리
    //3. 서버에서 3개의 관련 데이터가 왔을 때 다이얼로그 띄워준다.
    //0초가 됬을 때는 안 보내져야 한다.


    //게임 시작 기록하는 변수
    long startTime;
    //게임 끝났을 시간을 기록하는 변수, startTime과의 초가 게임 시간 차이이다.
    long endTime;

    //시간초가 실제로 내려가는 쓰레드
    TimerThread timerThread;
    //몇초인지 보여주는 뷰 ( Layout에 있어야 한다 )
    TextView textTimeLeft;
    //핸들러(쓰레드의 값을 보여주는 핸들러 객체)
    Handler handler;
    //내가 지정하고 싶은 시간
    int timeLimit = 15;

    //특정조건 만족했을 시에 서버에 정보를 보내주지 않게 하는 변수
    boolean isOver = false;
    SocketSendThread socketSendThread;
    @SuppressLint("HandlerLeak")
    private Handler getHandler() {
        return new Handler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Log.i(TAG, "handleMessage: 데이테 전달받음"+data.toString());
                switch (data.getString("isFrom")) {
                    case "timerThread":
                        //타이머스레드에서 데이터 받을 때

                        Log.d(TAG, "TimeLeft " + timeLimit);
                            if(data.getInt("second")==0) {
                                textTimeLeft.setText("종료되었습니다");

                                Log.d(TAG, "TimeLeftYet " + timeLimit);
                                //게임결과 전송
                                if(!isOver) {
                                    Log.d(TAG, "TimeLeftEnd " + timeLimit);
                                    //count변수 15초가 흘러간다.

                                    String request = "gameResult:"+Integer.toString(timeLimit);
                                    socketSendThread.sendData(request);
                                }

                            }else {
                                textTimeLeft.setText(data.getInt("second")+"초 남았습니다");
                            }


                        break;
                    case "receiveThread":
                        //소켓수신 스레드에서 데이터 받을 때
                        String value = data.getString("value");

                        Log.d("스위치","스위치 작동");
                        try {
                            JSONObject jsonObject = new JSONObject(value);

                            String token[] = jsonObject.getString(0+"").split(":");
                            String token1[] = jsonObject.getString(1+"").split(":");
                            String token2[] = jsonObject.getString(2+"").split(":");
                            //token 0 유저 닉네임
                            //token 1 결과값

                            String userNickname = token[0];
                            String userScore = token[1];
                            long score = Integer.parseInt(userScore);
                            String userNickname1 = token1[0];
                            String userScore1 = token1[1];
                            long score1 = Integer.parseInt(userScore1);
                            String userNickname2 = token2[0];
                            String userScore2 = token2[1];
                            long score2 = Integer.parseInt(userScore2);

                            if(score<score1&&score1<score2) {
                                rankOne.setText("1등:"+userNickname2);
                                rankTwo.setText("2등:"+userNickname1);
                                rankThree.setText("3등:"+userNickname);
                            } else if(score<score2&&score2<score1) {
                                rankOne.setText("1등:"+userNickname1);
                                rankTwo.setText("2등:"+userNickname2);
                                rankThree.setText("3등:"+userNickname);
                            } else if(score1<score&&score<score2) {
                                rankOne.setText("1등:"+userNickname2);
                                rankTwo.setText("2등:"+userNickname);
                                rankThree.setText("3등:"+userNickname1);
                            } else if(score1<score2&&score2<score) {
                                rankOne.setText("1등:"+userNickname);
                                rankTwo.setText("2등:"+userNickname2);
                                rankThree.setText("3등:"+userNickname1);

                            } else if(score2<score&&score<score1) {
                                rankOne.setText("1등:"+userNickname1);
                                rankTwo.setText("2등:"+userNickname);
                                rankThree.setText("3등:"+userNickname2);
                            } else if(score2<score1&&score1<score) {
                                rankOne.setText("1등:"+userNickname);
                                rankTwo.setText("2등:"+userNickname1);
                                rankThree.setText("3등:"+userNickname2);
                            }
                            linearLank.setVisibility(View.VISIBLE);


                            //결과값 보여줌




                        }catch (JSONException e){ e.printStackTrace();}
                        break;
                    default:
                        Log.i(TAG, "handleMessage: 아무것도 클릭되지 않음");
                        break;
                }
            }
        };
    }



}
