package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Locale;
import app.com.m20.customview.HoloCircularProgressBar;
import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;

/**
 * Created by kimyongyeon on 2017-11-20.
 */

public class DetailFrontActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    String TAG_ACTIVITY = "M20_DetailFront";

    SeekBar mSeekBar;
    private HoloCircularProgressBar mHoloCircularProgressBar;

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    //부분 강도 설정값 저장을 위한 변수
    private int abdomen_value = 0;
    private int arm_value = 0;
    private int bein_value = 0;
    private int brust_value = 0;

    private int arsch_value = 0;
    private int latt_value = 0;
    private int waist_value = 0;
    private int sideflank_value = 0;
    private int all_value = 0;
    String playID = null;  //어떤 운동인지 저장

    TextView textViewAbdomen21;
    TextView textViewAbdomen22;
    TextView textViewArm21;
    TextView textViewArm22;
    TextView textViewBein21;
    TextView textViewBein22;
    TextView textViewBrust21;
    TextView textViewBrust22;
    TextView textViewTxtname2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_2);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate.");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
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
            playID = intent.getStringExtra("playID");
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
//            Toast.makeText(this, "no connectionDetailFront", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
//                        if(str.equals("reg")) {
//                            Intent intent = new Intent(this, PersonCheckupActivity.class);
//                            activity.startActivity(intent);
//                            activity.finish();
//                        }
        }

        mHoloCircularProgressBar = findViewById(R.id.holoCircularProgressBar);

        TextView fronBackText = findViewById(R.id.btn_model_back);
        fronBackText.setText("");
        Resources resources = getResources();
        String str = resources.getString(R.string.front_back_btn_text);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fronBackText.append(builder);

        textViewAbdomen21 = findViewById(R.id.btn_abdomen_21);
        textViewAbdomen22 = findViewById(R.id.btn_abdomen_22);
        textViewArm21 = findViewById(R.id.btn_arm_21);
        textViewArm22 = findViewById(R.id.btn_arm_22);
        textViewBein21 = findViewById(R.id.btn_bein_21);
        textViewBein22 = findViewById(R.id.btn_bein_22);
        textViewBrust21 = findViewById(R.id.btn_brust_21);
        textViewBrust22 = findViewById(R.id.btn_brust_22);
        textViewTxtname2 = findViewById(R.id.txt_name2);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        textViewAbdomen21.setOnClickListener(this); // f
        textViewAbdomen22.setOnClickListener(this); // f
        textViewArm21.setOnClickListener(this); // f
        textViewArm22.setOnClickListener(this); // f
        textViewBein21.setOnClickListener(this); // fㅠ
        textViewBein22.setOnClickListener(this); // f
        textViewBrust21.setOnClickListener(this); // f
        textViewBrust22.setOnClickListener(this); // f
        findViewById(R.id.btn_model_back).setOnClickListener(this); // 백버튼
        findViewById(R.id.btn_strong_test).setOnClickListener(this); // 강도테스트

        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);

//        findViewById(R.id.btn_plus).setOnLongClickListener(this);
//        findViewById(R.id.btn_minus).setOnLongClickListener(this);

        mSeekBar = findViewById(R.id.btn_sb2);
        mSeekBar.setOnSeekBarChangeListener(this);

//        findViewById(R.id.btn_minus).setOnTouchListener((v, event) -> {
//            longClickMinus();
//            return true;
//        });
//        findViewById(R.id.btn_plus).setOnTouchListener((v, event) -> {
//            longClickPlus();
//            return true;
//        });
        //부분강도 조절 메인 타이틀 display
        TextView textView = findViewById(R.id.txtTitle);
        SharedPreferences setting ;
        setting = getSharedPreferences("setting", 0);
        textView.setText("전면 부분강도 조절");  //운동명 display가 아닌 전면 부분강도 조절 이라고 display m20 요구 사항
        //부분강도 조절 메인 타이틀 display
        if (setting.getString("model_sel", "").equals("abdo")) {
            textViewAbdomen21.setBackgroundResource(R.drawable.pad_abdomen_left);
            textViewAbdomen22.setBackgroundResource(R.drawable.pad_abdomen_right);
            channel = "2";

            textViewTxtname2.setText(getResources().getString(R.string.channel2));
            String abdomen_value_display = String.valueOf(abdomen_value);  //int를 string으로
            textViewAbdomen21.setText(abdomen_value_display);
            textViewAbdomen22.setText(abdomen_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(abdomen_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%d%%", abdomen_value));
            mHoloCircularProgressBar.setProgress((abdomen_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, abdomen_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String arm_value_display = String.valueOf(arm_value);  //int를 string으로
            textViewArm21.setText(arm_value_display);
            textViewArm22.setText(arm_value_display);

            String bein_value_display = String.valueOf(bein_value);  //int를 string으로
            textViewBein21.setText(bein_value_display);
            textViewBein22.setText(bein_value_display);

            String brust_value_display = String.valueOf(brust_value);  //int를 string으로
            textViewBrust21.setText(brust_value_display);
            textViewBrust22.setText(brust_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }

        if (setting.getString("model_sel", "").equals("arm")) {
            textViewArm21.setBackgroundResource(R.drawable.pad_arm_left);
            textViewArm22.setBackgroundResource(R.drawable.pad_arm_right);
            channel = "3";

            textViewTxtname2.setText(getResources().getString(R.string.channel3));
            String arm_value_display = String.valueOf(arm_value);  //int를 string으로
            textViewArm21.setText(arm_value_display);
            textViewArm22.setText(arm_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(arm_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%d%%", arm_value));
            mHoloCircularProgressBar.setProgress((arm_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, arm_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String abdomen_value_display = String.valueOf(abdomen_value);  //int를 string으로
            textViewAbdomen21.setText(abdomen_value_display);
            textViewAbdomen21.setText(abdomen_value_display);

            String bein_value_display = String.valueOf(bein_value);  //int를 string으로
            textViewBein21.setText(bein_value_display);
            textViewBein22.setText(bein_value_display);

            String brust_value_display = String.valueOf(brust_value);  //int를 string으로
            textViewBrust21.setText(brust_value_display);
            textViewBrust22.setText(brust_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }

        if (setting.getString("model_sel", "").equals("bein")) {
            textViewBein21.setBackgroundResource(R.drawable.pad_bein_left);
            textViewBein22.setBackgroundResource(R.drawable.pad_bein_right);
            channel = "7";

            textViewTxtname2.setText(getResources().getString(R.string.channel7));
            String bein_value_display = String.valueOf(bein_value);  //int를 string으로
            textViewBein21.setText(bein_value_display);
            textViewBein22.setText(bein_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(bein_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%d%%", bein_value));
            mHoloCircularProgressBar.setProgress((bein_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, bein_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String abdomen_value_display = String.valueOf(abdomen_value);  //int를 string으로
            textViewAbdomen21.setText(abdomen_value_display);
            textViewAbdomen22.setText(abdomen_value_display);

            String arm_value_display = String.valueOf(arm_value);  //int를 string으로
            textViewArm21.setText(arm_value_display);
            textViewArm22.setText(arm_value_display);

            String brust_value_display = String.valueOf(brust_value);  //int를 string으로
            textViewBrust21.setText(brust_value_display);
            textViewBrust22.setText(brust_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }

        if (setting.getString("model_sel", "").equals("brust")) {
            textViewBrust21.setBackgroundResource(R.drawable.pad_brust_left);
            textViewBrust22.setBackgroundResource(R.drawable.pad_brust_right);
            channel = "1";

            textViewTxtname2.setText(getResources().getString(R.string.channel1));
            String brust_value_display = String.valueOf(brust_value);  //int를 string으로
            textViewBrust21.setText(brust_value_display);
            textViewBrust22.setText(brust_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(brust_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%d%%", brust_value));
            mHoloCircularProgressBar.setProgress((brust_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, brust_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String abdomen_value_display = String.valueOf(abdomen_value);  //int를 string으로
            textViewAbdomen21.setText(abdomen_value_display);
            textViewAbdomen22.setText(abdomen_value_display);

            String arm_value_display = String.valueOf(arm_value);  //int를 string으로
            textViewArm21.setText(arm_value_display);
            textViewArm22.setText(arm_value_display);

            String bein_value_display = String.valueOf(bein_value);  //int를 string으로
            textViewBein21.setText(bein_value_display);
            textViewBein22.setText(bein_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }
    }

    public void buttonInit() {
        textViewAbdomen21.setBackgroundResource(R.drawable.pad_abdomen_left_off);
        textViewAbdomen22.setBackgroundResource(R.drawable.pad_abdomen_right_off);
        textViewArm21.setBackgroundResource(R.drawable.pad_arm_left_off);
        textViewArm22.setBackgroundResource(R.drawable.pad_arm_right_off);
        textViewBein21.setBackgroundResource(R.drawable.pad_bein_left_off);
        textViewBein22.setBackgroundResource(R.drawable.pad_bein_right_off);
        textViewBrust21.setBackgroundResource(R.drawable.pad_brust_left_off);
        textViewBrust22.setBackgroundResource(R.drawable.pad_brust_right_off);
    }

    private String channel;

    @Override
    public void onClick(View v) {
        SharedPreferences setting ;
        SharedPreferences.Editor editor;
        int progress;
        int intChannel;
        String per;

        switch (v.getId()) {
            case R.id.btn_start:  //완료 버튼
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                //startActivity(new Intent(DetailFrontActivity.this, DetailActivity.class));  //완료를 누르면 App이 죽는 현상 수정
                Intent a = new Intent(DetailFrontActivity.this, DetailActivity.class);
                a.putExtra("detailTo", "2");
                //완료 버튼 누르면 설정한값 넘긴다
                a.putExtra("abdomen",abdomen_value);
                a.putExtra("arm",arm_value);
                a.putExtra("bein",bein_value);
                a.putExtra("brust",brust_value);
                a.putExtra("arsch",arsch_value);
                a.putExtra("latt",latt_value);
                a.putExtra("waist",waist_value);
                a.putExtra("sideflank",sideflank_value);
                a.putExtra("all",all_value);
                startActivity(a);
                finish();
                break;

            case R.id.btn_model_back:
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                //전면에서 바로 후면으로 가면 어깨을 설정하도록
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "latt");
                editor.apply();
                Intent k = new Intent(DetailFrontActivity.this, DetailBackActivity.class);
                k.putExtra("arsch",arsch_value);
                k.putExtra("latt",latt_value);
                k.putExtra("waist",waist_value);
                k.putExtra("sideflank",sideflank_value);
                k.putExtra("abdomen",abdomen_value);
                k.putExtra("arm",arm_value);
                k.putExtra("bein",bein_value);
                k.putExtra("brust",brust_value);
                k.putExtra("all",all_value);
                startActivity(k);
                finish();
                break;

            case R.id.btn_strong_test:
                //Toast.makeText(this, "강도테스트 합니다.", Toast.LENGTH_SHORT).show();
                //////////////////////////////////
                // Serial
                //////////////////////////////////
                TextView textView = findViewById(R.id.txtPersent);
                String totalPer[] = textView.getText().toString().split("%");
                per = String.format(Locale.US, "%03d", Integer.parseInt(totalPer[0]));
                intChannel = Integer.parseInt(channel);
                Log.d(TAG_ACTIVITY, "Part = " + intChannel + ", Strength = " + per);

                mUsbReceiver.writeDataToSerial("S21;90;00;100;000;050;05;020;05;005;0;25;N");  //Waveform default
                mUsbReceiver.writeDataToSerial("S23;" + String.format(Locale.US, "%03d", all_value) + ";N");  //Waveform default
                switch (intChannel) {
                    case 1: // 흉부
                        mUsbReceiver.writeDataToSerial("A20;"+per+";000;000;000;000;000;000;000;N");
                        break;
                   case 2: // 복부
                        mUsbReceiver.writeDataToSerial("A20;000;"+per+";000;000;000;000;000;000;N");
                        break;
                   case 3: // 상완
                        mUsbReceiver.writeDataToSerial("A20;000;000;"+per+";000;000;000;000;000;N");
                       break;
                   case 7: // 허벅
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;"+per+";000;000;000;000;N");
                        break;
                }
                break;

            case R.id.btn_minus:
                progress = (mSeekBar.getProgress() - 1);
                mSeekBar.setProgress(progress);
                textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", progress));
                setPartValueSaved(progress);
                mHoloCircularProgressBar.setProgress((progress) * 0.01f);

                intChannel = Integer.parseInt(channel);
                per = String.format(Locale.US, "%03d", progress);
                switch (intChannel) {
                    case 1: // 흉부
                        mUsbReceiver.writeDataToSerial("A20;" + per + ";000;000;000;000;000;000;000;N");
                        break;
                    case 2: // 복부
                        mUsbReceiver.writeDataToSerial("A20;000;" + per + ";000;000;000;000;000;000;N");
                        break;
                    case 3: // 상완
                        mUsbReceiver.writeDataToSerial("A20;000;000;" + per + ";000;000;000;000;000;N");
                        break;
                    case 7: // 허벅
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;" + per + ";000;000;000;000;N");
                        break;
                }
                break;

            case R.id.btn_plus:
                progress = (mSeekBar.getProgress() + 1);
                mSeekBar.setProgress(progress);
                textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", progress));
                setPartValueSaved(progress);
                mHoloCircularProgressBar.setProgress((progress) * 0.01f);

                intChannel = Integer.parseInt(channel);
                per = String.format(Locale.US, "%03d", progress);
                switch (intChannel) {
                    case 1: // 흉부
                        mUsbReceiver.writeDataToSerial("A20;" + per + ";000;000;000;000;000;000;000;N");
                        break;
                    case 2: // 복부
                        mUsbReceiver.writeDataToSerial("A20;000;" + per + ";000;000;000;000;000;000;N");
                        break;
                    case 3: // 상완
                        mUsbReceiver.writeDataToSerial("A20;000;000;" + per + ";000;000;000;000;000;N");
                        break;
                    case 7: // 허벅
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;" + per + ";000;000;000;000;N");
                        break;
                }
                break;

            case R.id.btn_back:
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                Intent i = new Intent(DetailFrontActivity.this, DetailActivity.class);
                i.putExtra("detailTo", "2");
                i.putExtra("arsch",arsch_value);
                i.putExtra("latt",latt_value);
                i.putExtra("waist",waist_value);
                i.putExtra("sideflank",sideflank_value);
                i.putExtra("abdomen",abdomen_value);
                i.putExtra("arm",arm_value);
                i.putExtra("bein",bein_value);
                i.putExtra("brust",brust_value);
                i.putExtra("all",all_value);
                startActivity(i);
                finish();
                break;

            case R.id.btn_abdomen_21: // 복부
            case R.id.btn_abdomen_22:
                channel = "2";
                textViewTxtname2.setText(getResources().getString(R.string.channel2));
                buttonInit();
                textViewAbdomen21.setBackgroundResource(R.drawable.pad_abdomen_left);
                textViewAbdomen22.setBackgroundResource(R.drawable.pad_abdomen_right);
                fronstrenghtDisplay(abdomen_value);
                break;

            case R.id.btn_arm_21: // 상완
            case R.id.btn_arm_22:
                channel = "3";
                textViewTxtname2.setText(getResources().getString(R.string.channel3));
                buttonInit();
                textViewArm21.setBackgroundResource(R.drawable.pad_arm_left);
                textViewArm22.setBackgroundResource(R.drawable.pad_arm_right);
                fronstrenghtDisplay(arm_value);
                break;

            case R.id.btn_bein_21: // 허벅
            case R.id.btn_bein_22:
                channel = "7";
                textViewTxtname2.setText(getResources().getString(R.string.channel7));
                buttonInit();
                textViewBein21.setBackgroundResource(R.drawable.pad_bein_left);
                textViewBein22.setBackgroundResource(R.drawable.pad_bein_right);
                fronstrenghtDisplay(bein_value);
                break;

            case R.id.btn_brust_21: // 흉부
            case R.id.btn_brust_22:
                channel = "1";
                textViewTxtname2.setText(getResources().getString(R.string.channel1));
                buttonInit();
                textViewBrust21.setBackgroundResource(R.drawable.pad_brust_left);
                textViewBrust22.setBackgroundResource(R.drawable.pad_brust_right);
                fronstrenghtDisplay(brust_value);
                break;
        }
    }

    private void fronstrenghtDisplay (int value) {  //전면에서 부위 바꾸면 설정한 대로 보여주는 함수
        onProgressChanged(mSeekBar, value,true);
    }

    @Override
    public void onDestroy() {
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //seekBar 도 설정대로 나타나게 한다
        mSeekBar.setProgress(progress);
        //seekBar 도 설정대로 나타나게 한다
        TextView textView = findViewById(R.id.txtPersent);
        textView.setText(String.format(Locale.US, "%d%%", progress));
        setPartValueSaved(progress);
        mHoloCircularProgressBar.setProgress(progress * 0.01f);
//        setText(progress+"");
        String per;
        switch (channel) {
            case "1": // 흉부
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, brust_value));
                per = String.format(Locale.US, "%d", brust_value);  //int를 string으로
                textViewBrust21.setText(per);
                textViewBrust22.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;" + String.format(Locale.US, "%03d", brust_value) + ";000;000;000;000;000;000;000;N");
                break;
            case "2": // 복부
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, abdomen_value));
                per = String.format(Locale.US, "%d", abdomen_value);  //int를 string으로
                textViewAbdomen21.setText(per);
                textViewAbdomen22.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;" + String.format(Locale.US, "%03d", abdomen_value) + ";000;000;000;000;000;000;N");
                break;
            case "3": // 상완
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, arm_value));
                per = String.format(Locale.US, "%d", arm_value);  //int를 string으로
                textViewArm21.setText(per);
                textViewArm22.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;" + String.format(Locale.US, "%03d", arm_value) + ";000;000;000;000;000;N");
                break;
            case "7": // 허벅
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, bein_value));
                per = String.format(Locale.US, "%d", bein_value);  //int를 string으로
                textViewBein21.setText(per);
                textViewBein22.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;000;" + String.format(Locale.US, "%03d", bein_value) + ";000;000;000;000;N");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

//    @Override
//    public boolean onLongClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_minus:
//                SeekBar seekBar = findViewById(R.id.btn_sb);
//                seekBar.setProgress(seekBar.getProgress() - 1);
//                TextView textView = findViewById(R.id.txtPersent);
//                textView.setText(seekBar.getProgress() + "%");
//                break;
//
//            case R.id.btn_plus:
//                seekBar = findViewById(R.id.btn_sb);
//                seekBar.setProgress(seekBar.getProgress() + 1);
//                textView = findViewById(R.id.txtPersent);
//                textView.setText(seekBar.getProgress() + "%");
//                break;
//        }
//        return true;
//    }
//
//    public void longClickPlus() {
//        SeekBar seekBar = findViewById(R.id.btn_sb);
//        seekBar.setProgress(seekBar.getProgress() + 1);
//        TextView textView = findViewById(R.id.txtPersent);
//        textView.setText(seekBar.getProgress() + "%");
//        setText(seekBar.getProgress()+"");
//    }
//    public void longClickMinus() {
//        SeekBar seekBar = findViewById(R.id.btn_sb);
//        seekBar.setProgress(seekBar.getProgress() - 1);
//        TextView textView = findViewById(R.id.txtPersent);
//        textView.setText(seekBar.getProgress() + "%");
//        setText(seekBar.getProgress()+"");
//    }

    private void setPartValueSaved(int value) {  //설정한값 저장
        int intChannel = Integer.parseInt(channel);
        switch (intChannel) {
            case 1: // 흉부
                brust_value = value;
                break;
            case 2: // 복부
                abdomen_value = value;
                break;
            case 3: // 상완
                arm_value = value;
                break;
            case 7: // 허벅
                bein_value = value;
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
