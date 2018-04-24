package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;


public class WelcomeActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_Welcome";

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
        //super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

/*
    public void activityMove() throws InterruptedException {
        Intent intent = new Intent(WelcomeActivity.this, PersonCheckupActivity.class);
        startActivity(intent);
        finish();
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        Log.i(TAG_ACTIVITY, "onCreate().");

//        Realm realm = Realm.getDefaultInstance();
//        DbManagement dbManagement = new DbManagement(realm);
//        RealmResults<User> user = dbManagement.dbLastNoFilterQuery();
//        System.out.println(user.get(0).getName());

        Intent intent = getIntent();
        if(intent!=null) {
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            weight = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");

            Resources res = getResources();
            String str = name;
            String text = String.format(res.getString(R.string.hello_small_size), str);
            TextView tv = findViewById(R.id.mainTitle1);
            tv.setText(text);
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
            //mUsbReceiver.writeDataToSerial("S27;1;N"); // 체중 측정 요청 1이 이경우는 세자리가 아니다...왜 통일 안하냐??
        } else {
//            Toast.makeText(this, "no connectionWelcome", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
            // 프로그램 넘어가기 위해
//            Context context = this;
//            Handler handler = new Handler() {
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    Intent intent = new Intent(context, PersonCheckupActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            };
//            handler.sendEmptyMessageDelayed(0, 3000);

        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG_ACTIVITY, "Start WeighWanningActivity.");
                Intent i = new Intent(WelcomeActivity.this, WeighWanningActivity.class);
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
        mHandler.postDelayed(mRunnable, 5000);

        //////////////////////////////////
        // Serial
        //////////////////////////////////
    }

    public void setText(String str) {
        Resources res = getResources();
        String text = String.format(res.getString(R.string.hello_small_size), str);
//        CharSequence styledText = Html.fromHtml(text);
        TextView tv = findViewById(R.id.mainTitle1);
        tv.setText(text);
        //tv.setOnClickListener((v) -> {
            //startActivity(new Intent(WelcomeActivity.this, PersonCheckupActivity.class));
            //finish();
        //});
    }
}
