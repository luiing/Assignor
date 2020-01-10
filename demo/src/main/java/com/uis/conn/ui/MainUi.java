package com.uis.conn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.IResult;
import com.uis.assignor.utils.ALog;
import com.uis.conn.viewmodel.MainView;
import com.uis.conn.viewmodel.MainViewModel;
import com.uis.connector.demo.R;

import org.jetbrains.annotations.NotNull;


public class MainUi extends AppCompatActivity implements View.OnClickListener, MainView {

    Button btDemo,btSelf,btRead,btWrite,btBooks,btRemoveCache;
    TextView tvContent;
    MainViewModel viewModel = new MainViewModel(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        viewModel.readMemoryBook();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        btDemo = findViewById(R.id.bt_demo);
        btSelf = findViewById(R.id.bt_self);
        btRead = findViewById(R.id.bt_read);
        btWrite = findViewById(R.id.bt_write);
        btBooks = findViewById(R.id.bt_get_books);
        btRemoveCache = findViewById(R.id.bt_remove_cache);
        tvContent = findViewById(R.id.tv_content);

        btDemo.setOnClickListener(this);
        btSelf.setOnClickListener(this);
        btRead.setOnClickListener(this);
        btWrite.setOnClickListener(this);
        btBooks.setOnClickListener(this);
        btRemoveCache.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(R.id.bt_demo == id){
            startActivity(new Intent(this,DemoUi.class));
        }else if(R.id.bt_read == id){
            viewModel.readBook();

            Couple.Result result = Couple.newParams("Test")
                    .setAction("a")
                    .addParam("key","111")
                    .call();
            ALog.e(result.toString());
            Couple.newParams("Test")
                    .setAction("b")
                    .addParam("key","222").call(new IResult() {
                @Override
                public void onResult(Couple.Result result) {
                    ALog.e(result.toString());
                }
            });

        }else if(R.id.bt_write == id){
            Couple.Result result = Couple.newParams("Assignor").call();
            ALog.e(result.toString());
            Couple.newParams("Assignor").call(new IResult() {
                @Override
                public void onResult(Couple.Result result) {
                    ALog.e(result.toString());
                }
            });

            viewModel.writeBook();
        }else if(R.id.bt_remove_cache == id){
            viewModel.removeMemoryCache();
        }else if(R.id.bt_get_books == id){
            viewModel.getBooks();
        }else if(R.id.bt_self == id){
            startActivity(new Intent(this,MainUi.class));
        }
    }

    @Override
    public void displayContent(@NotNull String content) {
        tvContent.setText(content);
    }
}
