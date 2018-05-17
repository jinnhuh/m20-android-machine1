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

public class DetailBackActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    String TAG_ACTIVITY = "M20_DetailBack";

    SeekBar mSeekBar;
    private HoloCircularProgressBar mHoloCircularProgressBar;
    private int STRONG_BP_VALUE_DEFAULT = 30; // body part Strong value

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    //부분 강도 설정값 저장을 위한 변수
    private int arsch_value = 0;
    private int latt_value = 0;
    private int waist_value = 0;
    private int sideflank_value = 0;

    private int abdomen_value = 0;
    private int arm_value = 0;
    private int bein_value = 0;
    private int brust_value = 0;
    private int all_value = 0;
    String playID = null;  //어떤 운동인지 저장

    TextView textViewArsch31;
    TextView textViewArsch32;
    TextView textViewLatt31;
    TextView textViewLatt32;
    TextView textViewWaist31;
    TextView textViewWaist32;
    TextView textViewSideflank31;
    TextView textViewSideflank32;
    TextView textViewTxtname3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_3);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate()");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        Intent intent = getIntent();  //강도 설정한 값 받자
        if(intent!=null) {
            latt_value = intent.getIntExtra("latt", STRONG_BP_VALUE_DEFAULT);
            sideflank_value = intent.getIntExtra("sideflank", STRONG_BP_VALUE_DEFAULT);
            waist_value = intent.getIntExtra("waist", STRONG_BP_VALUE_DEFAULT);
            arsch_value = intent.getIntExtra("arsch", STRONG_BP_VALUE_DEFAULT);

            brust_value = intent.getIntExtra("brust", STRONG_BP_VALUE_DEFAULT);
            abdomen_value = intent.getIntExtra("abdomen", STRONG_BP_VALUE_DEFAULT);
            arm_value = intent.getIntExtra("arm", STRONG_BP_VALUE_DEFAULT);
            bein_value = intent.getIntExtra("bein", STRONG_BP_VALUE_DEFAULT);
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
//            Toast.makeText(this, "no connectionDetailBack", Toast.LENGTH_SHORT).show();
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
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        fronBackText.append(builder);

        textViewArsch31 = findViewById(R.id.btn_arsch_31);
        textViewArsch32 = findViewById(R.id.btn_arsch_32);
        textViewLatt31 = findViewById(R.id.btn_latt_31);
        textViewLatt32 = findViewById(R.id.btn_latt_32);
        textViewWaist31 = findViewById(R.id.btn_waist_31);
        textViewWaist32 = findViewById(R.id.btn_waist_32);
        textViewSideflank31 = findViewById(R.id.btn_sideflank_31);
        textViewSideflank32 = findViewById(R.id.btn_sideflank_32);
        textViewTxtname3 = findViewById(R.id.txt_name3);

        textViewArsch31.setOnClickListener(this);
        textViewArsch32.setOnClickListener(this);
        textViewLatt31.setOnClickListener(this);
        textViewLatt32.setOnClickListener(this);
        textViewWaist31.setOnClickListener(this);
        textViewWaist32.setOnClickListener(this);
        textViewSideflank31.setOnClickListener(this);
        textViewSideflank32.setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_model_back).setOnClickListener(this); // 백버튼
        findViewById(R.id.btn_strong_test).setOnClickListener(this); // 강도테스트

        mSeekBar = findViewById(R.id.btn_sb3);
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
        textView.setText("후면 부분강도 조절");  //운동명 display가 아닌 후면 부분강도 조절 이라고 display m20 요구 사항

        //부분강도 조절 메인 타이틀 display
        if (setting.getString("model_sel", "").equals("arsch")) {
            textViewArsch31.setBackgroundResource(R.drawable.pad_arsch_left);
            textViewArsch32.setBackgroundResource(R.drawable.pad_arsch_right);
            channel="8";
            textViewTxtname3.setText(getResources().getString(R.string.channel8));
            String arsch_value_display = String.valueOf(arsch_value);  //int를 string으로
            textViewArsch31.setText(arsch_value_display);
            textViewArsch32.setText(arsch_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(arsch_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%03d%%", arsch_value));
            mHoloCircularProgressBar.setProgress((arsch_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, arsch_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String latt_value_display = String.valueOf(latt_value);  //int를 string으로
            textViewLatt31.setText(latt_value_display);
            textViewLatt32.setText(latt_value_display);

            String waist_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewWaist31.setText(waist_value_display);
            textViewWaist32.setText(waist_value_display);

            String sideflank_value_display = String.valueOf(sideflank_value);  //int를 string으로
            textViewSideflank31.setText(sideflank_value_display);
            textViewSideflank32.setText(sideflank_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }
        if (setting.getString("model_sel", "").equals("latt")) {
            textViewLatt31.setBackgroundResource(R.drawable.pad_latt_left);
            textViewLatt32.setBackgroundResource(R.drawable.pad_latt_right);
            channel="5";
            textViewTxtname3.setText(getResources().getString(R.string.channel5));
            String latt_value_display = String.valueOf(latt_value);  //int를 string으로
            textViewLatt31.setText(latt_value_display);
            textViewLatt32.setText(latt_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(latt_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%03d%%", latt_value));
            mHoloCircularProgressBar.setProgress((latt_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, latt_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String arsch_value_display = String.valueOf(latt_value);  //int를 string으로
            textViewArsch31.setText(arsch_value_display);
            textViewArsch32.setText(arsch_value_display);

            String waist_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewWaist31.setText(waist_value_display);
            textViewWaist32.setText(waist_value_display);

            String sideflank_value_display = String.valueOf(sideflank_value);  //int를 string으로
            textViewSideflank31.setText(sideflank_value_display);
            textViewSideflank32.setText(sideflank_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }
        if (setting.getString("model_sel", "").equals("waist")) {
            textViewWaist31.setBackgroundResource(R.drawable.pad_waist_left);
            textViewWaist32.setBackgroundResource(R.drawable.pad_waist_right);
            channel="6";
            textViewTxtname3.setText(getResources().getString(R.string.channel6));
            String waist_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewWaist31.setText(waist_value_display);
            textViewWaist32.setText(waist_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(waist_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%03d%%", waist_value));
            mHoloCircularProgressBar.setProgress((waist_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, waist_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String arsch_value_display = String.valueOf(latt_value);  //int를 string으로
            textViewArsch31.setText(arsch_value_display);
            textViewArsch32.setText(arsch_value_display);

            String latt_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewLatt31.setText(latt_value_display);
            textViewLatt32.setText(latt_value_display);

            String sideflank_value_display = String.valueOf(sideflank_value);  //int를 string으로
            textViewSideflank31.setText(sideflank_value_display);
            textViewSideflank32.setText(sideflank_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }
        if (setting.getString("model_sel", "").equals("sideflank")) {
            textViewSideflank31.setBackgroundResource(R.drawable.pad_sideflank_left);
            textViewSideflank32.setBackgroundResource(R.drawable.pad_sideflank_right);
            channel="4";
            textViewTxtname3.setText(getResources().getString(R.string.channel4));
            String sideflank_value_display = String.valueOf(sideflank_value);  //int를 string으로
            textViewSideflank31.setText(sideflank_value_display);
            textViewSideflank32.setText(sideflank_value_display);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            mSeekBar.setProgress(sideflank_value);
            textView = findViewById(R.id.txtPersent);
            textView.setText(String.format(Locale.US, "%03d%%", sideflank_value));
            mHoloCircularProgressBar.setProgress((sideflank_value) * 0.01f);
            //Seekbar 와 Circular 를 받아온 값으로 보여준다
            onProgressChanged(mSeekBar, sideflank_value,true);

            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
            String arsch_value_display = String.valueOf(latt_value);  //int를 string으로
            textViewArsch31.setText(arsch_value_display);
            textViewArsch32.setText(arsch_value_display);

            String latt_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewLatt31.setText(latt_value_display);
            textViewLatt32.setText(latt_value_display);

            String waist_value_display = String.valueOf(waist_value);  //int를 string으로
            textViewWaist31.setText(waist_value_display);
            textViewWaist32.setText(waist_value_display);
            // 그 외의 것들 저장된 값으로 보여준다 이거 안해주면 20으로 보여줘서 짜증나지만 해준다
        }

    }

    public void buttonInit() {
        textViewArsch31.setBackgroundResource(R.drawable.pad_arsch_left_off);
        textViewArsch32.setBackgroundResource(R.drawable.pad_arsch_right_off);
        textViewLatt31.setBackgroundResource(R.drawable.pad_latt_left_off);
        textViewLatt32.setBackgroundResource(R.drawable.pad_latt_right_off);
        textViewWaist31.setBackgroundResource(R.drawable.pad_waist_left_off);
        textViewWaist32.setBackgroundResource(R.drawable.pad_waist_right_off);
        textViewSideflank31.setBackgroundResource(R.drawable.pad_sideflank_left_off);
        textViewSideflank32.setBackgroundResource(R.drawable.pad_sideflank_right_off);
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

                //startActivity(new Intent(DetailBackActivity.this, DetailActivity.class));  //완료를 누르면 App이 죽는 현상 수정
                Intent a = new Intent(DetailBackActivity.this, DetailActivity.class);
                a.putExtra("detailTo", "2");
                a.putExtra("arsch",arsch_value);
                a.putExtra("latt",latt_value);
                a.putExtra("waist",waist_value);
                a.putExtra("sideflank",sideflank_value);
                a.putExtra("abdomen",abdomen_value);
                a.putExtra("arm",arm_value);
                a.putExtra("bein",bein_value);
                a.putExtra("brust",brust_value);
                a.putExtra("all",all_value);
                startActivity(a);
                finish();
                break;

            case R.id.btn_model_back:
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                //후면에서 바로 전면으로 가면 가슴을 설정하도록
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "brust");
                editor.apply();
                Intent k = new Intent(DetailBackActivity.this, DetailFrontActivity.class);
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

                TextView textView = findViewById(R.id.txtPersent);
                String totalPer[] = textView.getText().toString().split("%");
                per = String.format(Locale.US, "%03d", Integer.parseInt(totalPer[0]));
                intChannel = Integer.parseInt(channel);
                Log.d(TAG_ACTIVITY, "Part = " + intChannel + ", Strength = " + per);

                mUsbReceiver.writeDataToSerial("S21;90;00;100;000;050;05;020;05;005;0;25;N");  //Waveform default
                mUsbReceiver.writeDataToSerial("S23;" + String.format(Locale.US, "%03d", all_value) + ";N");  //Waveform default
                switch (intChannel) {
                    case 4: // 옆구리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;"+per+";000;N");
                        break;
                    case 5: // 어깨
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;"+per+";000;000;000;N");
                        break;
                    case 6: // 허리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;"+per+";000;000;N");
                        break;
                    case 8: // 둔부
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;"+per+";N");
                        break;
                }
                break;

            case R.id.btn_minus:
                progress = (mSeekBar.getProgress() - 1);
                mSeekBar.setProgress(progress);
                textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", progress));
                setPartValueSaved(progress);
                mHoloCircularProgressBar.setProgress(progress * 0.01f);

                intChannel = Integer.parseInt(channel);
                per = String.format(Locale.US, "%03d", progress);
                switch (intChannel) {
                    case 4: // 옆구리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;"+per+";000;N");
                        break;
                    case 5: // 어깨
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;"+per+";000;000;000;N");
                        break;
                    case 6: // 허리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;"+per+";000;000;N");
                        break;
                    case 8: // 둔부
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;"+per+";N");
                        break;
                }
                break;

            case R.id.btn_plus:
                progress = (mSeekBar.getProgress() + 1);
                mSeekBar.setProgress(progress);
                textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", progress));
                setPartValueSaved(progress);
                mHoloCircularProgressBar.setProgress(progress * 0.01f);

                intChannel = Integer.parseInt(channel);
                per = String.format(Locale.US, "%03d", progress);
                switch (intChannel) {
                    case 4: // 옆구리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;"+per+";000;N");
                        break;
                    case 5: // 어깨
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;"+per+";000;000;000;N");
                        break;
                    case 6: // 허리
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;"+per+";000;000;N");
                        break;
                    case 8: // 둔부
                        mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;"+per+";N");
                        break;
                }
                break;

            case R.id.btn_back:
                mUsbReceiver.writeDataToSerial("S22;N");            // Stop.

                Intent i = new Intent(DetailBackActivity.this, DetailActivity.class);
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

            case R.id.btn_arsch_31: // 둔부
            case R.id.btn_arsch_32:
                channel = "8";
                textViewTxtname3.setText(getResources().getString(R.string.channel8));
                buttonInit();

                textViewArsch31.setBackgroundResource(R.drawable.pad_arsch_left);
                textViewArsch32.setBackgroundResource(R.drawable.pad_arsch_right);
                backstrenghtDisplay(arsch_value);
                break;

            case R.id.btn_latt_31: // 어깨
            case R.id.btn_latt_32:
               channel = "5";
                textViewTxtname3.setText(getResources().getString(R.string.channel5));
                buttonInit();
                textViewLatt31.setBackgroundResource(R.drawable.pad_latt_left);
                textViewLatt32.setBackgroundResource(R.drawable.pad_latt_right);
                backstrenghtDisplay(latt_value);
                break;

            case R.id.btn_waist_31: // 허리
            case R.id.btn_waist_32:
                channel = "6";
                textViewTxtname3.setText(getResources().getString(R.string.channel6));
                buttonInit();
                textViewWaist31.setBackgroundResource(R.drawable.pad_waist_left);
                textViewWaist32.setBackgroundResource(R.drawable.pad_waist_right);
                backstrenghtDisplay(waist_value);
                break;

            case R.id.btn_sideflank_31: // 옆구리
            case R.id.btn_sideflank_32:
                channel = "4";
                textViewTxtname3.setText(getResources().getString(R.string.channel4));
                buttonInit();
                textViewSideflank31.setBackgroundResource(R.drawable.pad_sideflank_left);
                textViewSideflank32.setBackgroundResource(R.drawable.pad_sideflank_right);
                backstrenghtDisplay(sideflank_value);
                break;
        }
    }

    private void backstrenghtDisplay (int value) {  //후면에서 부위 바꾸면 설정한 대로 보여주는 함수
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
            case "4": // 옆구리
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, sideflank_value));
                per = String.format(Locale.US, "%d", sideflank_value);  //int를 string으로
                textViewSideflank31.setText(per);
                textViewSideflank32.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;" + String.format(Locale.US, "%03d", sideflank_value) + ";000;N");
            case "5": // 어깨
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, latt_value));
                per = String.format(Locale.US, "%d", latt_value);  //int를 string으로
                textViewLatt31.setText(per);
                textViewLatt32.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;000;000;" + String.format(Locale.US, "%03d", latt_value) + ";000;000;000;N");
                break;
            case "6": // 허리
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, waist_value));
                per = String.format(Locale.US, "%d", waist_value);  //int를 string으로
                textViewWaist31.setText(per);
                textViewWaist32.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;" + String.format(Locale.US, "%03d", waist_value) + ";000;000;N");
                break;
            case "8": // 둔부
                Log.d(TAG_ACTIVITY, String.format(Locale.US, "Progress = %d, Value = %d.", progress, arsch_value));
                per = String.format(Locale.US, "%d", arsch_value);  //int를 string으로
                textViewArsch31.setText(per);
                textViewArsch32.setText(per);
//                mUsbReceiver.writeDataToSerial("A20;000;000;000;000;000;000;000;" + String.format(Locale.US, "%03d", arsch_value) + ";N");
                break;
        }
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
        switch (channel) {
            case "4": // 옆구리
                sideflank_value = value;
                break;
            case "5": // 어깨
                latt_value = value;
                break;
            case "6": // 허리
                waist_value = value;
                break;
            case "8": // 둔부
                arsch_value = value;
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
