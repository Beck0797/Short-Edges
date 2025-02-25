package ua.in.asilichenko.shortedges;

import static java.lang.Math.max;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ua.in.asilichenko.shortedges.data.PreferenceManager;
import ua.in.asilichenko.shortedges.viewmodel.MainViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MainViewModel viewModel;
    private String ipAddress;
    @Inject
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img_background);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ipAddress = getDeviceIpAddress(this);
        preferenceManager.init(this);

        getScreenSize();
        setDecorFitsSystemWindows();
        viewModel.startUdpClient();

        if (doImageExist()) {
            Log.e("ImageTest", "Image exits");

            //set image
            setImageIfResourceExists();

            //send ready command
            viewModel.sendReadyCommand(ipAddress);

        } else {
            Log.e("ImageTest", "Image  does not exits");

            viewModel.fetchImage(ipAddress);

            viewModel.getImageFetchResult().observe(this, success -> {
                if (success) {
                    runOnUiThread(this::setImageIfResourceExists);

                    viewModel.sendReadyCommand(ipAddress);
                    Log.e("ImageTest", "Image downloaded successfully!");

                } else {
                    Log.e("ImageTest", "Failed to download image!");
                }
            });
        }

        // observe udp
        viewModel.getReceivedMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {

                Log.e("ImageTest", "UDP onChanged: " + message);


                String command = message.split("_")[0];

                Log.e("ImageTest", "UDP command:" + command);

                switch (command) {
                    case "DEL": {
                        Log.e("ImageTest", "case del: ");

                        runOnUiThread(() -> imageView.setImageResource(R.drawable.black));
                        deleteImage();
                        break;
                    }

                    case "DLI": {
                        Log.e("ImageTest", "case dli: ");
                        runOnUiThread(() -> imageView.setImageResource(R.drawable.black));
                        deleteImage();

                        viewModel.fetchImage(ipAddress);
                        viewModel.getImageFetchResult().observe(MainActivity.this, success -> {
                            if (success) {
                                runOnUiThread(() -> setImageIfResourceExists());

                                Log.e("ImageTest", "Image downloaded successfully!");

                            } else {
                                Log.e("ImageTest", "Failed to download image!");
                            }
                        });


                        break;
                    }

                    case "SM": {
                        Log.e("ImageTest", "case sm: " + message);

                        message.trim();
                        if (message.endsWith("00")) {
                            // BLACK
                            Log.e("ImageTest", "UDP 00: " + message);
                            imageView.setImageResource(R.drawable.black);

                        } else if (message.endsWith("01")) {
                            // Image
                            Log.e("ImageTest", "UDP 01: " + message);
                            setImageIfResourceExists();
                        } else {
                            Log.e("ImageTest", "Unexpected message: " + message);
                        }


                        break;
                    }
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_btn), this::onApplyWindowInsets);
    }

    public boolean doImageExist() {
        Bitmap bitmap = viewModel.getUserImage();
        return bitmap != null;
    }

    public void deleteImage() {
        viewModel.delUserImage();
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


    public void setImageIfResourceExists() {
        Bitmap bitmap = viewModel.getUserImage();
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
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
