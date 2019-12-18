package com.antam.image.compression;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageCompression {

    public static ByteArrayOutputStream getCompressedImage(Bitmap original, long targetedOutputSize) {
        int scale = 2;
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        while (true) {
            Bitmap scaled = Bitmap.createScaledBitmap(original,
                    original.getWidth() / scale,
                    original.getHeight() / scale,
                    false);
            if (scaled != null) {
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOut);
                if (byteArrayOut.size() < targetedOutputSize) {
                    break;
                }
                scale += 1;
                byteArrayOut.reset();
            }
        }
        return byteArrayOut;
    }

    public static Bitmap getScaledImage(Bitmap original, int scale) {
        return Bitmap.createScaledBitmap(original, original.getWidth() / scale, original.getHeight() / scale, false);
    }

}
