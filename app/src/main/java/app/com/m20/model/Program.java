package app.com.m20.model;

import io.realm.RealmObject;

/**
 * Created by kimyongyeon on 2017-12-07.
 */

// 프로그램
public class Program extends RealmObject {

    // 운동시작
    private String sportsId;
    private String sportsSeq;
    private String totalStrong;

    // 운동종료

    // 전체강도
    private String change;

    // 부분강도
    private Strong partStrong;

    public String getSportsId() {
        return sportsId;
    }

    public void setSportsId(String sportsId) {
        this.sportsId = sportsId;
    }

    public String getSportsSeq() {
        return sportsSeq;
    }

    public void setSportsSeq(String sportsSeq) {
        this.sportsSeq = sportsSeq;
    }

    public String getTotalStrong() {
        return totalStrong;
    }

    public void setTotalStrong(String totalStrong) {
        this.totalStrong = totalStrong;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Strong getPartStrong() {
        return partStrong;
    }

    public void setPartStrong(Strong partStrong) {
        this.partStrong = partStrong;
    }
}
