package ua.in.asilichenko.shortedges.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageToStringConverter {

    public String convertImageToString(Bitmap realImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap convertStringToBitMap(String previouslyEncodedImage) {
        Bitmap bitmap = null;
        try {
            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}