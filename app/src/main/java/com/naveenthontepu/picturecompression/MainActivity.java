package com.naveenthontepu.picturecompression;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.rider_photo_text)
    TextView riderPhotoText;
    @Bind(R.id.rider_photo_front)
    Button riderPhotoFront;
    @Bind(R.id.rider_photo_status)
    TextView riderPhotoStatus;
    @Bind(R.id.rider_photo_card)
    CardView riderPhotoCard;

    int document = 0;

    Uri fileUri;
    static final int MEDIA_TYPE_IMAGE = 1;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    @Bind(R.id.scaled)
    ImageView scalledImage;
    @Bind(R.id.actual)
    ImageView actual;
    @Bind(R.id.convertPhoto)
    Button convertPhoto;
    private ByteArrayOutputStream finalOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.rider_photo_front)
    public void onClick() {
        document = 1;
        captureImage();
    }
    @OnClick(R.id.convertPhoto)
    public void convertPhoto() {
        if (finalOutputStream!=null) {
            Bitmap image = BitmapFactory.decodeByteArray(finalOutputStream.toByteArray(),0,finalOutputStream.size());
            Bitmap duplicateImage = image.copy(Bitmap.Config.ARGB_8888,true);
            int i,j;
            for (i=0;i<image.getHeight();i++){
                for (j=0;j<image.getWidth();j++){
                    int tempPixel = image.getPixel(j,i);
                    int tempRed= Color.red(tempPixel);
                    int tempGreen = Color.green(tempPixel);
                    int tempBlue = Color.blue(tempPixel);
//                    printLog("temp variables = "+tempBlue+"  "+tempGreen+"  "+tempRed);
                    int avg = (tempRed+tempGreen+tempBlue)/3;
//                    printLog("avg = "+avg);
                    duplicateImage.setPixel(j,i,Color.argb(255,avg,avg,avg));
                }
            }
            actual.setImageBitmap(duplicateImage);
        }else {
            captureImage();
            convertPhoto();
        }
    }

    private void captureImage() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Rapido");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (fileUri != null) {
                    if (fileUri.getPath() != null) {
                        uploadFile(fileUri, document);
                    }
                }
            } else {
                Toast.makeText(this, "Image could not be captured", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile(Uri fileUri, final int document) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(fileUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            Bitmap original = BitmapFactory.decodeStream(inputStream);
            actual.setImageBitmap(original);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            Bitmap scaled = getScaledImage(original);
            if (original != null) {
                original.compress(Bitmap.CompressFormat.JPEG, 80, out);
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, out1);
            }
            finalOutputStream = getCompressedImage(original);
            Bitmap scalledCompressed = BitmapFactory.decodeByteArray(finalOutputStream.toByteArray(), 0, finalOutputStream.size());
            scalledImage.setImageBitmap(scalledCompressed);
            printLog("the finalOutputStream size = " + finalOutputStream.size());
            printSize(original, "original");
            printSize(scaled, "scaled");
            printLog("the height of scaled = " + scaled.getHeight());
            printLog("the width of scaled = " + scaled.getWidth());
            Log.i("picture", " the size of out " + out.size());
            Log.i("picture", " the size of out1 " + out1.size());
            Bitmap compressed = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
            printSize(compressed, "compressed");
            printSize(scalledCompressed, "scalled compressed");
        }
    }

    private ByteArrayOutputStream getCompressedWithConstant(Bitmap orginal) {
        float maxHeight = 800.0f;
        float maxWidth = 600.0f;
        int actualHeight = orginal.getHeight();
        int actualWidth = orginal.getWidth();
        float originalRatio = orginal.getHeight() / orginal.getWidth();
        float tempRatio = maxHeight / maxWidth;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {

        }

        return null;
    }

    private ByteArrayOutputStream getCompressedImage(Bitmap orginal) {
        int scale = 2;
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        while (true) {
            Bitmap scaled = Bitmap.createScaledBitmap(orginal, orginal.getWidth() / scale, orginal.getHeight() / scale, false);
            if (scaled != null) {
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOut);
                printLog("the compressed image with scale " + scale + " = " + byteArrayOut.size());
                if (byteArrayOut.size() < 102400) {
                    break;
                }
                scale += 1;
                byteArrayOut.reset();
            }
        }
        return byteArrayOut;
    }

    private Bitmap getScaledImage(Bitmap original) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        printLog("the height of original = " + original.getHeight());
        printLog("the width of original = " + original.getWidth());
        return Bitmap.createScaledBitmap(original, original.getWidth() / 10, original.getHeight() / 10, false);
    }

    private void printLog(String message) {
        Log.i("picture", message);
    }


    private void printSize(Bitmap original, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i("picture", "the size of " + name + " in kitkat " + original.getAllocationByteCount());
        } else {
            Log.i("picture", "the size of " + name + " in kitkat " + original.getByteCount());
        }
    }

}
