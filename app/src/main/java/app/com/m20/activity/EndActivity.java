package app.com.m20.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import app.com.m20.R;
import app.com.m20.server.Body_composition_details;
import app.com.m20.server.Machine_program_list;
import app.com.m20.server.Machine_strength_list;
import app.com.m20.server.body_part_number_list;
import app.com.m20.utils.Utils;

/**
 * Created by kimyongyeon on 2017-11-17.
 */

public class EndActivity extends AppCompatActivity implements View.OnClickListener{
    String TAG_ACTIVITY = "M20_End";

    int machine_program_id = 0;
    String athletics_time = null;
    int latt_value = 0;
    int waist_value = 0;
    int sideflank_value = 0;
    int arsch_value = 0;
    int brust_value = 0;
    int arm_value = 0;
    int abdomen_value = 0;
    int bein_value = 0;

    // 2018-05-08, M20 request adding some items into RI00004. Start
    String weight_index = null;
    String weight_goal_target = null;
    String weight_control_target = null;
    String muscle_index = null;
    String muscle_goal_target = null;
    String muscle_control_target = null;
    String muscle_control = null;
    String body_fat_goal_target = null;
    String body_fat_control_target = null;
    String body_fat_control = null;
    String body_mass_goal_target = null;
    String body_mass_control_target = null;
    String recommended_calories_per_day = null;
    String basic_metabolism = null;
    String activity_metabolism = null;
    String digestive_metabolism = null;
    String body_impedance = null;
    String fat_free_mass = null;
    String body_fat_status = null;
    String body_water_status = null;
    String protein_status = null;
    String minerals_status = null;
    // 2018-05-08, M20 request adding some items into RI00004. End

    String height = null;
    String weight_imsi = null;
    String strstandardWeight = null;
    String resultweightIndex = null;
    String muscle = null;
    String muscleMin = null;
    String muscleMax = null;
    String strbodyFatPervalue = null;

    String strmanPerMin = null;
    String strmanPerMax = null;

    String strwomanPerMin = null;
    String strwomanPerMax = null;

    String bodyfat = null;

    String strmanMin = null;
    String strmanMax = null;

    String strwomanMin = null;
    String strwomanMax = null;

    String strfBMI = null;
    String strfBMImin = null;
    String strfBMImax = null;
    String totalbodywater = null;
    String totalbodywater_min = null;
    String totalbodywater_max = null;
    String protein = null;
    String protein_min = null;
    String protein_max = null;
    String mineral = null;
    String mineral_min = null;
    String mineral_max = null;
    String age = null;
    String gender = null;
    int bookingID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_end);
        Utils.fullScreen(this);
        Log.i(TAG_ACTIVITY, "onCreate().");

        readGenderDataSaved();
        readDate();
        readEndDataSaved();
        readAgedDataSaved();
        readBookingIDDataSaved();
        readHeightDataSaved();
        // 18-05-02, M20 Request for changing call sendReservationInform() always when program end.
        //if ((getCountSaved()) == 1 || (getCountSaved() % 5 == 0)) {  //체지방 측정했을 경우에만 저장
        Log.i(TAG_ACTIVITY, "sendReservationInform() is called");
        sendReservationInform();
        //}
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //화면 안꺼지게
//         mediaPlayer = new MediaPlayer();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.end);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener((tmp) -> {
            Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    startActivity(new Intent(EndActivity.this, IntroActivity.class));
                    finish();
                }
            };
            handler.sendEmptyMessageDelayed(0, 5000);
        });
        RelativeLayout relativeLayout = findViewById(R.id.rrLayout);
        relativeLayout.setOnClickListener((v)-> {
            //startActivity(new Intent(EndActivity.this, IntroActivity.class));
            //finish();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                break;
        }
    }

    private void readDate() {
        SharedPreferences prefs =getSharedPreferences("athletics_data", MODE_PRIVATE);
        String playID = prefs.getString("Data_kind", "0");
        machine_program_id = machineProgramID(playID);
        athletics_time = prefs.getString("Data_time", "0");
        latt_value = prefs.getInt("Data_latt", 0);
        waist_value = prefs.getInt("Data_waist", 0);
        sideflank_value = prefs.getInt("Data_sideflank", 0);
        arsch_value = prefs.getInt("Data_arsch", 0);
        brust_value = prefs.getInt("Data_brust", 0);
        arm_value = prefs.getInt("Data_arm", 0);
        abdomen_value = prefs.getInt("Data_abdomen", 0);
        bein_value = prefs.getInt("Data_bein", 0);
    }

    private void readEndDataSaved() {  //운동 종료 할 때 저장하는 함수
        SharedPreferences prefs =getSharedPreferences("end_data", MODE_PRIVATE);

        // 2018-05-08, M20 request adding some items into RI00004. Start
        weight_index = prefs.getString("Data_weight_index", "0");
        weight_goal_target = prefs.getString("Data_weight_goal_target", "0");
        weight_control_target = prefs.getString("Data_weight_control_target", "0");
        muscle_index = prefs.getString("Data_muscle_index", "0");
        muscle_goal_target = prefs.getString("Data_muscle_goal_target", "0");
        muscle_control_target = prefs.getString("Data_muscle_control_target", "0");
        muscle_control = prefs.getString("Data_muscle_control", "0");
        body_fat_goal_target = prefs.getString("Data_body_fat_goal_target", "0");
        body_fat_control_target = prefs.getString("Data_body_fat_control_target", "0");
        body_fat_control = prefs.getString("Data_body_fat_control", "0");
        body_mass_goal_target = prefs.getString("Data_body_mass_goal_target", "0");
        body_mass_control_target = prefs.getString("Data_body_mass_control_target", "0");
        recommended_calories_per_day = prefs.getString("Data_recommended_calories_per_day", "0");
        basic_metabolism = prefs.getString("Data_basic_metabolism", "0");
        activity_metabolism = prefs.getString("Data_activity_metabolism", "0");
        digestive_metabolism = prefs.getString("Data_digestive_metabolism", "0");
        body_impedance = prefs.getString("Data_body_impedance", "0");
        fat_free_mass = prefs.getString("Data_fat_free_mass", "0");
        body_fat_status = prefs.getString("Data_body_fat_status", "0");
        body_water_status = prefs.getString("Data_body_water_status", "0");
        protein_status = prefs.getString("Data_protein_status", "0");
        minerals_status = prefs.getString("Data_minerals_status", "0");
        // 2018-05-08, M20 request adding some items into RI00004. End

        height = prefs.getString("Data_height", "0");
        weight_imsi = prefs.getString("Data_weight", "0");
        strstandardWeight = prefs.getString("Data_strstandardWeight", "0");
        resultweightIndex = prefs.getString("Data_resultweightIndex", "0");
        muscle = prefs.getString("Data_muscle", "0");
        muscleMin = prefs.getString("Data_musclemin", "0");
        muscleMax = prefs.getString("Data_musclemax", "0");
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
        protein = prefs.getString("Data_protein", "0");
        protein_min = prefs.getString("Data_protein_min", "0");
        protein_max = prefs.getString("Data_protein_max", "0");
        mineral = prefs.getString("Data_mineral", "0");
        mineral_min = prefs.getString("Data_mineral_min", "0");
        mineral_max = prefs.getString("Data_mineral_max", "0");
    }

    private void readAgedDataSaved() {
        SharedPreferences prefs =getSharedPreferences("age_data", MODE_PRIVATE);
        age = prefs.getString("Data_age", "0");
    }

    private void readGenderDataSaved() {
        SharedPreferences prefs =getSharedPreferences("gender_data", MODE_PRIVATE);
        gender = prefs.getString("Data_gender", "0");
    }

    private void readHeightDataSaved() {
        SharedPreferences prefs =getSharedPreferences("height_data", MODE_PRIVATE);
        height = prefs.getString("Data_height", "0");
    }

    private int machineProgramID(String mPID) {
        int machine_program = 0;
        if (mPID.equals("근육강화"))
            machine_program = 1;
        else if (mPID.equals("순발력 강화"))
            machine_program = 2;
        else if (mPID.equals("근력강화"))
            machine_program = 3;
        else if (mPID.equals("근지구력 강화"))
            machine_program = 4;
        else if (mPID.equals("지구력 강화"))
            machine_program = 5;
        else if (mPID.equals("체지방"))
            machine_program = 6;
        else if (mPID.equals("셀룰라이트"))
            machine_program = 7;
        else if (mPID.equals("마른체형 근육"))
            machine_program = 8;
        else if (mPID.equals("마른체형 근력"))
            machine_program = 9;
        else if (mPID.equals("스트레칭"))
            machine_program = 10;
        else if (mPID.equals("위축된 근육 컨디션 조절"))
            machine_program = 11;
        else if (mPID.equals("정상 근육 컨디션 조절"))
            machine_program = 12;
        else if (mPID.equals("위축된 근력 컨디션 조절"))
            machine_program = 13;
        else if (mPID.equals("정상 근력 컨디션 조절"))
            machine_program = 14;
        else if (mPID.equals("혈액순환 개선"))
            machine_program = 15;
        else if (mPID.equals("저속 마사지"))
            machine_program = 16;
        else if (mPID.equals("중속 마사지"))
            machine_program = 17;
        else if (mPID.equals("고속 마사지"))
            machine_program = 18;
        else if (mPID.equals("림프 마사지"))
            machine_program = 19;
        else if (mPID.equals("회복 마사지"))
            machine_program = 20;
        return  machine_program;
    }

    private void readBookingIDDataSaved() {
        SharedPreferences prefs =getSharedPreferences("bookingID_data", MODE_PRIVATE);
        bookingID = prefs.getInt("Data_bookingID", 0);
    }

    public String getIDSaved() {
        SharedPreferences prefs =getSharedPreferences("IDPW", MODE_PRIVATE);
        return prefs.getString("ID", "0");
    }

    public String getPWSaved() {
        SharedPreferences prefs =getSharedPreferences("IDPW", MODE_PRIVATE);
        return prefs.getString("PW", "0");
    }

    public int getCountSaved() {
        SharedPreferences prefs =getSharedPreferences("count_data", MODE_PRIVATE);
        return prefs.getInt("Data_count", 0);
    }

    public int getstartTimeSaved() {
        long curTime = System.currentTimeMillis();  //종료 시간을 구한다

        SharedPreferences prefs =getSharedPreferences("startTime_data", MODE_PRIVATE);
        long startTime = prefs.getLong("Data_startTime", 0);
        return ((int) ((curTime - startTime) / 1000) % 60);
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

    private String make_booking_result() {
        String result = "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("access_key=3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!");
        stringBuilder.append("&secret_access_key=()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s");
        //stringBuilder.append("&machine_unit_id=" + "11");
        stringBuilder.append("&machine_unit_id=");
        stringBuilder.append(Integer.parseInt(getIDSaved()));
        //stringBuilder.append("&password=" + ")8]25[41[(_30.!277a23a705e9addeefa14d475eb8e36c066");
        stringBuilder.append("&password=");
        stringBuilder.append(getPWSaved());
        //stringBuilder.append("&booking_id=" + "11");
        stringBuilder.append("&booking_id=");
        stringBuilder.append(bookingID);

        // body_composition_data 추가
        stringBuilder.append("&body_composition_data=");

        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        // 2018-05-08, M20 request adding some items into RI00004. Start
        object.addProperty("weight_index", Double.valueOf(weight_index));
        object.addProperty("weight_goal_target", Double.valueOf(weight_goal_target));
        object.addProperty("weight_control_target", Double.valueOf(weight_control_target));
        object.addProperty("muscle_index", Double.valueOf(muscle_index));
        object.addProperty("muscle_goal_target", Double.valueOf(muscle_goal_target));
        object.addProperty("muscle_control_target", Double.valueOf(muscle_control_target));
        object.addProperty("muscle_control", Double.valueOf(muscle_control));
        object.addProperty("body_fat_goal_target", Double.valueOf(body_fat_goal_target));
        object.addProperty("body_fat_control_target", Double.valueOf(body_fat_control_target));
        object.addProperty("body_fat_control", Double.valueOf(body_fat_control));
        object.addProperty("body_mass_goal_target", Double.valueOf(body_mass_goal_target));
        object.addProperty("body_mass_control_target", Double.valueOf(body_mass_control_target));
        object.addProperty("recommended_calories_per_day", Integer.parseInt(recommended_calories_per_day));
        object.addProperty("basic_metabolism", Integer.parseInt(basic_metabolism));
        object.addProperty("activity_metabolism", Integer.parseInt(activity_metabolism));
        object.addProperty("digestive_metabolism", Integer.parseInt(digestive_metabolism));
        object.addProperty("body_impedance", Double.valueOf(body_impedance));
        object.addProperty("fat_free_mass", Double.valueOf(fat_free_mass));
        object.addProperty("body_fat_status", Integer.parseInt(body_fat_status));
        object.addProperty("body_water_status", Integer.parseInt(body_water_status));
        object.addProperty("protein_status", Integer.parseInt(protein_status));
        object.addProperty("minerals_status", Integer.parseInt(minerals_status));
        // 2018-05-08, M20 request adding some items into RI00004. End

        int send_age = Integer.parseInt(age);
        object.addProperty("body_age", send_age);  //나이
        double send_height = Double.valueOf(height);
        object.addProperty("height", send_height);   //키
        double send_weight = Double.valueOf(weight_imsi);
        object.addProperty("weight", send_weight);    //체중
        double send_weight_standard_min = Double.valueOf(strstandardWeight);
        object.addProperty("weight_standard_min", send_weight_standard_min);  //체중 min 이거는 무슨값?
        double send_weight_standard_max = Double.valueOf(resultweightIndex);
        object.addProperty("weight_standard_max", send_weight_standard_max);  //체중 max OK  이거는 무슨값?
        double send_muscle = Double.valueOf(muscle);
        object.addProperty("muscle", send_muscle);  //근육량
        double send_muscle_standard_min = Double.valueOf(muscleMin);
        object.addProperty("muscle_standard_min", send_muscle_standard_min);  //근육량 min
        double send_muscle_standard_max = Double.valueOf(muscleMax);
        object.addProperty("muscle_standard_max", send_muscle_standard_max);  //근육량 max
        double send_body_fat_percent = Double.valueOf(strbodyFatPervalue);
        object.addProperty("body_fat_percent", send_body_fat_percent);  //체지방률
        if (gender.equals("1")) {
            double send_body_fat_percent_standard_min = Double.valueOf(strmanPerMin);
            object.addProperty("body_fat_percent_standard_min", send_body_fat_percent_standard_min);  //체지방률  min
            double send_body_fat_percent_standard_max = Double.valueOf(strmanPerMax);
            object.addProperty("body_fat_percent_standard_max", send_body_fat_percent_standard_max);  //체지방률  max
        } else {
            double send_body_fat_percent_standard_min = 1.0;//Double.valueOf(strwomanPerMin).doubleValue();
            object.addProperty("body_fat_percent_standard_min", send_body_fat_percent_standard_min);  //체지방률  min
            double send_body_fat_percent_standard_max = 2.0;//Double.valueOf(strwomanPerMax).doubleValue();
            object.addProperty("body_fat_percent_standard_max", send_body_fat_percent_standard_max);
        }
        double send_body_fat = Double.valueOf(bodyfat);
        object.addProperty("body_fat", send_body_fat);   //체지방
        if (gender.equals("1")) {
            double send_body_fat_standard_min = Double.valueOf(strmanMin);
            object.addProperty("body_fat_standard_min", send_body_fat_standard_min);  //체지방 min
            double body_fat_standard_max = Double.valueOf(strmanMax);
            object.addProperty("body_fat_standard_max", body_fat_standard_max);  //체지방 max
        } else {
            double send_body_fat_standard_min = 3.0;//Double.valueOf(strwomanMin).doubleValue();
            object.addProperty("body_fat_standard_min", send_body_fat_standard_min);  //체지방 min
            double body_fat_standard_max = 4.0;//Double.valueOf(strwomanMax).doubleValue();
            object.addProperty("body_fat_standard_max", body_fat_standard_max);  //체지방 max
        }
        double send_body_mass = Double.valueOf(strfBMI);
        object.addProperty("body_mass", send_body_mass);  //BMI
        double send_body_mass_standard_min = Double.valueOf(strfBMImin);
        object.addProperty("body_mass_standard_min", send_body_mass_standard_min);  //BMI  min
        double send_body_mass_standard_max = Double.valueOf(strfBMImax);
        object.addProperty("body_mass_standard_max", send_body_mass_standard_max);  //BMI max
        double send_body_water = Double.valueOf(totalbodywater);
        object.addProperty("body_water", send_body_water);  //체수분
        double send_body_water_standard_min = Double.valueOf(totalbodywater_min);
        object.addProperty("body_water_standard_min", send_body_water_standard_min);  //체수분 min
        double send_body_water_standard_max = Double.valueOf(totalbodywater_max);
        object.addProperty("body_water_standard_max", send_body_water_standard_max);  //체수분 max
        double send_protein = Double.valueOf(protein);
        object.addProperty("protein", send_protein);  //단백질
        double send_protein_standard_min = Double.valueOf(protein_min);
        object.addProperty("protein_standard_min", send_protein_standard_min);  //단백질 min
        double send_protein_standard_max = Double.valueOf(protein_max);
        object.addProperty("protein_standard_max", send_protein_standard_max);  //단백질 max
        double send_minerals = Double.valueOf(mineral);
        object.addProperty("minerals", send_minerals);  //무기질
        double send_minerals_standard_min = Double.valueOf(mineral_min);
        object.addProperty("minerals_standard_min", send_minerals_standard_min);  //무기질 min
        double send_minerals_standard_max = Double.valueOf(mineral_max);
        object.addProperty("minerals_standard_max", send_minerals_standard_max);  //무기질 max

//        object.addProperty("weight", 80.0);    //체중
//        object.addProperty("weight_standard_min", 60.0);  //체중 min 이거는 무슨값?
//        object.addProperty("weight_standard_max", 90.0);  //체중 max OK  이거는 무슨값?
//        object.addProperty("muscle", 40.0);  //근육량
//        object.addProperty("muscle_standard_min", 30.0);  //근육량 min
//        object.addProperty("muscle_standard_max", 50.0);  //근육량 max
//        object.addProperty("body_fat_percent", 20.0);  //체지방률
//        if (gender.equals("1")) {
//            object.addProperty("body_fat_percent_standard_min", 30.0);  //체지방률  min
//            object.addProperty("body_fat_percent_standard_max", 40.0);  //체지방률  max
//        }
//        else {
//            object.addProperty("body_fat_percent_standard_min", 10.0);  //체지방률  min
//            object.addProperty("body_fat_percent_standard_max", 20.0);
//        }
//        object.addProperty("body_fat", 17.0);   //체지방
//        if (gender.equals("1")) {
//            Log.i("M20", "J.Y.T EndActivity 남 ");
//            object.addProperty("body_fat_standard_min", 17.0);  //체지방 min
//            object.addProperty("body_fat_standard_max", 17.0);  //체지방 max
//        }
//        else {
//            Log.i("M20", "J.Y.T EndActivity 여 ");
//            object.addProperty("body_fat_standard_min", 17.0);  //체지방 min
//            object.addProperty("body_fat_standard_max", 17.0);  //체지방 max
//        }
//        object.addProperty("body_mass", 17.0);  //BMI
//        object.addProperty("body_mass_standard_min", 17.0);  //BMI  min
//        object.addProperty("body_mass_standard_max", 17.0);  //BMI max
//        object.addProperty("body_water", 17.0);  //체수분
//        object.addProperty("body_water_standard_min", 17.0);  //체수분 min
//        object.addProperty("body_water_standard_max", 17.0);  //체수분 max
//        object.addProperty("protein", 17.0);  //단백질
//        object.addProperty("protein_standard_min", 17.0);  //단백질 min
//        object.addProperty("protein_standard_max", 17.0);  //단백질 max
//        object.addProperty("minerals", 17.0);  //무기질
//        object.addProperty("minerals_standard_min", 17.0);  //무기질 min
//        object.addProperty("minerals_standard_max", 17.0);  //무기질 max

        String json_body_composition_data = gson.toJson(object);

        stringBuilder.append(json_body_composition_data);


        // body_composition_details_list 추가
        stringBuilder.append("&body_composition_details_list=");

        Body_composition_details[]  body_composition_details;
        body_composition_details = new Body_composition_details[8];

        body_part_number_list number_list = new body_part_number_list();

        String json_body_composition_details_list;

        try {
            int temp_val = 0;

            for(int i=0;i<body_composition_details.length;i++)  //이거는 사용 안함
            {
                temp_val++;
                number_list.body_part_number_id = 1;
                number_list.value = 1.0;

                body_composition_details[i] = new Body_composition_details();
                body_composition_details[i].body_part_id = temp_val;

                body_composition_details[i].body_part_number_list = new ArrayList<>();

                // check : 여기는 동적으로 배열로 처리해주어야 함 - 일단 하나만 저장하도록 처리
                body_composition_details[i].body_part_number_list.add(0,number_list);
            }

            json_body_composition_details_list = gson.toJson(body_composition_details);
            stringBuilder.append(json_body_composition_details_list);

            // machine_program_list 추가
            stringBuilder.append("&machine_program_list=");

            Machine_program_list[]  machine_program_list = new Machine_program_list[1];

            //Machine_strength_list strength_list = new Machine_strength_list();

//            int temp_val1 = 0;
            for(int i=0;i<machine_program_list.length;i++)
            {
//                temp_val1++;
                //strength_list.body_part_id = 1;
                //strength_list.strength = 11;

                machine_program_list[i] = new Machine_program_list();
                //machine_program_list[i].machine_program_id = temp_val1;
                machine_program_list[i].machine_program_id = machine_program_id;
                machine_program_list[i].used_time = getstartTimeSaved();
                machine_program_list[i].machine_strength_list = new ArrayList<>();
                for (int k = 0; k < 8; k++) {
                    //machine_program_list[i].machine_strength_list = new ArrayList<Machine_strength_list>();
                    Machine_strength_list strength_list = new Machine_strength_list();

                    // check : 여기는 동적으로 배열로 처리해주어야 함 - 일단 하나만 저장하도록 처리
                    if (k == 0) {
                        strength_list.body_part_id = 1;
                        strength_list.strength = brust_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k== 1) {
                        strength_list.body_part_id = 2;
                        strength_list.strength = abdomen_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k== 2) {
                        strength_list.body_part_id = 3;
                        strength_list.strength = arm_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k== 3) {
                        strength_list.body_part_id = 4;
                        strength_list.strength = bein_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k == 4) {
                        strength_list.body_part_id = 5;
                        strength_list.strength = latt_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k == 5) {
                        strength_list.body_part_id = 6;
                        strength_list.strength = waist_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else if (k == 6) {
                        strength_list.body_part_id = 7;
                        strength_list.strength = sideflank_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                    else /* if (k == 7) */ {
                        strength_list.body_part_id = 8;
                        strength_list.strength = arsch_value;
                        machine_program_list[i].machine_strength_list.add(k, strength_list);
                    }
                }
            }
            String json_machine_program_list = gson.toJson(machine_program_list);
            stringBuilder.append(json_machine_program_list);

            result = stringBuilder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG_ACTIVITY, "result: "+result);
        return result;
    }

    private void sendReservationInform() {

        Thread thread = new Thread() {
            @Override
            public void run() {

                String urlString = "https://devmmapi.m20.co.kr/booking/result";      // 요구사항ID : RI00004

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

//                    HttpURLConnection connection = httpsURLConnection;
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);

                    String sendData = make_booking_result();
                    sendData = sendData.replaceAll(",null","");
                    Log.i(TAG_ACTIVITY, "sendData: "+sendData);

                    OutputStream outputStream = httpsURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(sendData);
                    //bufferedWriter.write(getURLQuery(nameValuePairs));
                    //bufferedWriter.write(json1);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    httpsURLConnection.connect();

                    StringBuilder responseStringBuilder = new StringBuilder();
                    if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                        String stringLine;
                        for (;;) {
                            stringLine = bufferedReader.readLine();
                            if (stringLine == null)
                                break;
                            responseStringBuilder.append(stringLine);
                            responseStringBuilder.append('\n');
                        }
                        bufferedReader.close();
                    }

                    httpsURLConnection.disconnect();

                    String res_json = responseStringBuilder.toString();
                    Log.d(TAG_ACTIVITY, "res_json: " + res_json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onBackPressed() {

    }
}
