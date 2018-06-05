package app.com.m20.driver.serial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import app.com.m20.activity.DetailStartActivity;
import app.com.m20.activity.IntroActivity;
import app.com.m20.activity.MainActivity;
import app.com.m20.activity.PersonCheckupActivity;
import app.com.m20.activity.RegActivity;
import app.com.m20.activity.WeighWanningActivity;
import app.com.m20.db.DbManagement;
import io.realm.Realm;


public class UsbReceiver extends BroadcastReceiver {
	//private Boolean SHOW_DEBUG = true;
	private Boolean SHOW_DEBUG = false;
	private String TAG = "M20_UsbReceiver";

	private Context mContext;
	private Activity mActivity;
	private FTDriver mSerial;
	private Handler mHandler = new Handler();
	private StringBuilder mText;

	private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

	private boolean mStop = false;
	private boolean mRunningMainLoop = false;

	private Timer mUARTtimer= null;
	private TimerTask mUARTimerTask = null;

	// Default settings
//	private int mTextFontSize       = 12;
//	private Typeface mTextTypeface  = Typeface.MONOSPACE;
	private int mDisplayType        = FTDriverUtil.DISP_CHAR;
//	private int mBaudrate           = FTDriver.BAUD57600;

	public UsbReceiver(Activity activity, FTDriver serial)
	{
		mActivity = activity;
		mContext = activity.getBaseContext();
		mSerial = serial;
	}

/*
	public int GetTextFontSize()
	{
		return mTextFontSize;
	}
*/

	// Load default baud rate
	public int loadDefaultBaudrate() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String res = pref.getString("baudrate_list", Integer.toString(FTDriver.BAUD57600));
		return Integer.valueOf(res);
	}

	public void loadDefaultSettingValues() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String res = pref.getString("display_list", Integer.toString(FTDriverUtil.DISP_CHAR));
		mDisplayType = Integer.valueOf(res);

/*
		res = pref.getString("fontsize_list", Integer.toString(12));
		mTextFontSize = Integer.valueOf(res);

		res = pref.getString("typeface_list", Integer.toString(3));
		switch(Integer.valueOf(res)){
		case 0:
			mTextTypeface = Typeface.DEFAULT;
			break;
		case 1:
			mTextTypeface = Typeface.SANS_SERIF;
			break;
		case 2:
			mTextTypeface = Typeface.SERIF;
			break;
		case 3:
			mTextTypeface = Typeface.MONOSPACE;
			break;
		}
*/

		res = pref.getString("readlinefeedcode_list", Integer.toString(FTDriverUtil.LINEFEED_CODE_CRLF));
		FTDriverUtil.mReadLinefeedCode = Integer.valueOf(res);

		res = pref.getString("writelinefeedcode_list", Integer.toString(FTDriverUtil.LINEFEED_CODE_CRLF));
		FTDriverUtil.mWriteLinefeedCode = Integer.valueOf(res);

//		res = pref.getString("email_edittext", "@gmail.com");

		res = pref.getString("databits_list", Integer.toString(FTDriver.FTDI_SET_DATA_BITS_8));
		mSerial.setSerialPropertyDataBit(Integer.valueOf(res), FTDriver.CH_A);

		res = pref.getString("parity_list", Integer.toString(FTDriver.FTDI_SET_DATA_PARITY_NONE));
		mSerial.setSerialPropertyParity((Integer.valueOf(res) << 8), FTDriver.CH_A);

		res = pref.getString("stopbits_list", Integer.toString(FTDriver.FTDI_SET_DATA_STOP_BITS_1));
		mSerial.setSerialPropertyStopBits((Integer.valueOf(res) << 11), FTDriver.CH_A);

		res = pref.getString("flowcontrol_list", Integer.toString(FTDriver.FTDI_SET_FLOW_CTRL_NONE));
		mSerial.setFlowControl(FTDriver.CH_A, (Integer.valueOf(res) << 8));

		res = pref.getString("break_list", Integer.toString(FTDriver.FTDI_SET_NOBREAK));
		mSerial.setSerialPropertyBreak((Integer.valueOf(res) << 14), FTDriver.CH_A);

		mSerial.setSerialPropertyToChip(FTDriver.CH_A);
	}

/*
	public void openUsbSerial() {
		if (!mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "onNewIntent begin");
			}
			mBaudrate = loadDefaultBaudrate();
			if (!mSerial.begin(mBaudrate)) {
				Toast.makeText(mContext, "cannot open", Toast.LENGTH_LONG).show();
				return;
			} else {
				Toast.makeText(mContext, "connected", Toast.LENGTH_SHORT).show();
			}
		}

		if (!mRunningMainLoop) {
			mainloop();
		}
	}
*/

	public void closeUsbSerial() {
		detachedUi();
		mStop = true;
		mSerial.end();

		stopUARTCheckMsg();
	}

	public void startUARTCheckMsg(){
		Log.i(TAG, "startUARTCheckMsg()");

		mUARTtimer = new Timer(true);
		mUARTimerTask = new TimerTask() {
			@Override
			public void run() {
				Log.v("M20_Utils","타이머 작동중 ");
				writeDataToSerial("S98;N"); // UART 연결 확인 요청
			}
			@Override
			public boolean cancel() {
				Log.v("","타이머 종료");
				return super.cancel();
			}
		};
		mUARTtimer.schedule(mUARTimerTask, 1100, 5000);
	}

	public void stopUARTCheckMsg(){
		Log.i(TAG, "stopUARTCheckMsg()");
		if(mUARTimerTask != null) {
			mUARTimerTask.cancel();
			mUARTimerTask = null;
		}
	}

	public void writeDataToSerial(String strWrite) {
		strWrite = FTDriverUtil.changeLinefeedcode(strWrite);
		if (SHOW_DEBUG) {
//			Log.i(TAG, String.format(Locale.US, "Send (%d) %s.", strWrite.length(), strWrite));
            Log.i(TAG, String.format(Locale.US, "Send %s.", strWrite));
		}
		
		if(mSerial.isConnected())
			mSerial.write(strWrite.getBytes(), strWrite.length());
		else {
			//Toast.makeText(mContext, "USB is disconnected", Toast.LENGTH_LONG).show();
			Log.e(TAG, "No connection!!!");
		}
	}

	DbManagement dbManagement;
	Realm realm;

	public void mainloop(Realm realm) {

		// -----------------------------------------
		// 데이터베이스
		// -----------------------------------------
//		Realm.init(mContext);
//		RealmConfiguration config = new RealmConfiguration
//				.Builder()
//				.build();
//		realm = Realm.getInstance(config);
		this.realm = realm;
		dbManagement = new DbManagement(realm);
		// -----------------------------------------
		mStop = false;
		mRunningMainLoop = true;
//		Toast.makeText(mContext, "mainloop connected", Toast.LENGTH_SHORT).show();
//		Log.i(TAG, "Connected");
		if (SHOW_DEBUG) {
			Log.i(TAG, "start mainloop");
		}
		new Thread(mLoop).start();
	}

	public void mainloop() {

		// -----------------------------------------
		// 데이터베이스
		// -----------------------------------------
//		Realm.init(mContext);
//		RealmConfiguration config = new RealmConfiguration
//				.Builder()
//				.build();
//		realm = Realm.getInstance(config);
//		dbManagement = new DbManagement(realm);
		// -----------------------------------------
		mStop = false;
		mRunningMainLoop = true;
//		Toast.makeText(mContext, "real mainloop connected", Toast.LENGTH_SHORT).show();
//		Log.i(TAG, "Connected");
		if (SHOW_DEBUG) {
			Log.i(TAG, "start mainloop");
		}
		new Thread(mLoop).start();

		startUARTCheckMsg();
	}

	private Runnable mLoop = new Runnable() {
		@Override
		public void run() {
			int len;
			byte[] rbuf = new byte[100];
			byte[] imsirbuf = new byte[100];
			int bMsgCnt = 0;
			
			for (;;) {// this is the main loop for transferring
				len = mSerial.read(rbuf);

				if (len > 0) {
					rbuf[len] = 0;
					if(bMsgCnt == 0) {
						mText = new StringBuilder();
					}
					if(len < 100) {
						OnReadMessage(rbuf, len);
						//bMsgCnt += len;
						System.arraycopy(rbuf, 0, imsirbuf, bMsgCnt, len);  //장비 용
						bMsgCnt += len;
					}
//					else if(len == 33)
//					{
//						OnReadMessage(rbuf, len);
//						bMsgCnt = len;
//					}

//					Log.d(TAG, "J.Y.T bMsgCnt: "+bMsgCnt);
//					for (int i = 0; i <bMsgCnt; i++){
//					    Log.d(TAG, "J.Y.T rbuf["+i+"]: "+rbuf[i]);
//	                }
					if(bMsgCnt >= 5 && imsirbuf[bMsgCnt-3] == 78)  //N 까지 체크 78 == N  장비 용
						//if(bMsgCnt >= 5 && rbuf[bMsgCnt-1] == 78)  //N 까지 체크 78 == N  tool 용
					{
                        bMsgCnt = 0;

						mHandler.post(new Runnable() {
							public void run() {
								String str = mText.toString();
								Log.i(TAG, String.format(Locale.US, "Receive %s.", str));
								//((RegActivity)mActivity).setText(str);

								String array[] = str.split(";");
								String key = array[0];
//								Log.d(TAG, "J.Y.T array[0]: "+array[0]);

/*
								if (key.equals("S10")) {
									// 맥어드레스
									String mac = array[1];
								}
								else if (key.equals("S02")) { // 체지방 진행 여부 판단해서 화면 이동
									String sel = array[1]; // sel
									((RegActivity)mActivity).setText(sel);

									// 시연용
									try {
										((RegActivity)mActivity).regCheck(sel);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									// 실사용
//									if (sel.equals("0")) { // 체지방 진행 YES
//										((RegActivity)mActivity).activityMove("0");
//									} else { // 체지방 진행 NO
//										((RegActivity)mActivity).activityMove("1");
//									}
								}
								else if (key.equals("S20")) { // 설정값
									String totalBody = array[1]; // 전체값
									String thorax = array[2]; // 흉부
									String stomach = array[3]; // 복부
									String arm = array[4]; // 상완
									String thigh = array[5]; // 허벅다리
									String shoulder = array[6]; // 어깨
									String waist = array[7]; // 허리
									String side = array[8]; // 옆구리
									String posterior = array[9]; // 둔부

									Body body = new Body();
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									dbManagement.dbDefaultBodySettingSave(body);
								}
								else if (key.equals("S21")) { // 마이프로그램1
									String id = array[1];
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									Body body = new Body();
									body.setProgramId(id);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									((MenuActivity) mActivity).setText("21", body);

								}
								else if (key.equals("S22")) { // 마이프로그램2
									String id = array[1];
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									Body body = new Body();
									body.setProgramId(id);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									((MenuActivity) mActivity).setText("22", body);

								}
								else if (key.equals("S23")) { // 마이프로그램3
									String id = array[1];
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									Body body = new Body();
									body.setProgramId(id);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									((MenuActivity) mActivity).setText("23", body);

								}
								else if (key.equals("S24")) { // 마이프로그램4
									String id = array[1];
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									Body body = new Body();
									body.setProgramId(id);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									((MenuActivity) mActivity).setText("24", body);
								}
								else if (key.equals("S25")) { // 마이프로그램5
									String id = array[1];
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									Body body = new Body();
									body.setProgramId(id);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);

									((MenuActivity) mActivity).setText("25", body);
								}
								else if (key.equals("S30")) { // 체지방 진행여부 요청
									String yesNo = array[1]; // 체지방 여부
									if (yesNo.equals("1")) {
										// 다음단계
										try {
											((WelcomeActivity)mActivity).activityMove();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
								else if (key.equals("S31")) { // 이름,키,몸무게
									String name = array[1]; // 이름
									String height = array[2]; // 키 height
									String weight = array[3]; // 몸무게 weight
									String age = array[4]; // 나이 age
									String sex = array[5]; // 성별 sex

									realm.beginTransaction();
									User user = realm.createObject(User.class);
									user.setName(name);
									user.setHeight(height);
									user.setWeight(weight);
									user.setAge(age);
									user.setSex(sex);
									realm.commitTransaction();


									//((RegActivity)mActivity).setText(array.toString());

//									User user = dbManagement.dbNoFilterQuery();
//									user.setName(name);
//									user.setHeight(height);
//									user.setWeight(weight);
//									user.setAge(age);
//									user.setSex(sex);
//									dbManagement.dbUserInfoSave(user);

									// 시연용
									try {
										((RegActivity) mActivity).activityMove(name);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//((WelcomeActivity)mActivity).setText(name);
								}
								else if (key.equals("S32")) { // 체지방 측정 결과
									String bodyFat = array[1]; // 체지방
									String weight = array[2]; // 체중
									BodyFat bodyFat1 = new BodyFat();
									bodyFat1.setBodyFat(bodyFat);
									bodyFat1.setWeight(weight);
									dbManagement.dbBodyFatSave(bodyFat1);
								}
								else if (key.equals("S35")) { // 추천 프로그램
									String programId = array[1]; // 추천프로그램ID
									String totalBody = array[2]; // 전체값
									String thorax = array[3]; // 흉부
									String stomach = array[4]; // 복부
									String arm = array[5]; // 상완
									String thigh = array[6]; // 허벅다리
									String shoulder = array[7]; // 어깨
									String waist = array[8]; // 허리
									String side = array[9]; // 옆구리
									String posterior = array[10]; // 둔부

									realm.beginTransaction();
									Body body = realm.createObject(Body.class);
									body.setProgramId(programId);
									body.setTotalBodySet(totalBody);
									body.setThorax(thorax);
									body.setStomach(stomach);
									body.setArm(arm);
									body.setThigh(thigh);
									body.setShoulder(shoulder);
									body.setWaist(waist);
									body.setSide(side);
									body.setPosterior(posterior);
									realm.commitTransaction();

									((DetailActivity)mActivity).setText();

								}
*/
								if (key.equals("B27")) { // 체중 응답
									String weigh = array[1]; // 체중 값
									((WeighWanningActivity)mActivity).receiveWeigh(weigh);
								}
								else if (key.equals("B28")) { // 체지방 응답
									String data1 = array[1]; // 임피던스
									//05-29 14:17:49.329 I 14920    14920    M20_UsbReceiver:                       Receive B28;err2;26.3;N
									if(data1.compareTo("err2")==0){
										Log.e(TAG, "Received error from Generator");
										((PersonCheckupActivity)mActivity).bodyFatreceived("0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0");
									}else{
										String data2 = array[2]; // FFM
										String data3 = array[3]; // 체지방량
										String data4 = array[4]; // 근육량
										String data5 = array[5]; // 체수분량
										String data6 = array[6]; // 체수분량_최소
										String data7 = array[7]; // 체수분량_최대
										String data8 = array[8]; // 단백질량
										String data9 = array[9]; // 단백질량_최소
										String data10 = array[10]; // 단백질량_최대
										String data11 = array[11]; // 무기질량
										String data12 = array[12]; // 무기질_최소
										String data13 = array[13]; // 무기질_최대
										String data14 = array[14]; // 체지방 조절량 (음수, 양수)
										String data15 = array[15]; // 근육 조절량 (음수, 양수)
										((PersonCheckupActivity)mActivity).bodyFatreceived(data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15);
									}
								}
								else if (key.equals("S11")) {
									// 기기 ID & Password
									String ID = array[1]; // ID
									String PW = array[2]; // Password
//									Log.d(TAG, "J.Y.T RegActivity  array[1]: "+array[1]);
//									Log.d(TAG, "J.Y.T RegActivity  array[2]: "+array[2]);
									((RegActivity) mActivity).setIDPW(ID, PW);
								}
								else if (key.equals("C67")) {
									// Connector 이상 유무
									String Connector_Check = array[1];
									((IntroActivity) mActivity).setConnectCheck(Connector_Check);
								}
								else if (key.equals("S98")) {
									// UART 연결 확인 결과
									String UART_Check = array[1];
									Log.i(TAG, "S98 received( " + UART_Check + " )" );
								}
								else if (key.equals("C60")) {
									// 운동 시작 후 리모컨으로 전체 강도를 바꿀 수 있다
									String allValueChange = array[1];
									//showErrorMsgTest(allValueChange);
									String now_activity = mActivity.toString();
									if (now_activity.contains("DetailStartActivity"))
										((DetailStartActivity) mActivity).setallValueChange(allValueChange);
								}
								else if (key.equals("C66")) {
									// 운동 시작 후 리모컨으로 멈춤
									String now_activity = mActivity.toString();
									if (now_activity.contains("DetailStartActivity"))
										((DetailStartActivity) mActivity).setRemotePause();
								}
								else if (key.equals("S99")) {
									// 1: 정상, 2: 사용중, 3: 대기중, 4: 리눅스통신 Error, 5: AVR 통신 Error
									// 6: 슈트 이상, 7: Channel 이상, 8: Connector: 이상, 9~00: Reserved
									String errorCode = array[1];
									showErrorMsg(errorCode);
									//showErrorMsg("01");
								}
							}
						});
					}
				}

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (mStop) {
					mRunningMainLoop = false;
					return;
				}
			}
		}
	};
	private void showErrorMsg(String errorCode){
		// 1: 정상, 2: 사용중, 3: 대기중, 4: 리눅스통신 Error, 5: AVR 통신 Error
		// 6: 슈트 이상, 7: Channel 이상, 8: Connector: 이상, 9~00: Reserved
		//Toast toast;
		String errMsg=null;
		switch (errorCode) {
			case "01":
				//toast = Toast.makeText(mContext, "정상", Toast.LENGTH_SHORT);
				errMsg = "정상";
				break;
			case "02":
				//toast = Toast.makeText(mContext, "사용중", Toast.LENGTH_SHORT);
				errMsg = "사용중";
				break;
			case "03":
				//toast = Toast.makeText(mContext, "대기중", Toast.LENGTH_SHORT);
				errMsg = "대기중";
				break;
			case "04":
				//toast = Toast.makeText(mContext, "리눅스 통신 Error", Toast.LENGTH_SHORT);
				errMsg = "리눅스 통신 Error";
				break;
			case "05":
				//toast = Toast.makeText(mContext, "AVR 통신 Error", Toast.LENGTH_SHORT);
				errMsg = "AVR 통신 Error";
				break;
			case "06":
				//toast = Toast.makeText(mContext, "슈트 이상", Toast.LENGTH_SHORT);
				errMsg = "슈트 이상";
				break;
			case "07":
				//toast = Toast.makeText(mContext, "Channel 이상", Toast.LENGTH_SHORT);
				errMsg = "Channel 이상";
				break;
			case "08":
				//toast = Toast.makeText(mContext, "Connect 이상", Toast.LENGTH_SHORT);
				errMsg = "Connect 이상";
				break;
			default:
				//toast = Toast.makeText(mContext, "Reserverd", Toast.LENGTH_SHORT);
				errMsg = "Reserverd";
				break;
		}
//		ViewGroup group = (ViewGroup) toast.getView();
//		TextView messageTextView = (TextView) group.getChildAt(0);
//		messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
//		toast.show();
		if(mActivity!=null) {
			Intent i = new Intent(mActivity, MainActivity.class);
			i.putExtra("error", errMsg);
			i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mActivity.startActivity(i);
		}
	}

	private void showErrorMsgTest(String errorCode){
		// 1: 정상, 2: 사용중, 3: 대기중, 4: 리눅스통신 Error, 5: AVR 통신 Error
		// 6: 슈트 이상, 7: Channel 이상, 8: Connector: 이상, 9~00: Reserved

		String errMsg=null;
		switch (errorCode) {
			case "001":
				errMsg = "정상";
				break;
			case "002":
				errMsg = "사용중";
				break;
			case "003":
				errMsg = "대기중";
				break;
			case "004":
				errMsg = "리눅스 통신 Error";
				break;
			case "005":
				errMsg = "AVR 통신 Error";
				break;
			case "006":
				errMsg = "슈트 이상";
				break;
			case "007":
				errMsg = "Channel 이상";
				break;
			case "008":
				errMsg = "Connect 이상";
				break;
			default:
				errMsg = "Reserverd";
				break;
		}

		if(mActivity!=null) {
			Intent i = new Intent(mActivity, MainActivity.class);
			i.putExtra("error", errMsg);
			i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mActivity.startActivity(i);
		}
	}

	private void OnReadMessage(byte[] rbuf, int len)
	{
		switch (mDisplayType) {
		case FTDriverUtil.DISP_CHAR:
			FTDriverUtil.setSerialDataToTextView(mText, mDisplayType, rbuf, len, "", "");
			break;
		case FTDriverUtil.DISP_DEC:
			FTDriverUtil.setSerialDataToTextView(mText, mDisplayType, rbuf, len, "013", "010");
			break;
		case FTDriverUtil.DISP_HEX:
			FTDriverUtil.setSerialDataToTextView(mText, mDisplayType, rbuf, len, "0d", "0a");
			break;
		}
		
		if (SHOW_DEBUG) {
			Log.d(TAG, "Read Length : " + len +"/" +mText);
		}
	}

	private void detachedUi() {
		if(mSerial.isConnected()) {
//			Toast.makeText(mContext, "disconnect", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Connected.");
		}
	}

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        int mBaudrate;

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            boolean beginStatus = false;
			if (SHOW_DEBUG) {
				Log.d(TAG, "Device attached");
				Toast.makeText(mContext, "Device attached", Toast.LENGTH_SHORT).show();
			}
			if (!mSerial.isConnected()) {
				if (SHOW_DEBUG) {
					Log.d(TAG, "Device attached begin");
					Toast.makeText(mContext, "Device attached begin", Toast.LENGTH_SHORT).show();
				}
				mBaudrate = loadDefaultBaudrate();
                beginStatus = mSerial.begin(mBaudrate);
				loadDefaultSettingValues();
			}else {
			    beginStatus = true;
            }

			if (beginStatus && !mRunningMainLoop) {
				if (SHOW_DEBUG) {
					Log.d(TAG, "Device attached mainloop");
					Toast.makeText(mContext, "Device attached mainloop", Toast.LENGTH_SHORT).show();
				}
				mainloop();
			}
		} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "Device detached");
				Toast.makeText(mContext, "Device detached", Toast.LENGTH_SHORT).show();
			}
			mStop = true;
			detachedUi();
			mSerial.usbDetached(intent);
			mSerial.end();
		} else if (ACTION_USB_PERMISSION.equals(action)) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "Request permission");
				Toast.makeText(mContext, "Request permission", Toast.LENGTH_SHORT).show();
			}
			synchronized (this) {
				if (!mSerial.isConnected()) {
					if (SHOW_DEBUG) {
						Log.d(TAG, "Request permission begin");
						Toast.makeText(mContext, "Request permission begin", Toast.LENGTH_SHORT).show();
					}
					mBaudrate = loadDefaultBaudrate();
					mSerial.begin(mBaudrate);
					loadDefaultSettingValues();
				}
			}
			if (!mRunningMainLoop) {
				if (SHOW_DEBUG) {
					Log.d(TAG, "Request permission mainloop");
					Toast.makeText(mContext, "Request permission mainloop", Toast.LENGTH_SHORT).show();
				}
				mainloop();
			}
		}
	}
}
