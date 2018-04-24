package app.com.m20.db;

import android.content.Context;

import app.com.m20.model.Body;
import app.com.m20.model.BodyFat;
import app.com.m20.model.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.RealmSchema;

/**
 * Created by kimyongyeon on 2017-12-07.
 */

public class DbManagement {
    private Realm realm;

    public DbManagement(Realm realm) {
        this.realm = realm;
    }

    public Realm getInstance() {
        return this.realm;
    }

    /**
     * 예약번호를 이용한 사용자 정보 조회
     * @param user
     * @return
     */
    public User dbQuery(User user) {
        return realm.where(User.class).equalTo("rNumber", user.getrNumber()).findFirst();
    }

    public RealmResults<User> dbNoFilterQuery() {
        return realm.where(User.class).findAll();
    }

    public RealmResults<User> dbLastNoFilterQuery() {
        return realm.where(User.class).findAllSorted("name");
    }

    /**
     * 체지방율 저장
     * @param _user
     */
    public void dbSelSave(User _user) {
        realm.executeTransaction((realm1 -> {
            // Add a person
            User user = realm.createObject(User.class);
            user.setSel(_user.getSel());
        }));
    }

    /**
     * 전체설정
     * @param _body
     */
    public void dbDefaultBodySettingSave(Body _body) {
//        realm = Realm.getDefaultInstance();
        realm.executeTransaction((realm1 -> {
            // Add a person
            Body body = realm.createObject(Body.class);
            body.setTotalBodySet(_body.getTotalBodySet());
            body.setThorax(_body.getThorax());
            body.setStomach(_body.getStomach());
            body.setArm(_body.getArm());
            body.setThigh(_body.getThigh());
            body.setShoulder(_body.getShoulder());
            body.setWaist(_body.getWaist());
            body.setSide(_body.getSide());
            body.setPosterior(_body.getPosterior());
        }));
    }

    /**
     * 사용자 정보 저장 및 마이프로그램 저장
     * @param _user
     */
    public void dbUserInfoSave(User _user) {
        realm.executeTransaction((realm1 -> {
            User user = realm.createObject(User.class);
            user.setrNumber(_user.getrNumber());
            user.setProgram(_user.getProgram());
            user.setName(_user.getName());
            user.setHeight(_user.getHeight());
            user.setWeight(_user.getWeight());
            user.setAge(_user.getAge());
        }));
    }

    /**
     * 체지방 측정결과 저장
     */
    public void dbBodyFatSave(BodyFat _bodyFat) {
        realm.executeTransaction((realm1 -> {
            BodyFat bodyFat = realm.createObject(BodyFat.class);
            bodyFat.setBodyFat(_bodyFat.getBodyFat());
            bodyFat.setWeight(_bodyFat.getWeight());
        }));
    }

}
