package app.com.m20.model;

import io.realm.RealmObject;

/**
 * Created by kimyongyeon on 2017-12-06.
 */

public class Body extends RealmObject {
    private String programId; // 추천프로그램 아이디
    // 명칭 / 자리위치 / 자릿수
    private String totalBodySet; // 전체설정 | 0 | 3
    private String thorax; // 흉부 | 1 | 3
    private String stomach; // 복부 | 2 | 3
    private String arm; // 상완 | 3 | 3
    private String thigh; // 허벅다리 | 4 | 3
    private String shoulder; // 어깨 | 5 | 3
    private String waist; // 허리 | 6 | 3
    private String side; // 옆구리 | 7 | 3
    private String posterior; // 둔부 | 8 | 3

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getTotalBodySet() {
        return totalBodySet;
    }

    public void setTotalBodySet(String totalBodySet) {
        this.totalBodySet = totalBodySet;
    }

    public String getThorax() {
        return thorax;
    }

    public void setThorax(String thorax) {
        this.thorax = thorax;
    }

    public String getStomach() {
        return stomach;
    }

    public void setStomach(String stomach) {
        this.stomach = stomach;
    }

    public String getArm() {
        return arm;
    }

    public void setArm(String arm) {
        this.arm = arm;
    }

    public String getThigh() {
        return thigh;
    }

    public void setThigh(String thigh) {
        this.thigh = thigh;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getPosterior() {
        return posterior;
    }

    public void setPosterior(String posterior) {
        this.posterior = posterior;
    }
}
