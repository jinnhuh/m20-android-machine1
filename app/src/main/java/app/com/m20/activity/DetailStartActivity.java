package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import app.com.m20.customview.HoloCircularProgressBar;
import app.com.m20.customview.PopupDialog;
import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;

/**
 * Created by kimyongyeon on 2017-11-20.
 */

public class DetailStartActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    String TAG_ACTIVITY = "M20_DetailStart";

    //스톱워치의 상태를 위한 상수
    final static int IDLE = 0;
    final static int RUNNING = 1;
    final static int PAUSE = 2;
    int mStatus = IDLE;//처음 상태는 IDLE
    long mBaseTime;
    int iEllapse;
    int iAlltimer;
    int iFirstAlltimer;

    CountDownTimer mCountDown = null;
    CountDownTimer mAllSender = null;
    CountDownTimer mFirstAllSender = null;
    static String timerBuffer; // 04:11:15 등의 경과 시간 문자열이 저장될 버퍼 정의

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    private int brust_value = 0;
    private int abdomen_value = 0;
    private int arm_value = 0;
    private int bein_value = 0;
    private int latt_value = 0;
    private int sideflank_value = 0;
    private int waist_value = 0;
    private int arsch_value = 0;
    private int all_value = 0;
    String playID = null;  //어떤 운동인지 저장
    private int seperatetime = 0;  //운동이 15분과 20분으로 나뉘어져 있어서 구별을 위해서
    String now_sequence = null;  //멈추었다가 다시 운동 시작 할 때 어떤 운동인지 알기 위해
    private int play_seq_num = 0;
    String play_seq_str = null;
    BroadcastReceiver mBR;
//    private boolean not_send_remote = false;  // 리모컨으로 전체 강도 바뀌었는지 flag


    TextView mEllapse;
    private PopupDialog popupDialog;
    SeekBar mSeekBar;
    private HoloCircularProgressBar mHoloCircularProgressBar;
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
    TextView textViewPercent4;

    final static int TOTAL = 0;
    final static int BRUST = 1;
    final static int ABDOMEN = 2;
    final static int ARM = 3;
    final static int BEIN = 4;
    final static int LATT = 5;
    final static int WAIST = 6;
    final static int FLANK = 7;
    final static int ARSCH = 8;
    int mSelect = TOTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_4);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
//        mUsbReceiver.writeDataToSerial("S22;N");  //처음 신호 안가게 종료 한번 준다
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
//            Toast.makeText(this, "no connectionDetailStart", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
//                        if(str.equals("reg")) {
//                            Intent intent = new Intent(this, PersonCheckupActivity.class);
//                            activity.startActivity(intent);
//                            activity.finish();
//                        }
        }
        Intent intent = getIntent();  //강도 설정한 값 받자
        if(intent!=null) {
            brust_value = intent.getIntExtra("brust", 20);
            abdomen_value = intent.getIntExtra("abdomen", 20);
            arm_value = intent.getIntExtra("arm", 20);
            bein_value = intent.getIntExtra("bein", 20);
            latt_value = intent.getIntExtra("latt", 20);
            sideflank_value = intent.getIntExtra("sideflank", 20);
            waist_value = intent.getIntExtra("waist", 20);
            arsch_value = intent.getIntExtra("arsch", 20);
            all_value = intent.getIntExtra("all", 5);
            playID = intent.getStringExtra("menu");  //어떤 운동인지 받자
        }
        if (playID.equals("위축된 근육 컨디션 조절") || playID.equals("위축된 근력 컨디션 조절")) //두 개의 운동은 시간이 15분임
            seperatetime = 15;
        else // 그외는 20분임
            seperatetime = 20;

        iAlltimer = 0;
        iFirstAlltimer = 0;

        TextView textView = findViewById(R.id.txtTitle);
        SharedPreferences setting = getSharedPreferences("setting", 0);
        textView.setText(setting.getString("main_title", ""));
        SharedPreferences time =getSharedPreferences("booking_end_time", MODE_PRIVATE);
        String booking_end_time = time.getString("end_time", "0"); //키값, 디폴트값
        Log.d(TAG_ACTIVITY, "Booking end time = " + booking_end_time);
        endtimecheck(booking_end_time);   //end time 과 현재 시간 체크하자

        startTimedataSaved(); //시작 시간 저장

        mHoloCircularProgressBar = findViewById(R.id.holoCircularProgressBar);
        textViewPercent4 = findViewById(R.id.txtPersent4);
        mSeekBar = findViewById(R.id.btn_sb4);

        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setEnabled(false);

        //처음부터 전체 강도 보여지게 하기 위해
//        onProgressChanged(mSeekBar, all_value,true);
        mSeekBar.setProgress(all_value);
        textViewPercent4.setText(String.format(Locale.US, "%d%%", all_value));
        mHoloCircularProgressBar.setProgress(all_value * 0.01f);

//         mediaPlayer = new MediaPlayer();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.start);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener((tmp) -> {
            //TextView textView1 = findViewById(R.id.txtPersent);
            //String totalPer[] = textView1.getText().toString().split("%");
            //String per = totalPer[0];
            //mUsbReceiver.writeDataToSerial("A41;01;1;"+per+";N");

            animationStart();

            if (mStatus != PAUSE ) {  //stop 버튼을 누르고 종료 팝업에서 취소 눌렀을 경우 시간이 멈춰 있어야 하는데 계속 가는 현상이 있어서 수정 (보통은 안그런데 activity 시작 후 잽싸게 하면 오류 생김)
                Log.d(TAG_ACTIVITY, "onCreate: mStatus != PAUSE.");

                mCountDown = new CountDownTimer(1000 * 60 * seperatetime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        iEllapse = (int) millisUntilFinished / 1000;
                        secToHHMMSS(iEllapse);
                        mEllapse.setText(timerBuffer);
                        waveform();
                    }

                    public void onFinish() {
                        mUsbReceiver.writeDataToSerial("S22;N");  //운동 끝나면  보낸다
                        now_sequence = null;  //종료시 초기화
                        // 모든 시간이 다 되면 이동한다.
                        enddataSaved();  //운동 종료 할때 저장하는 data
                        startActivity(new Intent(DetailStartActivity.this, EndActivity.class));
                        finish();
                    }
                }.start();
            }

            ImageButton imageButton = findViewById(R.id.btn_play);
            imageButton.setBackground(this.getResources().getDrawable(R.drawable.play_btn_on, this.getTheme()));
            imageButton.setEnabled(false);

            ImageButton imageButton2 = findViewById(R.id.btn_pause);
            imageButton2.setEnabled(true);

            ImageButton imageButton3 = findViewById(R.id.btn_stop);
            imageButton3.setEnabled(true);

        });

        btn_arsch_1 = findViewById(R.id.btn_arsch_41);
        btn_arsch_2 = findViewById(R.id.btn_arsch_42);
        btn_latt_1 = findViewById(R.id.btn_latt_41);
        btn_latt_2 = findViewById(R.id.btn_latt_42);
        btn_waist_1 = findViewById(R.id.btn_waist_41);
        btn_waist_2 = findViewById(R.id.btn_waist_42);
        btn_sideflank_1 = findViewById(R.id.btn_sideflank_41);
        btn_sideflank_2 = findViewById(R.id.btn_sideflank_42);
        btn_abdomen_1 = findViewById(R.id.btn_abdomen_41);
        btn_abdomen_2 = findViewById(R.id.btn_abdomen_42);
        btn_arm_1 = findViewById(R.id.btn_arm_41);
        btn_arm_2 = findViewById(R.id.btn_arm_42);
        btn_bein_1 = findViewById(R.id.btn_bein_41);
        btn_bein_2 = findViewById(R.id.btn_bein_42);
        btn_brust_1 = findViewById(R.id.btn_brust_41);
        btn_brust_2 = findViewById(R.id.btn_brust_42);
        mEllapse = findViewById(R.id.txtEllapse);
        secToHHMMSS(seperatetime*60);
        mEllapse.setText(timerBuffer);

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        btn_latt_1.setOnClickListener(this);
        btn_latt_2.setOnClickListener(this);
        btn_waist_1.setOnClickListener(this);
        btn_waist_2.setOnClickListener(this);
        btn_sideflank_1.setOnClickListener(this);
        btn_sideflank_2.setOnClickListener(this);
        btn_arsch_1.setOnClickListener(this);
        btn_arsch_2.setOnClickListener(this);
        btn_brust_1.setOnClickListener(this);
        btn_brust_2.setOnClickListener(this);
        btn_arm_1.setOnClickListener(this);
        btn_arm_2.setOnClickListener(this);
        btn_abdomen_1.setOnClickListener(this);
        btn_abdomen_2.setOnClickListener(this);
        btn_bein_1.setOnClickListener(this);
        btn_bein_2.setOnClickListener(this);
        textViewPercent4.setOnClickListener(this);

//        findViewById(R.id.btn_minus).setOnTouchListener((v, event) -> {
//            longClickMinus();
//            return true;
//        });
//        findViewById(R.id.btn_plus).setOnTouchListener((v, event) -> {
//            longClickPlus();
//            return true;
//        });

//        ImageButton imageButton = findViewById(R.id.btn_play);
//        imageButton.callOnClick();

        valueTextDisplay(); // 받은 값 display
    }

    private void startTimedataSaved() {  //시작 시간 저장
        long startTime = System.currentTimeMillis();  //운동 시작하면 현재 시간 저장
        SharedPreferences starttime =getSharedPreferences("startTime_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = starttime.edit();
        editor.putLong("Data_startTime",startTime);
        editor.apply();
    }

    private String intTostring (int value) {  //int to String 자릿수 3자리로 만드는 함수 예를 들어 int 0 -> String 000  으로
        return String.format(Locale.US, "%03d", value);
    }

    private String intTotwostring (int value) {  //int to String 자릿수 2자리로 만드는 함수 예를 들어 int 0 -> String 00  으로
        return String.format(Locale.US, "%02d", value);
    }

    private void allValueTostring () {  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
        startAllValueSendTimer();
    }

    private void allValueSendTimer() {  //Pause 후 resume 하면 또 어이없이 전체 강도 1부터 보내 달란다
        mAllSender = new CountDownTimer((all_value + 1) * 500, 500) {
            public void onTick(long millisUntilFinished) {
                iAlltimer ++;
                if (iAlltimer == 1) {
                    startWaveformResume ();  //현재의 WaveForm 한번 보냄
                    mUsbReceiver.writeDataToSerial("A20;" + intTostring(brust_value) + ";" + intTostring(abdomen_value) + ";" + intTostring(arm_value) + ";" + intTostring(bein_value) + ";" + intTostring(latt_value) + ";" + intTostring(waist_value) + ";" + intTostring(sideflank_value) + ";" + intTostring(arsch_value) + ";N");
                }
                mUsbReceiver.writeDataToSerial("S23;" + intTostring(iAlltimer) + ";N");
                //Log.d(TAG_ACTIVITY, "J.Y.T iAlltimer: "+iAlltimer);
            }
            public void onFinish() {
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "AllTimer is finished (%d).", iAlltimer));
                iAlltimer = 0;
            }
        }.start();
    }

    private void startAllValueSendTimer() {  //운동시작용 전체 강도 1부터 보내 달란다
        mFirstAllSender = new CountDownTimer((all_value + 1) * 500, 500) {
            public void onTick(long millisUntilFinished) {
                iFirstAlltimer ++;
                mUsbReceiver.writeDataToSerial("S23;" + intTostring(iFirstAlltimer) + ";N");
                //Log.d(TAG_ACTIVITY, "J.Y.T iFirstAlltimer: "+iFirstAlltimer);
            }
            public void onFinish() {
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "FirstAllTimer is finished (%d).", iFirstAlltimer));
                 iFirstAlltimer = 0;
            }
        }.start();
    }

    private void partAndall() {  //운동 시퀀스 바뀌면 쓸데없이 부분 전체 또 보내 달란다
        mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N");
        mUsbReceiver.writeDataToSerial("S23;" + intTostring(all_value) + ";N");
    }

    private void waveform() {
        if (playID.equals("근육강화")) {
            if (iEllapse == 1199) {
                now_sequence = "근육강화_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N"); //부분 강도와 전체 강도를 같이 달란다. 전체는 1부터 강도 설정까지
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1139 || iEllapse == 1063 || iEllapse == 987 || iEllapse == 911 || iEllapse == 835 || iEllapse == 759 || iEllapse == 683 || iEllapse == 607 || iEllapse == 531 || iEllapse == 455 || iEllapse == 379 || iEllapse == 303 || iEllapse == 227 || iEllapse == 151) {
                now_sequence = "근육강화_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";000;003;100;10;060;10;040;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1067 || iEllapse == 991 || iEllapse == 915 || iEllapse == 839 || iEllapse == 763 || iEllapse == 687 || iEllapse == 611 || iEllapse == 535 || iEllapse == 459 || iEllapse == 383 || iEllapse == 307 || iEllapse == 231 || iEllapse == 155 || iEllapse == 79) {
                mUsbReceiver.writeDataToSerial("S30;N");  //전체 강도 (휴식)
            }
            else if (iEllapse == 75) {
                now_sequence = "근육강화_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("근력강화")) {
            if (iEllapse == 1199) {
                now_sequence = "근력강화_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1129 || iEllapse == 1023 || iEllapse == 917 || iEllapse == 811 || iEllapse == 705 || iEllapse == 599) {
                now_sequence = "근력강화_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;070;100;20;030;30;020;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1029 || iEllapse == 923 || iEllapse == 817 || iEllapse == 711 || iEllapse == 605 || iEllapse == 499 || iEllapse == 413 || iEllapse == 327 || iEllapse == 241 || iEllapse == 155 || iEllapse == 69) {
                mUsbReceiver.writeDataToSerial("S30;N");  //전체 강도 (휴식)
            }
            else if (iEllapse == 493 || iEllapse == 407 || iEllapse == 321 || iEllapse == 235 || iEllapse == 149) {
                now_sequence = "근력강화_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;090;100;20;020;30;010;1;25;N");
                partAndall();
            }
            else if (iEllapse == 63) {
                now_sequence = "근력강화_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("순발력 강화")) {
            if (iEllapse == 1199) {
                now_sequence = "순발력 강화_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1119 || iEllapse == 1054 || iEllapse == 989 | iEllapse == 924 || iEllapse == 859 || iEllapse == 794 || iEllapse == 729 || iEllapse == 664 || iEllapse == 599 || iEllapse == 534 || iEllapse == 469 || iEllapse == 404 || iEllapse == 339 || iEllapse == 274 || iEllapse == 209 || iEllapse == 144) {
                now_sequence = "순발력 강화_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;085;100;20;020;30;040;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1064 || iEllapse == 999 || iEllapse == 934 || iEllapse == 869 || iEllapse == 804 || iEllapse == 739 || iEllapse == 674 || iEllapse == 609 || iEllapse == 544 || iEllapse == 479 || iEllapse == 414 || iEllapse == 349 || iEllapse == 284 || iEllapse == 219 || iEllapse == 154 || iEllapse == 89) {
                mUsbReceiver.writeDataToSerial("S30;N"); //전체 강도 (휴식)
            }
            else if (iEllapse == 79) {
                now_sequence = "순발력 강화_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("근지구력 강화")) {
            if (iEllapse == 1199) {
                now_sequence = "근지구력 강화_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1139 || iEllapse == 925 || iEllapse == 711 || iEllapse == 497 || iEllapse == 283) {
                now_sequence = "근지구력 강화_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;050;100;20;060;30;030;1;25;N");
                partAndall();
            }
            else if (iEllapse == 929 || iEllapse == 715 || iEllapse == 501 || iEllapse == 287 || iEllapse == 73) {
                mUsbReceiver.writeDataToSerial("S30;N"); //전체 강도 (휴식)
            }
            else if (iEllapse == 69) {
                now_sequence = "근지구력 강화_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("지구력 강화")) {
            if (iEllapse == 1199) {
                now_sequence = "지구력 강화_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;010;050;10;120;10;010;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1169 || iEllapse == 1043 || iEllapse == 917) {
                now_sequence = "지구력 강화_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;020;100;10;030;10;010;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1049 || iEllapse == 923 || iEllapse == 797 || iEllapse == 671 || iEllapse == 545 || iEllapse == 419 || iEllapse == 293 || iEllapse == 165 || iEllapse == 37) {
                mUsbReceiver.writeDataToSerial("S30;N"); //전체 강도 (휴식)
            }
            else if (iEllapse == 791 || iEllapse == 665 || iEllapse == 539) {
                now_sequence = "지구력 강화_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;040;100;10;030;10;010;1;25;N");
                partAndall();
            }
            else if (iEllapse == 413 || iEllapse == 285 || iEllapse == 157) {
                now_sequence = "지구력 강화_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";080;070;100;10;030;10;010;1;25;N");
                partAndall();
            }
            else if (iEllapse == 29) {
                now_sequence = "지구력 강화_5";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;010;050;10;030;10;010;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("체지방")) {
            if (iEllapse == 1199) {
                now_sequence = "체지방_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";050;100;050;10;030;10;010;1;20;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1079 || iEllapse == 779 || iEllapse == 479 || iEllapse == 179) {
                now_sequence = "체지방_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";075;010;075;04;012;04;004;1;20;N");
                partAndall();
            }
            else if (iEllapse == 899 || iEllapse == 599 || iEllapse == 359) {
                now_sequence = "체지방_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";050;100;050;10;030;10;010;1;20;N");
                partAndall();
            }
            else if (iEllapse == 59) {
                now_sequence = "체지방_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";030;100;000;00;000;00;000;0;20;N");
            }
        }
        else if (playID.equals("셀룰라이트")) {
            if (iEllapse == 1199) {
                now_sequence = "셀롤라이트_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1119) {
                now_sequence = "셀롤라이트_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;050;03;010;03;004;1;20;N");
                partAndall();
            }
            else if (iEllapse == 999) {
                now_sequence = "셀롤라이트_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;075;10;030;10;010;1;20;N");
                partAndall();
            }
            else if (iEllapse == 879) {
                now_sequence = "셀롤라이트_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;050;075;10;030;10;010;1;20;N");
                partAndall();
            }
            else if (iEllapse == 759) {
                now_sequence = "셀롤라이트_5";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;001;075;10;030;10;010;1;20;N");
                partAndall();
            }
            else if (iEllapse == 679) {
                now_sequence = "셀롤라이트_6";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";025;010;025;10;060;10;030;1;20;N");
                partAndall();
            }
            else if (iEllapse == 429) {
                now_sequence = "셀롤라이트_7";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";050;010;075;10;090;10;040;1;20;N");
                partAndall();
            }
            else if (iEllapse == 149) {
                now_sequence = "셀롤라이트_8";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";025;001;045;05;015;05;005;1;20;N");
                partAndall();
            }
        }
        else if (playID.equals("마른체형 근육")) {
            if (iEllapse == 1199) {
                now_sequence = "마른체형 근육_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;003;000;01;020;01;000;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1139 || iEllapse == 1063 || iEllapse == 987 || iEllapse == 911 || iEllapse == 835 || iEllapse == 759 || iEllapse == 683 || iEllapse == 607 || iEllapse == 531 || iEllapse == 455 || iEllapse == 379 || iEllapse == 303 || iEllapse == 227 || iEllapse == 151) {
                now_sequence = "마른체형 근육_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;008;100;10;060;10;040;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1067 || iEllapse == 991 || iEllapse == 915 || iEllapse == 839 || iEllapse == 763 || iEllapse == 687 || iEllapse == 611 || iEllapse == 535 || iEllapse == 459 || iEllapse == 383 || iEllapse == 307 || iEllapse == 231 || iEllapse == 155 || iEllapse == 79) {
                mUsbReceiver.writeDataToSerial("S30;N");//전체 강도 (휴식)
            }
            else if (iEllapse == 75) {
                now_sequence = "마른체형 근육_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;005;000;01;020;01;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("마른체형 근력")) {
            if (iEllapse == 1199) {
                now_sequence = "마른체형 근력_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;003;000;01;010;01;000;0;25;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1129 || iEllapse == 1023 || iEllapse == 917 || iEllapse == 811 || iEllapse == 705 || iEllapse == 599) {
                now_sequence = "마른체형 근력_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;007;100;10;040;10;040;1;25;N");
                partAndall();
            }
            else if (iEllapse == 1029 || iEllapse == 923 || iEllapse == 817 || iEllapse == 711 || iEllapse == 605 || iEllapse == 499 || iEllapse == 413 || iEllapse == 327 || iEllapse == 241 || iEllapse == 155 || iEllapse == 69) {
                mUsbReceiver.writeDataToSerial("S30;N");  //전체 강도 (휴식)
            }
            else if (iEllapse == 493 || iEllapse == 407 || iEllapse == 321 || iEllapse == 235 || iEllapse == 149) {
                now_sequence = "마른체형 근력_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;009;100;10;030;10;030;1;25;N");
                partAndall();
            }
            else if (iEllapse == 63) {
                now_sequence = "마른체형 근력_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;005;000;01;160;01;000;0;25;N");
                partAndall();
            }
        }
        else if (playID.equals("스트레칭")) {
            if (iEllapse == 1199) {
                now_sequence = "스트레칭_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1139 || iEllapse == 719 || iEllapse == 239) {
                now_sequence = "스트레칭_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";025;010;025;08;025;08;008;1;20;N");
                partAndall();
            }
            else if (iEllapse == 959) {
                now_sequence = "스트레칭_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";000;000;025;20;100;30;000;1;20;N");
                partAndall();
            }
            else if (iEllapse == 479) {
                now_sequence = "스트레칭_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";000;000;025;30;100;30;000;1;20;N");
                partAndall();
            }
        }
        else if (playID.equals("위축된 근육 컨디션 조절")) {  //시간이 20분이 아니라 15분임 그래서 899
            if (iEllapse == 899) {
                now_sequence = "위축된 근육 컨디션 조절_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;002;000;00;000;00;000;0;50;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 659 || iEllapse == 239) {
                now_sequence = "위축된 근육 컨디션 조절_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;004;000;00;000;00;000;0;50;N");
                partAndall();
            }
            else if (iEllapse == 419) {
                now_sequence = "위축된 근육 컨디션 조절_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;006;000;00;000;00;000;0;50;N");
                partAndall();
            }
        }
        else if (playID.equals("정상 근육 컨디션 조절")) {
            if (iEllapse == 1199) {
                now_sequence = "정상 근육 컨디션 조절_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;002;000;00;000;00;000;0;30;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 899) {
                now_sequence = "정상 근육 컨디션 조절_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;006;000;00;000;00;000;0;30;N");
                partAndall();
            }
            else if (iEllapse == 599) {
                now_sequence = "정상 근육 컨디션 조절_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;008;000;00;000;00;000;0;30;N");
                partAndall();
            }
            else if (iEllapse == 299) {
                now_sequence = "정상 근육 컨디션 조절_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;004;000;00;000;00;000;0;30;N");
                partAndall();
            }
        }
        else if (playID.equals("위축된 근력 컨디션 조절")) {   //시간이 20분이 아니라 15분임 그래서 899
            if (iEllapse == 899) {
                now_sequence = "위축된 근력 컨디션 조절_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";100;003;000;00;000;00;000;0;50;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 864 || iEllapse == 698 || iEllapse == 532) {
                now_sequence = "위축된 근력 컨디션 조절_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";080;035;100;10;040;10;010;1;50;N");
                partAndall();
            }
            else if (iEllapse == 704 || iEllapse == 538 || iEllapse == 372 || iEllapse == 206 || iEllapse == 40) {
                mUsbReceiver.writeDataToSerial("S30;N"); //전체 강도 (휴식)
            }
            else if (iEllapse == 366 || iEllapse == 200) {
                now_sequence = "위축된 근력 컨디션 조절_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";080;045;100;10;040;10;010;1;50;N");
                partAndall();
            }
            else if (iEllapse == 34) {
                now_sequence = "위축된 근력 컨디션 조절_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";100;002;000;00;000;00;000;0;50;N");
            }
        }
        else if (playID.equals("정상 근력 컨디션 조절")) {
            if (iEllapse == 1199) {
                now_sequence = "정상 근력 컨디션 조절_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";100;005;000;00;000;00;000;0;35;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1172 || iEllapse == 966 || iEllapse == 760) {
                now_sequence = "정상 근력 컨디션 조절_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";080;055;050;10;60;10;120;0;35;N");
                partAndall();
            }
            else if (iEllapse == 972 || iEllapse == 766 || iEllapse == 560 || iEllapse == 384 || iEllapse == 208 || iEllapse == 32) {
                mUsbReceiver.writeDataToSerial("S30;N"); //전체 강도 (휴식)
            }
            else if (iEllapse == 554 || iEllapse == 378 || iEllapse == 202) {
                now_sequence = "정상 근력 컨디션 조절_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";080;065;050;10;50;10;100;0;35;N");
                partAndall();
            }
            else if (iEllapse == 26) {
                now_sequence = "정상 근력 컨디션 조절_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";100;004;000;00;000;00;000;0;35;N");
                partAndall();
            }
        }
        else if (playID.equals("혈액순환 개선")) {
            if (iEllapse == 1199) {
                now_sequence = "혈액순환 개선_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;050;050;03;010;03;004;1;20;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1139 || iEllapse == 419) {
                now_sequence = "혈액순환 개선_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;020;050;03;010;03;040;1;20;N");
                partAndall();
            }
            else if (iEllapse == 1019 || iEllapse == 299) {
                now_sequence = "혈액순환 개선_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;010;050;03;010;03;040;1;20;N");
                partAndall();
            }
            else if (iEllapse == 839) {
                now_sequence = "혈액순환 개선_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";030;020;030;07;020;06;007;1;20;N");
                partAndall();
            }
            else if (iEllapse == 599) {
                now_sequence = "혈액순환 개선_5";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;010;030;07;026;07;000;1;20;N");
                partAndall();
            }
            else if (iEllapse == 479) {
                now_sequence = "혈액순환 개선_6";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;001;000;00;000;00;000;0;20;N");
                partAndall();
            }
            else if (iEllapse == 119) {
                now_sequence = "혈액순환 개선_7";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;001;000;00;000;00;000;1;20;N");
                partAndall();
            }
            else if (iEllapse == 59) {
                now_sequence = "혈액순환 개선_8";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;100;010;10;040;10;000;1;20;N");
                partAndall();
            }
        }
        else if (playID.equals("저속 마사지")) {
            if (iEllapse == 1199) {
                now_sequence = "저속 마사지";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;16;"+play_seq_str+";000;100;100;05;005;05;025;2;25;N");  //마지막 025~045 중 선택예정
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
        }
        else if (playID.equals("중속 마사지")) {
            if (iEllapse == 1199) {
                now_sequence = "중속 마사지";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;17;"+play_seq_str+";000;100;100;02;000;02;012;2;25;N");  //마지막 012~020 중 선택예정
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
        }
        else if (playID.equals("고속 마사지")) {
            if (iEllapse == 1199) {
                now_sequence = "고속 마사지";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;18;"+play_seq_str+";000;100;100;01;000;01;006;2;25;N");  //마지막 006~010 중 선택예정
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
        }
        else if (playID.equals("림프 마사지")) {
            if (iEllapse == 1199) {
                now_sequence = "림프 마사지_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 1079) {
                now_sequence = "림프 마사지_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";075;100;055;03;010;03;004;1;20;N");
                partAndall();
            }
            else if (iEllapse == 959) {
                now_sequence = "림프 마사지_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";000;000;080;15;050;15;020;1;20;N");
                partAndall();
            }
            else if (iEllapse == 779) {
                now_sequence = "림프 마사지_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;100;080;10;090;10;040;1;20;N");
                partAndall();
            }
            else if (iEllapse == 599) {
                now_sequence = "림프 마사지_5";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;030;055;03;010;03;040;1;20;N");
                partAndall();
            }
            else if (iEllapse == 239) {
                now_sequence = "림프 마사지_6";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;015;035;05;015;05;005;1;20;;N");
                partAndall();
            }
        }
        else if (playID.equals("회복 마사지")) {
            if (iEllapse == 1199) {
                now_sequence = "회복 마사지_1";
                play_seq_num = 1;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";050;035;100;10;080;10;020;1;35;N");
                mUsbReceiver.writeDataToSerial("A20;"+intTostring(brust_value)+";"+intTostring(abdomen_value)+";"+intTostring(arm_value)+";"+intTostring(bein_value)+";"+intTostring(latt_value)+";"+intTostring(waist_value)+";"+intTostring(sideflank_value)+";"+intTostring(arsch_value)+";N"); // 부분 강도 보내준다
                allValueTostring();  //운동 시작은 전체 강도를 1부터 설정값까지 달란다
            }
            else if (iEllapse == 899) {
                now_sequence = "회복 마사지_2";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";060;060;100;10;080;10;020;1;35;N");
                partAndall();
            }
            else if (iEllapse == 599) {
                now_sequence = "회복 마사지_3";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";070;100;100;10;080;10;020;1;40;N");
                partAndall();
            }
            else if (iEllapse == 299) {
                now_sequence = "회복 마사지_4";
                play_seq_num ++;
                play_seq_str = intTotwostring(play_seq_num);
                mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";080;020;100;10;080;10;020;1;40;N");
                partAndall();
            }
        }
    }

    public void animationStart() {
        Log.d(TAG_ACTIVITY, "animationStart()");

        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

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

    private void valueTextDisplay() {  //설정값 display 만약 설정 안했으면 default value 20 display
        int defaultValue = 20;  //강도 세기 설정 하지 않고 시작을 누른 경우 default value 20 으로 설정

        if (arsch_value != 0) {
            btn_arsch_1.setText(String.format(Locale.US, "%d", arsch_value));
            btn_arsch_1.setTextColor(Color.BLACK);  //activity_detail_4.xml 에서 android:text="20" 해주니 항상 20 이 display 되어진다 그래서 지웠더니 제대로 나오나 흐리게 나와서 색깔주는 꽁수 신공
            btn_arsch_2.setText(String.format(Locale.US, "%d", arsch_value));
            btn_arsch_2.setTextColor(Color.BLACK);
        }
        else {
            btn_arsch_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_arsch_1.setTextColor(Color.BLACK);
            btn_arsch_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_arsch_2.setTextColor(Color.BLACK);
        }

        if (latt_value != 0) {
            btn_latt_1.setText(String.format(Locale.US, "%d", latt_value));
            btn_latt_1.setTextColor(Color.BLACK);
            btn_latt_2.setText(String.format(Locale.US, "%d", latt_value));
            btn_latt_2.setTextColor(Color.BLACK);
        }
        else {
            btn_latt_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_latt_1.setTextColor(Color.BLACK);
            btn_latt_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_latt_2.setTextColor(Color.BLACK);
        }

        if (waist_value != 0) {
            btn_waist_1.setText(String.format(Locale.US, "%d", waist_value));
            btn_waist_1.setTextColor(Color.BLACK);
            btn_waist_2.setText(String.format(Locale.US, "%d", waist_value));
            btn_waist_2.setTextColor(Color.BLACK);
        }
        else {
            btn_waist_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_waist_1.setTextColor(Color.BLACK);
            btn_waist_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_waist_2.setTextColor(Color.BLACK);
        }

        if (sideflank_value != 0) {
            btn_sideflank_1.setText(String.format(Locale.US, "%d", sideflank_value));
            btn_sideflank_1.setTextColor(Color.BLACK);
            btn_sideflank_2.setText(String.format(Locale.US, "%d", sideflank_value));
            btn_sideflank_2.setTextColor(Color.BLACK);
        }
        else {
            btn_sideflank_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_sideflank_1.setTextColor(Color.BLACK);
            btn_sideflank_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_sideflank_2.setTextColor(Color.BLACK);
        }

        if (abdomen_value != 0) {
            btn_abdomen_1.setText(String.format(Locale.US, "%d", abdomen_value));
            btn_abdomen_1.setTextColor(Color.BLACK);
            btn_abdomen_2.setText(String.format(Locale.US, "%d", abdomen_value));
            btn_abdomen_2.setTextColor(Color.BLACK);
        }
        else {
            btn_abdomen_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_abdomen_1.setTextColor(Color.BLACK);
            btn_abdomen_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_abdomen_2.setTextColor(Color.BLACK);
        }

        if (arm_value != 0) {
            btn_arm_1.setText(String.format(Locale.US, "%d", arm_value));
            btn_arm_1.setTextColor(Color.BLACK);
            btn_arm_2.setText(String.format(Locale.US, "%d", arm_value));
            btn_arm_2.setTextColor(Color.BLACK);
        }
        else {
            btn_arm_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_arm_1.setTextColor(Color.BLACK);
            btn_arm_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_arm_2.setTextColor(Color.BLACK);
        }

        if (bein_value != 0) {
            btn_bein_1.setText(String.format(Locale.US, "%d", bein_value));
            btn_bein_1.setTextColor(Color.BLACK);
            btn_bein_2.setText(String.format(Locale.US, "%d", bein_value));
            btn_bein_2.setTextColor(Color.BLACK);
        }
        else {
            btn_bein_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_bein_1.setTextColor(Color.BLACK);
            btn_bein_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_bein_2.setTextColor(Color.BLACK);
        }

        if (brust_value != 0) {
            btn_brust_1.setText(String.format(Locale.US, "%d", brust_value));
            btn_brust_1.setTextColor(Color.BLACK);
            btn_brust_2.setText(String.format(Locale.US, "%d", brust_value));
            btn_brust_2.setTextColor(Color.BLACK);
        }
        else {
            btn_brust_1.setText(String.format(Locale.US, "%d", defaultValue));
            btn_brust_1.setTextColor(Color.BLACK);
            btn_brust_2.setText(String.format(Locale.US, "%d", defaultValue));
            btn_brust_2.setTextColor(Color.BLACK);
        }
    }

    // 정수로 된 시간을 초단위(sec)로 입력 받아, "04:11:15" 등의 형식의 문자열로 시분초를 저장
    public static void secToHHMMSS(int secs) {
        int sec = secs % 60;
        int min = secs / 60 % 60;
//        int hour = secs / 3600;

        timerBuffer = String.format(Locale.US, "%02d:%02d", min, sec);
    }

    public static int secToMillies(String timerBuffer) {
        String[] array = timerBuffer.split(":");
        return (Integer.parseInt(array[0]) * 1 * 60 * 1000) + (Integer.parseInt(array[1]) * 1000);
    }

    private final MyHandler mTimer = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<DetailStartActivity> mActivity;

        public MyHandler(DetailStartActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            DetailStartActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage();
            }
        }
    }

    private void handleMessage() {
        //텍스트뷰를 수정해준다.
        mEllapse.setText(getEllapse());
        //메시지를 다시 보낸다.
        mTimer.sendEmptyMessage(0);//0은 메시지를 구분하기 위한 것
    }

/*
    //스톱워치는 위해 핸들러를 만든다.
    Handler mTimer = new Handler() {
        //핸들러는 기본적으로 handleMessage에서 처리한다.
        public void handleMessage(android.os.Message msg) {
            //텍스트뷰를 수정해준다.
            mEllapse.setText(getEllapse());
            //메시지를 다시 보낸다.
            mTimer.sendEmptyMessage(0);//0은 메시지를 구분하기 위한 것
        }
    };
*/

    String getEllapse() {
        long now = SystemClock.elapsedRealtime();
        long ell = now - mBaseTime;//현재 시간과 지난 시간을 빼서 ell값을 구하고
        //아래에서 포맷을 예쁘게 바꾼다음 리턴해준다.
        return (String.format(Locale.US, "%02d:%02d", (ell / 1000) % 60, (ell % 1000) / 10));
    }

    @Override
    protected void onDestroy() {
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        mTimer.removeMessages(0);//메시지를 지워서 메모리릭 방지
        if (mAllSender != null) {
            mAllSender.cancel();
            mAllSender = null;
        }
        if (mFirstAllSender != null) {
            mFirstAllSender.cancel();
            mFirstAllSender = null;
        }
        if (mCountDown != null) {
            mCountDown.cancel();
            mCountDown = null;
        }
        if (mBR != null) {
            unregisterReceiver(mBR);
        }
        super.onDestroy();
    }

    private void endtimecheck(String booking_end_time)  //end time 과 현재 시간 체크하는 함수
    {
        mBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                String time = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis()));
                String newtime = time.replace(":","");  //현재시간 가져 와서 09:11:39 ->091139  이런 형태로 바꿈

                String new_booking_end_time = booking_end_time.replace(":","");  //예약시간 09:11:39 ->091139  이런 형태로 바꿈
                //값 비교하기 위해 String을 int로 바꿈
                int numnewtime = Integer.parseInt(newtime);
                int numnew_booking_end_time = Integer.parseInt(new_booking_end_time);

//                Log.d(TAG_ACTIVITY, numnewtime + " >= " + numnew_booking_end_time);
                if (numnewtime >= numnew_booking_end_time) {
                    Log.i(TAG_ACTIVITY, "Send S22;N");

                    mUsbReceiver.writeDataToSerial("S22;N");  //운동 끝나면  보낸다
                    now_sequence = null;  //종료시 초기화
                    enddataSaved();  //운동 종료 할때 저장하는 data
                    Intent i = new Intent(DetailStartActivity.this, LateEndActivity .class);
                    startActivity(i);
                    finish();
                }
//                else {
//                    Log.d(TAG_ACTIVITY, "J.Y.T DetailStartActivity 계속 운동 해라");
//                }
            }
        };
        registerReceiver(mBR, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void enddataSaved() {
        SharedPreferences athletics =getSharedPreferences("athletics_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = athletics.edit();
        String athletics_time = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis()));
        editor.putString("Data_kind",playID ); //운동 종류
        editor.putString("Data_time",athletics_time ); //운동 시간
        //부위별 강도
        editor.putInt("Data_latt",latt_value );
        editor.putInt("Data_waist",waist_value );
        editor.putInt("Data_sideflank",sideflank_value );
        editor.putInt("Data_arsch",arsch_value );
        editor.putInt("Data_brust",brust_value );
        editor.putInt("Data_arm",arm_value );
        editor.putInt("Data_abdomen",abdomen_value );
        editor.putInt("Data_bein",bein_value );
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        int progress;

        switch (v.getId()) {
            //case R.id.btn_back:  //운동시작 후에는 back은 없다
                //Intent i = new Intent(DetailStartActivity.this, DetailActivity.class);
                //i.putExtra("detailTo", "1");
                //startActivity(i);
                //finish();
                //break;
            case R.id.btn_play: // 운동시작
                ImageButton imageButton = findViewById(R.id.btn_pause);
                imageButton.setBackground(this.getResources().getDrawable(R.drawable.pause_btn_off, this.getTheme()));
                imageButton.setEnabled(true);

                ImageButton imageButton2 = findViewById(R.id.btn_play);
                imageButton2.setBackground(this.getResources().getDrawable(R.drawable.play_btn_on, this.getTheme()));
                imageButton2.setEnabled(false);

                ImageButton imageButton3 = findViewById(R.id.btn_stop);
                imageButton3.setBackground(this.getResources().getDrawable(R.drawable.stop_btn_off, this.getTheme()));
                imageButton3.setEnabled(true);

                //////////////////////////////////
                // Serial
                //////////////////////////////////
                // 근육강화 : 1, 순발력강화 : 2, 근력강화 : 3, 근지구력 강화 : 4, 지구력 강화 : 5
//                SharedPreferences setting = getSharedPreferences("setting", 0);

//                String totalPer[] = textView.getText().toString().split("%");
//                String per = totalPer[0];
                //mUsbReceiver.writeDataToSerial("A41;01;1;"+per+";N"); // 운동ID는 어떻게?

                //////////////////////////////////
                // Serial
                //////////////////////////////////

                switch (mStatus) {
                    case IDLE:
                        Log.d(TAG_ACTIVITY, "R.id.btn_play: IDLE: mStatus: RUNNING");
                        mStatus = RUNNING;
//                        Log.d("M20", "J.Y.T DetailStartActivity IDLE: ");
//                        mCountDown = new CountDownTimer(1000 * 60 * 20, 1000) {
//                            public void onTick(long millisUntilFinished) {
//                                iEllapse = (int) millisUntilFinished / 1000;
//                                secToHHMMSS(iEllapse);
//                                mEllapse.setText(timerBuffer);
//                                Log.d("M20", "J.Y.T DetailStartActivity onTick IDLE: "+iEllapse);
//                                waveform();
//                            }
//
//                            public void onFinish() {
//                                mUsbReceiver.writeDataToSerial("S22;N");  //운동 끝나면  보낸다
//                                now_sequence = null;  //종료시 초기화
//                                // 모든 시간이 다 되면 이동한다.
//                                enddataSaved();  //운동 종료 할때 저장하는 data
//                                startActivity(new Intent(DetailStartActivity.this, EndActivity.class));
//                                finish();
//                            }
//                        }.start();
                        break;

                    case RUNNING:
                        Log.d(TAG_ACTIVITY, "R.id.btn_play: RUNNING: mStatus: PAUSE");
                        mStatus = PAUSE;//상태를 멈춤으로 표시
//                        Log.d("M20", "J.Y.T DetailStartActivity RUNNING: ");
                        break;

                    //멈춤이면
                    case PAUSE:
//                        Log.d("M20", "J.Y.T DetailStartActivity PAUSE: ");
                        animationStart();  //깜빡이게 하기 위해서
                        mUsbReceiver.writeDataToSerial("S26;N");  //멈추었다가 다시 시작 S26
//                        //현재값 가져옴
                        Log.d(TAG_ACTIVITY, "R.id.btn_play: PAUSE: mStatus: RUNNING");
                        mStatus = RUNNING;
                        int ellapseTime = secToMillies(timerBuffer) + 1000;
                        mCountDown = new CountDownTimer(ellapseTime, 1000) {
                            public void onTick(long millisUntilFinished) {
                                iEllapse = (int) millisUntilFinished / 1000;
                                secToHHMMSS(iEllapse);
                                mEllapse.setText(timerBuffer);
                                waveform();
                            }

                            public void onFinish() {
                                mUsbReceiver.writeDataToSerial("S22;N");  //운동 끝나면  보낸다
                                now_sequence = null;  //종료시 초기화
                                // 모든 시간이 다 되면 이동한다.
                                enddataSaved();  //운동 종료 할때 저장하는 data
                                startActivity(new Intent(DetailStartActivity.this, EndActivity.class));
                                finish();
                            }
                        }.start();
                        allValueSendTimer();
                        break;
                }
                break;

            case R.id.btn_pause: // 멈춤
//                Log.d("M20", "J.Y.T btn_pause");
                animationStop();  //깜빡이던 그림 멈춰라
                imageButton = findViewById(R.id.btn_pause);
                imageButton.setBackground(this.getResources().getDrawable(R.drawable.pause_btn_on, this.getTheme()));
                imageButton.setEnabled(false);

                imageButton2 = findViewById(R.id.btn_play);
                imageButton2.setBackground(this.getResources().getDrawable(R.drawable.play_btn_off, this.getTheme()));
                imageButton2.setEnabled(true);

                imageButton3 = findViewById(R.id.btn_stop);
                imageButton3.setBackground(this.getResources().getDrawable(R.drawable.stop_btn_off, this.getTheme()));
                imageButton3.setEnabled(true);

                //////////////////////////////////
                // Serial
                //////////////////////////////////
                mUsbReceiver.writeDataToSerial("S29;N");  //멈추면 S29
                //////////////////////////////////
                // Serial
                //////////////////////////////////


                if(mCountDown != null)
                    mCountDown.cancel();
                if(mAllSender != null) {
                    mAllSender.cancel();
                    iAlltimer = 0;
                }
                if(mFirstAllSender != null) {
                    mFirstAllSender.cancel();
                    iFirstAlltimer = 0;
                }
                Log.d(TAG_ACTIVITY, "R.id.btn_pause: mStatus: PAUSE");
                mStatus = PAUSE;//상태를 멈춤으로 표시
                break;

            case R.id.btn_stop:
//                Log.d("M20", "J.Y.T btn_stop");
                animationStop();  //깜빡이던 그림 멈춰라
                mUsbReceiver.writeDataToSerial("S29;N");  //멈추면 S29

                 imageButton = findViewById(R.id.btn_pause);
                imageButton.setBackground(this.getResources().getDrawable(R.drawable.pause_btn_off, this.getTheme()));
                imageButton.setEnabled(true);

                 imageButton2 = findViewById(R.id.btn_play);
                imageButton2.setBackground(this.getResources().getDrawable(R.drawable.play_btn_off, this.getTheme()));
                imageButton2.setEnabled(true);

                 imageButton3 = findViewById(R.id.btn_stop);
                imageButton3.setBackground(this.getResources().getDrawable(R.drawable.stop_btn_on, this.getTheme()));
                imageButton3.setEnabled(false);

                if (mCountDown != null) {
                    mCountDown.cancel(); // 정지
                }
                if(mAllSender != null) {
                    mAllSender.cancel();
                    iAlltimer = 0;
                }
                if(mFirstAllSender != null) {
                    mFirstAllSender.cancel();
                    iFirstAlltimer = 0;
                }
                Log.d(TAG_ACTIVITY, "R.id.btn_stop: mStatus: PAUSE");
                mStatus = PAUSE;//상태를 멈춤으로 표시 일시정지와 동일하게 구현 해주라고 함.
                popupDialog = new PopupDialog(this,
                        "[다이얼로그 제목]", // 제목
                        "다이얼로그 내용 표시하기", // 내용
                        leftListener, // 취소
                        rightListener); // 확인
                popupDialog.show();
                break;

            case R.id.btn_minus:
/*
                progress = mSeekBar.getProgress() - 1;
                Log.d(TAG_ACTIVITY, "progress = "+ progress);
                mSeekBar.setProgress(progress);

                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));

                mHoloCircularProgressBar.setProgress(progress * 0.01f);

                mUsbReceiver.writeDataToSerial("S23;" + intTostring(progress) + ";N");

                all_value = progress;
*/
                switch (mSelect) {
                    case BRUST:
                        if (brust_value > 0)
                            brust_value --;
                        progress = brust_value;
                        btn_brust_1.setText(String.format(Locale.US, "%d", brust_value));
                        btn_brust_2.setText(String.format(Locale.US, "%d", brust_value));
                        break;
                    case ABDOMEN:
                        if (abdomen_value > 0)
                            abdomen_value --;
                        progress = abdomen_value;
                        btn_abdomen_1.setText(String.format(Locale.US, "%d", abdomen_value));
                        btn_abdomen_2.setText(String.format(Locale.US, "%d", abdomen_value));
                        break;
                    case ARM:
                        if (arm_value > 0)
                            arm_value --;
                        progress = arm_value;
                        btn_arm_1.setText(String.format(Locale.US, "%d", arm_value));
                        btn_arm_2.setText(String.format(Locale.US, "%d", arm_value));
                        break;
                    case BEIN:
                        if (bein_value > 0)
                            bein_value --;
                        progress = bein_value;
                        btn_bein_1.setText(String.format(Locale.US, "%d", bein_value));
                        btn_bein_2.setText(String.format(Locale.US, "%d", bein_value));
                        break;
                    case LATT:
                        if (latt_value > 0)
                            latt_value --;
                        progress = latt_value;
                        btn_latt_1.setText(String.format(Locale.US, "%d", latt_value));
                        btn_latt_2.setText(String.format(Locale.US, "%d", latt_value));
                        break;
                    case WAIST:
                        if (waist_value > 0)
                            waist_value --;
                        progress = waist_value;
                        btn_waist_1.setText(String.format(Locale.US, "%d", waist_value));
                        btn_waist_2.setText(String.format(Locale.US, "%d", waist_value));
                        break;
                    case FLANK:
                        if (sideflank_value > 0)
                            sideflank_value --;
                        progress = sideflank_value;
                        btn_sideflank_1.setText(String.format(Locale.US, "%d", sideflank_value));
                        btn_sideflank_2.setText(String.format(Locale.US, "%d", sideflank_value));
                        break;
                    case ARSCH:
                        if (arsch_value > 0)
                            arsch_value --;
                        progress = arsch_value;
                        btn_arsch_1.setText(String.format(Locale.US, "%d", arsch_value));
                        btn_arsch_2.setText(String.format(Locale.US, "%d", arsch_value));
                        break;
                    default:
                        if (all_value > 0)
                            all_value --;
                        progress = all_value;
                        mUsbReceiver.writeDataToSerial("S23;" + intTostring(progress) + ";N");
//                        Log.d(TAG_ACTIVITY, "S23;" + intTostring(progress) + ";N");
                        break;
                }
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                if (mSelect != TOTAL) {
                    mUsbReceiver.writeDataToSerial(String.format(Locale.US, "S24;%03d;%03d;N", mSelect, progress));
//                    Log.d(TAG_ACTIVITY, String.format(Locale.US, "S24;%03d;%03d;N", intTostring(mSelect), intTostring(progress)));
                }
                break;

            case R.id.btn_plus:
/*
                progress = mSeekBar.getProgress() + 1;
                Log.d(TAG_ACTIVITY, "progress = "+ progress);
                mSeekBar.setProgress(progress);

                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));

                mHoloCircularProgressBar.setProgress(progress * 0.01f);

                mUsbReceiver.writeDataToSerial("S23;" + intTostring(progress) + ";N");

                all_value = progress;
*/
                switch (mSelect) {
                    case BRUST:
                        if (brust_value < 99)
                            brust_value ++;
                        progress = brust_value;
                        btn_brust_1.setText(String.format(Locale.US, "%d", brust_value));
                        btn_brust_2.setText(String.format(Locale.US, "%d", brust_value));
                        break;
                    case ABDOMEN:
                        if (abdomen_value < 99)
                            abdomen_value ++;
                        progress = abdomen_value;
                        btn_abdomen_1.setText(String.format(Locale.US, "%d", abdomen_value));
                        btn_abdomen_2.setText(String.format(Locale.US, "%d", abdomen_value));
                        break;
                    case ARM:
                        if (arm_value < 99)
                            arm_value ++;
                        progress = arm_value;
                        btn_arm_1.setText(String.format(Locale.US, "%d", arm_value));
                        btn_arm_2.setText(String.format(Locale.US, "%d", arm_value));
                        break;
                    case BEIN:
                        if (bein_value < 99)
                            bein_value ++;
                        progress = bein_value;
                        btn_bein_1.setText(String.format(Locale.US, "%d", bein_value));
                        btn_bein_2.setText(String.format(Locale.US, "%d", bein_value));
                        break;
                    case LATT:
                        if (latt_value < 99)
                            latt_value ++;
                        progress = latt_value;
                        btn_latt_1.setText(String.format(Locale.US, "%d", latt_value));
                        btn_latt_2.setText(String.format(Locale.US, "%d", latt_value));
                        break;
                    case WAIST:
                        if (waist_value < 99)
                            waist_value ++;
                        progress = waist_value;
                        btn_waist_1.setText(String.format(Locale.US, "%d", waist_value));
                        btn_waist_2.setText(String.format(Locale.US, "%d", waist_value));
                        break;
                    case FLANK:
                        if (sideflank_value < 99)
                            sideflank_value ++;
                        progress = sideflank_value;
                        btn_sideflank_1.setText(String.format(Locale.US, "%d", sideflank_value));
                        btn_sideflank_2.setText(String.format(Locale.US, "%d", sideflank_value));
                        break;
                    case ARSCH:
                        if (arsch_value < 99)
                            arsch_value ++;
                        progress = arsch_value;
                        btn_arsch_1.setText(String.format(Locale.US, "%d", arsch_value));
                        btn_arsch_2.setText(String.format(Locale.US, "%d", arsch_value));
                        break;
                    default:
                        if (all_value < 99)
                            all_value ++;
                        progress = all_value;
                        mUsbReceiver.writeDataToSerial("S23;" + intTostring(progress) + ";N");
//                        Log.d(TAG_ACTIVITY, "S23;" + intTostring(progress) + ";N");
                        break;
                }
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                if (mSelect != TOTAL) {
                    mUsbReceiver.writeDataToSerial(String.format(Locale.US, "S24;%03d;%03d;N", mSelect, progress));
//                    Log.d(TAG_ACTIVITY, String.format(Locale.US, "S24;%03d;%03d;N", intTostring(mSelect), intTostring(progress)));
                }
                break;

            case R.id.btn_latt_41:
            case R.id.btn_latt_42:
                Log.d(TAG_ACTIVITY, "Latt.");
                mSelect = LATT;
                progress = latt_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_waist_41:
            case R.id.btn_waist_42:
                Log.d(TAG_ACTIVITY, "Waist.");
                mSelect = WAIST;
                progress = waist_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_sideflank_41:
            case R.id.btn_sideflank_42:
                Log.d(TAG_ACTIVITY, "Flank.");
                mSelect = FLANK;
                progress = sideflank_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_arsch_41:
            case R.id.btn_arsch_42:
                Log.d(TAG_ACTIVITY, "Arsch.");
                mSelect = ARSCH;
                progress = arsch_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_brust_41:
            case R.id.btn_brust_42:
                Log.d(TAG_ACTIVITY, "Brust.");
                mSelect = BRUST;
                progress = brust_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_arm_41:
            case R.id.btn_arm_42:
                Log.d(TAG_ACTIVITY, "Arm.");
                mSelect = ARM;
                progress = arm_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_abdomen_41:
            case R.id.btn_abdomen_42:
                Log.d(TAG_ACTIVITY, "Abdomen.");
                mSelect = ABDOMEN;
                progress = abdomen_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.btn_bein_41:
            case R.id.btn_bein_42:
                Log.d(TAG_ACTIVITY, "Bein.");
                mSelect = BEIN;
                progress = bein_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
            case R.id.txtPersent4:
                Log.d(TAG_ACTIVITY, "Percent.");
                mSelect = TOTAL;
                progress = all_value;
                mSeekBar.setProgress(progress);
                textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));
                mHoloCircularProgressBar.setProgress(progress * 0.01f);
                break;
        }
    }

    private void animationStop() {
        btn_arsch_1.clearAnimation();
        btn_arsch_2.clearAnimation();

        btn_waist_1.clearAnimation();
        btn_waist_2.clearAnimation();

        btn_brust_1.clearAnimation();
        btn_brust_2.clearAnimation();

        btn_bein_1.clearAnimation();
        btn_bein_2.clearAnimation();

        btn_arm_1.clearAnimation();
        btn_arm_2.clearAnimation();

        btn_sideflank_1.clearAnimation();
        btn_sideflank_2.clearAnimation();

        btn_abdomen_1.clearAnimation();
        btn_abdomen_2.clearAnimation();

        btn_latt_1.clearAnimation();
        btn_latt_2.clearAnimation();
    }

    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
//            Log.d(TAG_ACTIVITY, "J.Y.T popupDialog.dismiss()");
        }
    };

    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View v) {
            popupDialog.dismiss();
            mUsbReceiver.writeDataToSerial("S22;N");  //운동 끝나면  보낸다
            now_sequence = null;  //종료시 초기화
            enddataSaved();  //운동 종료 할때 저장하는 data
            startActivity(new Intent(DetailStartActivity.this, EndActivity.class));
            finish();

        }
    };

    public void setRemotePause() {
        animationStop();  //깜빡이던 그림 멈춰라
        ImageButton imageButton = findViewById(R.id.btn_pause);
        imageButton.setBackground(this.getResources().getDrawable(R.drawable.pause_btn_on, this.getTheme()));
        imageButton.setEnabled(false);

        ImageButton imageButton2 = findViewById(R.id.btn_play);
        imageButton2.setBackground(this.getResources().getDrawable(R.drawable.play_btn_off, this.getTheme()));
        imageButton2.setEnabled(true);

        ImageButton imageButton3 = findViewById(R.id.btn_stop);
        imageButton3.setBackground(this.getResources().getDrawable(R.drawable.stop_btn_off, this.getTheme()));
        imageButton3.setEnabled(true);

        if(mCountDown != null)
            mCountDown.cancel();
        if(mAllSender != null) {
            mAllSender.cancel();
            iAlltimer = 0;
        }
        if(mFirstAllSender != null) {
            mFirstAllSender.cancel();
            iFirstAlltimer = 0;
        }
        Log.d(TAG_ACTIVITY, "setRemotePause() mStatus: PAUSE");
        mStatus = PAUSE;//상태를 멈춤으로 표시
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG_ACTIVITY, String.format(Locale.US, "onProgressChanged: progress = %d.", progress));

/* schyun, Do nothing. *
        //seekBar 도 설정대로 나타나게 한다
        seekBar = findViewById(R.id.btn_sb4);
        seekBar.setProgress(progress);
        //seekBar 도 설정대로 나타나게 한다
        TextView textView = findViewById(R.id.txtPersent);
        textView.setText(String.format(Locale.US, "%d %%", progress));
        mHoloCircularProgressBar.setProgress(progress * 0.01f);
        //mUsbReceiver.writeDataToSerial("A20;"+progress+";0;0;0;0;0;0;0;0;N");
        all_value = progress;
        if (not_send_remote == true) {
            not_send_remote = false;
        }
        else {
            mUsbReceiver.writeDataToSerial("S23;" + intTostring(progress) + ";N");  //전체 강도 운동 시작후 치사하게 변경해도 값 보내준다
        }
* end */
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

//    public void longClickPlus() {
//        SeekBar seekBar = findViewById(R.id.btn_sb);
//        seekBar.setProgress(seekBar.getProgress() + 1);
//        TextView textView = findViewById(R.id.txtPersent);
//        textView.setText(seekBar.getProgress() + "%");
//    }
//    public void longClickMinus() {
//        SeekBar seekBar = findViewById(R.id.btn_sb);
//        seekBar.setProgress(seekBar.getProgress() - 1);
//        TextView textView = findViewById(R.id.txtPersent);
//        textView.setText(seekBar.getProgress() + "%");
//    }

    public void setallValueChange(String str) {  //리모콘으로 전체 강도 바꾸면 받아와서 전체 강도에 저장
        int progress = Integer.parseInt(str);  //String을 int로
//        not_send_remote = true;
//        onProgressChanged(mSeekBar, all_value,true);

        //seekBar 도 설정대로 나타나게 한다
        mSeekBar.setProgress(progress);

        textViewPercent4.setText(String.format(Locale.US, "%d%%", progress));

        mHoloCircularProgressBar.setProgress(progress * 0.01f);

        all_value = progress;
    }

    private void startWaveformResume () {  //Pause 했다가 resume 할 때 어떤 Waveform 인지 구별하여 보낸다
        if (now_sequence.equals("근육강화_1"))
            mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("근육강화_2"))
            mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";000;003;100;10;060;10;040;1;25;N");
        else if (now_sequence.equals("근육강화_3"))
            mUsbReceiver.writeDataToSerial("S21;01;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("근력강화_1"))
            mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("근력강화_2"))
            mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;070;100;20;030;30;020;1;25;N");
        else if (now_sequence.equals("근력강화_3"))
            mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;090;100;20;020;30;010;1;25;N");
        else if (now_sequence.equals("근력강화_4"))
            mUsbReceiver.writeDataToSerial("S21;02;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("순발력 강화_1"))
            mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("순발력 강화_2"))
            mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;085;100;20;020;30;040;1;25;N");
        else if (now_sequence.equals("순발력 강화_3"))
            mUsbReceiver.writeDataToSerial("S21;03;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("근지구력 강화_1"))
            mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;003;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("근지구력 강화_2"))
            mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;050;100;20;060;30;030;1;25;N");
        else if (now_sequence.equals("근지구력 강화_3"))
            mUsbReceiver.writeDataToSerial("S21;04;"+play_seq_str+";100;010;000;00;000;00;000;0;25;N");
        else if (now_sequence.equals("지구력 강화_1"))
            mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;010;050;10;120;10;010;0;25;N");
        else if (now_sequence.equals("지구력 강화_2"))
            mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;020;100;10;030;10;010;1;25;N");
        else if (now_sequence.equals("지구력 강화_3"))
            mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;040;100;10;030;10;010;1;25;N");
        else if (now_sequence.equals("지구력 강화_4"))
            mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";080;070;100;10;030;10;010;1;25;N");
        else if (now_sequence.equals("지구력 강화_5"))
            mUsbReceiver.writeDataToSerial("S21;05;"+play_seq_str+";100;010;050;10;030;10;010;0;25;N");
        else if (now_sequence.equals("체지방_1"))
            mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";050;100;050;10;030;10;010;1;20;N");
        else if (now_sequence.equals("체지방_2"))
            mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";075;010;075;04;012;04;004;1;20;N");
        else if (now_sequence.equals("체지방_3"))
            mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";050;100;050;10;030;10;010;1;20;N");
        else if (now_sequence.equals("체지방_4"))
            mUsbReceiver.writeDataToSerial("S21;06;"+play_seq_str+";030;100;000;00;000;00;000;0;20;N");
        else if (now_sequence.equals("셀롤라이트_1"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
        else if (now_sequence.equals("셀롤라이트_2"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;050;03;010;03;004;1;20;N");
        else if (now_sequence.equals("셀롤라이트_3"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;100;075;10;030;10;010;1;20;N");
        else if (now_sequence.equals("셀롤라이트_4"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;050;075;10;030;10;010;1;20;N");
        else if (now_sequence.equals("셀롤라이트_5"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";075;001;075;10;030;10;010;1;20;N");
        else if (now_sequence.equals("셀롤라이트_6"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";025;010;025;10;060;10;030;1;20;N");
        else if (now_sequence.equals("셀롤라이트_7"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";050;010;075;10;090;10;040;1;20;N");
        else if (now_sequence.equals("셀롤라이트_8"))
            mUsbReceiver.writeDataToSerial("S21;07;"+play_seq_str+";025;001;045;05;015;05;005;1;20;N");
        else if (now_sequence.equals("마른체형 근육_1"))
            mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;003;000;01;020;01;000;0;25;N");
        else if (now_sequence.equals("마른체형 근육_2"))
            mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;008;100;10;060;10;040;1;25;N");
        else if (now_sequence.equals("마른체형 근육_3"))
            mUsbReceiver.writeDataToSerial("S21;08;"+play_seq_str+";100;005;000;01;020;01;000;0;25;N");
        else if (now_sequence.equals("마른체형 근력_1"))
            mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;003;000;01;010;01;000;0;25;N");
        else if (now_sequence.equals("마른체형 근력_2"))
            mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;007;100;10;040;10;040;1;25;N");
        else if (now_sequence.equals("마른체형 근력_3"))
            mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;009;100;10;030;10;030;1;25;N");
        else if (now_sequence.equals("마른체형 근력_4"))
            mUsbReceiver.writeDataToSerial("S21;09;"+play_seq_str+";100;005;000;01;160;01;000;0;25;N");
        else if (now_sequence.equals("스트레칭_1"))
            mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
        else if (now_sequence.equals("스트레칭_2"))
            mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";025;010;025;08;025;08;008;1;20;N");
        else if (now_sequence.equals("스트레칭_3"))
            mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";000;000;025;20;100;30;000;1;20;N");
        else if (now_sequence.equals("스트레칭_4"))
            mUsbReceiver.writeDataToSerial("S21;10;"+play_seq_str+";000;000;025;30;100;30;000;1;20;N");
        else if (now_sequence.equals("위축된 근육 컨디션 조절_1"))
            mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;002;000;00;000;00;000;0;50;N");
        else if (now_sequence.equals("위축된 근육 컨디션 조절_2"))
            mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;004;000;00;000;00;000;0;50;N");
        else if (now_sequence.equals("위축된 근육 컨디션 조절_3"))
            mUsbReceiver.writeDataToSerial("S21;11;"+play_seq_str+";100;006;000;00;000;00;000;0;50;N");
        else if (now_sequence.equals("정상 근육 컨디션 조절_1"))
            mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;002;000;00;000;00;000;0;30;N");
        else if (now_sequence.equals("정상 근육 컨디션 조절_2"))
            mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;006;000;00;000;00;000;0;30;N");
        else if (now_sequence.equals("정상 근육 컨디션 조절_3"))
            mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;008;000;00;000;00;000;0;30;N");
        else if (now_sequence.equals("정상 근육 컨디션 조절_4"))
            mUsbReceiver.writeDataToSerial("S21;12;"+play_seq_str+";100;004;000;00;000;00;000;0;30;N");
        else if (now_sequence.equals("위축된 근력 컨디션 조절_1"))
            mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";100;003;000;00;000;00;000;0;50;N");
        else if (now_sequence.equals("위축된 근력 컨디션 조절_2"))
            mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";080;035;100;10;040;10;010;1;50;N");
        else if (now_sequence.equals("위축된 근력 컨디션 조절_3"))
            mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";080;045;100;10;040;10;010;1;50;N");
        else if (now_sequence.equals("위축된 근력 컨디션 조절_4"))
            mUsbReceiver.writeDataToSerial("S21;13;"+play_seq_str+";100;002;000;00;000;00;000;0;50;N");
        else if (now_sequence.equals("정상 근력 컨디션 조절_1"))
            mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";100;005;000;00;000;00;000;0;35;N");
        else if (now_sequence.equals("정상 근력 컨디션 조절_2"))
            mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";080;055;050;10;60;10;120;0;35;N");
        else if (now_sequence.equals("정상 근력 컨디션 조절_3"))
            mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";080;065;050;10;50;10;100;0;35;N");
        else if (now_sequence.equals("정상 근력 컨디션 조절_4"))
            mUsbReceiver.writeDataToSerial("S21;14;"+play_seq_str+";100;004;000;00;000;00;000;0;35;N");
        else if (now_sequence.equals("혈액순환 개선_1"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;050;050;03;010;03;004;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_2"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;020;050;03;010;03;040;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_3"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;010;050;03;010;03;040;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_4"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";030;020;030;07;020;06;007;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_5"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";025;010;030;07;026;07;000;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_6"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;001;000;00;000;00;000;0;20;N");
        else if (now_sequence.equals("혈액순환 개선_7"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;001;000;00;000;00;000;1;20;N");
        else if (now_sequence.equals("혈액순환 개선_8"))
            mUsbReceiver.writeDataToSerial("S21;15;"+play_seq_str+";050;100;010;10;040;10;000;1;20;N");
        else if (now_sequence.equals("저속 마사지"))
            mUsbReceiver.writeDataToSerial("S21;16;"+play_seq_str+";000;100;100;05;005;05;025;2;25;N");  //마지막 025~045 중 선택예정
        else if (now_sequence.equals("중속 마사지"))
            mUsbReceiver.writeDataToSerial("S21;17;"+play_seq_str+";000;100;100;02;000;02;012;2;25;N");  //마지막 012~020 중 선택예정
        else if (now_sequence.equals("고속 마사지"))
            mUsbReceiver.writeDataToSerial("S21;18;"+play_seq_str+";000;100;100;01;000;01;006;2;25;N");  //마지막 006~010 중 선택예정
        else if (now_sequence.equals("림프 마사지_1"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";075;100;000;00;000;00;000;0;20;N");
        else if (now_sequence.equals("림프 마사지_2"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";075;100;055;03;010;03;004;1;20;N");
        else if (now_sequence.equals("림프 마사지_3"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";000;000;080;15;050;15;020;1;20;N");
        else if (now_sequence.equals("림프 마사지_4"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;100;080;10;090;10;040;1;20;N");
        else if (now_sequence.equals("림프 마사지_5"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;030;055;03;010;03;040;1;20;N");
        else if (now_sequence.equals("림프 마사지_6"))
            mUsbReceiver.writeDataToSerial("S21;19;"+play_seq_str+";025;015;035;05;015;05;005;1;20;;N");
        else if (now_sequence.equals("회복 마사지_1"))
            mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";050;035;100;10;080;10;020;1;35;N");
        else if (now_sequence.equals("회복 마사지_2"))
            mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";060;060;100;10;080;10;020;1;35;N");
        else if (now_sequence.equals("회복 마사지_3"))
            mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";070;100;100;10;080;10;020;1;40;N");
        else if (now_sequence.equals("회복 마사지_4"))
            mUsbReceiver.writeDataToSerial("S21;20;"+play_seq_str+";080;020;100;10;080;10;020;1;40;N");
    }

    @Override
    public void onBackPressed() {

    }
}
