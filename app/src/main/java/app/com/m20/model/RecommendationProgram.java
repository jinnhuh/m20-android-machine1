package app.com.m20.model;

import io.realm.RealmObject;

/**
 * Created by kimyongyeon on 2017-12-07.
 */

// 추천 프로그램
public class RecommendationProgram extends RealmObject {
    private String id;
    private Body program;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Body getProgram() {
        return program;
    }

    public void setProgram(Body program) {
        this.program = program;
    }
}
