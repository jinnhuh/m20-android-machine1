package app.com.m20.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import app.com.m20.R;
import app.com.m20.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String TAG_ACTIVITY = "M20_Main";

    TextView tv1;
    TextView tv2;
    TextView tv3;

    MediaPlayer m_mediaPlayer = null;

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
        tv3 = findViewById(R.id.mainErrorMsg);

        tv1.setText("");
        tv2.setText("");
        tv3.setText("");

        Resources resources = getResources();
        String str = resources.getString(R.string.state1);

        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv1.append(builder);

        /* 2018-05-17 Deletion request from m20
        resources = getResources();
        str = resources.getString(R.string.state2);
        builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv2.append(builder);
        */

        Intent i= getIntent();
        if (i != null) {
            String errMsg = i.getStringExtra("error");
            if(errMsg.compareTo("정상") == 0)
            {
                Log.i("MainActivity", "finish after receiving OK");
                finish();
                return;
            }
            if(errMsg!=null ) {
                tv2.setTextColor(Color.YELLOW);
                tv2.setText(errMsg);
            }
        }

        m_mediaPlayer = MediaPlayer.create(this, R.raw.connect);
        m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        m_mediaPlayer.setLooping(false);
        m_mediaPlayer.start();

//        Display dis = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int densityDPI = displayMetrics.densityDpi;
//        int heightPixels = displayMetrics.heightPixels;
//        int widthPixels = displayMetrics.widthPixels;
//        int wh = dis.getWidth() * dis.getHeight() / widthPixels * heightPixels;
//        TextView textView = new TextView(this);
//        textView.setText(String.format(Locale.US, "dpi : %d, height : %d, width : %d, 해상도 : %d", densityDPI, heightPixels, widthPixels, wh));
//
//        relativeLayout.addView(textView);
        relativeLayout.setOnClickListener((v) -> {
            if(m_mediaPlayer!=null){
                m_mediaPlayer.stop();
            }
            finish();
        });

        findViewById(R.id.btn_back).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.btn_back ){
            if(m_mediaPlayer!=null){
                m_mediaPlayer.stop();
            }
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            String errMsg = intent.getStringExtra("error");
            if(errMsg.compareTo("정상") == 0)
            {
                Log.i("MainActivity", "finish after receiving OK");
                if(m_mediaPlayer!=null){
                    m_mediaPlayer.stop();
                }
                finish();
            }
            if(errMsg!=null && tv2!=null) {
                tv2.setTextColor(Color.YELLOW);
                tv2.setText(errMsg);
            }
        }
    }
}
