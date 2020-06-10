/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.bdmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import androidx.appcompat.app.AlertDialog;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRouteGuideManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBNaviListener;
import com.baidu.navisdk.adapter.map.BNItemizedOverlay;
import com.baidu.navisdk.adapter.map.BNOverlayItem;
import com.baidu.navisdk.adapter.struct.BNHighwayInfo;
import com.baidu.navisdk.adapter.struct.BNRoadCondition;
import com.baidu.navisdk.adapter.struct.BNaviInfo;
import com.baidu.navisdk.adapter.struct.BNaviLocation;
import com.baidu.navisdk.ui.routeguide.model.RGLineItem;
import com.example.bdmap.baidudemo.EventHandler;
import com.example.bdmap.R;

import java.util.List;


/**
 * 诱导界面
 */
public class DemoGuideActivity extends Activity {

    private static final String TAG = DemoGuideActivity.class.getName();

    private IBNRouteGuideManager mRouteGuideManager;
    private List<BNRoadCondition>bnRoadConditions;

    private IBNaviListener.DayNightMode mMode = IBNaviListener.DayNightMode.DAY;
    private boolean is_show_message=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean fullScreen = supportFullScreen();

        Bundle params = new Bundle();
        // todo 常量
        params.putBoolean(BNaviCommonParams.ProGuideKey.IS_SUPPORT_FULL_SCREEN, fullScreen);
        mRouteGuideManager = BaiduNaviManagerFactory.getRouteGuideManager();
        View view = mRouteGuideManager.onCreate(this, mOnNavigationListener,
                null, params);

        if (view != null) {
            setContentView(view);
        }

        BaiduNaviManagerFactory.getProfessionalNaviSettingManager().setShowMainAuxiliaryOrBridge(true);

        initTTSListener();
        routeGuideEvent();
        mRouteGuideManager.setNaviListener(new IBNaviListener() {
            @Override
            public void onRoadNameUpdate(String s) {


            }

            @Override
            public void onRemainInfoUpdate(int i, int i1) {

            }

            @Override
            public void onGuideInfoUpdate(BNaviInfo bNaviInfo) {

            }

            @Override
            public void onHighWayInfoUpdate(Action action, BNHighwayInfo bnHighwayInfo) {

            }

            @Override
            public void onFastExitWayInfoUpdate(Action action, String s, int i, String s1) {

            }

            @Override
            public void onEnlargeMapUpdate(Action action, View view, String s, int i, String s1, Bitmap bitmap) {

            }

            @Override
            public void onDayNightChanged(DayNightMode dayNightMode) {

            }

            @Override
            public void onRoadConditionInfoUpdate(double v, List<BNRoadCondition> list) {
                bnRoadConditions=list;
                if(bnRoadConditions==null)
                System.out.printf("无路况信息");
                else
                {
                        int i=0,low = 0, mid = 0, high = 0, fast = 0;
                        int j = bnRoadConditions.size();
                        for (i = 0; i < j; i++) {
                            int m = bnRoadConditions.get(i).getType();
                            if (m == 2) {
                                low++;
                            } else if (m == 3) {
                                mid++;
                            } else if (m == 4) {
                                high++;
                            } else {
                                fast++;
                            }

                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(DemoGuideActivity.this);
                        builder.setTitle("路况信息");
                        builder.setMessage("轻度拥堵路段：" + low + "\n中度拥堵路段：" + mid + "\n重度拥堵路段：" + high);
                        builder.setPositiveButton("确定", null);
                        builder.show();
                }


            }

            @Override
            public void onMainSideBridgeUpdate(int i) {

            }

            @Override
            public void onLaneInfoUpdate(Action action, List<RGLineItem> list) {

            }

            @Override
            public void onSpeedUpdate(String s, boolean b) {

            }

            @Override
            public void onArriveDestination() {

            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            @Override
            public void onLocationChange(BNaviLocation bNaviLocation) {

            }
        });
    }

    // 导航过程事件监听
    private void routeGuideEvent() {
       // EventHandler.getInstance().getDialog(this);
        //EventHandler.getInstance().showDialog();
        BaiduNaviManagerFactory.getRouteGuideManager().setRouteGuideEventListener(
                new IBNRouteGuideManager.IRouteGuideEventListener() {
                    @Override
                    public void onCommonEventCall(int what, int arg1, int arg2, Bundle bundle) {
                        EventHandler.getInstance().handleNaviEvent(what, arg1, arg2, bundle);
                    }
                }
        );
    }

    private void initTTSListener() {
        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayError");
                    }
                }
        );

        // 注册内置tts 异步状态消息
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("BNSDKDemo", "ttsHandler.msg.what=" + msg.what);
                    }
                }
        );
    }

    private void uninitTTSListener() {
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(null);
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(null);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRouteGuideManager.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mRouteGuideManager.onResume();
        // 自定义图层
        showOverlay();
    }

    private void showOverlay() {
        BNOverlayItem item =
                new BNOverlayItem(2563047.686035, 1.2695675172607E7, BNOverlayItem.CoordinateType
                        .BD09_MC);
        BNItemizedOverlay overlay = new BNItemizedOverlay(
                DemoGuideActivity.this.getResources().getDrawable(R.mipmap
                        .navi_guide_turn));
        overlay.addItem(item);
        overlay.show();
    }

    protected void onPause() {
        super.onPause();
        mRouteGuideManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRouteGuideManager.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRouteGuideManager.onDestroy(false);
        uninitTTSListener();
        EventHandler.getInstance().disposeDialog();
        mRouteGuideManager = null;
    }

    @Override
    public void onBackPressed() {
        mRouteGuideManager.onBackPressed(false, true);
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRouteGuideManager.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (!mRouteGuideManager.onKeyDown(keyCode, event)) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    private static final int MSG_RESET_NODE = 3;

    private Handler hd = null;

    private void createHandler() {
        if (hd == null) {
            hd = new Handler(getMainLooper()) {
                public void handleMessage(Message msg) {
                    if (msg.what == MSG_RESET_NODE) {
                        mRouteGuideManager.resetEndNodeInNavi(new BNRoutePlanNode.Builder()
                                .latitude(40.85087).longitude(116.21142).name("百度大厦")
                                .coordinateType(CoordinateType.GCJ02).build());
                    }
                }
            };
        }
    }

    private IBNRouteGuideManager.OnNavigationListener mOnNavigationListener =
            new IBNRouteGuideManager.OnNavigationListener() {

                @Override
                public void onNaviGuideEnd() {
                    // 退出导航
                    finish();
                }

                @Override
                public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
                    if (actionType == 0) {
                        // 导航到达目的地 自动退出
                        Log.i(TAG, "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");
                        mRouteGuideManager.forceQuitNaviWithoutDialog();
                    }
                }

            };

    private boolean supportFullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            int color;
            if (Build.VERSION.SDK_INT >= 23) {
                color = Color.TRANSPARENT;
            } else {
                color = 0x2d000000;
            }
            window.setStatusBarColor(color);

            if (Build.VERSION.SDK_INT >= 23) {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                int uiVisibility = window.getDecorView().getSystemUiVisibility();
                if (mMode == IBNaviListener.DayNightMode.DAY) {
                    uiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                window.getDecorView().setSystemUiVisibility(uiVisibility);
            } else {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mRouteGuideManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRouteGuideManager.onActivityResult(requestCode, resultCode, data);
    }
}