package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import app.com.m20.customview.HoloCircularProgressBar;
import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by kimyongyeon on 2017-11-20.
 */

public class DetailStrongActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {
    String TAG_ACTIVITY = "M20_DetailStrong";

    SeekBar mSeekBar;
    private HoloCircularProgressBar mHoloCircularProgressBar;

    private int startNumber = 1;
    private int STRONG_BP_VALUE_DEFAULT = 30; // body part Strong value

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";
    private int blinkNumber = 0;  //강도 설정하는 부위만 깜빡이게 하기 위해
    private boolean startButtonpush = false;  //시작 버튼 누른거 알기 위해
    private boolean AllstrongdefaultValue_onetime = false; //디폴트 값 한번만 전송하기 위한 값
    private int strongValue_brust = STRONG_BP_VALUE_DEFAULT;  //현재 강도 설정한 값 부위별로 저장
    private int strongValue_abdomen = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_arm = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_bein = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_latt = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_sideflank = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_waist = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_arsch = STRONG_BP_VALUE_DEFAULT;
    private int strongValue_all = 5;
    String playID = null;  //어떤 운동인지 저장

    TextView btn_latt_1; // 어깨1
    TextView btn_latt_2; // 어깨2
    TextView btn_waist_1; // 허리1
    TextView btn_waist_2; // 허리2
    TextView btn_sideflank_1; // 옆구리1
    TextView btn_sideflank_2; // 옆구리2
    TextView btn_arsch_1; // 둔부1
    TextView btn_arsch_2; // 둔부2
    TextView btn_brust_1; // 가슴1
    TextView btn_brust_2; // 가슴2
    TextView btn_arm_1; // 팔1
    TextView btn_arm_2; // 팔2
    TextView btn_abdomen_1; // 배1
    TextView btn_abdomen_2; // 배2
    TextView btn_bein_1; // 허벅지1
    TextView btn_bein_2; // 허벅지2
    TextView txtPersent;
    TextView btnStartText;

    @Override
    protected void onDestroy() {
        Log.i(TAG_ACTIVITY, "onDestroy()");

        // TODO Auto-generated method stub
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
    }

    private void valueDisplay() {  //강도 설정한 값 뿌리자
        btn_latt_1.setText(String.format(Locale.US, "%d", strongValue_latt));
        btn_latt_2.setText(String.format(Locale.US, "%d", strongValue_latt));

        btn_waist_1.setText(String.format(Locale.US, "%d", strongValue_waist));
        btn_waist_2.setText(String.format(Locale.US, "%d", strongValue_waist));

        btn_sideflank_1.setText(String.format(Locale.US, "%d", strongValue_sideflank));
        btn_sideflank_2.setText(String.format(Locale.US, "%d", strongValue_sideflank));

        btn_arsch_1.setText(String.format(Locale.US, "%d", strongValue_arsch));
        btn_arsch_2.setText(String.format(Locale.US, "%d", strongValue_arsch));

        btn_brust_1.setText(String.format(Locale.US, "%d", strongValue_brust));
        btn_brust_2.setText(String.format(Locale.US, "%d", strongValue_brust));

        btn_arm_1.setText(String.format(Locale.US, "%d", strongValue_arm));
        btn_arm_2.setText(String.format(Locale.US, "%d", strongValue_arm));

        btn_abdomen_1.setText(String.format(Locale.US, "%d", strongValue_abdomen));
        btn_abdomen_2.setText(String.format(Locale.US, "%d", strongValue_abdomen));

        btn_bein_1.setText(String.format(Locale.US, "%d", strongValue_bein));
        btn_bein_2.setText(String.format(Locale.US, "%d", strongValue_bein));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_5);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        AllstrongdefaultValue_onetime = true;  //처음 실행시에 참으로 설정

        mHoloCircularProgressBar = findViewById(R.id.holoCircularProgressBar);

        mSeekBar = findViewById(R.id.btn_sb5);
        mSeekBar.setEnabled(false);
        mSeekBar.setOnSeekBarChangeListener(this);

        btn_latt_1 = findViewById(R.id.btn_latt_51); // 어깨1
        btn_latt_2 = findViewById(R.id.btn_latt_52); // 어깨2
        btn_waist_1 = findViewById(R.id.btn_waist_51); // 허리1
        btn_waist_2 = findViewById(R.id.btn_waist_52); // 허리2
        btn_sideflank_1 = findViewById(R.id.btn_sideflank_51); // 옆구리1
        btn_sideflank_2 = findViewById(R.id.btn_sideflank_52); // 옆구리2
        btn_arsch_1 = findViewById(R.id.btn_arsch_51); // 둔부1
        btn_arsch_2 = findViewById(R.id.btn_arsch_52); // 둔부2
        btn_brust_1 = findViewById(R.id.btn_brust_51); // 가슴1
        btn_brust_2 = findViewById(R.id.btn_brust_52); // 가슴2
        btn_arm_1 = findViewById(R.id.btn_arm_51); // 팔1
        btn_arm_2 = findViewById(R.id.btn_arm_52); // 팔2
        btn_abdomen_1 = findViewById(R.id.btn_abdomen_51); // 배1
        btn_abdomen_2 = findViewById(R.id.btn_abdomen_52); // 배2
        btn_bein_1 = findViewById(R.id.btn_bein_51); // 허벅지1
        btn_bein_2 = findViewById(R.id.btn_bein_52); // 허벅지2
        txtPersent = findViewById(R.id.txtPersent);
        btnStartText = findViewById(R.id.btn_start);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);

        ImageButton imageButton = findViewById(R.id.btn_back);
        imageButton.setEnabled(false);

//        findViewById(R.id.btn_abdomen_1).setOnClickListener(this); // f
//        findViewById(R.id.btn_abdomen_2).setOnClickListener(this); // f
//        findViewById(R.id.btn_arm_1).setOnClickListener(this); // f
//        findViewById(R.id.btn_arm_2).setOnClickListener(this); // f
//        findViewById(R.id.btn_bein_1).setOnClickListener(this); // f
//        findViewById(R.id.btn_bein_2).setOnClickListener(this); // f
//        findViewById(R.id.btn_brust_1).setOnClickListener(this); // f
//        findViewById(R.id.btn_brust_2).setOnClickListener(this); // f
//
//        findViewById(R.id.btn_arsch_1).setOnClickListener(this);
//        findViewById(R.id.btn_arsch_2).setOnClickListener(this);
//        findViewById(R.id.btn_latt_1).setOnClickListener(this);
//        findViewById(R.id.btn_latt_2).setOnClickListener(this);
//        findViewById(R.id.btn_waist_1).setOnClickListener(this);
//        findViewById(R.id.btn_waist_2).setOnClickListener(this);
//        findViewById(R.id.btn_sideflank_1).setOnClickListener(this);
//        findViewById(R.id.btn_sideflank_2).setOnClickListener(this);

//        findViewById(R.id.btn_minus).setOnClickListener(this);
//        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btnOk).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

//        findViewById(R.id.btn_minus).setOnTouchListener((v, event) -> {
//            Log.d("M20", "minus++");
//            longClickMinus();
//            return true;
//        });
//        findViewById(R.id.btn_plus).setOnTouchListener((v, event) -> {
//            Log.d("M20", "plus++");
//            longClickPlus();
//            return true;
//        });
        Intent intent = getIntent();  //강도 설정한 값 받자
        if(intent!=null) {
            strongValue_brust = intent.getIntExtra("brust", STRONG_BP_VALUE_DEFAULT);
            strongValue_abdomen = intent.getIntExtra("abdomen", STRONG_BP_VALUE_DEFAULT);
            strongValue_arm = intent.getIntExtra("arm", STRONG_BP_VALUE_DEFAULT);
            strongValue_bein = intent.getIntExtra("bein", STRONG_BP_VALUE_DEFAULT);
            strongValue_latt = intent.getIntExtra("latt", STRONG_BP_VALUE_DEFAULT);
            strongValue_sideflank = intent.getIntExtra("sideflank", STRONG_BP_VALUE_DEFAULT);
            strongValue_waist = intent.getIntExtra("waist", STRONG_BP_VALUE_DEFAULT);
            strongValue_arsch = intent.getIntExtra("arsch", STRONG_BP_VALUE_DEFAULT);
            strongValue_all = intent.getIntExtra("all", 5);
            playID = intent.getStringExtra("playID");
//            Log.i(TAG_ACTIVITY, "J.Y.T DetailStrongActivity strongValue_all: "+strongValue_all);
        }
        //처음부터 전체 강도 보여지게 하기 위해
        onProgressChanged(mSeekBar, strongValue_all,true);
        //처음부터 전체 강도 보여지게 하기 위해
        TextView titleView = findViewById(R.id.txtTitle);
        SharedPreferences setting ;
        setting = getSharedPreferences("setting", 0);
        titleView.setText(setting.getString("main_title", ""));

        mSeekBar.setProgress(strongValue_all);
        txtPersent.setText(String.format(Locale.US, "%d%%", strongValue_all));
        mHoloCircularProgressBar.setProgress((strongValue_all + 1) * 0.01f);

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
//            Toast.makeText(this, "no connectionDetailStrong", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<DetailStrongActivity> mActivity;

        public MyHandler(DetailStrongActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            DetailStrongActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage();
            }
        }
    }

    private void handleMessage() {
        int progress = mSeekBar.getProgress();

        Log.d(TAG_ACTIVITY, "startNumber: "+startNumber);
/*
        if (startNumber == 1) {  //전체 강도는 1초에 5씩 증가 20 이후에는 1씩 증가
            if (progress == 0) {
                progress += strongValue_all;
            }
            else {
                if (progress < 20)  // 전체강도 세기 20 까지 체크하여 나눔
                    progress += 5;
                else
                    progress += 1;
            }
        } else {  // 부분 강도는 그냥 1씩 증가
            if (progress == 0) { //부분 강도는 처음에 20 이 default 값이다
                if ((startNumber == 2) && (strongValue_brust != 0))  //이렇게 하는 이유는 부분강도를 각 부위별로 세팅할 수 있어서 이리 한다
                    progress += strongValue_brust;
                else if ((startNumber == 3) && (strongValue_abdomen != 0))
                    progress += strongValue_abdomen;
                else if ((startNumber == 4) && (strongValue_arm != 0))
                    progress += strongValue_arm;
                else if ((startNumber == 5) && (strongValue_bein != 0))
                    progress += strongValue_bein;
                else if ((startNumber == 6) && (strongValue_latt != 0))
                    progress += strongValue_latt;
                else if ((startNumber == 7) && (strongValue_sideflank != 0))
                    progress += strongValue_sideflank;
                else if ((startNumber == 8) && (strongValue_waist != 0))
                    progress += strongValue_waist;
                else if ((startNumber == 9) && (strongValue_arsch != 0))
                    progress += strongValue_arsch;
                else
                    progress += 21;
            else
                progress += 1;
        }
*/
        switch (startNumber) {
            case 1:
                if (strongValue_all < 20) {
                    strongValue_all += 5;
                } else if (strongValue_all < 100){
                    strongValue_all++;
                }
                progress = strongValue_all;
                break;
            case 2:
                if (strongValue_brust < 100)
                    strongValue_brust++;
                progress = strongValue_brust;
                break;
            case 3:
                if (strongValue_abdomen < 100)
                    strongValue_abdomen++;
                progress = strongValue_abdomen;
                break;
            case 4:
                if (strongValue_arm < 100)
                    strongValue_arm++;
                progress = strongValue_arm;
                break;
            case 5:
                if (strongValue_bein < 100)
                    strongValue_bein++;
                progress = strongValue_bein;
                break;
            case 6:
                if (strongValue_latt < 100)
                    strongValue_latt++;
                progress = strongValue_latt;
                break;
            case 7:
                if (strongValue_sideflank < 100)
                    strongValue_sideflank++;
                progress = strongValue_sideflank;
                break;
            case 8:
                if (strongValue_waist < 100)
                    strongValue_waist++;
                progress = strongValue_waist;
                break;
            case 9:
                if (strongValue_arsch < 100)
                    strongValue_arsch++;
                progress = strongValue_arsch;
                break;
        }
        mSeekBar.setProgress(progress);
        txtPersent.setText(String.format(Locale.US, "%d%%", progress));
        mHoloCircularProgressBar.setProgress((progress + 1) * 0.01f);

        String progressBar = intTostring(progress);
        String progressBar2 = String.format(Locale.US, "%d", progress);
        blinkNumber = startNumber;
        if (startNumber == 1) {
            if (AllstrongdefaultValue_onetime) { //처음 실행시에 참으로 설정
                mUsbReceiver.writeDataToSerial("A20;" + intTostring(strongValue_brust) + ";" + intTostring(strongValue_abdomen) + ";" + intTostring(strongValue_arm) + ";" + intTostring(strongValue_bein) + ";" + intTostring(strongValue_latt) + ";" + intTostring(strongValue_waist) + ";" + intTostring(strongValue_sideflank) + ";" + intTostring(strongValue_arsch) + ";N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                AllstrongdefaultValue_onetime = false; //다시 보내지 않기 위해 거짓으로 설정
            }
            mUsbReceiver.writeDataToSerial("S23;" + progressBar + ";N");  //전체 강도 신호 보낼 때 A20;1(강도세기);0;0;0;0;0;0;0;0;N 에서 A23;4(강도세기);N로 바뀜
            strongValue_all = progress;  //값 저장
        }
        else if(startNumber == 2) {
            btn_brust_1.setText(progressBar2);
            btn_brust_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;" + progressBar + ";000;000;000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_brust = progress;  //값 저장
        }
        else if(startNumber == 3) {
            btn_abdomen_1.setText(progressBar2);
            btn_abdomen_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;" + progressBar + ";000;000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_abdomen = progress;  //값 저장
        }
        else if(startNumber == 4) {
            btn_arm_1.setText(progressBar2);
            btn_arm_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;" + progressBar + ";000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_arm = progress;  //값 저장
        }
        else if(startNumber == 5) {
            btn_bein_1.setText(progressBar2);
            btn_bein_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;000;" + progressBar + ";000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_bein = progress;  //값 저장
        }
        else if(startNumber == 6) {
            btn_latt_1.setText(progressBar2);
            btn_latt_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;000;000;" + progressBar + ";000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_latt = progress;  //값 저장
        }
        else if(startNumber == 7) {
            btn_sideflank_1.setText(progressBar2);
            btn_sideflank_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;" + progressBar + ";000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_sideflank = progress;  //값 저장
        }
        else if(startNumber == 8) {
            btn_waist_1.setText(progressBar2);
            btn_waist_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;" + progressBar + ";000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_waist = progress;  //값 저장
        }
        else if(startNumber == 9) {
            btn_arsch_1.setText(progressBar2);
            btn_arsch_2.setText(progressBar2);
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
            mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;" + progressBar + ";N"); //전체강도 설정 전에 디폴트값 한번 보내준다
            strongValue_arsch = progress;  //값 저장
        }

        if (progress >= 100) {  // 강도 세기 100 까지 올라갔으면 자동으로 다음으로 넘어감
            startNumber++;

            try {
                Log.i(TAG_ACTIVITY, "before sleep (progress >= 100)");
                Thread.sleep(2000);
                defaultStrongSet(startNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void animationStart() {
        Log.i(TAG_ACTIVITY, "animationStart: blinkNumber = "+blinkNumber);

        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        if (blinkNumber == 0) {  //전체 강도 세기는 모든 부위 깜빡임
            btn_arsch_1.startAnimation(startAnimation);
            btn_arsch_2.startAnimation(startAnimation);

            btn_latt_1.startAnimation(startAnimation);
            btn_latt_2.startAnimation(startAnimation);

            btn_waist_1.startAnimation(startAnimation);
            btn_waist_2.startAnimation(startAnimation);

            btn_sideflank_1.startAnimation(startAnimation);
            btn_sideflank_2.startAnimation(startAnimation);

            btn_abdomen_1.startAnimation(startAnimation);
            btn_abdomen_2.startAnimation(startAnimation);

            btn_arm_1.startAnimation(startAnimation);
            btn_arm_2.startAnimation(startAnimation);

            btn_bein_1.startAnimation(startAnimation);
            btn_bein_2.startAnimation(startAnimation);

            btn_brust_1.startAnimation(startAnimation);
            btn_brust_2.startAnimation(startAnimation);
        }
        else {
            btn_brust_1.clearAnimation();
            btn_brust_1.setVisibility(View.INVISIBLE);
            btn_brust_2.clearAnimation();
            btn_brust_2.setVisibility(View.INVISIBLE);

            btn_arsch_1.clearAnimation();
            btn_arsch_1.setVisibility(View.INVISIBLE);
            btn_arsch_2.clearAnimation();
            btn_arsch_2.setVisibility(View.INVISIBLE);

            btn_latt_1.clearAnimation();
            btn_latt_1.setVisibility(View.INVISIBLE);
            btn_latt_2.clearAnimation();
            btn_latt_2.setVisibility(View.INVISIBLE);

            btn_waist_1.clearAnimation();
            btn_waist_1.setVisibility(View.INVISIBLE);
            btn_waist_2.clearAnimation();
            btn_waist_2.setVisibility(View.INVISIBLE);

            btn_sideflank_1.clearAnimation();
            btn_sideflank_1.setVisibility(View.INVISIBLE);
            btn_sideflank_2.clearAnimation();
            btn_sideflank_2.setVisibility(View.INVISIBLE);

            btn_abdomen_1.clearAnimation();
            btn_abdomen_1.setVisibility(View.INVISIBLE);
            btn_abdomen_2.clearAnimation();
            btn_abdomen_2.setVisibility(View.INVISIBLE);

            btn_arm_1.clearAnimation();
            btn_arm_1.setVisibility(View.INVISIBLE);
            btn_arm_2.clearAnimation();
            btn_arm_2.setVisibility(View.INVISIBLE);

            btn_bein_1.clearAnimation();
            btn_bein_1.setVisibility(View.INVISIBLE);
            btn_bein_2.clearAnimation();
            btn_bein_2.setVisibility(View.INVISIBLE);

            if (blinkNumber == 1) {  //부분 부위 세기는 해당 부위만 깜빡임
                btn_brust_1.startAnimation(startAnimation);
                btn_brust_2.startAnimation(startAnimation);
            } else if (blinkNumber == 2) {
                btn_abdomen_1.startAnimation(startAnimation);
                btn_abdomen_2.startAnimation(startAnimation);
            } else if (blinkNumber == 3) {
                btn_arm_1.startAnimation(startAnimation);
                btn_arm_2.startAnimation(startAnimation);
            } else if (blinkNumber == 4) {
                btn_bein_1.startAnimation(startAnimation);
                btn_bein_2.startAnimation(startAnimation);
            } else if (blinkNumber == 5) {
                btn_latt_1.startAnimation(startAnimation);
                btn_latt_2.startAnimation(startAnimation);
            } else if (blinkNumber == 6) {
                btn_sideflank_1.startAnimation(startAnimation);
                btn_sideflank_2.startAnimation(startAnimation);
            } else if (blinkNumber == 7) {
                btn_waist_1.startAnimation(startAnimation);
                btn_waist_2.startAnimation(startAnimation);
            } else if (blinkNumber == 8) {
                btn_arsch_1.startAnimation(startAnimation);
                btn_arsch_2.startAnimation(startAnimation);
            }
        }
    }

    private String intTostring (int value) {  //int to String 자릿수 3자리로 만드는 함수 예를 들어 int 0 -> String 000  으로
        return String.format(Locale.US, "%03d", value);
    }

    private void defaultStrongSet(int number) {
        if(number > 9) {
            // 강도설정 화면으로 이동.
            try {
                Log.i(TAG_ACTIVITY, "before sleep (defaultStrongSet)");
                Thread.sleep(2000);

                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                //startActivity(new Intent(DetailStrongActivity.this, DetailActivity.class));  //App 이 죽어서 막고 밑에처럼 한다 DetailActivity에서 detailTo 을 받지 않음 죽는다
                Intent i = new Intent(DetailStrongActivity.this, DetailActivity.class);
                i.putExtra("detailTo", "2");
                //부분 강도 설정 안했으면 20으로 보내자 그 외에는 저장 된 값 보냄
                if (strongValue_brust == 0)
                    strongValue_brust = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("brust",strongValue_brust);
                if (strongValue_abdomen == 0)
                    strongValue_abdomen = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("abdomen",strongValue_abdomen);
                if (strongValue_arm == 0)
                    strongValue_arm = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("arm",strongValue_arm);
                if (strongValue_bein == 0)
                    strongValue_bein = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("bein",strongValue_bein);
                if (strongValue_latt == 0)
                    strongValue_latt = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("latt",strongValue_latt);
                if (strongValue_sideflank == 0)
                    strongValue_sideflank = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("sideflank",strongValue_sideflank);
                if (strongValue_waist == 0)
                    strongValue_waist = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("waist",strongValue_waist);
                if (strongValue_arsch == 0)
                    strongValue_arsch = STRONG_BP_VALUE_DEFAULT;
                i.putExtra("arsch",strongValue_arsch);
                if (strongValue_all == 0)
                    strongValue_all = 5;
                i.putExtra("all",strongValue_all);  // 전체 강도값
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        btnStartText.setBackground(this.getResources().getDrawable(R.drawable.start_btn_on, this.getTheme()));
        btnStartText.setEnabled(false);

        TextView btnStopText = findViewById(R.id.btn_stop);
        btnStopText.setBackground(this.getResources().getDrawable(R.drawable.auto_stop_btn_off, this.getTheme()));
        btnStopText.setEnabled(true);

//        Log.i(TAG_ACTIVITY, "J.Y.T btn_stop 누른 후 start_btn_on ");

        animationStart();
//        mSeekBar.setProgress(0);
//        txtPersent.setText("0%");

/*
        final Handler handler = new Handler()
        {
            public void handleMessage(Message msg) {
                int progress = mSeekBar.getProgress();

                if (number == 1) {  //전체 강도는 1초에 5씩 증가 20 이후에는 1씩 증가
                    if (progress == 0) {
                        progress += strongValue_all;
                    }
                    else {
                        if (progress < 20)  // 전체강도 세기 20 까지 체크하여 나눔
                            progress += 5;
                        else
                            progress += 1;
                    }
                } else {  // 부분 강도는 그냥 1씩 증가
                    if (progress == 0) { //부분 강도는 처음에 20 이 default 값이다
                        if ((number == 2) && (strongValue_brust != 0))  //이렇게 하는 이유는 부분강도를 각 부위별로 세팅할 수 있어서 이리 한다
                            progress += strongValue_brust;
                        else if ((number == 3) && (strongValue_abdomen != 0))
                            progress += strongValue_abdomen;
                        else if ((number == 4) && (strongValue_arm != 0))
                            progress += strongValue_arm;
                        else if ((number == 5) && (strongValue_bein != 0))
                            progress += strongValue_bein;
                        else if ((number == 6) && (strongValue_latt != 0))
                            progress += strongValue_latt;
                        else if ((number == 7) && (strongValue_sideflank != 0))
                            progress += strongValue_sideflank;
                        else if ((number == 8) && (strongValue_waist != 0))
                            progress += strongValue_waist;
                        else if ((number == 9) && (strongValue_arsch != 0))
                            progress += strongValue_arsch;
                        else
                            progress += 21;
                    }
                    else
                        progress += 1;
                }
                mSeekBar.setProgress(progress);

                txtPersent.setText(String.format(Locale.US, "%d%%", progress));

                mHoloCircularProgressBar.setProgress((progress + 1) * 0.01f);
                int progressBar = progress;
                int AllstrongdefaultValue = 20; //전체 강도 세기 설정값 보내기 전에 default value 20을 한번 보내 달라고 함
                blinkNumber = number;
                Log.d(TAG_ACTIVITY, "J.Y.T defaultStrongSet number: "+number);
                if (number == 1) {
                    if (AllstrongdefaultValue_onetime == true) { //처음 실행시에 참으로 설정
                        mUsbReceiver.writeDataToSerial("A20;" + intTostring(strongValue_brust) + ";" + intTostring(strongValue_abdomen) + ";" + intTostring(strongValue_arm) + ";" + intTostring(strongValue_bein) + ";" + intTostring(strongValue_latt) + ";" + intTostring(strongValue_waist) + ";" + intTostring(strongValue_sideflank) + ";" + intTostring(strongValue_arsch) + ";N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                        AllstrongdefaultValue_onetime = false; //다시 보내지 않기 위해 거짓으로 설정
                    }
                    mUsbReceiver.writeDataToSerial("S23;" + intTostring(progressBar) + ";N");  //전체 강도 신호 보낼 때 A20;1(강도세기);0;0;0;0;0;0;0;0;N 에서 A23;4(강도세기);N로 바뀜
                    strongValue_all = progressBar;  //값 저장
                }
                else if(number == 2) {
                    btn_brust_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_brust_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;" + intTostring(progressBar) + ";000;000;000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_brust = progressBar;  //값 저장
                }
                else if(number == 3) {
                    btn_abdomen_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_abdomen_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;" + intTostring(progressBar) + ";000;000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_abdomen = progressBar;  //값 저장
                }
                else if(number == 4) {
                    btn_arm_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_arm_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;" + intTostring(progressBar) + ";000;000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_arm = progressBar;  //값 저장
                }
                else if(number == 5) {
                    btn_bein_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_bein_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;000;" + intTostring(progressBar) + ";000;000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_bein = progressBar;  //값 저장
                }
                else if(number == 6) {
                    btn_latt_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_latt_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;000;000;" + intTostring(progressBar) + ";000;000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_latt = progressBar;  //값 저장
                }
                else if(number == 7) {
                    btn_sideflank_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_sideflank_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;" + intTostring(progressBar) + ";000;000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_sideflank = progressBar;  //값 저장
                }
                else if(number == 8) {
                    btn_waist_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_waist_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;" + intTostring(progressBar) + ";000;N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_waist = progressBar;  //값 저장
                }
                else if(number == 9) {
                    btn_arsch_1.setText(String.format(Locale.US, "%d", progressBar));
                    btn_arsch_2.setText(String.format(Locale.US, "%d", progressBar));
//                    mUsbReceiver.writeDataToSerial("S24;"+intTostring((number - 1))+";"+intTostring(progressBar)+";N");  //부분 강도 신호 보낼 때 A20;0;1(강도세기);0;0;0;0;0;0;0;N 에서 A24;부위(1~8);강도세기(21~100);N 으로 바뀜
                    mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;" + intTostring(progressBar) + ";N"); //전체강도 설정 전에 디폴트값 한번 보내준다
                    strongValue_arsch = progressBar;  //값 저장
                }
                if (progressBar == 100) {  // 강도 세기 100 까지 올라갔으면 자동으로 다음으로 넘어감
                    startNumber++;

                    try {
                        Thread.sleep(2000);
                        defaultStrongSet(startNumber);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
*/

        new Thread(new Runnable() {
            public void run() {
                for(;;) {
                    try {
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);

                        Thread.sleep(1000);  //강도 설정값 2초마다 보내는거 1초 마다 보내게 수정

                        if (number == 1) {
                            if(startNumber > 1) {
                                break;
                            }
                        }
                        else if (number == 2) {
                            if(startNumber > 2) {
                                break;
                            }
                        }
                        else if (number == 3) {
                            if(startNumber > 3) {
                                break;
                            }
                        }
                        else if (number == 4) {
                            if(startNumber > 4) {
                                break;
                            }
                        }
                        else if (number == 5) {
                            if(startNumber > 5) {
                                break;
                            }
                        }
                        else if (number == 6) {
                            if(startNumber > 6) {
                                break;
                            }
                        }
                        else if (number == 7) {
                            if(startNumber > 7) {
                                break;
                            }
                        }
                        else if (number == 8) {
                            if(startNumber > 8) {
                                break;
                            }
                        }
                        else if (number == 9) {
                            if(startNumber > 9) {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences setting ;
        SharedPreferences.Editor editor;
        int progress = mSeekBar.getProgress();

        switch (v.getId()) {
            case R.id.btnOk:
                //Log.i(TAG_ACTIVITY, "J.Y.T btnOk 누름");
                mUsbReceiver.writeDataToSerial("S21;90;00;100;000;050;05;020;05;005;1;25;N");  //Waveform default
                ImageButton imageButton = findViewById(R.id.btn_back);
                imageButton.setEnabled(true);
                LinearLayout ll = findViewById(R.id.subMenu);
                ll.setVisibility(View.GONE);
                valueDisplay();  //강도 설정 진입전에 설정된 값 뿌리기 위해
                break;

            case R.id.btn_cancel:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_cancel 누름");
                startNumber = 10;  //이거는 쓰레드 종료를 위하여
                Intent a = new Intent(DetailStrongActivity.this, DetailActivity.class);
                a.putExtra("detailTo", "2");
                a.putExtra("brust",strongValue_brust);
                a.putExtra("abdomen",strongValue_abdomen);
                a.putExtra("arm",strongValue_arm);
                a.putExtra("bein",strongValue_bein);
                a.putExtra("latt",strongValue_latt);
                a.putExtra("sideflank",strongValue_sideflank);
                a.putExtra("waist",strongValue_waist);
                a.putExtra("arsch",strongValue_arsch);
                a.putExtra("all",strongValue_all);
                startActivity(a);
                finish();
                break;

            case R.id.btn_minus:
                mSeekBar.setProgress(progress - 1);
                txtPersent.setText(String.format(Locale.US, "%d%%", progress));

                mHoloCircularProgressBar.setProgress((progress-1) * 0.01f);
                break;

            case R.id.btn_plus:
                mSeekBar.setProgress(progress + 1);
                txtPersent.setText(String.format(Locale.US, "%d%%", progress));

                mHoloCircularProgressBar.setProgress((progress+1) * 0.01f);
                break;

            case R.id.btn_back:
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                Intent i = new Intent(DetailStrongActivity.this, MenuActivity.class);
                startNumber = 10;  //이거는 쓰레드 종료를 위하여
                i.putExtra("detailTo", "1");
                startActivity(i);
                finish();
                break;

            case R.id.btn_stop: // 멈춤
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_stop 누름");
                if (!startButtonpush)  //강도 설정에서 시작 누르기 전에 멈춤부터 누르면 오류 방지를 위해
                    return;
                btnStartText.setBackground(this.getResources().getDrawable(R.drawable.start_btn_off, this.getTheme()));
                btnStartText.setEnabled(true);

                TextView btnStopText = findViewById(R.id.btn_stop);
                btnStopText.setBackground(this.getResources().getDrawable(R.drawable.auto_stop_btn_on, this.getTheme()));
                btnStopText.setEnabled(false);

                //Log.i(TAG_ACTIVITY, "J.Y.T btn_stop 누른 후 start_btn_off ");
                startNumber++;
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_stop startNumber: "+startNumber);
                // 2초후 다시 시작 버튼 클릭.
//                try {
//                    Thread.sleep(2000);
//                    //Log.i(TAG_ACTIVITY, "J.Y.T btn_stop 2초 후 ");
//                    defaultStrongSet(startNumber);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        defaultStrongSet(startNumber);
                    }
                };
                //handler.sendEmptyMessageDelayed(0, 2000);
                handler.sendEmptyMessageDelayed(0, 1000);

                break;

            case R.id.btn_start: // 시작
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_start 누름");
                if (!startButtonpush)  //강도세기 시작 누른 후 다시 시작을 누르면 이상해짐
                    startButtonpush = true;
                else // 그래서 한번 누른 후 그 다음 부터는 동작 안하게 함
                    return;
                if (startNumber == 1) { // 전체강도
                    //mUsbReceiver.writeDataToSerial("A20;000;N");
                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 2) { // 흉부 btn_brust_1
                    //TextView textView1 = findViewById(R.id.btn_brust_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_brust_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 3) { // 복부 btn_abdomen_1
                    //TextView textView1 = findViewById(R.id.btn_abdomen_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_abdomen_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 4) { // 상완 btn_arm_1

                    //TextView textView1 = findViewById(R.id.btn_arm_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_arm_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 5) { // 허벅다리 btn_bein_1
                    //TextView textView1 = findViewById(R.id.btn_bein_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_bein_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 6) { // 어깨 btn_latt_1
                    //TextView textView1 = findViewById(R.id.btn_latt_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_latt_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 7) { // 옆구리 btn_sideflank_1
                    //TextView textView1 = findViewById(R.id.btn_sideflank_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_sideflank_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 8) { // 허리 btn_waist_1

                    //TextView textView1 = findViewById(R.id.btn_waist_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_waist_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                else if(startNumber == 9) { // 둔부 btn_arsch_1
                    //TextView textView1 = findViewById(R.id.btn_arsch_1);
                    //Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView1.startAnimation(startAnimation);
                    //TextView textView2 = findViewById(R.id.btn_arsch_2);
                    //startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
                    //textView2.startAnimation(startAnimation);

                    defaultStrongSet(startNumber);
                }
                break;

            case R.id.btn_abdomen_51: // 복부
            case R.id.btn_abdomen_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_abdomen");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "abdo");
                editor.apply();

                startActivity(new Intent(DetailStrongActivity.this, DetailFrontActivity.class));
                finish();
                break;

            case R.id.btn_arm_51: // 상완 btn_arm_1
            case R.id.btn_arm_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_arm");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arm");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailFrontActivity.class));
                finish();
                break;

            case R.id.btn_bein_51: // 허벅다리
            case R.id.btn_bein_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_bein");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "bein");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailFrontActivity.class));
                finish();
                break;

            case R.id.btn_brust_51: // 흉부
            case R.id.btn_brust_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_brust");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "brust");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailFrontActivity.class));
                finish();
                break;

            case R.id.btn_arsch_51: // 둔부
            case R.id.btn_arsch_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_arsch");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arsch");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailBackActivity.class));
                finish();
                break;

            case R.id.btn_latt_51: // 어깨
            case R.id.btn_latt_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_latt");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "latt");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailBackActivity.class));
                finish();
                break;

            case R.id.btn_waist_51: // 허리
            case R.id.btn_waist_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_waist");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "waist");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailBackActivity.class));
                finish();
                break;

            case R.id.btn_sideflank_51: // 옆구리
            case R.id.btn_sideflank_52:
                //Log.i(TAG_ACTIVITY, "J.Y.T btn_sideflank");
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "sideflank");
                editor.commit();

                startActivity(new Intent(DetailStrongActivity.this, DetailBackActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG_ACTIVITY, "Progress = " + progress);
/*
        //seekBar 도 설정대로 나타나게 한다
        mSeekBar.setProgress(progress);
        //seekBar 도 설정대로 나타나게 한다
        txtPersent.setText(String.format(Locale.US, "%d%%", progress));
        mHoloCircularProgressBar.setProgress(progress * 0.01f);
*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

/*
    public void longClickPlus() {
        int progress = mSeekBar.getProgress();
        mSeekBar.setProgress(progress + 1);
        TextView textView = findViewById(R.id.txtPersent);
        textView.setText(String.format(Locale.US, "%d%%", progress));
    }
    public void longClickMinus() {
        int progress = mSeekBar.getProgress();
        mSeekBar.setProgress(progress - 1);
        TextView textView = findViewById(R.id.txtPersent);
        textView.setText(String.format(Locale.US, "%d%%", progress));
    }
*/

    @Override
    public void onBackPressed() {

    }
}
