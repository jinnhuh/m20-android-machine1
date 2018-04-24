package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import java.util.Locale;
import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;

/**
 * Created by Administrator on 2018-03-12.
 */

public class BodyFactWanningActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_BodyFactWanning";

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
        setContentView(R.layout.activity_bodyfactwanning);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게

        Log.i(TAG_ACTIVITY, "onCreate().");
        Intent intent = getIntent();
        if(intent!=null) {
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            weight = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");
            float pointheight = Float.parseFloat(height);
            height = String.format(Locale.US, "%.1f", pointheight);
        }
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
        } else {
//            Toast.makeText(this, "no connectionBodyFatWanningActivity", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                String packet = String.format(Locale.US, "S28;%s;%s;%s;%s;N", gender, age, weight, height);
                Log.i(TAG_ACTIVITY, String.format(Locale.US, "Send %s.", packet));
                mUsbReceiver.writeDataToSerial(packet);  //체지방 측정 요청 전달 성별(1),나이(2),체중(5),키(5) 전달

                Log.i(TAG_ACTIVITY, "Start PersonCheckupActivity.");
                Intent i = new Intent(BodyFactWanningActivity.this, PersonCheckupActivity.class);
                i.putExtra("name",name);
                i.putExtra("age", age);
                i.putExtra("gender", gender);
                i.putExtra("weight", weight);
                i.putExtra("height", height);
                startActivity(i);
                finish();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
        //////////////////////////////////
        // Serial
        //////////////////////////////////
    }
}
