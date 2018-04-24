package app.com.m20.model;

import io.realm.RealmObject;

/**
 * Created by kimyongyeon on 2017-12-07.
 */

// 체지방 측정결과
public class BodyFat extends RealmObject {
    private String id; // 아이디
    private String bodyFat; // 체지방
    private String weight; // 체중

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(String bodyFat) {
        this.bodyFat = bodyFat;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
