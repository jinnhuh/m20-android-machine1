package app.com.m20.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kimyongyeon on 2017-12-06.
 */

public class User extends RealmObject {

    private int rNumber; // 예약번호
    private String sel; // 체지방 진행 YES / NO ==== 0, 1

    // 마이프로그램
    private RealmList<Body> program; // 총 5개 생성.
    private String name; // 이름
    private String height; // 키
    private String weight; // 몸무게
    private String age; // 나이
    private String sex; // 성별

    private String s501; // 체지방 결과1
    private String s502; // 체중
    private String s503; // 체중 하한
    private String s504;
    private String s505;
    private String s506;
    private String s507;
    private String s508;
    private String s509;
    private String s5010;

    private String s511;
    private String s512;
    private String s513;
    private String s514;
    private String s515;
    private String s516;
    private String s517;
    private String s518;
    private String s519;
    private String s5110;

    private String s521;
    private String s522;
    private String s523;
    private String s524;
    private String s525;
    private String s526;
    private String s527;
    private String s528;
    private String s529;
    private String s5210;
    private String s5211;
    private String s5212;

    private String s531;
    private String s532;
    private String s533;
    private String s534;
    private String s535;
    private String s536;
    private String s537;
    private String s538;

    public String getS501() {
        return s501;
    }

    public void setS501(String s501) {
        this.s501 = s501;
    }

    public String getS502() {
        return s502;
    }

    public void setS502(String s502) {
        this.s502 = s502;
    }

    public String getS503() {
        return s503;
    }

    public void setS503(String s503) {
        this.s503 = s503;
    }

    public String getS504() {
        return s504;
    }

    public void setS504(String s504) {
        this.s504 = s504;
    }

    public String getS505() {
        return s505;
    }

    public void setS505(String s505) {
        this.s505 = s505;
    }

    public String getS506() {
        return s506;
    }

    public void setS506(String s506) {
        this.s506 = s506;
    }

    public String getS507() {
        return s507;
    }

    public void setS507(String s507) {
        this.s507 = s507;
    }

    public String getS508() {
        return s508;
    }

    public void setS508(String s508) {
        this.s508 = s508;
    }

    public String getS509() {
        return s509;
    }

    public void setS509(String s509) {
        this.s509 = s509;
    }

    public String getS5010() {
        return s5010;
    }

    public void setS5010(String s5010) {
        this.s5010 = s5010;
    }

    public String getS511() {
        return s511;
    }

    public void setS511(String s511) {
        this.s511 = s511;
    }

    public String getS512() {
        return s512;
    }

    public void setS512(String s512) {
        this.s512 = s512;
    }

    public String getS513() {
        return s513;
    }

    public void setS513(String s513) {
        this.s513 = s513;
    }

    public String getS514() {
        return s514;
    }

    public void setS514(String s514) {
        this.s514 = s514;
    }

    public String getS515() {
        return s515;
    }

    public void setS515(String s515) {
        this.s515 = s515;
    }

    public String getS516() {
        return s516;
    }

    public void setS516(String s516) {
        this.s516 = s516;
    }

    public String getS517() {
        return s517;
    }

    public void setS517(String s517) {
        this.s517 = s517;
    }

    public String getS518() {
        return s518;
    }

    public void setS518(String s518) {
        this.s518 = s518;
    }

    public String getS519() {
        return s519;
    }

    public void setS519(String s519) {
        this.s519 = s519;
    }

    public String getS5110() {
        return s5110;
    }

    public void setS5110(String s5110) {
        this.s5110 = s5110;
    }

    public String getS521() {
        return s521;
    }

    public void setS521(String s521) {
        this.s521 = s521;
    }

    public String getS522() {
        return s522;
    }

    public void setS522(String s522) {
        this.s522 = s522;
    }

    public String getS523() {
        return s523;
    }

    public void setS523(String s523) {
        this.s523 = s523;
    }

    public String getS524() {
        return s524;
    }

    public void setS524(String s524) {
        this.s524 = s524;
    }

    public String getS525() {
        return s525;
    }

    public void setS525(String s525) {
        this.s525 = s525;
    }

    public String getS526() {
        return s526;
    }

    public void setS526(String s526) {
        this.s526 = s526;
    }

    public String getS527() {
        return s527;
    }

    public void setS527(String s527) {
        this.s527 = s527;
    }

    public String getS528() {
        return s528;
    }

    public void setS528(String s528) {
        this.s528 = s528;
    }

    public String getS529() {
        return s529;
    }

    public void setS529(String s529) {
        this.s529 = s529;
    }

    public String getS5210() {
        return s5210;
    }

    public void setS5210(String s5210) {
        this.s5210 = s5210;
    }

    public String getS5211() {
        return s5211;
    }

    public void setS5211(String s5211) {
        this.s5211 = s5211;
    }

    public String getS5212() {
        return s5212;
    }

    public void setS5212(String s5212) {
        this.s5212 = s5212;
    }

    public String getS531() {
        return s531;
    }

    public void setS531(String s531) {
        this.s531 = s531;
    }

    public String getS532() {
        return s532;
    }

    public void setS532(String s532) {
        this.s532 = s532;
    }

    public String getS533() {
        return s533;
    }

    public void setS533(String s533) {
        this.s533 = s533;
    }

    public String getS534() {
        return s534;
    }

    public void setS534(String s534) {
        this.s534 = s534;
    }

    public String getS535() {
        return s535;
    }

    public void setS535(String s535) {
        this.s535 = s535;
    }

    public String getS536() {
        return s536;
    }

    public void setS536(String s536) {
        this.s536 = s536;
    }

    public String getS537() {
        return s537;
    }

    public void setS537(String s537) {
        this.s537 = s537;
    }

    public String getS538() {
        return s538;
    }

    public void setS538(String s538) {
        this.s538 = s538;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getrNumber() {
        return rNumber;
    }

    public void setrNumber(int rNumber) {
        this.rNumber = rNumber;
    }

    public String getSel() {
        return sel;
    }

    public void setSel(String sel) {
        this.sel = sel;
    }

    public RealmList<Body> getProgram() {
        return program;
    }

    public void setProgram(RealmList<Body> program) {
        this.program = program;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
