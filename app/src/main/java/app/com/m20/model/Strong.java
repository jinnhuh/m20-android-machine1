package app.com.m20.model;

import io.realm.RealmObject;

/**
 * Created by kimyongyeon on 2017-12-07.
 */

// 강도설정
public class Strong extends RealmObject {
    private String pad1;
    private String pad2;
    private String pad3;
    private String pad4;
    private String channel;
    private String change;

    public String getPad1() {
        return pad1;
    }

    public void setPad1(String pad1) {
        this.pad1 = pad1;
    }

    public String getPad2() {
        return pad2;
    }

    public void setPad2(String pad2) {
        this.pad2 = pad2;
    }

    public String getPad3() {
        return pad3;
    }

    public void setPad3(String pad3) {
        this.pad3 = pad3;
    }

    public String getPad4() {
        return pad4;
    }

    public void setPad4(String pad4) {
        this.pad4 = pad4;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
