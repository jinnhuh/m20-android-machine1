package app.com.m20.utils;

/**
 * Created by kimyongyeon on 2017-11-27.
 */

public class AndroidToLinux {
    private static final String S01 = "S01;"; // 최초 기기 web server 등록시 기기번호 전송
    private static final String S02 = "S02;"; // 예약번호
    private static final String S03 = "S03;"; // login시
    private static final String S20 = "S20;"; // 전체 설정과 부분설정
    private static final String S34 = "S34;"; // 강도설정
    private static final String S41 = "S41;"; // 운동시작
    private static final String S42 = "S42;"; // 운동멈춤
    private static final String S43 = "S43;"; // 전체강도변경
    private static final String S44 = "S44;"; // 부분강도변경
    private static final String S25 = "S25;"; // Waveform 설정 (사용안함)
    private static final String N = ";N";

    // 운동프로그램 목록
    private static final String EMA_11 = "01";
    private static final String EMA_12 = "02";
    private static final String EMA_13 = "03";
    private static final String EMA_14 = "04";
    private static final String EMA_15 = "05";
    private static final String SHAPING_21 = "06";
    private static final String SHAPING_22 = "07";
    private static final String SHAPING_23 = "08";
    private static final String SHAPING_24 = "09";
    private static final String SHAPING_25 = "10";
    private static final String HEALTHCARE_31 = "11";
    private static final String HEALTHCARE_32 = "12";
    private static final String HEALTHCARE_33 = "13";
    private static final String HEALTHCARE_34 = "14";
    private static final String HEALTHCARE_35 = "15";
    private static final String MASSAGE_41 = "16";
    private static final String MASSAGE_42 = "17";
    private static final String MASSAGE_43 = "18";
    private static final String MASSAGE_44 = "19";
    private static final String MASSAGE_45 = "20";



    // 기기등록
    public static String deviceReg(String inputString) {
        return Utils.stringToHex0x(S01+inputString+N);
    }
    // 예약번호
    public static String requestNumber(String inputString) {
        return Utils.stringToHex0x(S02+inputString+N);
    }
    // 로그인시
    public static String login(String inputString) {
        return Utils.stringToHex0x(S03+inputString+N);
    }
    // 전체설정과 부분설정
    public static String androidTotalSet(String inputString) {
        return Utils.stringToHex0x(S20+inputString+N);
    }
    // 강도설정
    public static String strongSet(String inputString) {
        return Utils.stringToHex0x(S34+inputString+N);
    }
    // 운동시작
    public static String programStart(String inputString) {
        return Utils.stringToHex0x(S41+inputString+N);
    }
    // 운동멈춤
    public static String programStop(String inputString) {
        return Utils.stringToHex0x(S42+inputString+N);
    }
    // 전체강도변경
    public static String programTotalStrongEdit(String inputString) {
        return Utils.stringToHex0x(S43+inputString+N);
    }
    // 부분강도변경
    public static String programPartStrongEdit(String inputString) {
        return Utils.stringToHex0x(S44+inputString+N);
    }
    // Waveform 설정 (사용안함)
    public static String waveformSet(String inputString) {
        return Utils.stringToHex0x(S25+inputString+N);
    }
}
