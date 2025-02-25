package ua.in.asilichenko.shortedges;

import static java.lang.Math.max;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import dagger.hilt.android.AndroidEntryPoint;
import ua.in.asilichenko.shortedges.viewmodel.MainViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MainViewModel viewModel;
    private String ipAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img_background);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ipAddress = getDeviceIpAddress(this);

        getScreenSize();
        setDecorFitsSystemWindows();

        if (doImageExist(this)) {
            Log.e("ImageTest", "Image exits");

            //set image
            setImageIfResourceExists(this);

            //send ready command
            viewModel.sendReadyCommand(ipAddress);

        } else {
            Log.e("ImageTest", "Image  does not exits");

            //get image from server
            viewModel.fetchImage(ipAddress);

            viewModel.getImageFetchResult().observe(this, success -> {
                if (success) {
                    setImageIfResourceExists(this);
                    viewModel.sendReadyCommand(ipAddress);
                    Log.e("ImageTest", "Image downloaded successfully!");

                } else {
                    Log.e("ImageTest", "Failed to download image!");
                }
            });
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_btn), this::onApplyWindowInsets);
    }

    public boolean doImageExist(Context context) {
        Uri downloadsUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {"img.png"};

        Cursor cursor = context.getContentResolver().query(downloadsUri, projection, selection, selectionArgs, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int height2 = displayMetrics.heightPixels + getNavigationBarHeight();

        Log.e("ScreenSize", "height: " + height + " width: " + width);
        Log.e("ScreenSize", "height2: " + height2 + " width: " + width);
    }

    private int getNavigationBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }


    public void setImageIfResourceExists(Context context) {
        // Path to the image in Downloads (Replace with actual filename if needed)
        Uri imageUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            imageUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

        // Query the URI for the image
        String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{"imp.png"}; // Your image name

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(imageUri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            long id = cursor.getLong(idColumn);
            Uri contentUri = Uri.withAppendedPath(imageUri, String.valueOf(id));

            // Get the InputStream for the image
            try (InputStream inputStream = contentResolver.openInputStream(contentUri)) {
                if (inputStream != null) {
                    // Decode the InputStream to a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Set the Bitmap to ImageView
                    imageView.setImageBitmap(bitmap);
                    Log.e("ImageTest", "setImageIfResourceExists yes image");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageView.setImageResource(R.drawable.black);

            Log.e("ImageTest", "setImageIfResourceExists no image");
        }
    }

    public String getDeviceIpAddress(Context context) {
        // Get the WifiManager system service
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // Get the current connection info
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Get the IP address in integer form
        int ipAddress = wifiInfo.getIpAddress();

        // Convert the IP address to a readable format
        String formattedIpAddress = Formatter.formatIpAddress(ipAddress);

        return formattedIpAddress;
    }

    /**
     * Hide program navigation bar: back, home, ... in the bottom of the screen
     * <a href="https://developer.android.com/develop/ui/views/layout/edge-to-edge">Display content edge-to-edge in your app</a>
     */
    private void setDecorFitsSystemWindows() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    /**
     * <a href="https://developer.android.com/develop/ui/views/layout/edge-to-edge">Display content edge-to-edge in your app</a>
     *
     * @see OnApplyWindowInsetsListener
     */
    @NonNull
    public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat windowInsets) {
        final Insets displayCutoutInsets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout());
        final Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        final Insets safeInsets = Insets.of(
                max(displayCutoutInsets.left, systemBarsInsets.left),
                max(displayCutoutInsets.top, systemBarsInsets.top),
                max(displayCutoutInsets.right, systemBarsInsets.right),
                max(displayCutoutInsets.bottom, systemBarsInsets.bottom)
        );

        // Hide the status bar
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Hide the navigation bar (for immersive mode)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        Log.d(getLocalClassName(), "");
        Log.d(getLocalClassName(), "displayCutoutInsets");
        Log.d(getLocalClassName(), "top=" + displayCutoutInsets.top);
        Log.d(getLocalClassName(), "left=" + displayCutoutInsets.left);
        Log.d(getLocalClassName(), "right=" + displayCutoutInsets.right);
        Log.d(getLocalClassName(), "bottom=" + displayCutoutInsets.bottom);

        Log.d(getLocalClassName(), "");
        Log.d(getLocalClassName(), "systemBarsInsets");
        Log.d(getLocalClassName(), "top=" + systemBarsInsets.top);
        Log.d(getLocalClassName(), "left=" + systemBarsInsets.left);
        Log.d(getLocalClassName(), "right=" + systemBarsInsets.right);
        Log.d(getLocalClassName(), "bottom=" + systemBarsInsets.bottom);

        Log.d(getLocalClassName(), "");
        Log.d(getLocalClassName(), "safeInsets");
        Log.d(getLocalClassName(), "top=" + safeInsets.top);
        Log.d(getLocalClassName(), "left=" + safeInsets.left);
        Log.d(getLocalClassName(), "right=" + safeInsets.right);
        Log.d(getLocalClassName(), "bottom=" + safeInsets.bottom);
        Log.d(getLocalClassName(), "");

        final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        mlp.leftMargin = safeInsets.left;
        mlp.topMargin = safeInsets.top;
        mlp.bottomMargin = safeInsets.bottom;
        mlp.rightMargin = safeInsets.right;
        v.setLayoutParams(mlp);

        return WindowInsetsCompat.CONSUMED;
    }


}
