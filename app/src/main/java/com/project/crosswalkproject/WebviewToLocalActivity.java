package com.project.crosswalkproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 加载本地网页，并进行js交互
 */
public class WebviewToLocalActivity extends AppCompatActivity {

    @BindView(R.id.webview_browser_interaction)
    XWalkView xwalkWebView;
    @BindView(R.id.et_data)
    EditText etData;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.bt_jsjava_num_add)
    Button btJsjavaNumAdd;
    @BindView(R.id.bt_jsjava_num_minuse)
    Button btJsjavaNumMinuse;
    @BindView(R.id.text_jsjava_num)
    TextView tvData;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.activity_browser_interaction)
    LinearLayout activityBrowserInteraction;

    private String TAG = "WebView1Activity";
    private String result;
    private int num;
    private XWalkSettings mMSettings;

    @OnClick({R.id.btn_send, R.id.bt_jsjava_num_add, R.id.bt_jsjava_num_minuse, R.id.btn_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send://android传递数据给js
                result=etData.getText().toString();
                if(!TextUtils.isEmpty(result)){
                    xwalkWebView.load("javascript:returnMsg",null);
                }
                break;
            case R.id.bt_jsjava_num_add:
                int data=Integer.parseInt(tvData.getText().toString());
                num=(--data);
                tvData.setText(num+"");
                xwalkWebView.load("javascript:returnNum()",null);
                break;
            case R.id.bt_jsjava_num_minuse:
                int data1=Integer.parseInt(tvData.getText().toString());
                num=(--data1);
                tvData.setText(num+"");
                xwalkWebView.load("javascript:returnNum()",null);
                break;
            case R.id.btn_close:
                xwalkWebView.load("javascript:closeWnd()",null);;
                Log.i(TAG,"关闭界面");
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置动画
        // ANIMATABLE_XWALK_VIEW preference key MUST be set before XWalkView creation.
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);
        setContentView(R.layout.activity_webview_to_local);
        ButterKnife.bind(this);
        initView();//初始化组件

    }

    /**
     * 初始化组件
     */
    private void initView() {
        //获取setting

        mMSettings = xwalkWebView.getSettings();
       //支持空间导航
        mMSettings.setSupportSpatialNavigation(true);
        mMSettings.setBuiltInZoomControls(false);
        mMSettings.setSupportZoom(false);
        mMSettings.setJavaScriptEnabled(true);
//        xwalkWebView.setDrawingCacheEnabled(false);//不使用缓存
//        xwalkWebView.getNavigationHistory().clear();//清除历史记录
//        xwalkWebView.clearCache(true);//清楚包括磁盘缓存
        //设置远程调试
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        //支持javascript
        XWalkPreferences.setValue("enable-javascript", true);
        //添加javasxript接口
        xwalkWebView.addJavascriptInterface(new JsInterface(),"JsInterface");

        //加载本地网页
        xwalkWebView.load("file:///android_asset/webpage/jsToJava.html", null);

        xwalkWebView.setResourceClient(new XWalkResourceClient(xwalkWebView) {
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
                Log.i(TAG, "开始加载网页：" + url);
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                Log.i(TAG, "结束加载网页：" + url);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                Log.i(TAG, "结束加载网页进度：" + progressInPercent);
            }

            @Override
            public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {

                super.onReceivedLoadError(view, errorCode, description, failingUrl);
            }

            @Override
            public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
                Log.i("http", "shouldOverrideUrlLoading-url=" + url);
                return super.shouldInterceptLoadRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }


    public class JsInterface {


        /**
         * android将数据传递给js
         * @return
         */

        @JavascriptInterface
        public String getAndroidMsg() {
            return etData.getText().toString();
        }
        @JavascriptInterface
        public  String  getAndroidNum(){
            return   tvData.getText().toString();
        }
        @JavascriptInterface
        public  void  closeCurrentWindow(){
            finish();
        }

        /**
         * js传递数据非android
         */
        @JavascriptInterface
        public void onSubmit(final String s){
            Log.i("jsToAndroid","onSubmit happend!");
            //需在ui线程操作，此处为非ui线程
          runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etData.setText(s+"");
                }
            });
        }

        @JavascriptInterface
        public void onSubmitNum(final String s){
            Log.i("jsToAndroid","onSubmitNum happend!");
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvData.setText(s+"");
                }
            });
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (xwalkWebView != null) {
            xwalkWebView.resumeTimers();
            xwalkWebView.onShow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (xwalkWebView != null) {
            xwalkWebView.pauseTimers();
            xwalkWebView.onHide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xwalkWebView != null) {
            xwalkWebView.onDestroy();
        }
        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, false);
    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (xwalkWebView.getNavigationHistory().canGoBack()) {

                xwalkWebView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1); //返回上一页面

            } else {

                       finish();

            }
            return true;

        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (xwalkWebView != null) {
            xwalkWebView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (xwalkWebView != null) {
            xwalkWebView.onNewIntent(intent);
        }
    }
}
