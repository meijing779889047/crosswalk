package com.project.crosswalkproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_webview1)
    Button btnWebview1;
       @BindView(R.id.btn_webview2)
    Button btnWebview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_webview1, R.id.btn_webview2,R.id.btn_webview3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_webview1:
                startActivity(new Intent(this,WebView1Activity.class));
                break;
            case R.id.btn_webview2:
                startActivity(new Intent(this,WebviewToLocalActivity.class));
                break;
            case R.id.btn_webview3:
                startActivity(new Intent(this,WebViewToNetworkActivity.class));
                break;
        }
    }
}
