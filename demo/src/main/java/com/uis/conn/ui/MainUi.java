package com.uis.conn.ui;

import android.arch.lifecycle.LifecycleObserver;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.uis.conn.lifecycle.MainLifecycle;
import com.uis.conn.view.MainView;
import com.uis.conn.observer.MainAction;
import com.uis.conn.view.TestA;
import com.uis.conn.view.TestB;
import com.uis.connector.comm.Clog;
import com.uis.connector.comm.ConnPlant;
import com.uis.connector.demo.R;
import com.uis.conn.observer.MainConn;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class MainUi extends AppCompatActivity implements View.OnClickListener,MainView {

    String TAG = "UI"+System.currentTimeMillis();
    Button btActionA,btActionB,btCacheA,btCacheB,btMultith,btStart;
    TextView tvContent;
    MainAction action = new MainConn(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        btActionA = findViewById(R.id.bt_action_a);
        btActionB = findViewById(R.id.bt_action_b);
        btCacheA = findViewById(R.id.bt_cache_a);
        btCacheB = findViewById(R.id.bt_cache_b);
        tvContent = findViewById(R.id.tv_content);
        btMultith = findViewById(R.id.bt_multith);
        btStart = findViewById(R.id.bt_start);

        btActionA.setOnClickListener(this);
        btActionB.setOnClickListener(this);
        btCacheA.setOnClickListener(this);
        btCacheB.setOnClickListener(this);
        findViewById(R.id.bt_action_c).setOnClickListener(this);
        btMultith.setOnClickListener(this);
        btStart.setOnClickListener(this);
        LifecycleObserver observer = new MainLifecycle();
        getLifecycle().addObserver(observer);
        action.attachActivity(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.bt_action_a == id){
            action.actionA();
            Intent it = new Intent(this,DemoUi.class);
            startActivity(it);
        }else if(R.id.bt_action_b == id){
            action.actionB();
            Intent it = new Intent(this,MainUi.class);
            startActivity(it);
        }else if(R.id.bt_action_c == id){
            action.actionC();
        }else if(R.id.bt_cache_a == id){
            action.actionCacheA();
        }else if(R.id.bt_cache_b == id){
            action.actionCacheB();
        }else if(R.id.bt_multith == id){
            action.actionMultith();
        }else if(R.id.bt_start == id){
            //clear memory of actionb
            ConnPlant.writeCache("actionb",null);
            startActivity(new Intent(this,MainUi.class));
        }
    }

    @Override
    public void cacheA(TestA a) {
        displayContent("cache A:\n"+ (a==null ? "TestA is null":a.toString()));
    }

    @Override
    public void displayA(TestA a) {
        displayContent("display A"+a.toString());
    }

    @Override
    public void cacheC(TestA a) {
        displayContent("cache C:\n"+a.toString());
    }

    @Override
    public void displayC(TestA a) {
        displayContent("display C"+a.toString());
    }

    @Override
    public void cacheB(TestB b) {
        displayContent("cache B:\n"+ (b==null ? "TestB is null" : b.toString()));
    }

    @Override
    public void displayB(TestB b) {
        displayContent("display B"+b.toString());
    }

    @Override
    public void displayMultith(String content) {
        displayContent(content);
    }

    private void displayContent(String content){
        if(tvContent!=null){
            tvContent.setText(content);
        }
        Clog.print(content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Clog.printStack(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Clog.printStack(TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Clog.printStack(TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Clog.printStack(TAG);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Clog.printStack(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clog.printStack(TAG);
    }
}
