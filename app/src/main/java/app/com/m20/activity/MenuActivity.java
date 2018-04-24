package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import app.com.m20.R;
import app.com.m20.db.DbManagement;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.model.Body;
import app.com.m20.utils.Utils;
import io.realm.Realm;

/**
 * Created by kimyongyeon on 2017-11-10.
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    String TAG_ACTIVITY = "M20_Menu";

    LinearLayout linearLayout;

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    Realm mRealm;
    DbManagement dbManagement;
    private void init() {
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        dbManagement = new DbManagement(mRealm);
    }

    String menu1;
    String menu2;
    String menu3;
    String menu4;
    String menu5;

    //machine_program_id 저장하는 변수 (my program 운동명 저장)
    private int my_program_id_1 = 0;
    private int my_program_id_2 = 0;
    private int my_program_id_3 = 0;
    private int my_program_id_4 = 0;
    private int my_program_id_5 = 0;
    //machine_program_id 저장하는 변수 (my program 강도 저장)
    int my_program_id_1_strength_1 = 0;
    int my_program_id_1_strength_2 = 0;
    int my_program_id_1_strength_3 = 0;
    int my_program_id_1_strength_4 = 0;
    int my_program_id_1_strength_5 = 0;
    int my_program_id_1_strength_6 = 0;
    int my_program_id_1_strength_7 = 0;
    int my_program_id_1_strength_8 = 0;

    int my_program_id_2_strength_1 = 0;
    int my_program_id_2_strength_2 = 0;
    int my_program_id_2_strength_3 = 0;
    int my_program_id_2_strength_4 = 0;
    int my_program_id_2_strength_5 = 0;
    int my_program_id_2_strength_6 = 0;
    int my_program_id_2_strength_7 = 0;
    int my_program_id_2_strength_8 = 0;

    int my_program_id_3_strength_1 = 0;
    int my_program_id_3_strength_2 = 0;
    int my_program_id_3_strength_3 = 0;
    int my_program_id_3_strength_4 = 0;
    int my_program_id_3_strength_5 = 0;
    int my_program_id_3_strength_6 = 0;
    int my_program_id_3_strength_7 = 0;
    int my_program_id_3_strength_8 = 0;

    int my_program_id_4_strength_1 = 0;
    int my_program_id_4_strength_2 = 0;
    int my_program_id_4_strength_3 = 0;
    int my_program_id_4_strength_4 = 0;
    int my_program_id_4_strength_5 = 0;
    int my_program_id_4_strength_6 = 0;
    int my_program_id_4_strength_7 = 0;
    int my_program_id_4_strength_8 = 0;

    int my_program_id_5_strength_1 = 0;
    int my_program_id_5_strength_2 = 0;
    int my_program_id_5_strength_3 = 0;
    int my_program_id_5_strength_4 = 0;
    int my_program_id_5_strength_5 = 0;
    int my_program_id_5_strength_6 = 0;
    int my_program_id_5_strength_7 = 0;
    int my_program_id_5_strength_8 = 0;

    String machine_program_name_1 = null;
    String machine_program_name_2 = null;
    String machine_program_name_3 = null;
    String machine_program_name_4 = null;
    String machine_program_name_5 = null;

    boolean my_program_button_push = false;  //my program 누르는지 알기 위해서

    public void setText(String str, Body body) {
        switch (str) {
            case "21":
                menu1 = body.getProgramId();
                mUsbReceiver.writeDataToSerial("A22;0;N"); // 마이프로그램
                break;
            case "22":
                menu2 = body.getProgramId();
                mUsbReceiver.writeDataToSerial("A23;0;N"); // 마이프로그램
                break;
            case "23":
                menu3 = body.getProgramId();
                mUsbReceiver.writeDataToSerial("A24;0;N"); // 마이프로그램
                break;
            case "24":
                menu4 = body.getProgramId();
                mUsbReceiver.writeDataToSerial("A25;0;N"); // 마이프로그램
                break;
            case "25":
                menu5 = body.getProgramId();
                break;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG_ACTIVITY, "onDestroy() ");
        if (mUsbReceiver != null) {  //운동 강도 설정에서 백해서 오면 운동 시간이 종료 될때  registerReceiver 가 아니라고 죽는다..그래서 일단 막는다
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
    }

    private void receiveDatafromRegProgramname() {  //Reg로 부터 data 받자
        SharedPreferences prefs =getSharedPreferences("machine_program_name", MODE_PRIVATE);
        machine_program_name_1 = prefs.getString("machine_program_name_1", "0"); //키값, 디폴트값
        machine_program_name_2 = prefs.getString("machine_program_name_2", "0"); //키값, 디폴트값
        machine_program_name_3 = prefs.getString("machine_program_name_3", "0"); //키값, 디폴트값
        machine_program_name_4 = prefs.getString("machine_program_name_4", "0"); //키값, 디폴트값
        machine_program_name_5 = prefs.getString("machine_program_name_5", "0"); //키값, 디폴트값
        my_program_id_1 = prefs.getInt("my_program_id_1", 0); //키값, 디폴트값
        my_program_id_2 = prefs.getInt("my_program_id_2", 0); //키값, 디폴트값
        my_program_id_3 = prefs.getInt("my_program_id_3", 0); //키값, 디폴트값
        my_program_id_4 = prefs.getInt("my_program_id_4", 0); //키값, 디폴트값
        my_program_id_5 = prefs.getInt("my_program_id_5", 0); //키값, 디폴트값
    }

    private void receiveDatafromRegStrength() {  //Reg로 부터 data 받자
        SharedPreferences prefs =getSharedPreferences("my_program_strength", MODE_PRIVATE);
        my_program_id_1_strength_1 = prefs.getInt("my_program_id_1_strength_1",0 );
        my_program_id_1_strength_2 = prefs.getInt("my_program_id_1_strength_2",0 );
        my_program_id_1_strength_3 = prefs.getInt("my_program_id_1_strength_3",0 );
        my_program_id_1_strength_4 = prefs.getInt("my_program_id_1_strength_4",0 );
        my_program_id_1_strength_5 = prefs.getInt("my_program_id_1_strength_5",0 );
        my_program_id_1_strength_6 = prefs.getInt("my_program_id_1_strength_6",0 );
        my_program_id_1_strength_7 = prefs.getInt("my_program_id_1_strength_7",0 );
        my_program_id_1_strength_8 = prefs.getInt("my_program_id_1_strength_8",0 );

        my_program_id_2_strength_1 = prefs.getInt("my_program_id_2_strength_1",0 );
        my_program_id_2_strength_2 = prefs.getInt("my_program_id_2_strength_2",0 );
        my_program_id_2_strength_3 = prefs.getInt("my_program_id_2_strength_3",0 );
        my_program_id_2_strength_4 = prefs.getInt("my_program_id_2_strength_4",0 );
        my_program_id_2_strength_5 = prefs.getInt("my_program_id_2_strength_5",0 );
        my_program_id_2_strength_6 = prefs.getInt("my_program_id_2_strength_6",0 );
        my_program_id_2_strength_7 = prefs.getInt("my_program_id_2_strength_7",0 );
        my_program_id_2_strength_8 = prefs.getInt("my_program_id_2_strength_8",0 );

        my_program_id_3_strength_1 = prefs.getInt("my_program_id_3_strength_1",0 );
        my_program_id_3_strength_2 = prefs.getInt("my_program_id_3_strength_2",0 );
        my_program_id_3_strength_3 = prefs.getInt("my_program_id_3_strength_3",0 );
        my_program_id_3_strength_4 = prefs.getInt("my_program_id_3_strength_4",0 );
        my_program_id_3_strength_5 = prefs.getInt("my_program_id_3_strength_5",0 );
        my_program_id_3_strength_6 = prefs.getInt("my_program_id_3_strength_6",0 );
        my_program_id_3_strength_7 = prefs.getInt("my_program_id_3_strength_7",0 );
        my_program_id_3_strength_8 = prefs.getInt("my_program_id_3_strength_8",0 );

        my_program_id_4_strength_1 = prefs.getInt("my_program_id_4_strength_1",0 );
        my_program_id_4_strength_2 = prefs.getInt("my_program_id_4_strength_2",0 );
        my_program_id_4_strength_3 = prefs.getInt("my_program_id_4_strength_3",0 );
        my_program_id_4_strength_4 = prefs.getInt("my_program_id_4_strength_4",0 );
        my_program_id_4_strength_5 = prefs.getInt("my_program_id_4_strength_5",0 );
        my_program_id_4_strength_6 = prefs.getInt("my_program_id_4_strength_6",0 );
        my_program_id_4_strength_7 = prefs.getInt("my_program_id_4_strength_7",0 );
        my_program_id_4_strength_8 = prefs.getInt("my_program_id_4_strength_8",0 );

        my_program_id_5_strength_1 = prefs.getInt("my_program_id_5_strength_1",0 );
        my_program_id_5_strength_2 = prefs.getInt("my_program_id_5_strength_2",0 );
        my_program_id_5_strength_3 = prefs.getInt("my_program_id_5_strength_3",0 );
        my_program_id_5_strength_4 = prefs.getInt("my_program_id_5_strength_4",0 );
        my_program_id_5_strength_5 = prefs.getInt("my_program_id_5_strength_5",0 );
        my_program_id_5_strength_6 = prefs.getInt("my_program_id_5_strength_6",0 );
        my_program_id_5_strength_7 = prefs.getInt("my_program_id_5_strength_7",0 );
        my_program_id_5_strength_8 = prefs.getInt("my_program_id_5_strength_8",0 );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        findViewById(R.id.ivClose).setOnClickListener(this);
        findViewById(R.id.btnMenu1).setOnClickListener(this);
        findViewById(R.id.btnMenu2).setOnClickListener(this);
        findViewById(R.id.btnMenu3).setOnClickListener(this);
        findViewById(R.id.btnMenu4).setOnClickListener(this);
        findViewById(R.id.btnMenu5).setOnClickListener(this);
        findViewById(R.id.btnMenu11).setOnClickListener(this);
        findViewById(R.id.btnMenu12).setOnClickListener(this);
        findViewById(R.id.btnMenu13).setOnClickListener(this);
        findViewById(R.id.btnMenu14).setOnClickListener(this);
        findViewById(R.id.btnMenu15).setOnClickListener(this);
//        findViewById(R.id.btn_back).setOnClickListener(this);

        linearLayout = findViewById(R.id.subMenu);

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
//        Log.i(TAG_ACTIVITY, "J.Y.T MenuActivity registerReceiver ");
        registerReceiver(mUsbReceiver, filter);
        // load default baud rate
        int mBaudrate = mUsbReceiver.loadDefaultBaudrate();
        // for requesting permission
        // setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);
        if (mSerial.begin(mBaudrate)) {
            mUsbReceiver.loadDefaultSettingValues();
            mUsbReceiver.mainloop(mRealm);
            //mUsbReceiver.writeDataToSerial("A21;0;N"); // 마이프로그램
        } else {
//            Toast.makeText(this, "no connectionMenu", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
        receiveDatafromRegProgramname();  //Reg로 부터 data 받자
        receiveDatafromRegStrength();

//        SharedPreferences prefs =getSharedPreferences("booking_end_time", MODE_PRIVATE);
//        String booking_end_time = prefs.getString("end_time", "0"); //키값, 디폴트값
    }

    private void transAnimation(boolean bool){

        AnimationSet aniInSet = new AnimationSet(true);
        AnimationSet aniOutSet = new AnimationSet(true);
        aniInSet.setInterpolator(new AccelerateInterpolator());
        Animation transInAni = new TranslateAnimation(0,0,500.0f,0);
        Animation transOutAni = new TranslateAnimation(0,0,0,500.0f);
        transInAni.setDuration(800);
        transOutAni.setDuration(800);
        aniInSet.addAnimation(transInAni);
        aniOutSet.addAnimation(transOutAni);

        if (bool) {
            linearLayout.setAnimation(aniInSet);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setAnimation(aniOutSet);
            linearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        Button button;
        Resources res;
        String text;
        SharedPreferences setting;
        SharedPreferences.Editor editor;

        switch (v.getId()) {
            case R.id.ivClose:
                transAnimation(false);
                break;
/*
            case R.id.btn_back:
                //Intent intent = new Intent(MenuActivity.this, PersonTabActivity.class);
                Intent intent = new Intent(MenuActivity.this, RegActivity.class);
                startActivity(intent);
                finish();
                break;
*/

            case R.id.btnMenu1:
                my_program_button_push = false;
                TextView textView = findViewById(R.id.txtTitle);
                Resources resources = getResources();
                String str = resources.getString(R.string.program_menu_buldup_title);
                textView.setText(str);
                button = findViewById(R.id.btnMenu11);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_buldup_menu1);
                button.setText(text);

                button = findViewById(R.id.btnMenu12);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_buldup_menu2);
                button.setText(text);

                button = findViewById(R.id.btnMenu13);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_buldup_menu3);
                button.setText(text);

                button = findViewById(R.id.btnMenu14);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_buldup_menu4);
                button.setText(text);

                button = findViewById(R.id.btnMenu15);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_buldup_menu5);
                button.setText(text);

                transAnimation(true);
                break;

            case R.id.btnMenu2:
                my_program_button_push = false;
                textView = findViewById(R.id.txtTitle);
                resources = getResources();
                str = resources.getString(R.string.program_menu_shaping_title);
                textView.setText(str);

                button = findViewById(R.id.btnMenu11);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_shaping_menu1);
                button.setText(text);

                button = findViewById(R.id.btnMenu12);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_shaping_menu2);
                button.setText(text);

                button = findViewById(R.id.btnMenu13);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_shaping_menu3);
                button.setText(text);

                button = findViewById(R.id.btnMenu14);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_shaping_menu4);
                button.setText(text);

                button = findViewById(R.id.btnMenu15);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_shaping_menu5);
                button.setText(text);

                transAnimation(true);
                break;

            case R.id.btnMenu3:
                my_program_button_push = false;
                textView = findViewById(R.id.txtTitle);
                resources = getResources();
                str = resources.getString(R.string.program_menu_healthcare_title);
                textView.setText(str);

                button = findViewById(R.id.btnMenu11);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_healthcare_menu1);
                button.setText(text);

                button = findViewById(R.id.btnMenu12);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_healthcare_menu2);
                button.setText(text);

                button = findViewById(R.id.btnMenu13);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_healthcare_menu3);
                button.setText(text);

                button = findViewById(R.id.btnMenu14);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_healthcare_menu4);
                button.setText(text);

                button = findViewById(R.id.btnMenu15);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_healthcare_menu5);
                button.setText(text);

                transAnimation(true);
                break;

            case R.id.btnMenu4:
                my_program_button_push = false;
                textView = findViewById(R.id.txtTitle);
                resources = getResources();
                str = resources.getString(R.string.program_menu_massage_title);
                textView.setText(str);

                button = findViewById(R.id.btnMenu11);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_massage_menu1);
                button.setText(text);

                button = findViewById(R.id.btnMenu12);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_massage_menu2);
                button.setText(text);

                button = findViewById(R.id.btnMenu13);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_massage_menu3);
                button.setText(text);

                button = findViewById(R.id.btnMenu14);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_massage_menu4);
                button.setText(text);

                button = findViewById(R.id.btnMenu15);
                button.setVisibility(View.VISIBLE);
                res = getResources();
                text = res.getString(R.string.program_massage_menu5);
                button.setText(text);

                transAnimation(true);
                break;

            case R.id.btnMenu5: // my program
                my_program_button_push = true;
                if (my_program_id_1 != 0) {
                    textView = findViewById(R.id.txtTitle);
                    resources = getResources();
                    str = resources.getString(R.string.program_menu_my_title);
                    textView.setText(str);
                    if (my_program_id_1 != 0) {
                        button = findViewById(R.id.btnMenu11);
                        text = (intTostring(my_program_id_1));
                        button.setText(idToStr(text));
                    }
                    if (my_program_id_2 != 0) {
                        button = findViewById(R.id.btnMenu12);
                        text = (intTostring(my_program_id_2));
                        button.setText(idToStr(text));
                    }
                    else {
                        button = findViewById(R.id.btnMenu12);
                        button.setVisibility(View.GONE);
                    }
                    if (my_program_id_3 != 0) {
                        button = findViewById(R.id.btnMenu13);
                        text = (intTostring(my_program_id_3));
                        button.setText(idToStr(text));
                    }
                    else {
                        button = findViewById(R.id.btnMenu13);
                        button.setVisibility(View.GONE);
                    }
                    if (my_program_id_4 != 0) {
                        button = findViewById(R.id.btnMenu14);
                        text = (intTostring(my_program_id_4));
                        button.setText(idToStr(text));
                    }
                    else {
                        button = findViewById(R.id.btnMenu14);
                        button.setVisibility(View.GONE);
                    }
                    if (my_program_id_5 != 0) {
                        button = findViewById(R.id.btnMenu15);
                        text = (intTostring(my_program_id_5));
                        button.setText(idToStr(text));
                    }
                    else {
                        button = findViewById(R.id.btnMenu15);
                        button.setVisibility(View.GONE);
                    }
                    transAnimation(true);
                }
                else
//                    Toast.makeText(this, "My Program 이 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG_ACTIVITY, "No My Program!!!");
                break;

            case R.id.btnMenu11:

                findViewById(R.id.subMenu).setVisibility(View.GONE);
                Intent intent = new Intent(MenuActivity.this, DetailActivity.class);
                Button button1 = findViewById(R.id.btnMenu11);

                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.clear();
                editor.putString("main_title", button1.getText().toString());
                editor.putString("id", strToId(button1.getText().toString()));
                editor.apply();

                intent.putExtra("title", button1.getText());
                intent.putExtra("detailTo", "0");
                if (my_program_button_push) {
                    intent.putExtra("brust", my_program_id_1_strength_1); //각 운동의 운동 세기  burst 1 흉부
                    intent.putExtra("abdomen", my_program_id_1_strength_2);  //abdomen 2 복부
                    intent.putExtra("arm", my_program_id_1_strength_3);  //arm 3 팔
                    intent.putExtra("sideflank", my_program_id_1_strength_4);  //sideflank 4 옆구리
                    intent.putExtra("latt", my_program_id_1_strength_5);  //latt 5 어깨
                    intent.putExtra("waist", my_program_id_1_strength_6);  //waist  6  허리
                    intent.putExtra("bein", my_program_id_1_strength_7);   //bein  7  허벅다리
                    intent.putExtra("arsch", my_program_id_1_strength_8);  //arsch   8 둔부
                }
                if (my_program_id_1 != 0) {
                    intent.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    intent.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    intent.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    intent.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    intent.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    intent.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    intent.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    intent.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    intent.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    intent.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    intent.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    intent.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    intent.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    intent.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    intent.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    intent.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    intent.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    intent.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    intent.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    intent.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    intent.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    intent.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    intent.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    intent.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    intent.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    intent.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    intent.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    intent.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    intent.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    intent.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    intent.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    intent.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    intent.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    intent.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    intent.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    intent.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    intent.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    intent.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    intent.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    intent.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    intent.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    intent.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    intent.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    intent.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    intent.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(intent);
                finish();
                break;
            case R.id.btnMenu12:
                findViewById(R.id.subMenu).setVisibility(View.GONE);
                intent = new Intent(MenuActivity.this, DetailActivity.class);
                button1 = findViewById(R.id.btnMenu12);

                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.clear();
                editor.putString("main_title", button1.getText().toString());
                editor.putString("id", strToId(button1.getText().toString()));
                editor.commit();
                intent.putExtra("title", button1.getText());
                intent.putExtra("detailTo", "0");
                if (my_program_button_push) {
                    intent.putExtra("brust", my_program_id_2_strength_1); //각 운동의 운동 세기  burst 1 흉부
                    intent.putExtra("abdomen", my_program_id_2_strength_2);  //abdomen 2 복부
                    intent.putExtra("arm", my_program_id_2_strength_3);  //arm 3 팔
                    intent.putExtra("sideflank", my_program_id_2_strength_4);  //sideflank 4 옆구리
                    intent.putExtra("latt", my_program_id_2_strength_5);  //latt 5 어깨
                    intent.putExtra("waist", my_program_id_2_strength_6);  //waist  6  허리
                    intent.putExtra("bein", my_program_id_2_strength_7);   //bein  7  허벅다리
                    intent.putExtra("arsch", my_program_id_2_strength_8);  //arsch   8 둔부
                }
                if (my_program_id_1 != 0) {
                    intent.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    intent.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    intent.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    intent.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    intent.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    intent.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    intent.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    intent.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    intent.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    intent.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    intent.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    intent.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    intent.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    intent.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    intent.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    intent.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    intent.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    intent.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    intent.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    intent.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    intent.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    intent.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    intent.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    intent.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    intent.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    intent.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    intent.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    intent.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    intent.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    intent.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    intent.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    intent.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    intent.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    intent.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    intent.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    intent.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    intent.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    intent.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    intent.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    intent.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    intent.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    intent.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    intent.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    intent.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    intent.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(intent);
                finish();
                break;
            case R.id.btnMenu13:
                findViewById(R.id.subMenu).setVisibility(View.GONE);
                intent = new Intent(MenuActivity.this, DetailActivity.class);
                button1 = findViewById(R.id.btnMenu13);

                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.clear();
                editor.putString("main_title", button1.getText().toString());
                editor.putString("id", strToId(button1.getText().toString()));
                editor.commit();
                intent.putExtra("title", button1.getText());
                intent.putExtra("detailTo", "0");
                if (my_program_button_push) {
                    intent.putExtra("brust", my_program_id_3_strength_1); //각 운동의 운동 세기  burst 1 흉부
                    intent.putExtra("abdomen", my_program_id_3_strength_2);  //abdomen 2 복부
                    intent.putExtra("arm", my_program_id_3_strength_3);  //arm 3 팔
                    intent.putExtra("sideflank", my_program_id_3_strength_4);  //sideflank 4 옆구리
                    intent.putExtra("latt", my_program_id_3_strength_5);  //latt 5 어깨
                    intent.putExtra("waist", my_program_id_3_strength_6);  //waist  6  허리
                    intent.putExtra("bein", my_program_id_3_strength_7);   //bein  7  허벅다리
                    intent.putExtra("arsch", my_program_id_3_strength_8);  //arsch   8 둔부
                }
                if (my_program_id_1 != 0) {
                    intent.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    intent.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    intent.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    intent.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    intent.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    intent.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    intent.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    intent.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    intent.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    intent.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    intent.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    intent.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    intent.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    intent.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    intent.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    intent.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    intent.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    intent.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    intent.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    intent.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    intent.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    intent.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    intent.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    intent.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    intent.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    intent.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    intent.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    intent.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    intent.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    intent.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    intent.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    intent.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    intent.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    intent.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    intent.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    intent.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    intent.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    intent.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    intent.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    intent.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    intent.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    intent.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    intent.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    intent.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    intent.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(intent);
                finish();
                break;
            case R.id.btnMenu14:
                findViewById(R.id.subMenu).setVisibility(View.GONE);
                intent = new Intent(MenuActivity.this, DetailActivity.class);
                button1 = findViewById(R.id.btnMenu14);

                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.clear();
                editor.putString("main_title", button1.getText().toString());
                editor.putString("id", strToId(button1.getText().toString()));
                editor.commit();
                intent.putExtra("title", button1.getText());
                intent.putExtra("detailTo", "0");
                if (my_program_button_push) {
                    intent.putExtra("brust", my_program_id_4_strength_1); //각 운동의 운동 세기  burst 1 흉부
                    intent.putExtra("abdomen", my_program_id_4_strength_2);  //abdomen 2 복부
                    intent.putExtra("arm", my_program_id_4_strength_3);  //arm 3 팔
                    intent.putExtra("sideflank", my_program_id_4_strength_4);  //sideflank 4 옆구리
                    intent.putExtra("latt", my_program_id_4_strength_5);  //latt 5 어깨
                    intent.putExtra("waist", my_program_id_4_strength_6);  //waist  6  허리
                    intent.putExtra("bein", my_program_id_4_strength_7);   //bein  7  허벅다리
                    intent.putExtra("arsch", my_program_id_4_strength_8);  //arsch   8 둔부
                }
                if (my_program_id_1 != 0) {
                    intent.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    intent.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    intent.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    intent.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    intent.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    intent.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    intent.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    intent.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    intent.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    intent.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    intent.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    intent.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    intent.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    intent.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    intent.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    intent.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    intent.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    intent.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    intent.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    intent.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    intent.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    intent.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    intent.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    intent.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    intent.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    intent.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    intent.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    intent.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    intent.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    intent.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    intent.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    intent.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    intent.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    intent.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    intent.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    intent.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    intent.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    intent.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    intent.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    intent.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    intent.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    intent.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    intent.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    intent.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    intent.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(intent);
                finish();
                break;
            case R.id.btnMenu15:
                findViewById(R.id.subMenu).setVisibility(View.GONE);
                intent = new Intent(MenuActivity.this, DetailActivity.class);
                button1 = findViewById(R.id.btnMenu15);

                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.clear();
                editor.putString("main_title", button1.getText().toString());
                editor.putString("id", strToId(button1.getText().toString()));
                editor.commit();
                intent.putExtra("title", button1.getText());
                intent.putExtra("detailTo", "0");
                if (my_program_button_push) {
                    intent.putExtra("brust", my_program_id_5_strength_1); //각 운동의 운동 세기  burst 1 흉부
                    intent.putExtra("abdomen", my_program_id_5_strength_2);  //abdomen 2 복부
                    intent.putExtra("arm", my_program_id_5_strength_3);  //arm 3 팔
                    intent.putExtra("sideflank", my_program_id_5_strength_4);  //sideflank 4 옆구리
                    intent.putExtra("latt", my_program_id_5_strength_5);  //latt 5 어깨
                    intent.putExtra("waist", my_program_id_5_strength_6);  //waist  6  허리
                    intent.putExtra("bein", my_program_id_5_strength_7);   //bein  7  허벅다리
                    intent.putExtra("arsch", my_program_id_5_strength_8);  //arsch   8 둔부
                }
                if (my_program_id_1 != 0) {
                    intent.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    intent.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    intent.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    intent.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    intent.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    intent.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    intent.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    intent.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    intent.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    intent.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    intent.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    intent.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    intent.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    intent.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    intent.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    intent.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    intent.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    intent.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    intent.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    intent.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    intent.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    intent.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    intent.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    intent.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    intent.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    intent.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    intent.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    intent.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    intent.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    intent.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    intent.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    intent.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    intent.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    intent.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    intent.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    intent.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    intent.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    intent.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    intent.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    intent.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    intent.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    intent.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    intent.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    intent.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    intent.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    intent.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(intent);
                finish();
                break;
        }
    }

    private String idToStr(String str) {
        Resources res = getResources();
        String text1 = res.getString(R.string.program_buldup_menu1);
        String text2 = res.getString(R.string.program_buldup_menu2);
        String text3 = res.getString(R.string.program_buldup_menu3);
        String text4 = res.getString(R.string.program_buldup_menu4);
        String text5 = res.getString(R.string.program_buldup_menu5);

        String text6 = res.getString(R.string.program_shaping_menu1);
        String text7 = res.getString(R.string.program_shaping_menu2);
        String text8 = res.getString(R.string.program_shaping_menu3);
        String text9 = res.getString(R.string.program_shaping_menu4);
        String text10 = res.getString(R.string.program_shaping_menu5);

        String text11 = res.getString(R.string.program_healthcare_menu1);
        String text12 = res.getString(R.string.program_healthcare_menu2);
        String text13 = res.getString(R.string.program_healthcare_menu3);
        String text14 = res.getString(R.string.program_healthcare_menu4);
        String text15 = res.getString(R.string.program_healthcare_menu5);

        String text16 = res.getString(R.string.program_massage_menu1);
        String text17 = res.getString(R.string.program_massage_menu2);
        String text18 = res.getString(R.string.program_massage_menu3);
        String text19 = res.getString(R.string.program_massage_menu4);
        String text20 = res.getString(R.string.program_massage_menu5);

        if (str.equals("01")) {
            return text1;
        }
        else if (str.equals("02")) {
            return text2;
        }
        else if (str.equals("03")) {
            return text3;
        }
        else if (str.equals("04")) {
            return text4;
        }
        else if (str.equals("05")) {
            return text5;
        }
        else if (str.equals("06")) {
            return text6;
        }
        else if (str.equals("07")) {
            return text7;
        }
        else if (str.equals("08")) {
            return text8;
        }
        else if (str.equals("09")) {
            return text9;
        }
        else if (str.equals("10")) {
            return text10;
        }
        else if (str.equals("11")) {
            return text11;
        }
        else if (str.equals("12")) {
            return text12;
        }
        else if (str.equals("13")) {
            return text13;
        }
        else if (str.equals("14")) {
            return text14;
        }
        else if (str.equals("15")) {
            return text15;
        }
        else if (str.equals("16")) {
            return text16;
        }
        else if (str.equals("17")) {
            return text17;
        }
        else if (str.equals("18")) {
            return text18;
        }
        else if (str.equals("19")) {
            return text19;
        }
        else if (str.equals("20")) {
            return text20;
        } else {
            return text1;
        }
    }

    private String strToId(String str) {
        Resources res = getResources();

        String text1 = res.getString(R.string.program_buldup_menu1);
        String text2 = res.getString(R.string.program_buldup_menu2);
        String text3 = res.getString(R.string.program_buldup_menu3);
        String text4 = res.getString(R.string.program_buldup_menu4);
        String text5 = res.getString(R.string.program_buldup_menu5);

        String text6 = res.getString(R.string.program_shaping_menu1);
        String text7 = res.getString(R.string.program_shaping_menu2);
        String text8 = res.getString(R.string.program_shaping_menu3);
        String text9 = res.getString(R.string.program_shaping_menu4);
        String text10 = res.getString(R.string.program_shaping_menu5);

        String text11 = res.getString(R.string.program_healthcare_menu1);
        String text12 = res.getString(R.string.program_healthcare_menu2);
        String text13 = res.getString(R.string.program_healthcare_menu3);
        String text14 = res.getString(R.string.program_healthcare_menu4);
        String text15 = res.getString(R.string.program_healthcare_menu5);

        String text16 = res.getString(R.string.program_massage_menu1);
        String text17 = res.getString(R.string.program_massage_menu2);
        String text18 = res.getString(R.string.program_massage_menu3);
        String text19 = res.getString(R.string.program_massage_menu4);
        String text20 = res.getString(R.string.program_massage_menu5);

        if (str.equals(text1)) {
            return "01";
        }
        else if (str.equals(text2)) {
            return "02";
        }
        else if (str.equals(text3)) {
            return "03";
        }
        else if (str.equals(text4)) {
            return "04";
        }
        else if (str.equals(text5)) {
            return "05";
        }
        else if (str.equals(text6)) {
            return "06";
        }
        else if (str.equals(text7)) {
            return "07";
        }
        else if (str.equals(text8)) {
            return "08";
        }
        else if (str.equals(text9)) {
            return "09";
        }
        else if (str.equals(text10)) {
            return "10";
        }
        else if (str.equals(text11)) {
            return "11";
        }
        else if (str.equals(text12)) {
            return "12";
        }
        else if (str.equals(text13)) {
            return "13";
        }
        else if (str.equals(text14)) {
            return "14";
        }
        else if (str.equals(text15)) {
            return "15";
        }
        else if (str.equals(text16)) {
            return "16";
        }
        else if (str.equals(text17)) {
            return "17";
        }
        else if (str.equals(text18)) {
            return "18";
        }
        else if (str.equals(text19)) {
            return "19";
        }
        else if (str.equals(text20)) {
            return "20";
        } else {
            return "01";
        }
    }

    private String intTostring (int value) {  //int to String 자릿수 2자리로 만드는 함수 예를 들어 int 0 -> String 00  으로
        return String.format(Locale.US, "%02d", value);
    }
}