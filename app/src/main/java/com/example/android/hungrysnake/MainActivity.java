package com.example.android.hungrysnake;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner mSpinner = (Spinner)findViewById(R.id.difficultySpinner);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Baby");
        list.add("Teenager");
        list.add("Adult");
        list.add("Dark Souls");

        //为下拉列表定义一个适配器
        final ArrayAdapter<String> ad = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        //设置下拉菜单样式。
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //添加数据
        mSpinner.setAdapter(ad);
        //点击响应事件
        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void openSnakeGame(View view) {
        Intent i = new Intent(this, SnakeActivity.class);
        //用Bundle携带数据
        Bundle bundle=new Bundle();
        //传递name参数为tinyphp
        bundle.putString("difficulty", "tinyphp");
        i.putExtras(bundle);

        startActivity(i);
    }
}
