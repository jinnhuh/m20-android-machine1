package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import app.com.m20.db.DbManagement;
import app.com.m20.R;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;
import app.com.m20.driver.serial.FTDriver;
import io.realm.Realm;

/**
 * Created by kimyongyeon on 2017-11-10.
 */

public class RegActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG_ACTIVITY = "M20_Reg";

    LinearLayout linearLayout;
    EditText et;
    String mInputData = "";
    TextView tv;
    TextView txtResult;

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    private Realm mRealm;

    private TimerTask mTask;
    private Timer mTimer;

    //machine_program_id 저장하는 변수 (my program 운동명 저장)
    private int my_program_id_1 = 0;
    private int my_program_id_2 = 0;
    private int my_program_id_3 = 0;
    private int my_program_id_4 = 0;
    private int my_program_id_5 = 0;

    int booking_total_machine_used_count;  //사용 횟수
    String user_name;  //사용자 이름 name
    String user_age;   //사용자 나이 age
    String user_gender; //사용자 성별
    String user_weight;  //사용자 몸무게
    String user_height;  //사용자 키

    // 2018-05-08, M20 request handling activity_constant into RI00003
    String activity_constant;  //활동상수 : 1(비활동적), 2(약간활동적), 3(적당히 활동적), 4(아주 활동적)

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

    String machine_program_name = null;  //Server로 부터 받는 my program 운동 이름
    //Server로 부터 받는 my program 운동 이름 저장하는 변수
    String machine_program_name_1 = null;
    String machine_program_name_2 = null;
    String machine_program_name_3 = null;
    String machine_program_name_4 = null;
    String machine_program_name_5 = null;
    int wrong_number = 0;  //인증 번호 틀린 횟수

/*
    private TextView setColorInPartitial(String pre_string, String string, String color, TextView textView) {
        SpannableStringBuilder builder = new SpannableStringBuilder(pre_string + string);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor(color)), 0, pre_string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(builder);
        return textView;
    }

    public void onSetText(String buf) {
        String temp = buf + "\n";
        tv.setText(temp);
    }
*/

    DbManagement dbManagement;

/*
    private void usbInit() {
        mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));
        // listen for new devices
        mUsbReceiver = new UsbReceiver(this, mSerial);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        // load default baud rate
        mBaudrate = mUsbReceiver.loadDefaultBaudrate();
        // for requesting permission
        // setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);
        if (mSerial.begin(mBaudrate)) {
            mUsbReceiver.loadDefaultSettingValues();
            mUsbReceiver.mainloop(mRealm);
            Toast.makeText(this, "connectionReg", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no connectionReg", Toast.LENGTH_SHORT).show();
            if (et.getText().toString().equals("000000")) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
*/

    private void transAnimation(boolean bool, int code){
//        Log.i(TAG_ACTIVITY, "transAnimation().");

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
            Log.e(TAG_ACTIVITY, String.format(Locale.US, "Error code = %d.", code));

            String strErr = getErrorMsg(code);
            Resources resources = getResources();
            txtResult.setText(String.format(resources.getString(R.string.regnumberwrong), strErr));

            linearLayout.setAnimation(aniInSet);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setAnimation(aniOutSet);
            linearLayout.setVisibility(View.GONE);
        }
    }

    private String getErrorMsg(int code){
        /*
        -101 : [DB 오류] 예약 인증번호와 일치하는 예약 정보가 없음  예약 번호를 확인하세요 (Error code : 101)
        -102 : [DB 오류] 예약 인증번호는 일치하나 API 파라미터 기계번호(machine_unit_id)와 DB 예약 기계번호(machine_unit_id)가 일치하지 않음  장비 번호를 확인하세요 (Error code : 102)
        -103 : [DB 오류] 예약 상태가 '예약 완료'가 아님 (예약 상태가 '예약 완료'일 경우만 기계를 사용할 수 있음)  관리자에게 문의하세요. (Error code : 103)
        -104 : [DB 오류] 기계UNIT 상태가 '활성화'가 아님 관리자에게 문의하세요. (Error code :104)
        -105 : [DB 오류] 기계 상태가 '활성화'가 아님   관리자에게 문의하세요. (Error code :105)
        -106 : [DB 오류] 시설 상태가 '활성화'가 아님  관리자에게 문의하세요. (Error code :106)
        -107 : [DB 오류] 예약 사용자 상태가 '활동'이 아님   예약 번호를 확인하세요. (Error code :107)
        -108 : [DB 오류] 현재 시간이 예약한 운동 시간 초과   예약시간을 확인하세요. (Error code :108)
        -109 : [DB 오류] 예약 수정 실패(예약 상태를 '기계 사용 중'으로 수정)  관리자에게 문의하세요. (Error code :109)
         */
        Resources resources = getResources();
        String errMsg = null;
        switch(code){
            case -101:
            case -107:
                errMsg = resources.getString(R.string.regnumberwrong_101);
                break;
            case -102:
                errMsg = resources.getString(R.string.regnumberwrong_102);
                break;
            case -103:
            case -104:
            case -105:
            case -106:
            case -109:
                errMsg = resources.getString(R.string.regnumberwrong_103);
                break;
            case -108:
                errMsg = resources.getString(R.string.regnumberwrong_108);
                break;
            default :
                errMsg = "Unknown";
                break;
        }
        return String.format("%d: %s", code, errMsg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reg);
        Utils.fullScreen(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        Log.i(TAG_ACTIVITY, "onCreate().");

        et = findViewById(R.id.etReg);
        tv = findViewById(R.id.txtComment);
        txtResult = findViewById(R.id.txtResult);

        findViewById(R.id.btnOk).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btnYES).setOnClickListener(this);
        linearLayout = findViewById(R.id.subMenu);

        tv.setText("");
        Resources resources = getResources();
        String str = resources.getString(R.string.reg_comment);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#fff200")), 7, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.append(builder);

//         mediaPlayer = new MediaPlayer();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.reg);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        wrong_number = 0;

//        setIDPW("11", ")8]25[41[(_30.!277a23a705e9addeefa14d475eb8e36c066");        // SCHYUN.

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
            mUsbReceiver.mainloop(mRealm);

            SharedPreferences prefs =getSharedPreferences("IDPW", MODE_PRIVATE);
            String IDresult = prefs.getString("ID", "0");
            String PWresult = prefs.getString("PW", "0");
            Log.i(TAG_ACTIVITY, String.format(Locale.US, "ID = %s, PWD = %s.", IDresult, PWresult));
            if (IDresult.equals("0") || PWresult.equals("0") ) { //ID & PW 가 없으면 Linux에게 요청 한다
                Log.i(TAG_ACTIVITY, "Request ID and Password.");
                mUsbReceiver.writeDataToSerial("S11;N"); // ID & Password 요청
            }
        }

        mTask = new TimerTask() {
            @Override
            public void run() {    //아무 입력 없이 5분 지나면 Logo화면으로 이동한다
                Log.i(TAG_ACTIVITY, "Start IntroActivity.");

                Intent intent = new Intent(RegActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, 300000);

        init();

//        usbInit();
//        mRealm.beginTransaction();
//        RealmResults<User> duser = dbManagement.dbNoFilterQuery();
//        duser.deleteAllFromRealm();
//        mRealm.commitTransaction();

//        mRealm.beginTransaction();
//        User user = mRealm.createObject(User.class);
//        user.setName("1111");
//        mRealm.commitTransaction();

//        RealmResults<User> user2 = dbManagement.dbNoFilterQuery();
//        System.out.println(user2.size());

//        try {
//            activityMove("kkk");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void init() {
        //Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration
//                .Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        dbManagement = new DbManagement(mRealm);
    }

/*
    public void activityMove(String name) throws InterruptedException {
        // user 정보가 있을때, 화면으로 이동한다.
//        RealmResults<User> user = dbManagement.dbNoFilterQuery();
//        tv = findViewById(R.id.txtComment);
//        tv.setText(user.get(0).getName());

        if (name != null) {
            Intent intent = new Intent(RegActivity.this, WelcomeActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
    }
*/

    public void setText(String str) {
        Resources res = getResources();
//        String text = String.format(res.getString(R.string.hello_small_size), str);
//        TextView tv = findViewById(R.id.txtComment);
        tv.setText(String.format(res.getString(R.string.hello_small_size), str));
    }

/*
    public void regCheck(String str) throws InterruptedException {
        // 해당 화면 가서 다시 요청해서 유저 정보를 가져 온다.
//        User user = new User();
//        user.setrNumber(Integer.parseInt(et.getText().toString()) + 820410);
//        dbManagement.dbUserInfoSave(user);

        // 시연용
        if (str.equals("1")) {
            mUsbReceiver.writeDataToSerial("A31;01;N");
//            Intent intent = new Intent(RegActivity.this, DetailActivity.class);
//            intent.putExtra("detailTo", "0");
//            startActivity(intent);
//            finish();
        }

        // 실사용
//        if (str.equals("0")) {
//            Toast.makeText(this, "예약번호가 올바르지 않습니다." ,Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(RegActivity.this, WelcomeActivity.class);
//            startActivity(intent);
//            finish();
//         } else {
//            Intent intent = new Intent(RegActivity.this, DetailActivity.class);
//            intent.putExtra("detailTo", "0");
//            startActivity(intent);
//            finish();
//        }
    }
*/

    private void timerrestart() {  //아무 입력 없이 5분 지나면 Logo화면으로 이동한다
        mTimer.cancel();

        mTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG_ACTIVITY, "Start IntroActivity.");

                Intent intent = new Intent(RegActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        };

        mTimer = new Timer();

        mTimer.schedule(mTask, 300000);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYES:
//                Log.i(TAG_ACTIVITY, "J.Y.T RegActivity btnYES 누름 ");
                transAnimation(false, 0);
                break;

            case R.id.btnOk:
                timerrestart();
                if (et.getText().toString().equals("")) {
                    Toast.makeText(this, "예약번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (et.getText().toString().equals("123456")) {
                    //이곳에 버튼 클릭시 일어날 일을 적습니다.
                    Intent intent = new Intent(RegActivity.this, SettingActivity.class);
                    startActivity(intent);
                    finish();

                }
//                else if (et.getText().toString().equals("000000")) {
////                    bookingendTimeSaved("1000000");     // SCHYUN, Add for just TEST.
//
//                    //Intent i = new Intent(RegActivity.this, DetailActivity.class);  //테스트
//                    Intent i = new Intent(RegActivity.this, MenuActivity.class); //정상
//                    //테스트
////                    user_name = "전용태";
////                    user_age = "45";
////                    user_gender = "1";
////                    user_weight = "76.9";
////                    user_height = "176.5";
////                    //Intent i = new Intent(this, WelcomeActivity.class);
////                    i.putExtra("name",user_name);
////                    i.putExtra("age", user_age);
////                    i.putExtra("gender", user_gender);
////                    i.putExtra("weight", user_weight);
////                    i.putExtra("height", user_height);
////                    i.putExtra("detailTo", "1");
//                    startActivity(i);
//                    finish();
//                }
                else {
/*
                    if (mSerial.begin(mBaudrate)) {
                        mUsbReceiver.loadDefaultSettingValues();
                        mUsbReceiver.mainloop(mRealm);
                        //mUsbReceiver.writeDataToSerial("A02;" + et.getText().toString() + ";N");
                        //비밀번호 입력 후 OK 누르면 지운다..틀렸을 경우를 대비하여
                        //mInputData = mInputData.substring(mInputData.length());
                        //et.setText("");
                        //비밀번호 입력 후 OK 누르면 지운다..틀렸을 경우를 대비하여
                    } else {
                        Toast.makeText(this, "no connectionReg", Toast.LENGTH_SHORT).show();
                        if (et.getText().toString().equals("000000")) {
                            Intent intent = new Intent(this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
*/
                    getReservationInform(et.getText().toString());  //위의 if에 들어가야 하나 일단 개발 편의상 여기에 넣어둠
                }
                break;
            case R.id.btn_0:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "0";
                et.setText(mInputData);
                break;
            case R.id.btn_1:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "1";
                et.setText(mInputData);
                break;
            case R.id.btn_2:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "2";
                et.setText(mInputData);
                break;
            case R.id.btn_3:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "3";
                et.setText(mInputData);
                break;
            case R.id.btn_4:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "4";
                et.setText(mInputData);
                break;
            case R.id.btn_5:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "5";
                et.setText(mInputData);
                break;
            case R.id.btn_6:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "6";
                et.setText(mInputData);
                break;
            case R.id.btn_7:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "7";
                et.setText(mInputData);
                break;
            case R.id.btn_8:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "8";
                et.setText(mInputData);
                break;
            case R.id.btn_9:
                timerrestart();
                if (mInputData.length() == 6)
                    break;
                mInputData += "9";
                et.setText(mInputData);
                break;
            case R.id.btn_del:
                timerrestart();
                if (mInputData.length() == 0) {
                    et.setText("");
                    break;
                }
                mInputData = mInputData.substring(0, mInputData.length() - 1);
                et.setText(mInputData);
                break;
        }
    }

    public void setIDPW(String ID, String PW) {  //ID & PW Saved from Linux
        Log.i(TAG_ACTIVITY, String.format(Locale.US, "ID = %s, PW = %s.", ID, PW));

        SharedPreferences pref =getSharedPreferences("IDPW", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ID",ID );
        editor.putString("PW",PW );
        editor.apply();
    }

    public String getIDSaved() {
        SharedPreferences prefs =getSharedPreferences("IDPW", MODE_PRIVATE);
        String result = prefs.getString("ID", "0");
        Log.i(TAG_ACTIVITY, String.format(Locale.US, "ID = %s.", result));
        return result;
    }

    public String getPWSaved() {
        SharedPreferences prefs =getSharedPreferences("IDPW", MODE_PRIVATE);
        String result = prefs.getString("PW", "0");
        Log.i(TAG_ACTIVITY, String.format(Locale.US, "PW = %s.", result));
        return result;
    }

    private void usedCount(int used_count, String name, String age, String gender, String weight, String height) {  //total_machine_used_count 의 횟수에 따라 MenuActivity로 갈지 아님 WelcomeActivity로 갈지 분기
        //if (used_count > 0) {
        if ((used_count == 1) || ((used_count % 5) == 0)) {
            Intent intent = new Intent(this, WelcomeActivity.class);  //횟수가 1 이거나 5의 배수 이면 체중 측정 (WelcomeActivity)
            intent.putExtra("name",name);
            intent.putExtra("age", age);
            intent.putExtra("gender", gender);
            intent.putExtra("weight", weight);
            intent.putExtra("height", height);
            startActivity(intent);
            finish();
        }
        else {
            //menuData ();  // 그 외에는 운동 선택 (MenuActivity)
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void agedataSaved() {  //나이 저장
        SharedPreferences age =getSharedPreferences("age_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = age.edit();
        editor.putString("Data_age",user_age );
        editor.apply();
    }

    private void genderdataSaved() {  //성별 저장
        SharedPreferences gender =getSharedPreferences("gender_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = gender.edit();
        editor.putString("Data_gender",user_gender );
        editor.apply();
    }

    private void bookingIDdataSaved(int booking_id) {  //예약 번호 저장
        SharedPreferences bookingID =getSharedPreferences("bookingID_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = bookingID.edit();
        editor.putInt("Data_bookingID",booking_id);
        editor.apply();
    }

    private void weightdataSaved() {  //키 저장
        SharedPreferences height =getSharedPreferences("end_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = height.edit();
        editor.putString("Data_weight",user_weight);
        editor.apply();
    }

    private void heightdataSaved() {  //키 저장
        SharedPreferences height =getSharedPreferences("height_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = height.edit();
        editor.putString("Data_height",user_height);
        editor.apply();
    }

    private void countdataSaved() {  //횟수 저장
        SharedPreferences count =getSharedPreferences("count_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = count.edit();
        editor.putInt("Data_count",booking_total_machine_used_count);
        editor.apply();
    }

    // 2018-05-08, M20 request handling activity_constant into RI00003
    private void activitydataSaved() {  //activity_constant
        SharedPreferences count =getSharedPreferences("activity_constant", MODE_PRIVATE);
        SharedPreferences.Editor editor = count.edit();
        editor.putString("Data_activity_constant",activity_constant);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG_ACTIVITY, "onDestroy()");
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        mTimer.cancel();
        super.onDestroy();
    }

    private void getReservationInform(String regnumber)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                //HttpClient HttpClient = new DefaultHttpClient();
//                        HttpClient httpClient = getHttpClient();

                //String urlString = "https://devmmapi.m20.co.kr/login/process";
                //String m_id = "00000000-4dbb-f67f-0033-c5870033c587";   //Application.get_android_id();

                String urlString = "https://devmmapi.m20.co.kr/booking/certification";      // 요구사항ID : RI00003

                try {
                    URL url = new URL(urlString);

//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    trustAllHosts();

                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });

                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);

                    //Gson gson = new Gson();
                    //JsonParser parser = new JsonParser();
                    //JsonObject object = new JsonObject();

                    //object.addProperty("access_key", "3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!");
                    //object.addProperty("secret_access_key", "()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s");
                    //object.addProperty("unique_identifer", "B8:27:EB:50:E6:1E");
                    //String json = gson.toJson(object);

                    //String json1 = "{\"access_key\":\"3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!\",\"secret_access_key\":()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s,\"unique_identifer\":\"00000000-4dbb-f67f-0033-c5870033c587\"}";
                    //String json2 = "{\"access_key\":\"3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!\",\"secret_access_key\":()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s,\"unique_identifer\":\"00000000-4dbb-f67f-0033-c5870033c587\"}";


                    StringBuilder stringBuilder = new StringBuilder();  //보내는 부분
                    stringBuilder.append("access_key=3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!");
                    stringBuilder.append("&secret_access_key=()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s");
                    //리눅스와 연결 안하고 테스트 할때는 풀어 주자  연결 되면 막는다
                    //stringBuilder.append("&machine_unit_id=" + "11");
                    //stringBuilder.append("&password=" + ")8]25[41[(_30.!277a23a705e9addeefa14d475eb8e36c066");
                    //리눅스와 연결 안하고 테스트 할때는 풀어 주자 연결 되면 막는다
                    //리눅스와 연결 하면 풀어 주자 연결 안하면 막는다
                    stringBuilder.append("&machine_unit_id=");   //ID값
                    stringBuilder.append(getIDSaved());   //ID값
                    stringBuilder.append("&password=");
                    stringBuilder.append(getPWSaved());
                    //리눅스와 연결 하면 풀어 주자 연결 안하면 막는다
                    //stringBuilder.append("&certification_key=" + "100007");

                    Log.i(TAG_ACTIVITY, "regnumber: "+regnumber);
                    stringBuilder.append("&certification_key=");
                    stringBuilder.append(regnumber);
                    //stringBuilder.append("&unique_identifier=" + "B8:27:EB:50:E6:1E");
                    String sendData = stringBuilder.toString();
                    Log.i(TAG_ACTIVITY, String.format(Locale.US, "sendData: %s.", sendData));

                    OutputStream outputStream = httpsURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(sendData);
                    //bufferedWriter.write(getURLQuery(nameValuePairs));
                    //bufferedWriter.write(json1);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    httpsURLConnection.connect();

                    //받는 부분
                    StringBuilder responseStringBuilder = new StringBuilder();
                    if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        for (;;){
                            String stringLine = bufferedReader.readLine();
                            if (stringLine == null ) break;
                            responseStringBuilder.append(stringLine);
                            responseStringBuilder.append('\n');
                        }
                        bufferedReader.close();
                    }

                    httpsURLConnection.disconnect();

                    String res_json = responseStringBuilder.toString();
                    Log.i(TAG_ACTIVITY, String.format(Locale.US, "res_json: %s.", res_json));

                    //String res_json2 = "{\"response_status_code\":0,\"booking_data\":{\"booking_id\":33,\"total_machine_used_count\":0,\"start_time\":\"20:00:00\",\"end_time\":\"20:30:00\"},\"user_data\":{\"name\":\"\\ud64d\\uae38\\ub3d9\",\"gender_code\":\"M\",\"age\":28,\"weight\":70,\"height\":176},\"machine_program_list\":[{\"machine_program_id\":1,\"machine_program_name\":\"\\uadfc\\uc721 \\uac15\\ud654\",\"created_at\":1520398847,\"created_at_text\":\"2018-03-07 14:00:47\",\"machine_strength_list\":[{\"body_part_id\":1,\"strength\":21},{\"body_part_id\":2,\"strength\":22},{\"body_part_id\":3,\"strength\":23},{\"body_part_id\":4,\"strength\":24},{\"body_part_id\":5,\"strength\":25},{\"body_part_id\":6,\"strength\":26},{\"body_part_id\":7,\"strength\":27},{\"body_part_id\":8,\"strength\":28}]}]}\n";

                    //String res_json1 = "{\"response_status_code\":0,\"booking_data\":{\"booking_id\":29,\"total_machine_used_count\":0,\"start_time\":\"14:00:00\",\"end_time\":\"14:30:00\"},\"user_data\":{\"name\":\"\\ud64d\\uae38\\ub3d9\",\"gender_code\":\"M\",\"age\":28,\"weight\":70,\"height\":176},\"machine_program_group1_list\":[{\"machine_program_group1_id\":1,\"machine_program_group1_name\":\"EMA PROGRAM\"},{\"machine_program_group1_id\":2,\"machine_program_group1_name\":\"SHAPING PROGRAM\"},{\"machine_program_group1_id\":3,\"machine_program_group1_name\":\"HEALTHCARE PROGRAM\"},{\"machine_program_group1_id\":4,\"machine_program_group1_name\":\"MASSAGE PROGRAM\"}],\"machine_program_list\":null}\n";

                    JsonParser parser = new JsonParser();
                    //JsonElement element = parser.parse(res_json2);
                    JsonElement element = parser.parse(res_json);

                    //String name = element.getAsJsonObject().get("booking_data").getAsString();

                    int res_code = element.getAsJsonObject().get("response_status_code").getAsInt();
                    if (res_code == 0)  // success and parsing
                    {
                        // booking_data
                        JsonObject booking_Datajobject = element.getAsJsonObject();
                        booking_Datajobject = booking_Datajobject.getAsJsonObject("booking_data");
                        if (booking_Datajobject != null) {
                            int booking_id = booking_Datajobject.getAsJsonObject().get("booking_id").getAsInt();
                            bookingIDdataSaved(booking_id);
                            booking_total_machine_used_count = booking_Datajobject.getAsJsonObject().get("total_machine_used_count").getAsInt();
                            countdataSaved();  //회수 저장
//                            String booking_start_time = booking_Datajobject.getAsJsonObject().get("start_time").getAsString();
                            String booking_end_time = booking_Datajobject.getAsJsonObject().get("end_time").getAsString();
                            //end_time 저장
                            bookingendTimeSaved(booking_end_time);
                        }

                        // user_data
                        JsonObject user_data_Datajobject = element.getAsJsonObject();
                        user_data_Datajobject = user_data_Datajobject.getAsJsonObject("user_data");
                        if (user_data_Datajobject != null) {
                            user_name = user_data_Datajobject.getAsJsonObject().get("name").getAsString();
                            //사용자 이름 저장 LateEndActivity 에서 사용
                            SharedPreferences pref =getSharedPreferences("user_name", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("user_name", user_name );
                            editor.apply();
                            //사용자 이름 저장 LateEndActivity 에서 사용
                            Log.i(TAG_ACTIVITY, "user_name: "+user_name);
                            user_age = user_data_Datajobject.getAsJsonObject().get("age").getAsString();
                            agedataSaved();
                            user_gender = user_data_Datajobject.getAsJsonObject().get("gender_code").getAsString();
                            if (user_gender.equals("M"))
                                user_gender = "1";
                            else
                                user_gender = "2";
                            genderdataSaved();
                            user_weight = user_data_Datajobject.getAsJsonObject().get("weight").getAsString();
                            weightdataSaved();
                            user_height = user_data_Datajobject.getAsJsonObject().get("height").getAsString();
                            heightdataSaved();

                            // 2018-05-08, M20 request handling activity_constant into RI00003
                            activity_constant = user_data_Datajobject.getAsJsonObject().get("activity_constant").getAsString();
                            activitydataSaved();
                            Log.i(TAG_ACTIVITY, "activity_constant :" + activity_constant);
                        }

                        // machine_program_list
                        JsonObject machine_program_list_data_Datajobject = element.getAsJsonObject();
                        if (machine_program_list_data_Datajobject != null) {
                            JsonElement machine_program_list_null_check = machine_program_list_data_Datajobject.getAsJsonObject().get("machine_program_list");
                            if (machine_program_list_null_check.isJsonNull()) {
                                Log.i(TAG_ACTIVITY, "No exercise information!!!");
                            }
                            else {
                                JsonArray machine_program_list_data_Datajobject_arr = machine_program_list_data_Datajobject.getAsJsonArray("machine_program_list");
                                if (machine_program_list_data_Datajobject_arr != null) {
                                    for (int j = 0; j < machine_program_list_data_Datajobject_arr.size(); j++) {
                                        machine_program_list_data_Datajobject = machine_program_list_data_Datajobject_arr.get(j).getAsJsonObject();

                                        int machine_program_id = machine_program_list_data_Datajobject.getAsJsonObject().get("machine_program_id").getAsInt();
                                        machine_program_name = machine_program_list_data_Datajobject.getAsJsonObject().get("machine_program_name").getAsString();
                                        Log.i(TAG_ACTIVITY, String.format(Locale.US, "machine_program_name = %s.", machine_program_name));
                                        //for 문을 돌면서 j값에 따라 각 machine_program_id 값을 my_program_id_1~4 에 대입한다
                                        if (j == 0) {
                                            my_program_id_1 = machine_program_id;
                                            machine_program_name_1 = machine_program_name;
//                                        Log.i(TAG_ACTIVITY, "J.Y.T RegActivity  my_program_id_1: " + my_program_id_1);
//                                        Log.i(TAG_ACTIVITY, "J.Y.T RegActivity  machine_program_name_1: " + machine_program_name_1);
                                        }
                                        if (j == 1) {
                                            my_program_id_2 = machine_program_id;
                                            machine_program_name_2 = machine_program_name;
                                        }
                                        if (j == 2) {
                                            my_program_id_3 = machine_program_id;
                                            machine_program_name_3 = machine_program_name;
                                        }
                                        if (j == 3) {
                                            my_program_id_4 = machine_program_id;
                                            machine_program_name_4 = machine_program_name;
                                        }
                                        if (j == 4) {
                                            my_program_id_5 = machine_program_id;
                                            machine_program_name_5 = machine_program_name;
                                        }

                                        JsonObject machine_strength_list = machine_program_list_data_Datajobject.getAsJsonObject();
                                        JsonArray machine_strength_list_arr = machine_strength_list.getAsJsonArray("machine_strength_list");

                                        for (int k = 0; k < machine_strength_list_arr.size(); k++) {
                                            machine_strength_list = machine_strength_list_arr.get(k).getAsJsonObject();

//                                int body_part_id = machine_strength_list.getAsJsonObject().get("body_part_id").getAsInt();
                                            int strength = machine_strength_list.getAsJsonObject().get("strength").getAsInt();
                                            bodyidstrengthSaved(j, k, strength);  //각 강도 저장
                                        }
                                    }
                                }
                            }
                        }
                        programNameSaved();
                        usedCount(booking_total_machine_used_count,user_name, user_age, user_gender, user_weight, user_height);
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                            // error 처리
                                wrongNumberTen(res_code);
                            }
                        });
                    }
                    //} catch (MalformedURLException e) {
                    //   e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void wrongNumberTen(int code) {
        wrong_number++;
        if (wrong_number >= 10) {
            Intent intent = new Intent(RegActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            mInputData = mInputData.substring(mInputData.length());
            et.setText("");

            transAnimation(true, code);
        }
    }

    private void programNameSaved() {
        //프로그램 이름 저장한다 My program 에서 사용할거
        SharedPreferences pref =getSharedPreferences("machine_program_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("machine_program_name_1", machine_program_name_1 );
        editor.putString("machine_program_name_2", machine_program_name_2 );
        editor.putString("machine_program_name_3", machine_program_name_3 );
        editor.putString("machine_program_name_4", machine_program_name_4 );
        editor.putString("machine_program_name_5", machine_program_name_5 );
        editor.putInt("my_program_id_1",my_program_id_1 );
        editor.putInt("my_program_id_2",my_program_id_2 );
        editor.putInt("my_program_id_3",my_program_id_3 );
        editor.putInt("my_program_id_4",my_program_id_4 );
        editor.putInt("my_program_id_5",my_program_id_5 );
        editor.apply();
    }

    private void bookingendTimeSaved(String booking_end_time) {  //end time 저장하는 함수
        Log.i(TAG_ACTIVITY, "booking_end_time: "+booking_end_time);
        SharedPreferences pref =getSharedPreferences("booking_end_time", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("end_time", booking_end_time );
        editor.apply();
    }

    private void bodyidstrengthSaved(int j, int k, int strength) {  //강도 저장
        SharedPreferences pref =getSharedPreferences("my_program_strength", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (my_program_id_1 != 0 && j == 0) {
            if ( k == 0) {
                my_program_id_1_strength_1 = strength;
                editor.putInt("my_program_id_1_strength_1",my_program_id_1_strength_1 );
            }
            if ( k == 1) {
                my_program_id_1_strength_2 = strength;
                editor.putInt("my_program_id_1_strength_2",my_program_id_1_strength_2 );
            }
            if ( k == 2) {
                my_program_id_1_strength_3 = strength;
                editor.putInt("my_program_id_1_strength_3",my_program_id_1_strength_3 );
            }
            if ( k == 3) {
                my_program_id_1_strength_4 = strength;
                editor.putInt("my_program_id_1_strength_4",my_program_id_1_strength_4 );
            }
            if ( k == 4) {
                my_program_id_1_strength_5 = strength;
                editor.putInt("my_program_id_1_strength_5",my_program_id_1_strength_5 );
            }
            if ( k == 5) {
                my_program_id_1_strength_6 = strength;
                editor.putInt("my_program_id_1_strength_6",my_program_id_1_strength_6 );
            }
            if ( k == 6) {
                my_program_id_1_strength_7 = strength;
                editor.putInt("my_program_id_1_strength_7",my_program_id_1_strength_7 );
            }
            if ( k == 7) {
                my_program_id_1_strength_8 = strength;
                editor.putInt("my_program_id_1_strength_8",my_program_id_1_strength_8 );
            }
        }
        if (my_program_id_2 != 0 && j == 1) {
            if ( k == 0) {
                my_program_id_2_strength_1 = strength;
                editor.putInt("my_program_id_2_strength_1",my_program_id_2_strength_1 );
            }
            if ( k == 1) {
                my_program_id_2_strength_2 = strength;
                editor.putInt("my_program_id_2_strength_2",my_program_id_2_strength_2 );
            }
            if ( k == 2) {
                my_program_id_2_strength_3 = strength;
                editor.putInt("my_program_id_2_strength_3",my_program_id_2_strength_3 );
            }
            if ( k == 3) {
                my_program_id_2_strength_4 = strength;
                editor.putInt("my_program_id_2_strength_4",my_program_id_2_strength_4 );
            }
            if ( k == 4) {
                my_program_id_2_strength_5 = strength;
                editor.putInt("my_program_id_2_strength_5",my_program_id_2_strength_5 );
            }
            if ( k == 5) {
                my_program_id_2_strength_6 = strength;
                editor.putInt("my_program_id_2_strength_6",my_program_id_2_strength_6 );
            }
            if ( k == 6) {
                my_program_id_2_strength_7 = strength;
                editor.putInt("my_program_id_2_strength_7",my_program_id_2_strength_7 );
            }
            if ( k == 7) {
                my_program_id_2_strength_8 = strength;
                editor.putInt("my_program_id_2_strength_8",my_program_id_2_strength_8 );
            }
        }
        if (my_program_id_3 != 0 && j == 2) {
            if ( k == 0) {
                my_program_id_3_strength_1 = strength;
                editor.putInt("my_program_id_3_strength_1",my_program_id_3_strength_1 );
            }
            if ( k == 1) {
                my_program_id_3_strength_2 = strength;
                editor.putInt("my_program_id_3_strength_2",my_program_id_3_strength_2 );
            }
            if ( k == 2) {
                my_program_id_3_strength_3 = strength;
                editor.putInt("my_program_id_3_strength_3",my_program_id_3_strength_3 );
            }
            if ( k == 3) {
                my_program_id_3_strength_4 = strength;
                editor.putInt("my_program_id_3_strength_4",my_program_id_3_strength_4 );
            }
            if ( k == 4) {
                my_program_id_3_strength_5 = strength;
                editor.putInt("my_program_id_3_strength_5",my_program_id_3_strength_5 );
            }
            if ( k == 5) {
                my_program_id_3_strength_6 = strength;
                editor.putInt("my_program_id_3_strength_6",my_program_id_3_strength_6 );
            }
            if ( k == 6) {
                my_program_id_3_strength_7 = strength;
                editor.putInt("my_program_id_3_strength_7",my_program_id_3_strength_7 );
            }
            if ( k == 7) {
                my_program_id_3_strength_8 = strength;
                editor.putInt("my_program_id_3_strength_8",my_program_id_3_strength_8 );
            }
        }
        if (my_program_id_4 != 0 && j == 3) {
            if ( k == 0) {
                my_program_id_4_strength_1 = strength;
                editor.putInt("my_program_id_4_strength_1",my_program_id_4_strength_1 );
            }
            if ( k == 1) {
                my_program_id_4_strength_2 = strength;
                editor.putInt("my_program_id_4_strength_2",my_program_id_4_strength_2 );
            }
            if ( k == 2) {
                my_program_id_4_strength_3 = strength;
                editor.putInt("my_program_id_4_strength_3",my_program_id_4_strength_3 );
            }
            if ( k == 3) {
                my_program_id_4_strength_4 = strength;
                editor.putInt("my_program_id_4_strength_4",my_program_id_4_strength_4 );
            }
            if ( k == 4) {
                my_program_id_4_strength_5 = strength;
                editor.putInt("my_program_id_4_strength_5",my_program_id_4_strength_5 );
            }
            if ( k == 5) {
                my_program_id_4_strength_6 = strength;
                editor.putInt("my_program_id_4_strength_6",my_program_id_4_strength_6 );
            }
            if ( k == 6) {
                my_program_id_4_strength_7 = strength;
                editor.putInt("my_program_id_4_strength_7",my_program_id_4_strength_7 );
            }
            if ( k == 7) {
                my_program_id_4_strength_8 = strength;
                editor.putInt("my_program_id_4_strength_8",my_program_id_4_strength_8 );
            }
        }
        if (my_program_id_5 != 0 && j == 4) {
            if ( k == 0) {
                my_program_id_5_strength_1 = strength;
                editor.putInt("my_program_id_5_strength_1",my_program_id_5_strength_1 );
            }
            if ( k == 1) {
                my_program_id_5_strength_2 = strength;
                editor.putInt("my_program_id_5_strength_2",my_program_id_5_strength_2 );
            }
            if ( k == 2) {
                my_program_id_5_strength_3 = strength;
                editor.putInt("my_program_id_5_strength_3",my_program_id_5_strength_3 );
            }
            if ( k == 3) {
                my_program_id_5_strength_4 = strength;
                editor.putInt("my_program_id_5_strength_4",my_program_id_5_strength_4 );
            }
            if ( k == 4) {
                my_program_id_5_strength_5 = strength;
                editor.putInt("my_program_id_5_strength_5",my_program_id_5_strength_5 );
            }
            if ( k == 5) {
                my_program_id_5_strength_6 = strength;
                editor.putInt("my_program_id_5_strength_6",my_program_id_5_strength_6 );
            }
            if ( k == 6) {
                my_program_id_5_strength_7 = strength;
                editor.putInt("my_program_id_5_strength_7",my_program_id_5_strength_7 );
            }
            if ( k == 7) {
                my_program_id_5_strength_8 = strength;
                editor.putInt("my_program_id_5_strength_8",my_program_id_5_strength_8 );
            }
        }
        editor.apply();
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
