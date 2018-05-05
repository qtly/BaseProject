package com.cg.baseproject.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cg.baseproject.R;
import com.cg.baseproject.manager.ActivityStackManager;
import com.cg.baseproject.manager.ScreenManager;

import java.security.KeyStore;

/**
 * @author sam
 * @version 1.0
 * @date 2018/5/4
 * https://blog.csdn.net/xx244488877/article/details/65937778
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected LinearLayout badnetworkLayout,
            loadingLayout;
    protected LayoutInflater inflater;
    /**
     * 是否沉浸状态栏
     **/
    private boolean isStatusBar = true;
    /**
     * 是否允许全屏
     **/
    private boolean isFullScreen = true;
    /**
     * 是否禁止旋转屏幕
     **/
    private boolean isScreenRoate = false;

    /**
     * context
     **/
    protected Context ctx;
    /**
     * 是否输出日志信息
     **/
    private boolean isDebug;

    private boolean isBackExit = true;

    //布局中Fragment的ID
    protected abstract int getFragmentContentId();


    /**
     * 初始化界面
     **/
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定事件
     */
    protected abstract void setEvent();

    private ScreenManager screenManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--->onCreate()");
        setContentView(R.layout.activity_base);
        initView();
        initBaseActivityView(true);
        inflater = LayoutInflater.from(this);
        initData();
        setEvent();
        ctx = this;
        ActivityStackManager.getActivityStackManager().pushActivity(this);
        screenManager = ScreenManager.getInstance();
        screenManager.setStatusBar(isStatusBar, this);
        screenManager.setScreenRoate(isScreenRoate, this);
        screenManager.setFullScreen(isFullScreen, this);
    }

    private void initBaseActivityView(boolean isShow){
        badnetworkLayout = (LinearLayout) findViewById(R.id.baseactivity_badnetworkLayout);
        loadingLayout = (LinearLayout) findViewById(R.id.baseactivity_loadingLayout);
    }
    /**
     * 跳转Activity
     * skip Another Activity
     *
     * @param activity
     * @param cls
     */
    public static void skipAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 退出应用
     * called while exit app.
     */
    public void exitLogic() {
        ActivityStackManager.getActivityStackManager().popAllActivity();//remove all activity.
        System.exit(0);//system exit.
    }
    
    
    /**
     * [是否设置沉浸状态栏]
     * @param statusBar
     */
    public void setStatusBar(boolean statusBar) {
        isStatusBar = statusBar;
    }

    /**
     * [是否设置全屏]
     * @param fullScreen
     */
    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    /**
     * [是否设置旋转屏幕]
     * @param screenRoate
     */
    public void setScreenRoate(boolean screenRoate) {
        isScreenRoate = screenRoate;
    }

    /**
     * [是否连续两次返回退出]
     *
     */
    public void setBackExit(boolean isBackExit) {
        this.isBackExit = isBackExit;
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isBackExit) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * 显示加载前的动画
     */
    protected void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
        badnetworkLayout.setVisibility(View.GONE);
        // ImageView loading_imageView = (ImageView) loadingLayout
        // .findViewById(R.id.loading_imageView);
        // loading_imageView.setBackgroundResource(R.anim.loading);
        // AnimationDrawable animationDrawable = (AnimationDrawable)
        // loading_imageView
        // .getBackground();
        // animationDrawable.start();
    }

    /*
     * 加载失败或没网络
     */
    protected void showBadnetworkLayout() {
        loadingLayout.setVisibility(View.GONE);
        badnetworkLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 加载完
     */
    protected void showContextLayout() {
        loadingLayout.setVisibility(View.GONE);
        badnetworkLayout.setVisibility(View.GONE);
    }

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "--->onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "--->onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "--->onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "--->onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "--->onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--->onDestroy()");
        ActivityStackManager.getActivityStackManager().popActivity(this);
    }
}
