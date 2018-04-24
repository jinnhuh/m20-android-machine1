package app.com.m20.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import app.com.m20.R;
import app.com.m20.utils.Utils;

public class MainActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_Main";

    TextView tv1;
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        RelativeLayout relativeLayout = findViewById(R.id.mainIdRl);

        tv1 = findViewById(R.id.mainTitle1);
        tv2 = findViewById(R.id.mainTitle2);

        tv1.setText("");
        tv2.setText("");

        Resources resources = getResources();
        String str = resources.getString(R.string.state1);

        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv1.append(builder);

        resources = getResources();
        str = resources.getString(R.string.state1);
        builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv2.append(builder);

        Display dis = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int densityDPI = displayMetrics.densityDpi;
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;
        int wh = dis.getWidth() * dis.getHeight() / widthPixels * heightPixels;
        TextView textView = new TextView(this);
        textView.setText(String.format(Locale.US, "dpi : %d, height : %d, width : %d, 해상도 : %d", densityDPI, heightPixels, widthPixels, wh));

        relativeLayout.addView(textView);
        relativeLayout.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        });

    }

    @Override
    public void onBackPressed() {

    }

}
