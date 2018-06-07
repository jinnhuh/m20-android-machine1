package app.com.m20.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import app.com.m20.R;
import app.com.m20.db.DbManagement;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.model.Body;
import app.com.m20.model.User;
import app.com.m20.utils.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

public class IntroActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_Intro";

    private Realm mRealm;
    private DbManagement dbManagement;

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    BroadcastReceiver mBR;

//    private Handler mCCHandler = null;

    private void init() {
        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration
//                .Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        mRealm = Realm.getInstance(config);
        mRealm = Realm.getDefaultInstance();
        dbManagement = new DbManagement(mRealm);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        Utils.fullScreen(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        Log.i(TAG_ACTIVITY, "onCreate().");

        // Print version info
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = pInfo.versionCode;
        String versionName = pInfo.versionName;

        Log.e(TAG_ACTIVITY, "VersionCode:"+Integer.toString(versionCode));
        Log.e(TAG_ACTIVITY, "VersionName:"+versionName);

        init();

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
//            Log.i("M20", "no connectionIntroActivity");
//            Toast.makeText(this, "no connectionIntroActivity", Toast.LENGTH_LONG).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }

        mRealm.beginTransaction();
        RealmResults<User> duser = dbManagement.dbNoFilterQuery();
        duser.deleteAllFromRealm();
        RealmResults<Body> bodies = mRealm.where(Body.class).findAll();
        bodies.deleteAllFromRealm();
        mRealm.commitTransaction();

        ImageView imageView = findViewById(R.id.introTitle);
        imageView.setOnClickListener((v) -> {  //부팅시에 logo를 클릭하여 예약 번호 입력 화면으로 진입
            boolean isConnected = false;

            saveDummyData();

            if(!mSerial.isConnected()) {
                if (mSerial.begin(mBaudrate)) {
                    mUsbReceiver.loadDefaultSettingValues();
                    mUsbReceiver.mainloop();
                    isConnected = true;
                } else {
                    Toast.makeText(this, "Failed to try connecting, Check USB cable", Toast.LENGTH_LONG).show();
                    Log.i(TAG_ACTIVITY, "Failed to try connecting, Check USB cable");
                }
            } else {
                isConnected = true;
            }
            if(isConnected) {
                sendConnectCheckMsg();
            }

            //Log.i(TAG_ACTIVITY, "Start RegActivity.");
            //startActivity(new Intent(IntroActivity.this, RegActivity.class));
            //startActivity(new Intent(IntroActivity.this, PersonCheckupActivity.class));
            //finish();
        });
        //Handler handler = new Handler();  //로고 클릭하여 예약번호 입력화면으로 진입하지 않고 5초 후 바로 예약 번호 입력 화면으로 진입
        //handler.postDelayed(new Runnable() {
            //@Override
            //public void run() {
                //Intent intent = new Intent(IntroActivity.this, RegActivity.class);
                //startActivity(intent);

                //finish();
            //}
        //}, 5000);
    }
    private void sendConnectCheckMsg(){
        Log.i(TAG_ACTIVITY, "sendConnectCheckMsg() Send S67;N.");
        mUsbReceiver.writeDataToSerial("S67;N"); // Connect Check 요청

//        mCCHandler = new Handler();
//        mCCHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG_ACTIVITY, "Connect Check : No Response");
//                // TODO: hkpark
//                // Connect Check에 대한 응답이 없는 경우 UART 오류로 간주 하고,
//                // 관제센터 시스템 상태 전송 (오류)
//            }
//        }, 5000);

        // TODO: hkpark
        // 모래시계 띄우기
    }


/*
    // 장치별 유니크 아이디
    @SuppressLint("MissingPermission")
    public String getUniqueID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }
*/

    @Override
    public void onDestroy() {
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
        if (mBR!= null)
            unregisterReceiver(mBR);

//        if( mCCHandler != null ) {
//            Log.i(TAG_ACTIVITY, "call mCCHandler.removeMessages()");
//            mCCHandler.removeMessages(0);
//            mCCHandler = null;
//        }
    }

    @Override
    public void onBackPressed() {

    }

    public void setConnectCheck(String str) {
        String msg = null;
        Log.i(TAG_ACTIVITY, "setConnectCheck().");

//        if( mCCHandler != null ) {
//            Log.i(TAG_ACTIVITY, "call mCCHandler.removeMessages()");
//            mCCHandler.removeMessages(0);
//            mCCHandler = null;
//        }

        switch (str) {
            case "1":  // Connector 비 정상
                msg = "Connector 비 정상.";
                break;
            case "2":  // Suit 흉부 비 정상
                msg = "Suit 흉부 비 정상.";
                break;
            case "3":  // Suit 복부 비 정상
                msg = "Suit 복부 비 정상.";
                break;
            case "4":  // Suit 상완 비 정상
                msg = "Suit 상완 비 정상.";
                break;
            case "5":  // Suit 옆구리 비 정상
                msg = "Suit 옆구리 비 정상.";
                break;
            case "6":  // Suit 어깨 비 정상
                msg = "Suit 어깨 비 정상.";
                break;
            case "7":  // Suit 허리 비 정상
                msg = "Suit 허리 비 정상.";
                break;
            case "8":  // Suit 허벅다리 비 정상
                msg = "Suit 허벅다리 비 정상.";
                break;
            case "9":  // Suit 엉덩이 비 정상
                msg = "Suit 엉덩이 비 정상.";
                break;
        }
        if (msg != null) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>" + msg + "</font></big></big></big></big></H6> <br>")));
//            alertDialogBuilder.setNegativeButton("확인",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();

            Intent i = new Intent(IntroActivity.this, MainActivity.class);
            i.putExtra("error", msg);
            startActivity(i);
            //finish();
        }

        //if (str.equals("0") || str.equals("1") ) {  //정상이면 입력 화면으로 이동
        if (str.equals("0")) {  //정상이면 입력 화면으로 이동
            startActivity(new Intent(IntroActivity.this, RegActivity.class));
            finish();
        }
/*
        if(str.equals("1")) {  // Connector 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.connect);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Connector 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    dialog.cancel();
//                                    startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                                    finish();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("2")) {  // Suit 흉부 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.intro);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 흉부 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("3")) {  // Suit 복부 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.reg);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 복부 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("4")) {  // Suit 상완 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.start);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 상완 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("5")) {  // Suit 옆구리 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.check);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 옆구리 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("6")) {  // Suit 어깨 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.start);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 어깨 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("7")) {  // Suit 허리 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.start);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 허리 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("8")) {  // Suit 허벅다리 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.start);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 허벅다리 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(str.equals("9")) {  // Suit 엉덩이 비 정상
            //mediaPlayer = MediaPlayer.create(this, R.raw.start);
            alertDialogBuilder.setMessage(Html.fromHtml(("<H6><big><big><big><big><font color=green>Suit 엉덩이 비 정상.</font></big></big></big></big></H6> <br>")));
            alertDialogBuilder.setNegativeButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
//                            startActivity(new Intent(IntroActivity.this, RegActivity.class));
//                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
*/
/*
        if (str.equals("0")) {  //정상이면 입력 화면으로 이동
            startActivity(new Intent(IntroActivity.this, RegActivity.class));
            finish();
        }
*/
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mediaPlayer.setLooping(false);
        //mediaPlayer.start();
    }
    private void saveDummyData(){
//        SharedPreferences age =getSharedPreferences("dummy_data", MODE_PRIVATE);
//        SharedPreferences.Editor editor = age.edit();
//
//        editor.apply();

        SharedPreferences prefs =getSharedPreferences("user_name", MODE_PRIVATE);
        if(prefs!=null) {
            String str_name = prefs.getString("user_name", "0"); //키값, 디폴트값
            Log.i(TAG_ACTIVITY, "user_name: " + str_name);
        }else{
            Log.i(TAG_ACTIVITY, "user_name: null ");
        }
    }
}
