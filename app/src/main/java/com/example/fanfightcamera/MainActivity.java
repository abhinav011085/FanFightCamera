package com.example.fanfightcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.example.fanfightcamera.helper.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Context context = this;
    private CameraKitView cameraKitView;
    ImageButton capture;
    ImageView ivFlash;
    LinearLayout llGallery;

    int PICK_IMAGE = 101;
    boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraKitView = findViewById(R.id.camera);
        capture = findViewById(R.id.capture);
        llGallery = findViewById(R.id.ll_gallery);
        ivFlash = findViewById(R.id.flash);

        cameraKitView.setPermissions(CameraKitView.PERMISSION_STORAGE);
        cameraKitView.requestPermissions(this);
        cameraKitView.setErrorListener(new CameraKitView.ErrorListener() {
            @Override
            public void onError(CameraKitView cameraKitView, CameraKitView.CameraException e) {
                e.printStackTrace();
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        final File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                                Bitmap croppedBitmap = cropBitmapCenter(bitmap);

                                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 0, blob);
                                byte[] bitmapdata = blob.toByteArray();

                                try {
                                    FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                                    outputStream.write(bitmapdata);
                                    outputStream.close();
                                } catch (java.io.IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Intent intent = new Intent(context, PhotoPreviewActivity.class);
                        intent.putExtra("path", savedPhoto.getPath());
                        startActivity(intent);
                    }
                });
            }
        });

        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });

        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFlashOn) {
                    ivFlash.setImageResource(R.drawable.ic_flash_off);
                    cameraKitView.setFlash(CameraKit.FLASH_OFF);
                } else {
                    ivFlash.setImageResource(R.drawable.ic_flash_on);
                    cameraKitView.setFlash(CameraKit.FLASH_ON);
                }
                isFlashOn = !isFlashOn;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Intent intent = new Intent(context, PhotoPreviewActivity.class);
            intent.putExtra("uri", uri);
            startActivity(intent);
        }
    }

    private Bitmap cropBitmapCenter(Bitmap bitmap) {
//        int bitmapWidth     = bitmap.getWidth();
        int bitmapheight = bitmap.getHeight();


        int sideMargins = getResources().getInteger(R.integer.image_box_side_margin);
        int height = getResources().getInteger(R.integer.image_box_height);

        int newX = sideMargins;//  - (Utility.getScreenWidth(this) - 100)/2;
        int newY = bitmapheight / 2 - (height / 2);

        return Bitmap.createBitmap(bitmap, newX, newY, Utility.getScreenWidth(this) - (sideMargins * 2), height);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
