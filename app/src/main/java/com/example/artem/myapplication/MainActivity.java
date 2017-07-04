package com.example.artem.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.artem.myapplication.control.imp.Container;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Container cafe = (Container)findViewById(R.id.cPlace);
    }
}
