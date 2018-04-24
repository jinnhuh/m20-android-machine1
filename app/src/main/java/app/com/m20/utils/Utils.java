package app.com.m20.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by kimyongyeon on 2017-11-13.
 */

public class Utils {
    public static void fullScreen(Activity activity) {
        View decorView;
        int uiOption;
        decorView = activity.getWindow().getDecorView();
        uiOption = activity.getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOption);
    }

    // 문자열을 헥사 스트링으로 변환하는 메서드
    public static String stringToHex(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("%02X ", (int) s.charAt(i));
        }

        return result;
    }

    public static String hexToString(String input) {
//        String sumStr = "";
//        String[] tokens = input.split(" ");
//
//        for(String token : tokens)
//        {
//            int temp = Integer.parseInt(token.substring(2, 4), 16);
//            char c = (char)temp;
////            System.out.print(c);
//            sumStr += c;
//        }
        return input;
    }


    // 헥사 접두사 "0x" 붙이는 버전
    public static String stringToHex0x(String s) {
//        String result = "";
//
//        for (int i = 0; i < s.length(); i++) {
//            result += String.format("0x%02X ", (int) s.charAt(i));
//        }

        return s;
    }

    public static String stringToHex0xs(String s) {
        String sumString = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sumString += stringToHex0x(String.valueOf(c));
        }
        return sumString;
    }

    private static String postHeader(String deviceId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("access_key=3w7z!df2mt5nrh68k43b)gfgs4ra)6kst()ae3jbp!znihy77!");
        stringBuilder.append("&secret_access_key=()wz!t8fmtg!tq7e9y(!25bxwr!b7)cs24gd3!s9m(k6)ji32s");
        stringBuilder.append("&unique_identifier=" + deviceId);
        return stringBuilder.toString();
    }

    // HTTP POST request
    public static String sendPost(String deviceId) throws Exception {

        //Your server URL
        String url = "https://devmmapi.m20.co.kr/login/process";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //Request Parameters you want to send
        String urlParameters = postHeader(deviceId);

        // Send post request
        con.setDoOutput(true);// Should be part of code only for .Net web-services else no need for PHP
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        return response.toString() + "@" + urlParameters;

    }

    public static String getHttpHTML_POST(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader br;
        BufferedWriter bw;
        String line;
        String result = "";
        String parameter = String.format("param1=%s¶m2=%s", URLEncoder.encode("param1"),URLEncoder.encode("param2"));

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //파라메터가 없으면 지워주면 된다.
            bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            bw.write(parameter);
            //요기까지

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
//          br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"euc-kr"));
            while ((line = br.readLine()) != null)
                result += line + "\n";
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
