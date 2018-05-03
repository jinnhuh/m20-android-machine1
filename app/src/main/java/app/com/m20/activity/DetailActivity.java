package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Locale;
import app.com.m20.customview.HoloCircularProgressBar;
import app.com.m20.R;
import app.com.m20.db.DbManagement;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.model.Body;
import app.com.m20.utils.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by kimyongyeon on 2017-11-20.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {
    String TAG_ACTIVITY = "M20_Detail";

    SeekBar mSeekBar;
    private HoloCircularProgressBar mHoloCircularProgressBar;

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
    //
    DbManagement dbManagement;
    private Realm mRealm;
    private void init() {
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        dbManagement = new DbManagement(mRealm);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG_ACTIVITY, "onDestroy().");

        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
    }

    public void setText(){  //이거 뭐하는 함수인가?
        RealmResults<Body> bodies = mRealm.where(Body.class).findAll();

//        bodies.get(0).getTotalBodySet(); // 전체값
//        bodies.get(0).getThorax(); // 흉부
//        bodies.get(0).getStomach(); // 복부
//        bodies.get(0).getArm(); // 상완
//        bodies.get(0).getThigh(); // 허벅다리
//        bodies.get(0).getShoulder(); // 어깨
//        bodies.get(0).getWaist(); // 허리
//        bodies.get(0).getSide(); // 옆구리
//        bodies.get(0).getPosterior();// 둔부

        // 초기 받은값 설정.
        TextView btn_latt_1 = findViewById(R.id.btn_latt_1); // 어깨1
        TextView btn_latt_2 = findViewById(R.id.btn_latt_2); // 어깨2
        btn_latt_1.setText(bodies.get(0).getShoulder());
        btn_latt_2.setText(bodies.get(0).getShoulder());

        TextView btn_waist_1 = findViewById(R.id.btn_waist_1); // 허리1
        TextView btn_waist_2 = findViewById(R.id.btn_waist_2); // 허리2
        btn_waist_1.setText(bodies.get(0).getWaist());
        btn_waist_2.setText(bodies.get(0).getWaist());

        TextView btn_sideflank_1 = findViewById(R.id.btn_sideflank_1); // 옆구리1
        TextView btn_sideflank_2 = findViewById(R.id.btn_sideflank_2); // 옆구리2
        btn_sideflank_1.setText(bodies.get(0).getSide());
        btn_sideflank_2.setText(bodies.get(0).getSide());

        TextView btn_arsch_1 = findViewById(R.id.btn_arsch_1); // 둔부1
        TextView btn_arsch_2 = findViewById(R.id.btn_arsch_2); // 둔부2
        btn_arsch_1.setText(bodies.get(0).getPosterior());
        btn_arsch_2.setText(bodies.get(0).getPosterior());

        TextView btn_brust_1 = findViewById(R.id.btn_brust_1); // 가슴1
        TextView btn_brust_2 = findViewById(R.id.btn_brust_2); // 가슴2
        btn_brust_1.setText(bodies.get(0).getThorax());
        btn_brust_2.setText(bodies.get(0).getThorax());

        TextView btn_arm_1 = findViewById(R.id.btn_arm_1); // 팔1
        TextView btn_arm_2 = findViewById(R.id.btn_arm_2); // 팔2
        btn_arm_1.setText(bodies.get(0).getArm());
        btn_arm_2.setText(bodies.get(0).getArm());

        TextView btn_abdomen_1 = findViewById(R.id.btn_abdomen_1); // 배1
        TextView btn_abdomen_2 = findViewById(R.id.btn_abdomen_2); // 배2
        btn_abdomen_1.setText(bodies.get(0).getStomach());
        btn_abdomen_2.setText(bodies.get(0).getStomach());

        TextView btn_bein_1 = findViewById(R.id.btn_bein_1); // 허벅지1
        TextView btn_bein_2 = findViewById(R.id.btn_bein_2); // 허벅지2
        btn_bein_1.setText(bodies.get(0).getThigh());
        btn_bein_2.setText(bodies.get(0).getThigh());

        TextView txtPersent = findViewById(R.id.txtPersent);
        txtPersent.setText(String.format(Locale.US, "%s%%", bodies.get(0).getTotalBodySet()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_1);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate.");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게


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
            mUsbReceiver.mainloop(mRealm);

/*
            Intent intent = getIntent();
            if(intent!=null){
                String str = intent.getStringExtra("detailTo");
                if(str.equals("1")) {
                    // 추천 프로그램 요청
                    //mUsbReceiver.writeDataToSerial("S35;N"); // 추천 프로그램 요청 PersonTabActivity 오는 경우
                }
//                else {
//                    // 일반 요청
//                    mUsbReceiver.writeDataToSerial("S20;N"); // 설정정보 요청 regActivity 오는경우
//                }
            }
*/
        } else {
//            Toast.makeText(this, "no connectionDetail", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
        //////////////////////////////////
        // Serial
        //////////////////////////////////
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
            //
            SharedPreferences prefs =getSharedPreferences("machine_program_name", MODE_PRIVATE);
            machine_program_name_1 = prefs.getString("machine_program_name_1", "0");
            machine_program_name_2 = prefs.getString("machine_program_name_2", "0");
            machine_program_name_3 = prefs.getString("machine_program_name_3", "0");
            machine_program_name_4 = prefs.getString("machine_program_name_4", "0");
            machine_program_name_5 = prefs.getString("machine_program_name_5", "0");
            my_program_id_1 = prefs.getInt("my_program_id_1", 0); //키값, 디폴트값
            my_program_id_2 = prefs.getInt("my_program_id_2", 0); //키값, 디폴트값
            my_program_id_3 = prefs.getInt("my_program_id_3", 0); //키값, 디폴트값
            my_program_id_4 = prefs.getInt("my_program_id_4", 0); //키값, 디폴트값
            my_program_id_5 = prefs.getInt("my_program_id_5", 0); //키값, 디폴트값
//            SharedPreferences time =getSharedPreferences("booking_end_time", MODE_PRIVATE);
//            String booking_end_time = time.getString("end_time", "0"); //키값, 디폴트값
            String str = intent.getStringExtra("detailTo");
            if(str.equals("2")) //앞면 / 뒷면 부분 강도에서 20보다 적은 숫자 설정 후 왔을 경우를 대비 하여 둘로 나눈다
                savedValueDisplay();
            else
                valueDisplay();
        }
        mHoloCircularProgressBar = findViewById(R.id.holoCircularProgressBar);

        mSeekBar = findViewById(R.id.btn_sb);
        mSeekBar.setOnSeekBarChangeListener(this);
        //처음부터 전체 강도 보여지게 하기 위해
        onProgressChanged(mSeekBar, all_value,true);
        //처음부터 전체 강도 보여지게 하기 위해

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_strong_test).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);

        findViewById(R.id.btn_abdomen_1).setOnClickListener(this); // f
        findViewById(R.id.btn_abdomen_2).setOnClickListener(this); // f
        findViewById(R.id.btn_arm_1).setOnClickListener(this); // f
        findViewById(R.id.btn_arm_2).setOnClickListener(this); // f
        findViewById(R.id.btn_bein_1).setOnClickListener(this); // f
        findViewById(R.id.btn_bein_2).setOnClickListener(this); // f
        findViewById(R.id.btn_brust_1).setOnClickListener(this); // f
        findViewById(R.id.btn_brust_2).setOnClickListener(this); // f

        findViewById(R.id.btn_arsch_1).setOnClickListener(this);
        findViewById(R.id.btn_arsch_2).setOnClickListener(this);
        findViewById(R.id.btn_latt_1).setOnClickListener(this);
        findViewById(R.id.btn_latt_2).setOnClickListener(this);
        findViewById(R.id.btn_waist_1).setOnClickListener(this);
        findViewById(R.id.btn_waist_2).setOnClickListener(this);
        findViewById(R.id.btn_sideflank_1).setOnClickListener(this);
        findViewById(R.id.btn_sideflank_2).setOnClickListener(this);

        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);

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

        TextView textView = findViewById(R.id.txtTitle);
        SharedPreferences setting ;
        setting = getSharedPreferences("setting", 0);
        Log.d(TAG_ACTIVITY, "main_title = " + setting.getString("main_title", ""));
        textView.setText(setting.getString("main_title", ""));
        //어떤 운동인지 체크한다
        String title = setting.getString("main_title", "");
        Log.d(TAG_ACTIVITY, "title = " + title);
        switch (title) {
            case "근육강화":
                playID = "근육강화";
                break;
            case "근력강화":
                playID = "근력강화";
                break;
            case "순발력 강화":
                playID = "순발력 강화";
                break;
            case "근지구력 강화":
                playID = "근지구력 강화";
                break;
            case "지구력 강화":
                playID = "지구력 강화";
                break;
            case "체지방":
                playID = "체지방";
                break;
            case "셀룰라이트":
                playID = "셀룰라이트";
                break;
            case "마른체형 근육":
                playID = "마른체형 근육";
                break;
            case "마른체형 근력":
                playID = "마른체형 근력";
                break;
            case "스트레칭":
                playID = "스트레칭";
                break;
            case "위축된 근육 컨디션 조절":
                playID = "위축된 근육 컨디션 조절";
                break;
            case "정상 근육 컨디션 조절":
                playID = "정상 근육 컨디션 조절";
                break;
            case "위축된 근력 컨디션 조절":
                playID = "위축된 근력 컨디션 조절";
                break;
            case "정상 근력 컨디션 조절":
                playID = "정상 근력 컨디션 조절";
                break;
            case "혈액순환 개선":
                playID = "혈액순환 개선";
                break;
            case "저속 마사지":
                playID = "저속 마사지";
                break;
            case "중속 마사지":
                playID = "중속 마사지";
                break;
            case "고속 마사지":
                playID = "고속 마사지";
                break;
            case "림프 마사지":
                playID = "림프 마사지";
                break;
            case "회복 마사지":
                playID = "회복 마사지";
            break;
        }
    }

    @Override
    public void onClick(View v) {
        SharedPreferences setting ;
        SharedPreferences.Editor editor;

        switch (v.getId()) {
            case R.id.btn_minus:
                SeekBar seekBar = findViewById(R.id.btn_sb);
                seekBar.setProgress(seekBar.getProgress()-1);
                TextView textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", seekBar.getProgress()));
                mHoloCircularProgressBar.setProgress((seekBar.getProgress()-1) * 0.01f);
                //String value = seekBar.getProgress() + "";
                //mUsbReceiver.writeDataToSerial(" S34;000000;9;"+value+";N");
                break;

            case R.id.btn_plus:
                seekBar = findViewById(R.id.btn_sb);
                seekBar.setProgress(seekBar.getProgress()+1);
                textView = findViewById(R.id.txtPersent);
                textView.setText(String.format(Locale.US, "%d%%", seekBar.getProgress()));
                mHoloCircularProgressBar.setProgress((seekBar.getProgress()+1) * 0.01f);
                //value = seekBar.getProgress() + "";
                //mUsbReceiver.writeDataToSerial(" S34;000000;9;"+value+";N");
                break;

            case R.id.btn_back:
                Intent i = new Intent(DetailActivity.this, MenuActivity.class);
                if (my_program_id_1 != 0) {
                    i.putExtra("my_program_id_1", my_program_id_1); //My program 운동명
                    i.putExtra("my_program_id_1_strength_1", my_program_id_1_strength_1); //각 운동의 운동 세기
                    i.putExtra("my_program_id_1_strength_2", my_program_id_1_strength_2);
                    i.putExtra("my_program_id_1_strength_3", my_program_id_1_strength_3);
                    i.putExtra("my_program_id_1_strength_4", my_program_id_1_strength_4);
                    i.putExtra("my_program_id_1_strength_5", my_program_id_1_strength_5);
                    i.putExtra("my_program_id_1_strength_6", my_program_id_1_strength_6);
                    i.putExtra("my_program_id_1_strength_7", my_program_id_1_strength_7);
                    i.putExtra("my_program_id_1_strength_8", my_program_id_1_strength_8);
                    i.putExtra("machine_program_name_1", machine_program_name_1);  //My program 운동명
                }
                if (my_program_id_2 != 0) {
                    i.putExtra("my_program_id_2", my_program_id_2); //My program 운동명
                    i.putExtra("my_program_id_2_strength_1", my_program_id_2_strength_1); //각 운동의 운동 세기
                    i.putExtra("my_program_id_2_strength_2", my_program_id_2_strength_2);
                    i.putExtra("my_program_id_2_strength_3", my_program_id_2_strength_3);
                    i.putExtra("my_program_id_2_strength_4", my_program_id_2_strength_4);
                    i.putExtra("my_program_id_2_strength_5", my_program_id_2_strength_5);
                    i.putExtra("my_program_id_2_strength_6", my_program_id_2_strength_6);
                    i.putExtra("my_program_id_2_strength_7", my_program_id_2_strength_7);
                    i.putExtra("my_program_id_2_strength_8", my_program_id_2_strength_8);
                    i.putExtra("machine_program_name_2", machine_program_name_2);  //My program 운동명
                }
                if (my_program_id_3 != 0) {
                    i.putExtra("my_program_id_3", my_program_id_3); //My program 운동명
                    i.putExtra("my_program_id_3_strength_1", my_program_id_3_strength_1); //각 운동의 운동 세기
                    i.putExtra("my_program_id_3_strength_2", my_program_id_3_strength_2);
                    i.putExtra("my_program_id_3_strength_3", my_program_id_3_strength_3);
                    i.putExtra("my_program_id_3_strength_4", my_program_id_3_strength_4);
                    i.putExtra("my_program_id_3_strength_5", my_program_id_3_strength_5);
                    i.putExtra("my_program_id_3_strength_6", my_program_id_3_strength_6);
                    i.putExtra("my_program_id_3_strength_7", my_program_id_3_strength_7);
                    i.putExtra("my_program_id_3_strength_8", my_program_id_3_strength_8);
                    i.putExtra("machine_program_name_3", machine_program_name_3);  //My program 운동명
                }
                if (my_program_id_4 != 0) {
                    i.putExtra("my_program_id_4", my_program_id_4);  //My program 운동명
                    i.putExtra("my_program_id_4_strength_1", my_program_id_4_strength_1); //각 운동의 운동 세기
                    i.putExtra("my_program_id_4_strength_2", my_program_id_4_strength_2);
                    i.putExtra("my_program_id_4_strength_3", my_program_id_4_strength_3);
                    i.putExtra("my_program_id_4_strength_4", my_program_id_4_strength_4);
                    i.putExtra("my_program_id_4_strength_5", my_program_id_4_strength_5);
                    i.putExtra("my_program_id_4_strength_6", my_program_id_4_strength_6);
                    i.putExtra("my_program_id_4_strength_7", my_program_id_4_strength_7);
                    i.putExtra("my_program_id_4_strength_8", my_program_id_4_strength_8);
                    i.putExtra("machine_program_name_4", machine_program_name_4);  //My program 운동명
                }
                if (my_program_id_5 != 0) {
                    i.putExtra("my_program_id_5", my_program_id_5);  //My program 운동명
                    i.putExtra("my_program_id_5_strength_1", my_program_id_5_strength_1); //각 운동의 운동 세기
                    i.putExtra("my_program_id_5_strength_2", my_program_id_5_strength_2);
                    i.putExtra("my_program_id_5_strength_3", my_program_id_5_strength_3);
                    i.putExtra("my_program_id_5_strength_4", my_program_id_5_strength_4);
                    i.putExtra("my_program_id_5_strength_5", my_program_id_5_strength_5);
                    i.putExtra("my_program_id_5_strength_6", my_program_id_5_strength_6);
                    i.putExtra("my_program_id_5_strength_7", my_program_id_5_strength_7);
                    i.putExtra("my_program_id_5_strength_8", my_program_id_5_strength_8);
                    i.putExtra("machine_program_name_5", machine_program_name_5);  //My program 운동명
                }
                startActivity(i);
                finish();
                break;

            case R.id.btn_strong_test:
                // 자체 화면 에서 이벤트만 보낸다.
                //startActivity(new Intent(DetailActivity.this, DetailStrongActivity.class));
                Intent a = new Intent(DetailActivity.this, DetailStrongActivity.class);
                a.putExtra("brust",brust_value);
                a.putExtra("abdomen",abdomen_value);
                a.putExtra("arm",arm_value);
                a.putExtra("bein",bein_value);
                a.putExtra("latt",latt_value);
                a.putExtra("sideflank",sideflank_value);
                a.putExtra("waist",waist_value);
                a.putExtra("arsch",arsch_value);
                a.putExtra("all",all_value);
                a.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(a);
                finish();
                break;

            case R.id.btn_start:
                // 운동 시작 화면에서 설정된 부분 강도 보여지게 값 전달하자
                Intent k = new Intent(DetailActivity.this, DetailStartActivity.class);
                k.putExtra("brust",brust_value);
                k.putExtra("abdomen",abdomen_value);
                k.putExtra("arm",arm_value);
                k.putExtra("bein",bein_value);
                k.putExtra("latt",latt_value);
                k.putExtra("sideflank",sideflank_value);
                k.putExtra("waist",waist_value);
                k.putExtra("arsch",arsch_value);
                k.putExtra("all",all_value);
                k.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(k);
                finish();
                break;

            case R.id.btn_abdomen_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "abdo");
                editor.apply();

                Intent abdomen_1 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                abdomen_1.putExtra("abdomen",abdomen_value);
                abdomen_1.putExtra("arm",arm_value);
                abdomen_1.putExtra("bein",bein_value);
                abdomen_1.putExtra("brust",brust_value);
                abdomen_1.putExtra("latt",latt_value);
                abdomen_1.putExtra("sideflank",sideflank_value);
                abdomen_1.putExtra("waist",waist_value);
                abdomen_1.putExtra("arsch",arsch_value);
                abdomen_1.putExtra("all",all_value);
                abdomen_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(abdomen_1);
                finish();
                break;
            case R.id.btn_abdomen_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "abdo");
                editor.commit();

                Intent abdomen_2 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                abdomen_2.putExtra("abdomen",abdomen_value);
                abdomen_2.putExtra("arm",arm_value);
                abdomen_2.putExtra("bein",bein_value);
                abdomen_2.putExtra("brust",brust_value);
                abdomen_2.putExtra("latt",latt_value);
                abdomen_2.putExtra("sideflank",sideflank_value);
                abdomen_2.putExtra("waist",waist_value);
                abdomen_2.putExtra("arsch",arsch_value);
                abdomen_2.putExtra("all",all_value);
                abdomen_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(abdomen_2);
                finish();
                break;
            case R.id.btn_arm_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arm");
                editor.commit();

                Intent arm_1 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                arm_1.putExtra("abdomen",abdomen_value);
                arm_1.putExtra("arm",arm_value);
                arm_1.putExtra("bein",bein_value);
                arm_1.putExtra("brust",brust_value);
                arm_1.putExtra("latt",latt_value);
                arm_1.putExtra("sideflank",sideflank_value);
                arm_1.putExtra("waist",waist_value);
                arm_1.putExtra("arsch",arsch_value);
                arm_1.putExtra("all",all_value);
                arm_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(arm_1);
                finish();
                break;
            case R.id.btn_arm_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arm");
                editor.commit();

                Intent arm_2 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                arm_2.putExtra("abdomen",abdomen_value);
                arm_2.putExtra("arm",arm_value);
                arm_2.putExtra("bein",bein_value);
                arm_2.putExtra("brust",brust_value);
                arm_2.putExtra("latt",latt_value);
                arm_2.putExtra("sideflank",sideflank_value);
                arm_2.putExtra("waist",waist_value);
                arm_2.putExtra("arsch",arsch_value);
                arm_2.putExtra("all",all_value);
                arm_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(arm_2);
                finish();
                break;

            case R.id.btn_bein_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "bein");
                editor.commit();

                Intent bein_1 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                bein_1.putExtra("abdomen",abdomen_value);
                bein_1.putExtra("arm",arm_value);
                bein_1.putExtra("bein",bein_value);
                bein_1.putExtra("brust",brust_value);
                bein_1.putExtra("latt",latt_value);
                bein_1.putExtra("sideflank",sideflank_value);
                bein_1.putExtra("waist",waist_value);
                bein_1.putExtra("arsch",arsch_value);
                bein_1.putExtra("all",all_value);
                bein_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(bein_1);
                finish();
                break;
            case R.id.btn_bein_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "bein");
                editor.commit();

                Intent bein_2 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                bein_2.putExtra("abdomen",abdomen_value);
                bein_2.putExtra("arm",arm_value);
                bein_2.putExtra("bein",bein_value);
                bein_2.putExtra("brust",brust_value);
                bein_2.putExtra("latt",latt_value);
                bein_2.putExtra("sideflank",sideflank_value);
                bein_2.putExtra("waist",waist_value);
                bein_2.putExtra("arsch",arsch_value);
                bein_2.putExtra("all",all_value);
                bein_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(bein_2);
                finish();
                break;
            case R.id.btn_brust_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "brust");
                editor.commit();

                Intent brust_1 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                brust_1.putExtra("abdomen",abdomen_value);
                brust_1.putExtra("arm",arm_value);
                brust_1.putExtra("bein",bein_value);
                brust_1.putExtra("brust",brust_value);
                brust_1.putExtra("latt",latt_value);
                brust_1.putExtra("sideflank",sideflank_value);
                brust_1.putExtra("waist",waist_value);
                brust_1.putExtra("arsch",arsch_value);
                brust_1.putExtra("all",all_value);
                brust_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(brust_1);
                finish();
                break;
            case R.id.btn_brust_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "brust");
                editor.commit();

                Intent brust_2 = new Intent(DetailActivity.this, DetailFrontActivity.class);
                brust_2.putExtra("abdomen",abdomen_value);
                brust_2.putExtra("arm",arm_value);
                brust_2.putExtra("bein",bein_value);
                brust_2.putExtra("brust",brust_value);
                brust_2.putExtra("latt",latt_value);
                brust_2.putExtra("sideflank",sideflank_value);
                brust_2.putExtra("waist",waist_value);
                brust_2.putExtra("arsch",arsch_value);
                brust_2.putExtra("all",all_value);
                brust_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(brust_2);
                finish();
                break;

            case R.id.btn_arsch_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arsch");
                editor.commit();

                Intent arsch_1 = new Intent(DetailActivity.this, DetailBackActivity.class);
                arsch_1.putExtra("arsch",arsch_value);
                arsch_1.putExtra("latt",latt_value);
                arsch_1.putExtra("waist",waist_value);
                arsch_1.putExtra("sideflank",sideflank_value);
                arsch_1.putExtra("abdomen",abdomen_value);
                arsch_1.putExtra("arm",arm_value);
                arsch_1.putExtra("bein",bein_value);
                arsch_1.putExtra("brust",brust_value);
                arsch_1.putExtra("all",all_value);
                arsch_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(arsch_1);
                finish();
                break;
            case R.id.btn_arsch_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "arsch");
                editor.commit();

                Intent arsch_2 = new Intent(DetailActivity.this, DetailBackActivity.class);
                arsch_2.putExtra("arsch",arsch_value);
                arsch_2.putExtra("latt",latt_value);
                arsch_2.putExtra("waist",waist_value);
                arsch_2.putExtra("sideflank",sideflank_value);
                arsch_2.putExtra("abdomen",abdomen_value);
                arsch_2.putExtra("arm",arm_value);
                arsch_2.putExtra("bein",bein_value);
                arsch_2.putExtra("brust",brust_value);
                arsch_2.putExtra("all",all_value);
                arsch_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(arsch_2);
                finish();
                break;
            case R.id.btn_latt_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "latt");
                editor.commit();

                Intent latt_1 = new Intent(DetailActivity.this, DetailBackActivity.class);
                latt_1.putExtra("latt",latt_value);
                latt_1.putExtra("arsch",arsch_value);
                latt_1.putExtra("waist",waist_value);
                latt_1.putExtra("sideflank",sideflank_value);
                latt_1.putExtra("abdomen",abdomen_value);
                latt_1.putExtra("arm",arm_value);
                latt_1.putExtra("bein",bein_value);
                latt_1.putExtra("brust",brust_value);
                latt_1.putExtra("all",all_value);
                latt_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(latt_1);
                finish();
                break;
            case R.id.btn_latt_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "latt");
                editor.commit();

                Intent latt_2 = new Intent(DetailActivity.this, DetailBackActivity.class);
                latt_2.putExtra("latt",latt_value);
                latt_2.putExtra("arsch",arsch_value);
                latt_2.putExtra("waist",waist_value);
                latt_2.putExtra("sideflank",sideflank_value);
                latt_2.putExtra("abdomen",abdomen_value);
                latt_2.putExtra("arm",arm_value);
                latt_2.putExtra("bein",bein_value);
                latt_2.putExtra("brust",brust_value);
                latt_2.putExtra("all",all_value);
                latt_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(latt_2);
                finish();
                break;
            case R.id.btn_waist_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "waist");
                editor.commit();

                Intent waist_1 = new Intent(DetailActivity.this, DetailBackActivity.class);
                waist_1.putExtra("waist",waist_value);
                waist_1.putExtra("latt",latt_value);
                waist_1.putExtra("arsch",arsch_value);
                waist_1.putExtra("sideflank",sideflank_value);
                waist_1.putExtra("abdomen",abdomen_value);
                waist_1.putExtra("arm",arm_value);
                waist_1.putExtra("bein",bein_value);
                waist_1.putExtra("brust",brust_value);
                waist_1.putExtra("all",all_value);
                waist_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(waist_1);
                finish();
                break;
            case R.id.btn_waist_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "waist");
                editor.commit();

                Intent waist_2 = new Intent(DetailActivity.this, DetailBackActivity.class);
                waist_2.putExtra("waist",waist_value);
                waist_2.putExtra("latt",latt_value);
                waist_2.putExtra("arsch",arsch_value);
                waist_2.putExtra("sideflank",sideflank_value);
                waist_2.putExtra("abdomen",abdomen_value);
                waist_2.putExtra("arm",arm_value);
                waist_2.putExtra("bein",bein_value);
                waist_2.putExtra("brust",brust_value);
                waist_2.putExtra("all",all_value);
                waist_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(waist_2);
                finish();
                break;
            case R.id.btn_sideflank_1:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "sideflank");
                editor.commit();

                Intent sideflank_1 = new Intent(DetailActivity.this, DetailBackActivity.class);
                sideflank_1.putExtra("sideflank",sideflank_value);
                sideflank_1.putExtra("latt",latt_value);
                sideflank_1.putExtra("waist",waist_value);
                sideflank_1.putExtra("arsch",arsch_value);
                sideflank_1.putExtra("abdomen",abdomen_value);
                sideflank_1.putExtra("arm",arm_value);
                sideflank_1.putExtra("bein",bein_value);
                sideflank_1.putExtra("brust",brust_value);
                sideflank_1.putExtra("all",all_value);
                sideflank_1.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(sideflank_1);
                finish();
                break;
            case R.id.btn_sideflank_2:
                setting = getSharedPreferences("setting", 0);
                editor = setting.edit();
                editor.putString("model_sel", "sideflank");
                editor.commit();

                Intent sideflank_2 = new Intent(DetailActivity.this, DetailBackActivity.class);
                sideflank_2.putExtra("sideflank",sideflank_value);
                sideflank_2.putExtra("latt",latt_value);
                sideflank_2.putExtra("waist",waist_value);
                sideflank_2.putExtra("arsch",arsch_value);
                sideflank_2.putExtra("abdomen",abdomen_value);
                sideflank_2.putExtra("arm",arm_value);
                sideflank_2.putExtra("bein",bein_value);
                sideflank_2.putExtra("brust",brust_value);
                sideflank_2.putExtra("all",all_value);
                sideflank_2.putExtra("menu",playID);  //어떤 운동인지 전달하자
                startActivity(sideflank_2);
                finish();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //seekBar 도 설정대로 나타나게 한다
        seekBar = findViewById(R.id.btn_sb);
        seekBar.setProgress(progress);
        //seekBar 도 설정대로 나타나게 한다
        TextView textView = findViewById(R.id.txtPersent);
        textView.setText(String.format(Locale.US, "%d%%", progress));
        mHoloCircularProgressBar.setProgress(progress * 0.01f);
        Log.d(TAG_ACTIVITY, "J.Y.T DetailActivity progress: "+ progress);
        all_value = progress;
        //String value = progress + "";
//        mUsbReceiver.writeDataToSerial("S23;"+intTostring(all_value)+";N");
    }

/*
    private String intTostring (int value) {  //int to String 자릿수 3자리로 만드는 함수 예를 들어 int 0 -> String 000  으로
        return (String.format(Locale.US, "%03d", value));
    }
*/

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

    private void valueDisplay() {  //강도 설정한 값 뿌리자
        int default_vlaue = 20;  //부분강도는 20 부터 이므로

        TextView btn_latt_1 = findViewById(R.id.btn_latt_1); // 어깨1
        TextView btn_latt_2 = findViewById(R.id.btn_latt_2); // 어깨2
        if (latt_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            latt_value = default_vlaue;
        btn_latt_1.setText(String.format(Locale.US, "%d", latt_value));
        btn_latt_2.setText(String.format(Locale.US, "%d", latt_value));

        TextView btn_waist_1 = findViewById(R.id.btn_waist_1); // 허리1
        TextView btn_waist_2 = findViewById(R.id.btn_waist_2); // 허리2
        if (waist_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            waist_value = default_vlaue;
        btn_waist_1.setText(String.format(Locale.US, "%d", waist_value));
        btn_waist_2.setText(String.format(Locale.US, "%d", waist_value));

        TextView btn_sideflank_1 = findViewById(R.id.btn_sideflank_1); // 옆구리1
        TextView btn_sideflank_2 = findViewById(R.id.btn_sideflank_2); // 옆구리2
        if (sideflank_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            sideflank_value = default_vlaue;
        btn_sideflank_1.setText(String.format(Locale.US, "%d", sideflank_value));
        btn_sideflank_2.setText(String.format(Locale.US, "%d", sideflank_value));

        TextView btn_arsch_1 = findViewById(R.id.btn_arsch_1); // 둔부1
        TextView btn_arsch_2 = findViewById(R.id.btn_arsch_2); // 둔부2
        if (arsch_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            arsch_value = default_vlaue;
        btn_arsch_1.setText(String.format(Locale.US, "%d", arsch_value));
        btn_arsch_2.setText(String.format(Locale.US, "%d", arsch_value));

        TextView btn_brust_1 = findViewById(R.id.btn_brust_1); // 가슴1
        TextView btn_brust_2 = findViewById(R.id.btn_brust_2); // 가슴2
        if (brust_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            brust_value = default_vlaue;
        btn_brust_1.setText(String.format(Locale.US, "%d", brust_value));
        btn_brust_2.setText(String.format(Locale.US, "%d", brust_value));

        TextView btn_arm_1 = findViewById(R.id.btn_arm_1); // 팔1
        TextView btn_arm_2 = findViewById(R.id.btn_arm_2); // 팔2
        if (arm_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            arm_value = default_vlaue;
        btn_arm_1.setText(String.format(Locale.US, "%d", arm_value));
        btn_arm_2.setText(String.format(Locale.US, "%d", arm_value));

        TextView btn_abdomen_1 = findViewById(R.id.btn_abdomen_1); // 배1
        TextView btn_abdomen_2 = findViewById(R.id.btn_abdomen_2); // 배2
        if (abdomen_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            abdomen_value = default_vlaue;
        btn_abdomen_1.setText(String.format(Locale.US, "%d", abdomen_value));
        btn_abdomen_2.setText(String.format(Locale.US, "%d", abdomen_value));

        TextView btn_bein_1 = findViewById(R.id.btn_bein_1); // 허벅지1
        TextView btn_bein_2 = findViewById(R.id.btn_bein_2); // 허벅지2
        if (bein_value < default_vlaue)  //값이 20 보다 적으면 20 으로 세팅
            bein_value = default_vlaue;
        btn_bein_1.setText(String.format(Locale.US, "%d", bein_value));
        btn_bein_2.setText(String.format(Locale.US, "%d", bein_value));
        if (all_value == 0)  //전체 강도 설정 안되어 있으면 5로 세팅
            all_value = 5;
    }

    private void savedValueDisplay() {  //강도 설정한 값 뿌리자
        TextView btn_latt_1 = findViewById(R.id.btn_latt_1); // 어깨1
        TextView btn_latt_2 = findViewById(R.id.btn_latt_2); // 어깨2
        btn_latt_1.setText(String.format(Locale.US, "%d", latt_value));
        btn_latt_2.setText(String.format(Locale.US, "%d", latt_value));

        TextView btn_waist_1 = findViewById(R.id.btn_waist_1); // 허리1
        TextView btn_waist_2 = findViewById(R.id.btn_waist_2); // 허리2
        btn_waist_1.setText(String.format(Locale.US, "%d", waist_value));
        btn_waist_2.setText(String.format(Locale.US, "%d", waist_value));

        TextView btn_sideflank_1 = findViewById(R.id.btn_sideflank_1); // 옆구리1
        TextView btn_sideflank_2 = findViewById(R.id.btn_sideflank_2); // 옆구리2
        btn_sideflank_1.setText(String.format(Locale.US, "%d", sideflank_value));
        btn_sideflank_2.setText(String.format(Locale.US, "%d", sideflank_value));

        TextView btn_arsch_1 = findViewById(R.id.btn_arsch_1); // 둔부1
        TextView btn_arsch_2 = findViewById(R.id.btn_arsch_2); // 둔부2
        btn_arsch_1.setText(String.format(Locale.US, "%d", arsch_value));
        btn_arsch_2.setText(String.format(Locale.US, "%d", arsch_value));

        TextView btn_brust_1 = findViewById(R.id.btn_brust_1); // 가슴1
        TextView btn_brust_2 = findViewById(R.id.btn_brust_2); // 가슴2
        btn_brust_1.setText(String.format(Locale.US, "%d", brust_value));
        btn_brust_2.setText(String.format(Locale.US, "%d", brust_value));

        TextView btn_arm_1 = findViewById(R.id.btn_arm_1); // 팔1
        TextView btn_arm_2 = findViewById(R.id.btn_arm_2); // 팔2
        btn_arm_1.setText(String.format(Locale.US, "%d", arm_value));
        btn_arm_2.setText(String.format(Locale.US, "%d", arm_value));

        TextView btn_abdomen_1 = findViewById(R.id.btn_abdomen_1); // 배1
        TextView btn_abdomen_2 = findViewById(R.id.btn_abdomen_2); // 배2
        btn_abdomen_1.setText(String.format(Locale.US, "%d", abdomen_value));
        btn_abdomen_2.setText(String.format(Locale.US, "%d", abdomen_value));

        TextView btn_bein_1 = findViewById(R.id.btn_bein_1); // 허벅지1
        TextView btn_bein_2 = findViewById(R.id.btn_bein_2); // 허벅지2
        btn_bein_1.setText(String.format(Locale.US, "%d", bein_value));
        btn_bein_2.setText(String.format(Locale.US, "%d", bein_value));
        if (all_value == 0)  //전체 강도 설정 안되어 있으면 5로 세팅
            all_value = 5;
    }

    @Override
    public void onBackPressed() {

    }
}
