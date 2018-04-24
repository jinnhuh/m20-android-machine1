package app.com.m20;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import app.com.m20.activity.PersonCheckupActivity;
import app.com.m20.driver.serial.FTDriver;
import app.com.m20.driver.serial.UsbReceiver;

/**
 * Created by kimyongyeon on 2017-12-08.
 */

public class Application {

    private static FTDriver mSerial;
    private static UsbReceiver mUsbReceiver;
    private static int mBaudrate;
    private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

    public UsbReceiver getUsbReceiver() {
        return this.mUsbReceiver;
    }

    public static void serialConnect(String str, Activity activity) {
        //////////////////////////////////
        // Serial
        //////////////////////////////////
        mSerial = new FTDriver((UsbManager) activity.getSystemService(Context.USB_SERVICE));
        // listen for new devices
        mUsbReceiver = new UsbReceiver(activity, mSerial);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        activity.registerReceiver(mUsbReceiver, filter);
        // load default baud rate
        mBaudrate = mUsbReceiver.loadDefaultBaudrate();
        // for requesting permission
        // setPermissionIntent() before begin()
        PendingIntent permissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mSerial.setPermissionIntent(permissionIntent);
        if (mSerial.begin(mBaudrate)) {
            mUsbReceiver.loadDefaultSettingValues();
            mUsbReceiver.mainloop();
        } else {
            Toast.makeText(activity, "no connectionApplication", Toast.LENGTH_SHORT).show();
            if(str.equals("reg")) {
                Intent intent = new Intent(activity, PersonCheckupActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
        //////////////////////////////////
        // Serial
        //////////////////////////////////
    }
}
