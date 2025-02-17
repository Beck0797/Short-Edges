package ua.in.asilichenko.shortedges;

import static java.lang.Math.max;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

  int clickCount = 0;
  private Handler handler = new Handler();
  private ImageView imageView;

  private SharedPreferences sharedPreferences;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sharedPreferences = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);

    setDecorFitsSystemWindows();
    initUI();


    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_btn), this::onApplyWindowInsets);
  }

  private void initUI() {
    Spinner numberSpinner = findViewById(R.id.numberSpinner);
    Button showButton = (Button) findViewById(R.id.btn_hidden);
    imageView = findViewById(R.id.img_background);

    // Create an array of numbers 1-10
    String[] numbers = new String[40];
    for (int i = 0; i < 40; i++) {
      numbers[i] = String.valueOf(i + 1);
    }

    // Set up an adapter for the Spinner
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numbers);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    numberSpinner.setAdapter(adapter);
    // Restore last selected number
    int position = sharedPreferences.getInt("KEY_LAST_SELECTED", 0); // Default to index 0
    numberSpinner.setSelection(position);

    setImageViewImg(""+(position+1));



    // Set an item selected listener
    numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("SpinnerCheck", "Selected: " + (position + 1));
        sharedPreferences.edit().putInt("KEY_LAST_SELECTED", position).apply();
        setImageViewImg("" + (position + 1));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
      }
    });

    // Button Click Logic
    showButton.setOnClickListener(v -> {
      clickCount++;

      Log.d("SpinnerCheck", "clicked: " + clickCount);


      // Start/reset a countdown to reset click count after 2 seconds
      handler.removeCallbacksAndMessages(null);
      handler.postDelayed(() -> clickCount = 0, 1500);

      if (clickCount == 3) {
        if(numberSpinner.getVisibility() == View.GONE) {
          numberSpinner.setVisibility(View.VISIBLE);
          Log.d("SpinnerCheck", "Spinner is now visible");
        } else {
          numberSpinner.setVisibility(View.GONE);
          Log.d("SpinnerCheck", "Spinner is now visible");
        }

      }
    });

  }

  private void setImageViewImg(String selectedNumber) {
    switch (selectedNumber) {
      case "1": {
        imageView.setImageResource(R.drawable.img1);
        break;
      }

      case "2": {
        imageView.setImageResource(R.drawable.img2);
        break;
      }

      case "3": {
        imageView.setImageResource(R.drawable.img3);
        break;
      }

      case "4": {
        imageView.setImageResource(R.drawable.img4);
        break;
      }

      case "5": {
        imageView.setImageResource(R.drawable.img5);
        break;
      }

      case "6": {
        imageView.setImageResource(R.drawable.img6);
        break;
      }

      case "7": {
        imageView.setImageResource(R.drawable.img7);
        break;
      }

      case "8": {
        imageView.setImageResource(R.drawable.img8);
        break;
      }

      case "9": {
        imageView.setImageResource(R.drawable.img9);
        break;
      }

      case "10": {
        imageView.setImageResource(R.drawable.img10);
        break;
      }

      case "11": {
        imageView.setImageResource(R.drawable.img11);
        break;
      }

      case "12": {
        imageView.setImageResource(R.drawable.img12);
        break;
      }

      case "13": {
        imageView.setImageResource(R.drawable.img13);
        break;
      }

      case "14": {
        imageView.setImageResource(R.drawable.img14);
        break;
      }

      case "15": {
        imageView.setImageResource(R.drawable.img15);
        break;
      }

      case "16": {
        imageView.setImageResource(R.drawable.img16);
        break;
      }

      case "17": {
        imageView.setImageResource(R.drawable.img17);
        break;
      }

      case "18": {
        imageView.setImageResource(R.drawable.img18);
        break;
      }

      case "19": {
        imageView.setImageResource(R.drawable.img19);
        break;
      }

      case "20": {
        imageView.setImageResource(R.drawable.img20);
        break;
      }

      case "21": {
        imageView.setImageResource(R.drawable.img21);
        break;
      }

      case "22": {
        imageView.setImageResource(R.drawable.img22);
        break;
      }

      case "23": {
        imageView.setImageResource(R.drawable.img23);
        break;
      }

      case "24": {
        imageView.setImageResource(R.drawable.img24);
        break;
      }

      case "25": {
        imageView.setImageResource(R.drawable.img25);
        break;
      }

      case "26": {
        imageView.setImageResource(R.drawable.img26);
        break;
      }

      case "27": {
        imageView.setImageResource(R.drawable.img27);
        break;
      }

      case "28": {
        imageView.setImageResource(R.drawable.img28);
        break;
      }

      case "29": {
        imageView.setImageResource(R.drawable.img29);
        break;
      }

      case "30": {
        imageView.setImageResource(R.drawable.img30);
        break;
      }

      case "31": {
        imageView.setImageResource(R.drawable.img31);
        break;
      }

      case "32": {
        imageView.setImageResource(R.drawable.img32);
        break;
      }

      case "33": {
        imageView.setImageResource(R.drawable.img33);
        break;
      }

      case "34": {
        imageView.setImageResource(R.drawable.img34);
        break;
      }

      case "35": {
        imageView.setImageResource(R.drawable.img35);
        break;
      }

      case "36": {
        imageView.setImageResource(R.drawable.img36);
        break;
      }

      case "37": {
        imageView.setImageResource(R.drawable.img37);
        break;
      }

      case "38": {
        imageView.setImageResource(R.drawable.img38);
        break;
      }

      case "39": {
        imageView.setImageResource(R.drawable.img39);
        break;
      }

      case "40": {
        imageView.setImageResource(R.drawable.img40);
        break;
      }
    }
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
