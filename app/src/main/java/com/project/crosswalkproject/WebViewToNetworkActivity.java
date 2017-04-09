package com.project.crosswalkproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewToNetworkActivity extends AppCompatActivity {

    @BindView(R.id.mXWalkView)
    XWalkView mXWalkView;
    @BindView(R.id.activity_web_view_to_network)
    LinearLayout activityWebViewToNetwork;
    private String url="http://120.76.222.220/h5/creatingActivities.html?id=978&userAgent=0";
    private String TAG="WebViewToNetwork";
    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri> mUploadMessage;
    private XWalkSettings mMSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_to_network);
        ButterKnife.bind(this);
        //初始化视图
        initView();
    }

    /**
     * 初始化视图
     */
    public void initView() {
        //获取setting

        mMSettings = mXWalkView.getSettings();
        //支持空间导航
        mMSettings.setSupportSpatialNavigation(true);
        mMSettings.setBuiltInZoomControls(false);
        mMSettings.setSupportZoom(false);
        mMSettings.setJavaScriptEnabled(true);
        //设置远程调试
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        //支持javascript
        XWalkPreferences.setValue("enable-javascript", true);
        //置是否允许通过file url加载的Javascript可以访问其他的源,包括其他的文件和http,https等其他的源XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
       //JAVASCRIPT_CAN_OPEN_WINDOW=
        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
       // enable multiple windows.
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
        //支持javascript
        XWalkPreferences.setValue("enable-javascript", true);
        //添加javasxript接口
        mXWalkView.addJavascriptInterface(new InJavaScript(),"JsInterface");
        //加载网页
        mXWalkView.loadUrl(url,null);
        mXWalkView.setResourceClient(new XWalkResourceClient(mXWalkView) {
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

        mXWalkView.setUIClient(new XWalkUIClient(mXWalkView){
            @Override
            public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                super.openFileChooser(view, uploadFile, acceptType, capture);
                mUploadMessage = uploadFile;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mXWalkView != null) {
            mXWalkView.resumeTimers();
            mXWalkView.onShow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXWalkView != null) {
            mXWalkView.pauseTimers();
            mXWalkView.onHide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXWalkView != null) {
            mXWalkView.onDestroy();
        }
    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mXWalkView.getNavigationHistory().canGoBack()) {
                mXWalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1); //返回上一页面

            } else {

                       finish();

            }
            return true;

        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (mXWalkView != null) {
            mXWalkView.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null: data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }
    }
    final class InJavaScript {
        @JavascriptInterface
        public void runOnAndroid() {
            Toast.makeText(WebViewToNetworkActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXWalkView != null) {
            mXWalkView.onNewIntent(intent);
        }
    }
}
