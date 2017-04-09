package com.project.crosswalkproject;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebView1Activity extends AppCompatActivity {

    @BindView(R.id.xwalkWebView)
    XWalkView xwalkWebView;
    @BindView(R.id.activity_web_view1)
    RelativeLayout activityWebView1;
    private String TAG="WebView1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
        setContentView(R.layout.activity_web_view1);
        ButterKnife.bind(this);
//        //加载本地网页
//        xwalkWebView.load("file:///android_asset/webpage/jsToJava.html",null);
        initView();//初始化组件

    }

    /**
     * 初始化组件
     */
    private void initView() {
        //设置远程调试
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING,true);
        //加载网页
        xwalkWebView.load("http://www.baidu.com",null);


        xwalkWebView.setResourceClient(new XWalkResourceClient(xwalkWebView){
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
                Log.i(TAG,"开始加载网页："+url);
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                Log.i(TAG,"结束加载网页："+url);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                Log.i(TAG,"结束加载网页进度："+progressInPercent);
            }

            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        xwalkWebView.setUIClient(new XWalkUIClient(xwalkWebView){
            @Override
            public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                super.openFileChooser(view, uploadFile, acceptType, capture);
            }
        });
    }






    @Override
    protected void onResume() {
        super.onResume();
        if(xwalkWebView!=null){
            xwalkWebView.resumeTimers();
            xwalkWebView.onShow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(xwalkWebView!=null){
            xwalkWebView.pauseTimers();
            xwalkWebView.onHide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(xwalkWebView!=null){
            xwalkWebView.onDestroy();
        }
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, false);
    }
}
