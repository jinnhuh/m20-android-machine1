package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import app.com.m20.R;
import app.com.m20.db.DbManagement;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;
import io.realm.Realm;

/**
 * Created by kimyongyeon on 2017-11-10.
 */

public class PersonTabActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_PersonTab";

    public WebView WebView01;
    private final Handler handler = new Handler();
    Context context;

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";
    //받는 데이타 정의
    private float weight = 0; // 체중
    private float minWeight = 0; // 체중 하한
    private float maxWeight = 0; // 체중 상한
    private float excerGoalWeight = 0; // 체중 운동 목표
    private float adjGoalWeight = 0; // 체중 조절 목표

    private float bodyFatPer = 0; // 체지방률
    private float minBodyFat = 0; // 체지방 하한
    private float maxBodyFat = 0; // 체지방 상한
    private float excerGoalBodyFat = 0; // 체지방 운동 목표
    private float adjGoalBodyFat = 0; // 체지방 조절 목표
    //여기까지 receiveData1

    private float musMass = 0; // 근육량
    private float minMusMass = 0; // 근육량 하한
    private float maxMusMass = 0; // 근육량 상한
    private float excerGoalMusMass = 0; // 근육량 운동 목표
    private float adjGoalMusMass = 0; // 근육량 조절 목표

    private float bmi = 0; // BMI
    private float minBmi = 0; // BMI 하한
    private float maxBmi = 0; // BMI 상한
    private float excerGoalBmi = 0; // BMI 운동 목표
    private float adjGoalBmi = 0; // BMI 조절 목표
    //여기까지 receiveData2

    private float bodyWater = 0; // 체수분
    private float minBodyWater = 0; // 체수분 하한
    private float maxBodyWater = 0; // 체수분 상한
    private int bodyWaterEval = 0; // 체수분 평가

    private float protein = 0; // 단백질
    private float minProtein = 0; // 단백질 하한
    private float maxProtein = 0; // 단백질 상한
    private int proteinEval = 0; // 단백질 평가

    private float minerals = 0; // 무기질
    private float minMinerals = 0; // 무기질 하한
    private float maxMinerals = 0; // 무기질 상한
    private int mineralsEval = 0; // 무기질 평가
    //여기까지 receiveData3

    private float bodyFatPer2 = 0; // 체지방
    private float minBodyFatPer2 = 0; // 체지방 하한
    private float maxBodyFatPer2 = 0; // 체지방 상한
    private int bodyFatPer2Eval = 0; // 체지방 평가

    private int basicMeta = 0; // 기초대사량
    private int digeMeta = 0; // 소화대사량
    private int activiMeta = 0; // 활동대사량
    private int oneKcal = 0; // 1일 권장 칼로리
    private String strOneKcal; // 1일 권장 칼로리
    //여기까지 receiveData4
    //받는 데이타 정의

    String name;
    String age;
    String gender;
    String weight_imsi;
    String height;

    //체지방량 정보 받은 거 저장
    String impedance;
    String ffm;
    String bodyfat;
    String muscle;
    String muscle_min;
    String muscle_max;
    String totalbodywater;
    String totalbodywater_min;
    String totalbodywater_max;
    String protein_imsi;
    String protein_min;
    String protein_max;
    String mineral;
    String mineral_min;
    String mineral_max;
    String bodyfat_control;
    String muscle_control;
    //체지방량 정보 받은 거 저장

    String strweightIndex;
    String strjudgmentValue;
    String strweighttargetControl;
    String strweighttargetExercise;
    String strbodyFatPertargetExercise;
    String strbodyFatPervalue;
    String strmuscletargetExercise;
    String strfBMI;
    String strstandardBMI;
    String strBMItargetControl;
    String strbasemeta;
    String stractivitymeta;
    String strdigestmeta;
    String strkcal;
    String strmanMin;
    String strmanMax;
    String strwomanMin;
    String strwomanMax;
    int resultBodywater;
    int resultProtein;
    int resultMineral;
    int resultBodyfat;
    String resultexerciseRecommend;
    String strgraphmuscleIndex;
    String strgraphbodyFatPervalue;
    String strgrapfBMI;
    String strstandardWeight;
    String resultweightIndex;
    String strmanPerMin;
    String strmanPerMax;
    String strwomanPerMin;
    String strwomanPerMax;
    String strfBMImin;
    String strfBMImax;

    Realm mRealm;
    DbManagement dbManagement;

    private void init() {
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();
        dbManagement = new DbManagement(mRealm);
    }

    public void imsireceiveData() {  //체지방 측정 된 값 보여지기 위해 임의의 data 대입
        weight = Float.parseFloat(weight_imsi); // 체중
        minWeight = Float.parseFloat(strstandardWeight); // 체중 하한  표준체중
        maxWeight = Float.parseFloat(resultweightIndex); // 체중 상한  체중 인덱스
        excerGoalWeight = Float.parseFloat(strweighttargetExercise); // 체중 운동 목표
        adjGoalWeight = Float.parseFloat(strweighttargetControl); // 체중 조절 목표

        bodyFatPer = Float.parseFloat(strbodyFatPervalue); // 체지방률
        if (gender.equals("1")) {
            minBodyFat = Float.parseFloat(strmanMin); // 체지방 하한
            maxBodyFat = Float.parseFloat(strmanMax); // 체지방 상한
        }
        else {
            minBodyFat = Float.parseFloat(strwomanMin); // 체지방 하한
            maxBodyFat = Float.parseFloat(strwomanMax); // 체지방 상한
        }
        excerGoalBodyFat = Float.parseFloat(strbodyFatPertargetExercise); // 체지방 운동 목표
        adjGoalBodyFat = Float.parseFloat(bodyfat_control); // 체지방 조절 목표

        musMass = Float.parseFloat(muscle); // 근육량
        minMusMass = Float.parseFloat(muscle_min); // 근육량 하한
        maxMusMass = Float.parseFloat(muscle_max); // 근육량 상한
        excerGoalMusMass = Float.parseFloat(strmuscletargetExercise); // 근육량 운동 목표
        adjGoalMusMass = Float.parseFloat(muscle_control); // 근육량 조절 목표

        bmi = Float.parseFloat(strfBMI); // BMI
        minBmi = Float.parseFloat(strfBMImin); // BMI 하한
        maxBmi = Float.parseFloat(strfBMImax); // BMI 상한
        excerGoalBmi = Float.parseFloat(strstandardBMI); // BMI 운동 목표
        adjGoalBmi = Float.parseFloat(strBMItargetControl); // BMI 조절 목표

        bodyWater = Float.parseFloat(totalbodywater); // 체수분
        minBodyWater = Float.parseFloat(totalbodywater_min); // 체수분 하한
        maxBodyWater = Float.parseFloat(totalbodywater_max); // 체수분 상한
        bodyWaterEval = resultBodywater; // 체수분 평가

        protein = Float.parseFloat(protein_imsi); // 단백질
        minProtein = Float.parseFloat(protein_min); // 단백질 하한
        maxProtein = Float.parseFloat(protein_max); // 단백질 상한
        proteinEval = resultProtein; // 단백질 평가

        minerals = Float.parseFloat(mineral); // 무기질
        minMinerals = Float.parseFloat(mineral_min); // 무기질 하한
        maxMinerals = Float.parseFloat(mineral_max); // 무기질 상한
        mineralsEval = resultMineral; // 무기질 평가

        bodyFatPer2 = Float.parseFloat(bodyfat); // 체지방
        if (gender.equals("1")) {
            minBodyFatPer2 = Float.parseFloat(strmanMin); // 체지방 하한
            maxBodyFatPer2 = Float.parseFloat(strmanMax); // 체지방 상한
        }
        else {
            minBodyFatPer2 = Float.parseFloat(strwomanMin); // 체지방 하한
            maxBodyFatPer2 = Float.parseFloat(strwomanMax); // 체지방 상한
        }
        bodyFatPer2Eval = resultBodyfat; // 체지방 평가

        basicMeta = Integer.parseInt(strbasemeta); // 기초대사량
        digeMeta = Integer.parseInt(strdigestmeta); // 소화대사량
        activiMeta = Integer.parseInt(stractivitymeta); // 활동대사량
        oneKcal = Integer.parseInt(strkcal); // 1일 권장 칼로리
        Log.i(TAG_ACTIVITY, "hk oneKcal :"+oneKcal);
        strOneKcal = currentpoint(strkcal);
        Log.i(TAG_ACTIVITY, "hk strOneKcal :"+strOneKcal);
    }

    public String currentpoint(String result){
        DecimalFormat df = new DecimalFormat("#,##0");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setDecimalFormatSymbols(dfs);

        double inputNum = Double.parseDouble(result);
        result = df.format(inputNum).toString();
        return result;
    }

    //public void receiveData(String str) {
    public void receiveData() {
            // 이제 화면에 그리기...
            WebView01 = new WebView(this);
            WebView01.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            WebView01.addJavascriptInterface(new PersonTabActivity.AndroidBridge(), "HybridApp");
            WebView01.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                }
            });
            WebSettings webSettings = WebView01.getSettings();
            webSettings.setJavaScriptEnabled(true);

//            RealmResults<User> user = dbManagement.dbNoFilterQuery();
//            String name = user.get(0).getName();
//            int age = Integer.parseInt(user.get(0).getAge());
//            String sex = user.get(0).getSex();
//            String height = user.get(0).getHeight();

            try {
                //int weight = Integer.parseInt(user.get(0).getS501()); // 체중
                //int minWeight = Integer.parseInt(user.get(0).getS502()); // 체중 하한
                //int maxWeight = Integer.parseInt(user.get(0).getS503()); // 체중 상한
                //int excerGoalWeight = Integer.parseInt(user.get(0).getS504()); // 체중 운동 목표
                //int adjGoalWeight = Integer.parseInt(user.get(0).getS505()); // 체중 조절 목표

                //int bodyFatPer = Integer.parseInt(user.get(0).getS506()); // 체지방률
                //int minBodyFat = Integer.parseInt(user.get(0).getS507()); // 체지방 하한
                //int maxBodyFat = Integer.parseInt(user.get(0).getS508()); // 체지방 상한
                //int excerGoalBodyFat = Integer.parseInt(user.get(0).getS509()); // 체지방 운동 목표
                //int adjGoalBodyFat = Integer.parseInt(user.get(0).getS5010()); // 체지방 조절 목표

                //int musMass = Integer.parseInt(user.get(0).getS511()); // 근육량
                //int minMusMass = Integer.parseInt(user.get(0).getS512()); // 근육량 하한
                //int maxMusMass = Integer.parseInt(user.get(0).getS513()); // 근육량 상한
                //int excerGoalMusMass = Integer.parseInt(user.get(0).getS514()); // 근육량 운동 목표
                //int adjGoalMusMass = Integer.parseInt(user.get(0).getS515()); // 근육량 조절 목표

                //int bmi = Integer.parseInt(user.get(0).getS516()); // BMI
                //int minBmi = Integer.parseInt(user.get(0).getS517()); // BMI 하한
                //int maxBmi = Integer.parseInt(user.get(0).getS518()); // BMI 상한
                //int excerGoalBmi = Integer.parseInt(user.get(0).getS519()); // BMI 운동 목표
                //int adjGoalBmi = Integer.parseInt(user.get(0).getS5110()); // BMI 조절 목표

                //int bodyWater = Integer.parseInt(user.get(0).getS521()); // 체수분
                //int minBodyWater = Integer.parseInt(user.get(0).getS522()); // 체수분 하한
                //int maxBodyWater = Integer.parseInt(user.get(0).getS523()); // 체수분 상한
                //int bodyWaterEval = Integer.parseInt(user.get(0).getS524()); // 체수분 평가

                //int protein = Integer.parseInt(user.get(0).getS525()); // 단백질
                //int minProtein = Integer.parseInt(user.get(0).getS526()); // 단백질 하한
                //int maxProtein = Integer.parseInt(user.get(0).getS527()); // 단백질 상한
                //int proteinEval = Integer.parseInt(user.get(0).getS528()); // 단백질 평가

                //int minerals = Integer.parseInt(user.get(0).getS529()); // 무기질
                //int minMinerals = Integer.parseInt(user.get(0).getS5210()); // 무기질 하한
                //int maxMinerals = Integer.parseInt(user.get(0).getS5211()); // 무기질 상한
                //int mineralsEval = Integer.parseInt(user.get(0).getS5212()); // 무기질 평가

                //int bodyFatPer2 = Integer.parseInt(user.get(0).getS531()); // 체지방
                //int minBodyFatPer2 = Integer.parseInt(user.get(0).getS532()); // 체지방 하한
                //int maxBodyFatPer2 = Integer.parseInt(user.get(0).getS533()); // 체지방 상한
                //int bodyFatPer2Eval = Integer.parseInt(user.get(0).getS534()); // 체지방 평가

                //int basicMeta = Integer.parseInt(user.get(0).getS535()); // 기초대사량
                //int digeMeta = Integer.parseInt(user.get(0).getS536()); // 소화대사량
                //int activiMeta = Integer.parseInt(user.get(0).getS537()); // 활동대사량
                //int oneKcal = Integer.parseInt(user.get(0).getS538()); // 1일 권장 칼로리


                StringBuilder params = new StringBuilder();
                params.append("?name=");
                params.append(name);
                params.append("&age=");
                params.append(age);
                if (gender.equals("1")) {
                    gender = "남";
                } else {
                    gender = "여";
                }
                params.append("&sex=");
                params.append(gender);
                params.append("&height=");
                params.append(height);

                Log.i(TAG_ACTIVITY, "hk weight :"+weight);
                params.append("&weight=" + weight); // 체중
                Log.i(TAG_ACTIVITY, "hk weight :"+weight);
                params.append("&strweightIndex=" + strweightIndex); // 체중 그래프 그리기 위한 index
                params.append("&excerGoalWeight=" + excerGoalWeight); // 운동목표
                Log.i(TAG_ACTIVITY, "hk adjGoalWeight :"+adjGoalWeight);
                if(adjGoalWeight > 0 )
                    params.append("&adjGoalWeight=+" + adjGoalWeight); // 조절목표
                else
                    params.append("&adjGoalWeight=" + adjGoalWeight); // 조절목표

                params.append("&minWeight=" + minWeight ); // 체중 하한
                params.append("&maxWeight=" + maxWeight ); // 체중 상한

                params.append("&bodyFatPer=" + bodyFatPer); // 체지방률
                params.append("&excerGoalBodyFat=" + excerGoalBodyFat);
                Log.i(TAG_ACTIVITY, "hk adjGoalBodyFat :"+adjGoalBodyFat);
                if(adjGoalBodyFat > 0 )
                    params.append("&adjGoalBodyFat=+" + adjGoalBodyFat);
                else
                    params.append("&adjGoalBodyFat=" + adjGoalBodyFat);

                params.append("&minBodyFat=" + minBodyFat );
                params.append("&maxBodyFat=" + maxBodyFat );
                params.append("&strgraphbodyFatPervalue=" + strgraphbodyFatPervalue); // 체지방 그래프 그리기 위한 index

                params.append("&musMass=" + musMass); // 근육량
                params.append("&excerGoalMusMass=" + excerGoalMusMass);

                Log.i(TAG_ACTIVITY, "hk adjGoalMusMass :"+adjGoalMusMass);
                if(adjGoalMusMass > 0 )
                    params.append("&adjGoalMusMass=+" + adjGoalMusMass);
                else
                    params.append("&adjGoalMusMass=" + adjGoalMusMass);

                params.append("&minMusMass=" + minMusMass );
                params.append("&maxMusMass=" + maxMusMass );
                params.append("&strgraphmuscleIndex=" + strgraphmuscleIndex); // 근육량 그래프 그리기 위한 index

                params.append("&bmi=" + bmi); // BMI
                params.append("&strfBMI=" + strfBMI); // BMI
                params.append("&strgrapfBMI=" + strgrapfBMI); // BMI
                params.append("&excerGoalBmi=" + excerGoalBmi);
                Log.i(TAG_ACTIVITY, "hk adjGoalBmi :"+adjGoalBmi);
                if(adjGoalBmi > 0 )
                    params.append("&adjGoalBmi=+" + adjGoalBmi);
                else
                    params.append("&adjGoalBmi=" + adjGoalBmi);
                params.append("&minBmi=" + minBmi );
                params.append("&maxBmi=" + maxBmi );
//
                params.append("&oneKcal=" + strOneKcal); // 1일 권장
                //Log.i(TAG_ACTIVITY, "hk oneKcal :"+oneKcal);
                params.append("&basicMeta=" + basicMeta);  // 기초대사량
                params.append("&digeMeta=" + digeMeta);   // 소화대사량
                params.append("&activiMeta=" + activiMeta);  // 활동대사량

                params.append("&bodyWater=" + bodyWater); // 체수분
                params.append("&minBodyWater=" + minBodyWater );
                params.append("&maxBodyWater=" + maxBodyWater );
                params.append("&bodyWaterEval=" + bodyWaterEval );

                params.append("&protein=" + protein); // 단백질
                params.append("&minProtein=" + minProtein );
                params.append("&maxProtein=" + maxProtein );
                params.append("&proteinEval=" + proteinEval );

                params.append("&minerals=" + minerals); // 무기질
                params.append("&minMinerals=" + minMinerals );
                params.append("&maxMinerals=" + maxMinerals );
                params.append("&mineralsEval=" + mineralsEval );

                params.append("&bodyFatPer2=" + bodyFatPer2); // 체지방
                params.append("&minBodyFatPer2=" + minBodyFatPer2 );
                params.append("&maxBodyFatPer2=" + maxBodyFatPer2 );
                params.append("&bodyFatPer2Eval=" + bodyFatPer2Eval );

                WebView01.loadUrl("file:///android_asset/result.html" + params.toString());

                RelativeLayout relativeLayout = findViewById(R.id.tab_result);
                relativeLayout.addView(WebView01);
            } catch(Exception e) {
                e.getStackTrace();
//                StringBuilder params = new StringBuilder();
//                params.append("?name=" + e.getLocalizedMessage());
//                params.append("&age=" + e.getStackTrace());
//                WebView01.loadUrl("file:///android_asset/result.html" + params.toString());
//
//                RelativeLayout relativeLayout = findViewById(R.id.tab_result);
//                relativeLayout.addView(WebView01);
            }
        //}
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
    public void onBackPressed() {

    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setMessage(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    if (arg.equals("y")) {
                        SharedPreferences setting ;
                        SharedPreferences.Editor editor;
                        setting = getSharedPreferences("setting", 0);
                        editor = setting.edit();
                        editor.clear();
                        editor.putString("main_title", resultexerciseRecommend);
                        editor.apply();
                        Log.i(TAG_ACTIVITY, "J.Y.T PersonTabActivity resultexerciseRecommend:"+resultexerciseRecommend);
//                        Log.i("M20", "J.Y.T PersonTabActivity setting.getString():"+setting.getString("main_title", ""));

                        Intent i = new Intent(PersonTabActivity.this, DetailActivity.class);
                        i.putExtra("detailTo", "1");
                        startActivity(i);
                        finish();
                    } else {
                        Intent intent = new Intent(PersonTabActivity.this, MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void endDataSaved() {  //운동 종료 할 때 저장하는 함수
        SharedPreferences prefs =getSharedPreferences("end_data", MODE_PRIVATE);
        prefs.getString("Data_height", height);
        height = prefs.getString("Data_height", "0");
        weight_imsi = prefs.getString("Data_weight", "0");
        strstandardWeight = prefs.getString("Data_strstandardWeight", "0");
        resultweightIndex = prefs.getString("Data_resultweightIndex", "0");
        muscle = prefs.getString("Data_muscle", "0");
        muscle_min = prefs.getString("Data_musclemin", "0");
        muscle_max = prefs.getString("Data_musclemax", "0");
        strbodyFatPervalue = prefs.getString("Data_strbodyFatPervalue", "0");
        if (gender.equals("1")) {
            strmanPerMin = prefs.getString("Data_strmanPerMin", "0");
            strmanPerMax = prefs.getString("Data_strmanPerMax", "0");
        }
        else {
            strwomanPerMin = prefs.getString("Data_strwomanPerMin", "0");
            strwomanPerMax = prefs.getString("Data_strwomanPerMax", "0");
        }

        bodyfat = prefs.getString("Data_bodyfat", "0");
        if (gender.equals("1")) {
            strmanMin = prefs.getString("Data_strmanMin", "0");
            strmanMax = prefs.getString("Data_strmanMax", "0");
        }
        else {
            strwomanMin = prefs.getString("Data_strwomanMin", "0");
            strwomanMax = prefs.getString("Data_strwomanMax", "0");
        }

        strfBMI = prefs.getString("Data_strfBMI", "0");
        strfBMImin = prefs.getString("Data_strfBMImin", "0");
        strfBMImax = prefs.getString("Data_strfBMImax", "0");
        totalbodywater = prefs.getString("Data_totalbodywater", "0");
        totalbodywater_min = prefs.getString("Data_totalbodywater_min", "0");
        totalbodywater_max = prefs.getString("Data_totalbodywater_max", "0");
        protein_imsi = prefs.getString("Data_protein", "0");
        protein_min = prefs.getString("Data_protein_min", "0");
        protein_max = prefs.getString("Data_protein_max", "0");
        mineral = prefs.getString("Data_mineral", "0");
        mineral_min = prefs.getString("Data_mineral_min", "0");
        mineral_max = prefs.getString("Data_mineral_max", "0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        context = this;

        init();

//        SharedPreferences prefs =getSharedPreferences("booking_end_time", MODE_PRIVATE);
//        String booking_end_time = prefs.getString("end_time", "0"); //키값, 디폴트값

        Intent intent = getIntent();
        if(intent!=null) {
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            weight_imsi = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");

            impedance = intent.getStringExtra("impedance");
            ffm = intent.getStringExtra("ffm");
//            bodyfat = intent.getStringExtra("bodyfat");
//            muscle = intent.getStringExtra("muscle");
//            totalbodywater = intent.getStringExtra("totalbodywater");
//            totalbodywater_min = intent.getStringExtra("totalbodywater_min");
//            totalbodywater_max = intent.getStringExtra("totalbodywater_max");
//            protein_imsi = intent.getStringExtra("protein");
//            protein_min = intent.getStringExtra("protein_min");
//            protein_max = intent.getStringExtra("protein_max");
//            mineral = intent.getStringExtra("mineral");
//            mineral_min = intent.getStringExtra("mineral_min");
//            mineral_max = intent.getStringExtra("mineral_max");
            bodyfat_control = intent.getStringExtra("bodyfat_control");
            muscle_control = intent.getStringExtra("muscle_control");

            strweightIndex = intent.getStringExtra("strweightIndex");
            strjudgmentValue = intent.getStringExtra("strjudgmentValue");
            strweighttargetControl = intent.getStringExtra("strweighttargetControl");
            strweighttargetExercise = intent.getStringExtra("strweighttargetExercise");
//            strbodyFatPervalue = intent.getStringExtra("strbodyFatPervalue");
            strbodyFatPertargetExercise = intent.getStringExtra("strbodyFatPertargetExercise");
            strmuscletargetExercise = intent.getStringExtra("strmuscletargetExercise");
            strfBMI = intent.getStringExtra("strfBMI");
            strstandardBMI = intent.getStringExtra("strstandardBMI");
            strBMItargetControl = intent.getStringExtra("strBMItargetControl");
            strbasemeta = intent.getStringExtra("strbasemeta");
            stractivitymeta = intent.getStringExtra("stractivitymeta");
            strdigestmeta = intent.getStringExtra("strdigestmeta");
            strkcal = intent.getStringExtra("strkcal");
//            if (gender.equals("1")) {
//                strmanMin = intent.getStringExtra("strmanMin");
//                strmanMax = intent.getStringExtra("strmanMax");
//            }
//            else {
//                strwomanMin = intent.getStringExtra("strwomanMin");
//                strwomanMax = intent.getStringExtra("strwomanMax");
//            }
            resultBodywater = intent.getIntExtra("resultBodywater",0);
            resultProtein = intent.getIntExtra("resultProtein", 0);
            resultMineral = intent.getIntExtra("resultMineral", 0);
            resultBodyfat = intent.getIntExtra("resultBodyfat", 0);
            resultexerciseRecommend = intent.getStringExtra("resultexerciseRecommend");
            strgraphmuscleIndex = intent.getStringExtra("strgraphmuscleIndex");
            strgraphbodyFatPervalue = intent.getStringExtra("strgraphbodyFatPervalue");
            strgrapfBMI = intent.getStringExtra("strgrapfBMI");
            endDataSaved();
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
            mUsbReceiver.mainloop(mRealm);
            //mUsbReceiver.writeDataToSerial("A50;0;N"); // 체지방 데이터 요청
        } else {
//            Toast.makeText(this, "no connectionPersonTab", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
        //////////////////////////////////
        // Serial
        //////////////////////////////////
        imsireceiveData();  // 임시로 data 넣어줌
        receiveData();  //화면 그리기
    }
}