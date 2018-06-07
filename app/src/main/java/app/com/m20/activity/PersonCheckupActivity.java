package app.com.m20.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import app.com.m20.R;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;
import app.com.m20.utils.Utils;

/**
 * Created by kimyongyeon on 2017-11-10.
 */

public class PersonCheckupActivity extends AppCompatActivity {
    String TAG_ACTIVITY = "M20_PersonCheckup";

    private UsbReceiver mUsbReceiver;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    String name;
    String age;
    String gender;
    String weight;
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
    String protein;
    String protein_min;
    String protein_max;
    String mineral;
    String mineral_min;
    String mineral_max;
    String bodyfat_control;
    String muscle_control;
    //체지방량 정보 받은 거 저장

    //체지방&체성분 표를 위해
    String strjudgmentValue;
    String strweighttargetControl;
    String strweighttargetExercise;
    String strweightIndex;
    String strbodyFatPervalue;
    String strbodyFatPertargetExercise;
    String strmuscletargetExercise;
    String strstandardBMI;
    String strBMItargetControl;
    String strfBMI;
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
    int judgmentValue;  //체중 Table 저장 변수
    int judgmentbodyFatvalue;  //체지방 Table 저장 변수
    int judgmentmusclevalue;  //근육량 Table 저장 변수
    String resultexerciseRecommend;
    String strgraphmuscleIndex;
    String strMuscleIndex;
    String strgraphbodyFatPervalue;
    String strgrapfBMI;
    String strstandardWeight;
    String resultweightIndex;
    String strstandardWeightMin;
    String strstandardWeightMax;
    // 2018-05-08, M20 request handling activity_constant into RI00003
    String activity_constant;  //활동상수 : 1(비활동적), 2(약간활동적), 3(적당히 활동적), 4(아주 활동적)

    @Override
    public void onDestroy() {
        if (mUsbReceiver != null) {
            mUsbReceiver.closeUsbSerial();
            unregisterReceiver(mUsbReceiver);
        }
        super.onDestroy();
    }

    Context context;

    class JavaScriptInterface {
        @JavascriptInterface
        public String getFileContents(String assetName){
            return readAssetsContent(context, assetName);
        }

        //Read resources from "assets" folder in string
        public String readAssetsContent(Context context, String name) {
            BufferedReader in = null;
            try {
                StringBuilder buf = new StringBuilder();
                InputStream is = context.getAssets().open(name);
                in = new BufferedReader(new InputStreamReader(is));

                String str;
                boolean isFirst = true;
                while ( (str = in.readLine()) != null ) {
                    if (isFirst)
                        isFirst = false;
                    else
                        buf.append('\n');
                    buf.append(str);
                }
                return buf.toString();
            } catch (IOException e) {
                Log.e(TAG_ACTIVITY, "Exception opening asset " + name);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG_ACTIVITY, "Exception closing asset " + name);
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person_checkup);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
        context = this;

        Intent intent = getIntent();
        if(intent!=null) {
            name = intent.getStringExtra("name");
            age = intent.getStringExtra("age");
            gender = intent.getStringExtra("gender");
            weight = intent.getStringExtra("weight");
            height = intent.getStringExtra("height");
        }

        // 2018-05-08, M20 request handling activity_constant into RI00003, Start
        SharedPreferences prefs =getSharedPreferences("activity_constant", MODE_PRIVATE);
        activity_constant = prefs.getString("Data_activity_constant", "0");
        // 2018-05-08, M20 request handling activity_constant into RI00003, End

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
//            Toast.makeText(this, "no connectionPersonCheckup", Toast.LENGTH_SHORT).show();
            Log.e(TAG_ACTIVITY, "No connection!!!");
        }
        //mUsbReceiver.writeDataToSerial("S32;N"); // 체지방 체크 요청
        //////////////////////////////////
        // Serial
        //////////////////////////////////

//         mediaPlayer = new MediaPlayer();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.check);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        WebView WebView01 = findViewById(R.id.webview);
        WebView01.addJavascriptInterface(new JavaScriptInterface(), "HybridApp");
        WebSettings webSettings = WebView01.getSettings();

        // alert debuging
        WebView01.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });


        WebView01.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // 3초후 자동 이동 소스 참조
                //Handler handler = new Handler() {
                    //public void handleMessage(Message msg) {
                        //super.handleMessage(msg);
                        //startActivity(new Intent(PersonCheckupActivity.this, PersonTabActivity.class));
                        //finish();
                    //}
                //};
                //handler.sendEmptyMessageDelayed(0, 3500);
            }
        });
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        WebView01.loadUrl("file:///android_asset/prog.html");

//        TextView tv = findViewById(R.id.txtComment1);
//        tv.setOnClickListener((v)->{
//
//            Intent intent = new Intent(PersonCheckupActivity.this, PersonTabActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            overridePendingTransition(R.anim.fade, R.anim.hold);
//            finish();
//
//        });
//
//        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
//        tv.startAnimation(startAnimation);

        // hkpark, for debugging
        // 3초후 자동 이동 소스 참조

//        Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                name="남궁옥분아";
//                age="28";
//                gender="0"; // 남:1, 여: 0
//                weight="72.3";   // 빨강 없음
//                //weight="72.4"; // 빨강
//                height="176.0";
//
//                super.handleMessage(msg);
//                //0735.7, 56.8, 15.6, 32.3, 41.7, 38.3, 46.8, 11.3, 10.3, 12.6, 03.86, 03.57, 04.36, -05.3, 01.1 end
//                //표준이하 bodyFatreceived("0735.7", "56.8", "10.6", "32.3", "37.7", "38.3", "46.8", "9.3", "10.3", "12.6", "02.86", "03.57", "04.36", "-05.3", "01.1");
//                bodyFatreceived("0735.7", "56.8", "15.6", "32.3", "41.7", "38.3", "46.8", "11.3", "10.3", "12.6", "03.86", "03.57", "04.36", "-05.3", "01.1");
//                //표준이상 bodyFatreceived("0735.7", "56.8", "30.6", "32.3", "47.7", "38.3", "46.8", "13.3", "10.3", "12.6", "04.86", "03.57", "04.36", "-05.3", "01.1");
//                //0484.3;62.1;10.8;35.3;45.5;38.3;46.8;12.3;10.3;12.6;04.22;03.57;04.36;-00.0;00.0;N
//                //bodyFatreceived("0484.3", "62.1", "10.8", "35.3", "45.5", "38.3", "46.8", "12.3", "10.3", "12.6", "04.22", "03.57", "04.36", "-00.0", "00.0");
////                startActivity(new Intent(PersonCheckupActivity.this, PersonTabActivity.class));
////                finish();
//            }
//        };
//        handler.sendEmptyMessageDelayed(0, 3000);


    }
    // handle -00.0 or -05.3 etc..., to the nearest tenth
    private String verifyDataDp1( String datax ){
        Log.i(TAG_ACTIVITY, "verifyDataDp1(" + datax +" )");
        String strTemp;
        float temp = Float.parseFloat(datax);
        if(temp==0)
            strTemp = "0.0"; // delete '-', ex. -0.0 ==> 0.0
        else
            strTemp = String.format(Locale.US, "%.1f", temp);

        Log.i(TAG_ACTIVITY, "strTemp = " + strTemp);
        return strTemp;
    }
    // handle -00.00 or +03.57 etc... to the nearest hundredth
    private String verifyDataDp2( String datax ){
        Log.i(TAG_ACTIVITY, "verifyDataDp2(" + datax +" )");
        String strTemp;
        float temp = Float.parseFloat(datax);
        if(temp==0)
            strTemp = "0.0"; // delete '-', ex. -0.0 ==> 0.0
        else
            strTemp = String.format(Locale.US, "%.2f", temp);

        Log.i(TAG_ACTIVITY, "strTemp = " + strTemp);
        return strTemp;
    }

    public void bodyFatreceived(String data1,String data2,String data3,String data4,String data5,String data6,String data7,String data8,String data9,String data10,String data11,String data12,String data13,String data14,String data15) {  //체지방 측정 정보 받았으면 PersonTabActivity 으로 이동하자
        //Log.i(TAG_ACTIVITY, "hk: " + data1 + ", " + data2 + ", " + data3 + ", " + data4 + ", " + data5 + ", " + data6 + ", " + data7 + ", " + data8 + ", " + data9 + ", " + data10 + ", " + data11 + ", " + data12 + ", " + data13 + ", " + data14 + ", " + data15 + " end");
        impedance = data1;
        ffm = data2;
        bodyfat = data3;
        muscle = data4;
        totalbodywater = data5;
        totalbodywater_min = data6;
        totalbodywater_max = data7;
        protein = data8;
        protein_min = data9;
        protein_max = data10;
        mineral = verifyDataDp2(data11);
        mineral_min = verifyDataDp2(data12);
        mineral_max = verifyDataDp2(data13);
        // handle "-00.0" value from generator
        bodyfat_control = verifyDataDp1(data14);
        muscle_control = verifyDataDp1(data15);
        weightCal();
        bodyFatPerCal();
        BMICal();
        caloryCal();
        bodyFatStandardCal();
        resultBodywater = bodyCompositionEvalution(1, totalbodywater, totalbodywater_min, totalbodywater_max);
        resultProtein = bodyCompositionEvalution(2, protein, protein_min, protein_max);
        resultMineral = bodyCompositionEvalution(3, mineral, mineral_min, mineral_max);
        if (gender.equals("1"))
            resultBodyfat = bodyCompositionEvalution(4, bodyfat, strmanMin, strmanMax);
        else
            resultBodyfat = bodyCompositionEvalution(4, bodyfat, strwomanMin, strwomanMax);
        Intent i = new Intent(PersonCheckupActivity.this, PersonTabActivity.class);
        i.putExtra("name",name);
        i.putExtra("age", age);
        i.putExtra("gender", gender);
        i.putExtra("weight", weight);
        i.putExtra("height", height);

        Log.i(TAG_ACTIVITY, "weight: "+weight);

        i.putExtra("impedance",impedance);
        i.putExtra("ffm", ffm);
//        i.putExtra("bodyfat", bodyfat);  //체지방량
//        i.putExtra("muscle", muscle);
//        i.putExtra("totalbodywater", totalbodywater);
//        i.putExtra("totalbodywater_min",totalbodywater_min);
//        i.putExtra("totalbodywater_max", totalbodywater_max);
//        i.putExtra("protein", protein);
//        i.putExtra("protein_min", protein_min);
//        i.putExtra("protein_max", protein_max);
//        i.putExtra("mineral",mineral);
//        i.putExtra("mineral_min", mineral_min);
//        i.putExtra("mineral_max", mineral_max);
        i.putExtra("bodyfat_control", bodyfat_control);
        //float fmuscle_control = strTofloat(muscle_control);
        //String strmuscle_control = String.format(Locale.US, "%.1f", fmuscle_control);
        i.putExtra("muscle_control", muscle_control);        // 근육량 조절 목표
        i.putExtra("strweightIndex", strweightIndex);
        i.putExtra("strjudgmentValue", strjudgmentValue);
        i.putExtra("strweighttargetControl", strweighttargetControl);  //체중 조절목표
        Log.i(TAG_ACTIVITY, "strweighttargetControl: "+strweighttargetControl);
        i.putExtra("strweighttargetExercise", strweighttargetExercise);  //체중 근육목표

        //체지방률 운동목표=체지방량-체지방 조절 목표
//        i.putExtra("strbodyFatPervalue", strbodyFatPervalue);  //체지방률
        float bodyFatPertargetExercise = strTofloat(bodyfat) + strTofloat(bodyfat_control);
        strbodyFatPertargetExercise = String.format(Locale.US, "%.1f", bodyFatPertargetExercise);
        i.putExtra("strbodyFatPertargetExercise", strbodyFatPertargetExercise);  //운동목표=체지방량+체지방

        //근육량 = muscle, 운동목표 = 근육량(muscle) - 근육 조절 목표(muscle_control)
        float muscletargetExercise = strTofloat(muscle) + strTofloat(muscle_control);
        strmuscletargetExercise = String.format(Locale.US, "%.1f", muscletargetExercise);
        i.putExtra("strmuscletargetExercise", strmuscletargetExercise);  //근육 운동목표
        Log.i(TAG_ACTIVITY, "strmuscletargetExercise: "+strmuscletargetExercise);

        //BMI
        i.putExtra("strfBMI", strfBMI);  //BMI
        i.putExtra("strstandardBMI", strstandardBMI);  //BMI 운동목표
        i.putExtra("strBMItargetControl", strBMItargetControl);  //BMI 조절목표
        Log.i(TAG_ACTIVITY, "strBMItargetControl: "+strBMItargetControl);

        //1일 권장 칼로리
        i.putExtra("strbasemeta", strbasemeta);
        i.putExtra("stractivitymeta", stractivitymeta);
        i.putExtra("strdigestmeta", strdigestmeta);
        i.putExtra("strkcal", strkcal);

        Log.i(TAG_ACTIVITY, "strbasemeta: "+strbasemeta);
        Log.i(TAG_ACTIVITY, "strkcal: "+strkcal);

        //체지방 표준 범위 최대 최소
        if (gender.equals("1")) {
            i.putExtra("strmanMin", strmanMin);
            i.putExtra("strmanMax", strmanMax);
        }
        else {
            i.putExtra("strwomanMin", strwomanMin);
            i.putExtra("strwomanMax", strwomanMax);
        }
        i.putExtra("resultBodywater", resultBodywater);
        i.putExtra("resultProtein", resultProtein);
        i.putExtra("resultMineral", resultMineral);
        i.putExtra("resultBodyfat", resultBodyfat);
        resultexerciseRecommend =  exerciseRecommend(judgmentValue, judgmentbodyFatvalue, judgmentmusclevalue);
        i.putExtra("resultexerciseRecommend", resultexerciseRecommend);
        i.putExtra("strgraphmuscleIndex", strgraphmuscleIndex);
        Log.i(TAG_ACTIVITY, "strgraphmuscleIndex: "+strgraphmuscleIndex);
        i.putExtra("strgraphbodyFatPervalue", strgraphbodyFatPervalue);
        i.putExtra("strgrapfBMI", strgrapfBMI);

        SharedPreferences endsaved =getSharedPreferences("end_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = endsaved.edit();

        // 2018-05-08, M20 request adding some items into RI00004. Start
        editor.putString("Data_weight_index",strweightIndex );
        editor.putString("Data_weight_goal_target",strweighttargetExercise );
        editor.putString("Data_weight_control_target",strweighttargetControl );
        editor.putString("Data_muscle_index",strMuscleIndex );
        editor.putString("Data_muscle_goal_target",strmuscletargetExercise );
        editor.putString("Data_muscle_control_target",muscle_control );
        editor.putString("Data_muscle_control",muscle_control );
        editor.putString("Data_body_fat_goal_target",strbodyFatPertargetExercise );
        editor.putString("Data_body_fat_control_target",bodyfat_control );
        editor.putString("Data_body_fat_control",bodyfat_control );
        editor.putString("Data_body_mass_goal_target",strstandardBMI );
        editor.putString("Data_body_mass_control_target",strBMItargetControl );
        editor.putString("Data_recommended_calories_per_day",strkcal );
        editor.putString("Data_basic_metabolism",strbasemeta );
        editor.putString("Data_activity_metabolism",stractivitymeta );
        editor.putString("Data_digestive_metabolism",strdigestmeta );
        editor.putString("Data_body_impedance",impedance );
        editor.putString("Data_fat_free_mass", ffm);
        editor.putString("Data_body_fat_status",Integer.toString(resultBodyfat) );
        editor.putString("Data_body_water_status", Integer.toString(resultBodywater));
        editor.putString("Data_protein_status", Integer.toString(resultProtein));
        editor.putString("Data_minerals_status", Integer.toString(resultMineral));
        // 2018-05-08, M20 request adding some items into RI00004. End

        editor.putString("Data_height",height ); //키
        editor.putString("Data_weight",weight ); //체중
        editor.putString("Data_strstandardWeight",strstandardWeight ); //체중(min) = 표준체중
        editor.putString("Data_resultweightIndex",resultweightIndex ); //체중(max) = 체중 인덱스
        editor.putString("Data_strstandardWeightMin",strstandardWeightMin ); //체중(min)
        editor.putString("Data_strstandardWeightMax",strstandardWeightMax ); //체중(max)
        editor.putString("Data_muscle",muscle ); //근육량
        editor.putString("Data_musclemin",muscle_min ); //근육량(min) 어떤 값인지 몰라서 일단 근육량
        editor.putString("Data_musclemax",muscle_max ); //근육량(max) 어떤 값인지 몰라서 일단 근육량
        editor.putString("Data_strbodyFatPervalue",strbodyFatPervalue ); //체지방률
        if (gender.equals("1")) {
            editor.putString("Data_strmanPerMin","12.0" );   //체지방률 (min)  어떤 값인지 몰라서 일단 체지방 (min)
            editor.putString("Data_strmanPerMax","20.0" );   //체지방률 (max)  어떤 값인지 몰라서 일단 체지방 (max)
        }
        else {
            editor.putString("Data_strwomanPerMin","20.0" );   //체지방률 (min)  어떤 값인지 몰라서 일단 체지방 (min)
            editor.putString("Data_strwomanPerMax","28.0" );   //체지방률 (max)  어떤 값인지 몰라서 일단 체지방 (max)
        }
        editor.putString("Data_bodyfat",bodyfat ); //체지방
        if (gender.equals("1")) {
            editor.putString("Data_strmanMin",strmanMin );   //체지방 (min)
            editor.putString("Data_strmanMax",strmanMax );   //체지방 (max)
        }
        else {
            editor.putString("Data_strwomanMin",strwomanMin );   //체지방 (min)
            editor.putString("Data_strwomanMax",strwomanMax );   //체지방 (max)
        }
        editor.putString("Data_strfBMI",strfBMI );  //BMI
        editor.putString("Data_strfBMImin","18.5" );  //BMI (min)  어떤 값인지 몰라서 일단 BMI
        editor.putString("Data_strfBMImax","24.9" );  //BMI (max) 어떤 값인지 몰라서 일단 BMI
        editor.putString("Data_totalbodywater",totalbodywater );  //체수분량
        editor.putString("Data_totalbodywater_min",totalbodywater_min );  //체수분량 (min)
        editor.putString("Data_totalbodywater_max",totalbodywater_max );  //체수분량 (max)
        editor.putString("Data_protein",protein );  //단백질
        editor.putString("Data_protein_min",protein_min );  //단백질 (min)
        editor.putString("Data_protein_max",protein_max );  //단백질 (max)
        editor.putString("Data_mineral",mineral );  //무기질
        editor.putString("Data_mineral_min",mineral_min );  //무기질 (min)
        editor.putString("Data_mineral_max",mineral_max );  //무기질 (max)
        editor.apply();

        startActivity(i);
        finish();
    }

    private void weightCal() {  //체중 tab PersonTabActivity로 넘겨야 할 값   strweightIndex, strjudgmentValue, strweighttargetControl, strweighttargetExercise
        float standardWeight, weightIndex, weighttargetExercise, weighttargetControl, graphweightIndex=0;
        float standardWeightMin, standardWeightMax =0;
        float minWeightIndex = 90, maxWeightIndex = 110, limitWeightIndex=200;

        if (gender.equals("1"))  //표준 체중 구하는 공식 (남/여가 다르다)
            standardWeight = strTofloat(height) * strTofloat(height) * 22 / 10000 ;  //남자는 22
        else
            standardWeight = strTofloat(height) * strTofloat(height) * 21 / 10000 ;  //여자는 21
        strstandardWeight = String.format(Locale.US, "%.1f", standardWeight);
        Log.i(TAG_ACTIVITY, "strstandardWeight: "+strstandardWeight);

        standardWeightMin = Float.valueOf(strstandardWeight) * (float)0.9;
        standardWeightMax = Float.valueOf(strstandardWeight) * (float)1.1;
        strstandardWeightMin = String.format(Locale.US, "%.1f", standardWeightMin);
        strstandardWeightMax = String.format(Locale.US, "%.1f", standardWeightMax);

        judgmentmusclevalue = judgmentmuscleTable(standardWeight);
        weightIndex = 100 + ((strTofloat(weight) - standardWeight) / standardWeight * 100);  //체중 index 구하는 공식 그래프용
        resultweightIndex = String.format(Locale.US, "%.1f", weightIndex);  //판정 Table이 소수점 한자리이므로 소수점 한자리까지만 끈어서 넘긴다
        weightIndex = strTofloat(resultweightIndex);  //소수점 1자리까지 자른 string->float 로  그래프 그리는 값
        //체중그래프 그리는 공식
        /*
        if (weightIndex <= 70)
            graphweightIndex = 0;
        else if (weightIndex <= 130 && weightIndex > 70)
            graphweightIndex = (weightIndex - 70) * (float) 1.6;
        else if (weightIndex > 130)
            graphweightIndex = 100;
            */
        if (weightIndex <= minWeightIndex)
            graphweightIndex = weightIndex/minWeightIndex*(float)33.3 ;
        else if (weightIndex <= maxWeightIndex && weightIndex > minWeightIndex)
            graphweightIndex = ((weightIndex - minWeightIndex) / (maxWeightIndex-minWeightIndex))*(float)33.3 + (float) 33.3;
        else if (weightIndex > maxWeightIndex)
            graphweightIndex = ((weightIndex-maxWeightIndex) / (limitWeightIndex-maxWeightIndex))*(float)33.3 + (float) 66.6;
        if(weightIndex> limitWeightIndex)
            graphweightIndex = 100;

        //체중그래프 그리는 공식
        // 2018-05-08, M20 request adding some items into RI00004.
        //strweightIndex = floatToString(graphweightIndex);
        strweightIndex = String.format(Locale.US, "%.1f", graphweightIndex);
        judgmentValue = judgmentTable(weightIndex);
        strjudgmentValue = intToString(judgmentValue);
        weighttargetControl = strTofloat(bodyfat_control) + strTofloat(muscle_control);  //조절목표
        Log.i(TAG_ACTIVITY, "weighttargetControl: "+weighttargetControl);
        strweighttargetControl = String.format(Locale.US, "%.1f", weighttargetControl);

        weighttargetExercise = strTofloat(weight) + weighttargetControl;  //운동목표
        strweighttargetExercise = String.format(Locale.US, "%.1f", weighttargetExercise);
    }

    private void bodyFatPerCal() {  //체지방률 계산하는 함수  체지방 tab PersonTabActivity로 넘겨야 할 값  strbodyFatPervalue
        float bodyFatPervalue;

        bodyFatPervalue = (strTofloat(bodyfat) / strTofloat(weight)) *100;  //체지방률 계산  그래프 그리는 값
        judgmentbodyFatvalue = judgmentbodyFatTable(bodyFatPervalue);
        strbodyFatPervalue = String.format(Locale.US, "%.1f", bodyFatPervalue);
    }

    private int judgmentbodyFatTable (float bodyFatPervalue) {  //체지방 판정 Table
        int result = 0;
        float graphbodyFatPervalue = 0;
        float minBodyFatPerMan = 12, maxBodyFatPerMan = 20, limitBodyFatPerMan =50;
        float minBodyFatPerWom = 20, maxBodyFatPerWom = 28, limitBodyFatPerWom =55;

        if (gender.equals("1")) {  //남자
            if ( bodyFatPervalue <= minBodyFatPerMan) {
                graphbodyFatPervalue = bodyFatPervalue/minBodyFatPerMan*(float)33.3 ;
                result = 0;
            }
            else if (bodyFatPervalue > minBodyFatPerMan && bodyFatPervalue <= maxBodyFatPerMan) {
                graphbodyFatPervalue = ((bodyFatPervalue-minBodyFatPerMan)/(maxBodyFatPerMan-minBodyFatPerMan))*(float)33.3 + (float) 33.3;
                result = 1;
            }
            else if (bodyFatPervalue >= maxBodyFatPerMan) {
                graphbodyFatPervalue = ((bodyFatPervalue-maxBodyFatPerMan)/(limitBodyFatPerMan-maxBodyFatPerMan))*(float)33.3 + (float) 66.6;
                result = 2;
            }
            if(bodyFatPervalue > limitBodyFatPerMan )
                graphbodyFatPervalue = 100;
        }
        else {
            if (bodyFatPervalue <= minBodyFatPerWom) {
                graphbodyFatPervalue = bodyFatPervalue/minBodyFatPerWom*(float)33.3 ;
                result = 0;
            }
            else if (bodyFatPervalue > minBodyFatPerWom && bodyFatPervalue <= maxBodyFatPerWom) {
                graphbodyFatPervalue = ((bodyFatPervalue - minBodyFatPerWom) / (maxBodyFatPerWom-minBodyFatPerWom))*(float)33.3 + (float) 33.3;
                result = 1;
            }
            else if (bodyFatPervalue >= maxBodyFatPerWom ) {
                graphbodyFatPervalue = ((bodyFatPervalue - maxBodyFatPerWom) / (limitBodyFatPerWom-maxBodyFatPerWom))*(float)33.3 + (float) 66.6;
                result = 2;
            }
            if(bodyFatPervalue > limitBodyFatPerWom )
                graphbodyFatPervalue = 100;
        }
        strgraphbodyFatPervalue = String.format(Locale.US, "%.1f", graphbodyFatPervalue);

        return result;
    }

    private int judgmentmuscleTable (float standardWeight) {  //근육량 판정 Table 함수
        int result;
        float standardMuscle, muscleindex, graphmuscleIndex = 0;
        float minMuscleIndex = 90, maxMuscleIndex = 110, limitMuscleIndex = 200;
        float f_muscle_min, f_muscle_max =0;
        String str_standardMuscle=null;

        if (gender.equals("1"))  //표준 근육량 구하는 공식 (남/여가 다르다)
            standardMuscle = standardWeight * (float) 0.85 * (float) 0.559;
        else
            standardMuscle = standardWeight * (float) 0.77 * (float) 0.545;
        muscleindex = (strTofloat(muscle) / standardMuscle) * 100;  //근육 index
        strMuscleIndex = String.format(Locale.US, "%.1f", muscleindex);

        /* 2018-06-07, updated 요구 기능 정의서
        근육량 min, Max  --> 근육량 = (근육량 index /100 ) *표준 근육량
        근육량 (min) = (90/100) * (표준 근육량) =연산값 (요구 기능 정의서에 있는 24는 해당 케이스에서의 표준 근육량임)
        근육량 (max) =(110/100) * (표준 근육량) =연산값
         */
        str_standardMuscle = String.format(Locale.US, "%.1f", standardMuscle);
        f_muscle_min = ((float)90/(float)100)* Float.parseFloat(str_standardMuscle);
        f_muscle_max = ((float)110/(float)100)* Float.parseFloat(str_standardMuscle);
        muscle_min = String.format(Locale.US, "%.1f", f_muscle_min);
        muscle_max = String.format(Locale.US, "%.1f", f_muscle_max);

        Log.i(TAG_ACTIVITY, "standardMuscle: "+standardMuscle);
        Log.i(TAG_ACTIVITY, "muscleindex: "+muscleindex);
        Log.i(TAG_ACTIVITY, "muscle_min: "+muscle_min);
        Log.i(TAG_ACTIVITY, "muscle_max: "+muscle_max);

        //근육그래프 그리는 공식
        /*
        if (muscleindex <= 70)
            graphmuscleIndex = 0;
        else if (muscleindex <= 130 && muscleindex > 70)
            graphmuscleIndex = (muscleindex - 70) * (float) 1.6;
        else if (muscleindex > 130)
            graphmuscleIndex = 100;
            */
        if (muscleindex <= minMuscleIndex)
            graphmuscleIndex = muscleindex/minMuscleIndex*(float)33.3;
        else if (muscleindex <= maxMuscleIndex && muscleindex > minMuscleIndex)
            graphmuscleIndex = ((muscleindex-minMuscleIndex)/(maxMuscleIndex-minMuscleIndex))*(float)33.3 + (float) 33.3;
        else if (muscleindex > maxMuscleIndex)
            graphmuscleIndex = ((muscleindex-maxMuscleIndex) / (limitMuscleIndex-maxMuscleIndex))*(float)33.3 + (float) 66.6;
        if(muscleindex> limitMuscleIndex)
            graphmuscleIndex = 100;

        strgraphmuscleIndex = String.format(Locale.US, "%.1f", graphmuscleIndex);
        //근육그래프 그리는 공식

        Log.i(TAG_ACTIVITY, "strgraphmuscleIndex: "+strgraphmuscleIndex);

        if (muscleindex <= 89.9)  //판정 table
            result = 0;
        else if (90 <= muscleindex && muscleindex <= 110)
            result = 1;
        else
            result = 2;
        return result;
    }

    private void BMICal() {  //BMI 계산하는 함수  BMI tab PersonTabActivity로 넘겨야 할 값  strstandardBMI,
        float fBMI, standardBMI, standardWeightforBMI, BMItargetControl, grapfBMI = 0;
        float maxBMI = (float)25, minBMI = (float)18.5, limitBMI = 50;//minBMI + maxBMI; // 43.3
        fBMI =  strTofloat(weight) / (strTofloat(height) * strTofloat(height)) *10000;
        /*
        if (fBMI <= 18) {
            grapfBMI = (fBMI - 15) * (float) 11.3;
        }
        else if (fBMI > 18 && fBMI <= 25) {
            grapfBMI = ((fBMI - 18) * (float) 4.7) + (float) 33.3;
        }
        else if (fBMI > 25) {
            grapfBMI = ((fBMI - 25) * (float) 6.6) + (float) 66.6;
        }*/
        if (fBMI <= minBMI) {
            grapfBMI = fBMI/minBMI*(float)33.3 ;
        }
        else if (fBMI > minBMI && fBMI <= maxBMI) {
            grapfBMI = ((fBMI - minBMI) / (maxBMI-minBMI))*(float)33.3 + (float) 33.3;
        }
        else if (fBMI > maxBMI) {
            grapfBMI = ((fBMI - maxBMI) / (limitBMI-maxBMI))*(float)33.3 + (float) 66.6;
        }
        if(fBMI > limitBMI){ // if fBMI > 43.3
            grapfBMI = 100;
        }
        strgrapfBMI = String.format(Locale.US, "%.1f", grapfBMI);  //그래프 그리는 값
        strfBMI = String.format(Locale.US, "%.1f", fBMI);  //BMI
        Log.d(TAG_ACTIVITY, "strfBMI: "+strfBMI);
        if (gender.equals("1"))  //표준 체중 구하는 공식 (남/여가 다르다 남여 차별이냐?)
            standardWeightforBMI = strTofloat(height) * strTofloat(height) * 22 / 10000 ;  //남자는 22
        else
            standardWeightforBMI = strTofloat(height) * strTofloat(height) * 21 / 10000 ;  //여자는 21
        standardBMI = (standardWeightforBMI / (strTofloat(height) * strTofloat(height))) * 10000;  //표준 BMI
        strstandardBMI = String.format(Locale.US, "%.1f", standardBMI); //BMI 운동 목표
        BMItargetControl = standardBMI - fBMI;  //BMI 조절 목표
        strBMItargetControl = String.format(Locale.US, "%.1f", BMItargetControl);
        Log.i(TAG_ACTIVITY, "strBMItargetControl: "+strBMItargetControl);
        Log.d(TAG_ACTIVITY, String.format(Locale.US, "strstandardBMI: %s, strBMItargetControl: %s.", strstandardBMI, strBMItargetControl));
    }

    private void caloryCal() {  //칼로리 구하는 함수  PersonTabActivity로 넘겨야 할 값  strbasemeta  stractivitymeta   strdigestmeta  strkcal
        float basemeta, activitymeta, digestmeta, kcal;
        basemeta = strTofloat(ffm) * (float) 21.5 + 370;   //기초대사량
        strbasemeta = String.format(Locale.US, "%.1f", basemeta);
        Log.i(TAG_ACTIVITY, "strbasemeta: "+strbasemeta);
        strbasemeta = String.format(Locale.US, "%d", (int)basemeta);
        // 2018-05-08, M20 request handling activity_constant into RI00003, Start
        // activitymeta = basemeta * activity_constant;
        float ac = (float)getActivityConstant();
        Log.i(TAG_ACTIVITY, "activity_constant : " + activity_constant + " float ac = " + ac);
        activitymeta = basemeta * ac;
        //activitymeta = basemeta * (float) 0.375;  //활동대사량 (0.375는 활동상수로 원래는 서버에서 내려오는 값이나 현재 안내려 와서 0.375로 박아둔다)
        // 2018-05-08, M20 request handling activity_constant into RI00003, End
        stractivitymeta = String.format(Locale.US, "%.1f", activitymeta);
        Log.i(TAG_ACTIVITY, "stractivitymeta: "+stractivitymeta);
        stractivitymeta = String.format(Locale.US, "%d", (int)activitymeta);
        digestmeta = (basemeta + activitymeta) / (float) 0.9 * (float) 0.1;  //소화 대사량
        strdigestmeta = String.format(Locale.US, "%.1f", digestmeta);
        Log.i(TAG_ACTIVITY, "strdigestmeta: "+strdigestmeta);
        strdigestmeta = String.format(Locale.US, "%d", (int)digestmeta);
        kcal = basemeta + activitymeta + digestmeta;

        strkcal = intToString(Math.round(kcal));  //반올림 해준다
    }

    // 2018-05-08, M20 request handling activity_constant into RI00003
    private double getActivityConstant(){
    /*--------------------------------------
    활동상수 : 1(비활동적), 2(약간활동적), 3(적당히 활동적), 4(아주 활동적)
        비활동적	    운동 거의 안 함	0.200
        약간 활동적	주 1~2일 운동	    0.375
        적당히 활동적	주 3~5일 운동	    0.555
        아주 활동적	주 5~7일 운동	    0.725
    ----------------------------------------
        <활동 상수 Table>  */
        switch(activity_constant){
            case "1":
                return 0.200;
            case "2":
                return 0.375;
            case "3":
                return 0.555;
            case "4":
                return 0.725;
            default:
                return 0.375;
        }
    }

    private void bodyFatStandardCal() {  //표준 체지방 검사하는 함수
        float manMin, womanMin, manMax, womanMax;
        if (gender.equals("1")) { //체지방 최소값, 최대값 계산
            manMin = (float) 0.12 * strTofloat(weight);  //남자 체지방 최소값
            manMax = (float) 0.20 * strTofloat(weight);  //남자 체지방 최대값
            strmanMin = String.format(Locale.US, "%.1f", manMin);
            strmanMax = String.format(Locale.US, "%.1f", manMax);
        }
        else {
            womanMin = (float) 0.20 * strTofloat(weight);  //여자 체지방 최소값
            womanMax = (float) 0.28 * strTofloat(weight);  //여자 체지방 최대값
            strwomanMin = String.format(Locale.US, "%.1f", womanMin);
            strwomanMax = String.format(Locale.US, "%.1f", womanMax);
        }
    }

    private int bodyCompositionEvalution(int what, String what_value, String min, String max) {  //체성분 평가 계산 함수
        int result = 0;
        if (what == 1) {  //체수분
            if (strTofloat(what_value) < strTofloat(min))  //표준이하
                result = 0;
            else if (strTofloat(what_value) > strTofloat(max))  //표준이상
                result = 2;
            else   //표준
                result = 1;
        }
        if (what == 2) {  //단백질
            if (strTofloat(what_value) < strTofloat(min))  //표준이하
                result = 0;
            else if (strTofloat(what_value) > strTofloat(max))  //표준이상
                result = 2;
            else   //표준
                result = 1;
        }
        if (what == 3) {  //무기질
            if (strTofloat(what_value) < strTofloat(min))  //표준이하
                result = 0;
            else if (strTofloat(what_value) > strTofloat(max))  //표준이상
                result = 2;
            else  //표준
                result = 1;
        }
        if (what == 4) {  //체지방
            if (strTofloat(what_value) < strTofloat(min))  //표준이하
                result = 0;
            else if (strTofloat(what_value) > strTofloat(max))  //표준이상
                result = 2;
            else   //표준
                result = 1;
        }
        return result;
    }

    private float strTofloat (String str) {  //String으로 받아온 값을 계산을 위해 float로 변환해 주는 함수
        return Float.parseFloat(str);
    }

    private String intToString (int num) {  //Int->String 함수
        return String.valueOf(num);
    }

    private String floatToString (float num) { //float->String 함수
        return Float.toString(num);
    }

    private int judgmentTable (float weightIndex) {  //체중 판정 Table
        int weightJudgment;

        if (weightIndex <= 89.9)  //저체중
            weightJudgment = 0;
        else if (90 <= weightIndex && weightIndex <= 110)  //평균
            weightJudgment = 1;
        else  //고체중
            weightJudgment = 2;
        return weightJudgment;
    }

    private String exerciseRecommend(int judgmentValue, int judgmentbodyFatvalue, int judgmentmusclevalue) {  //추천 운동 판정
        String result = null;

        if (
                (judgmentValue == 2 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 0)||
                (judgmentValue == 2 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 0)||
                (judgmentValue == 1 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 0)||
                (judgmentValue == 1 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 0))
            result = "근육강화";
        else if ((judgmentValue == 1 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 2) ||
                (judgmentValue == 1 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 1) ||

                (judgmentValue == 1 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 2) ||
                (judgmentValue == 1 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 1) ||

                (judgmentValue == 2 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 2) ||
                (judgmentValue == 2 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 1) ||

                (judgmentValue == 2 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 1) ||
                (judgmentValue == 2 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 2))
            result = "근력강화";
        else if ((judgmentValue == 0 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 2) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 1) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 0) ||

                (judgmentValue == 1 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 2) ||
                (judgmentValue == 1 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 1) ||
                (judgmentValue == 1 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 0) ||

                (judgmentValue == 2 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 0) ||
                (judgmentValue == 2 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 1) ||
                (judgmentValue == 2 && judgmentbodyFatvalue == 2 && judgmentmusclevalue == 2) )
            result = "체지방";
        else if ((judgmentValue == 0 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 0) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 0))
            result ="마른체형 근육";
        else if ((judgmentValue == 0 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 2) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 1 && judgmentmusclevalue == 1) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 2) ||
                (judgmentValue == 0 && judgmentbodyFatvalue == 0 && judgmentmusclevalue == 1))
            result ="마른체형 근력";
        else {
            result = "체지방";
            Log.e(TAG_ACTIVITY, "Not found match case" + result);
        }

        Log.d(TAG_ACTIVITY, "exerciseRecommend " + result);

        return result;
    }
}
