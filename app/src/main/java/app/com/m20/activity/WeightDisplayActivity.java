package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;

/**
 * Created by Administrator on 2018-03-23.
 */

public class WeightDisplayActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_WeightDisplay";

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    private Handler mHandler;
    private Runnable mRunnable;

    String name;
    String age;
    String gender;
    String weight;
    String height;

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weightdisplay);
        Utils.fullScreen(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        Log.i(TAG_ACTIVITY, "onCreate().");

        Resources res = getResources();

        //////////////////////////////////
        // Serial
        //////////////////////////////////
        FTDriver mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));
        // listen for new devices
        mUsbReceiver = new UsbReceiver(this, mSerial);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        // load default baud rate
        int mBaudrate = mUsbReceiver.loadDefaultBaudrate();
        // for requesting permission
        // setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);
        if (mSerial.begin(mBaudrate)) {
            mUsbReceiver.loadDefaultSettingValues();
            mUsbReceiver.mainloop();
        }
        //////////////////////////////////
        // Serial
        //////////////////////////////////
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            weight = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");
        }
        Log.i(TAG_ACTIVITY, "weight : " + weight );

        Matcher mat;
        Pattern script = Pattern.compile("(^0+)", Pattern.DOTALL);
        mat = script.matcher(weight);
        String tmpStr = mat.replaceAll("");
        Log.i(TAG_ACTIVITY, "tmpStr : " + tmpStr );

        String text = String.format(res.getString(R.string.weight_display), tmpStr);

        TextView tv = findViewById(R.id.mainTitleKG);
        tv.setText(text);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG_ACTIVITY, "Start BodyFatActivity.");
                Intent i = new Intent(WeightDisplayActivity.this, BodyFatActivity.class);
                i.putExtra("name", name);
                i.putExtra("age", age);
                i.putExtra("gender", gender);
                i.putExtra("weight", weight);
                i.putExtra("height", height);
                startActivity(i);
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 3000);
    }
}
