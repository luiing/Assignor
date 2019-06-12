package com.uis.conn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.uis.connector.demo.R;


public class MainUi extends AppCompatActivity implements View.OnClickListener {

    String TAG = "UI"+System.currentTimeMillis();
    Button btActionA,btActionB,btCacheA,btCacheB,btMultith,btStart;
    TextView tvContent;


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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.bt_action_a == id){
            Intent it = new Intent(this,DemoUi.class);
            startActivity(it);
        }else if(R.id.bt_action_b == id){

            Intent it = new Intent(this,MainUi.class);
            startActivity(it);
        }else if(R.id.bt_action_c == id){

        }else if(R.id.bt_cache_a == id){

        }else if(R.id.bt_cache_b == id){

        }else if(R.id.bt_multith == id){

        }else if(R.id.bt_start == id){
            startActivity(new Intent(this,MainUi.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
