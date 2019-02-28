package com.wyk.ioc_sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wyk.ioc_annotation.BindView
import com.wyk.ioc_api.ViewInjector

class MainActivity: AppCompatActivity() {

    @BindView(R.id.tv_inject)
    lateinit var mTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewInjector.injectView(this)
        mTv.text = "税道"

        val list = mutableListOf<String>()
        val map = mutableMapOf<Int, String>()
        val i = 2
        for((i,value) in list.withIndex()){}
        for((i,value) in map.iterator()){ }
        for(i in 0..10 step 2){}
        for(i in 10 downTo 0){}
        for(i in 0 until 10){}



    }
}
