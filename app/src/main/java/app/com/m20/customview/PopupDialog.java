package app.com.m20.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import app.com.m20.R;
import app.com.m20.utils.Utils;
import butterknife.ButterKnife;

/**
 * Created by kimyongyeon on 2017-11-22.
 */

public class PopupDialog extends Dialog {

    TextView btn_modify_done1;
    TextView btn_modify_done2;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public PopupDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* when using popupDialog.show()
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        */
        setContentView(R.layout.popup_dialog_box);

        /* when using popupDialog.show()
        View decorView;
        int uiOption;
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);
        */

        // 다이얼로그 외부 화면 흐리게 표현
//        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
//        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        lpWindow.dimAmount = 0.8f;
//        getWindow().setAttributes(lpWindow);

        btn_modify_done1 = findViewById(R.id.btn_modify_done1);
        btn_modify_done2 = findViewById(R.id.btn_modify_done2);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            btn_modify_done1.setOnClickListener(mLeftClickListener);
            btn_modify_done2.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            btn_modify_done1.setOnClickListener(mLeftClickListener);
        } else {

        }

        // when using popupDialog.showImmersive()
        // Temporarily set the dialogs window to not focusable to prevent the short
        // popup of the navigation bar.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public PopupDialog(Context context, String title,
                        View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public PopupDialog(Context context, String title,
                        String content, View.OnClickListener leftListener,
                        View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    public void showImmersive(Activity activity) {

        // Show the dialog.
        show();

        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        //getWindow().executePendingTransactions();

        // Hide the navigation bar. It is important to do this after show() was called.
        // If we would do this in onCreateDialog(), we would get a requestFeature()
        // error.
        getWindow().getDecorView().setSystemUiVisibility(
                activity.getWindow().getDecorView().getSystemUiVisibility()
        );

        // Make the dialogs window focusable again.
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        );

    }

}


