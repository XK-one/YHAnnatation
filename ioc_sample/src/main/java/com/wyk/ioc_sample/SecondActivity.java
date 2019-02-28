package com.wyk.ioc_sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wyk.ioc_annotation.BindView;
import com.wyk.ioc_api.ViewInjector;

public class SecondActivity extends AppCompatActivity {

    @BindView(R.id.tv_inject)
    TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.Companion.injectView(this);
        mTv.setText("税道2");

    }
}
