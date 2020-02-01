package com.example.viewdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FloatLaytoutActivity extends AppCompatActivity {

    View mView;
    FloatLayout floatLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatLayout = findViewById(R.id.layout);
        mView = findViewById(R.id.view);

        floatLayout.enableDrag(true);
        floatLayout.enableSide(true);

        floatLayout.setFinalDragOffsets(80,80,80,80);
        floatLayout.setFinalOffsets(-50);

        floatLayout.requestLayout();
        floatLayout.invalidate();


        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatLaytoutActivity.this,"view被点击了", Toast.LENGTH_LONG).show();
            }
        });
    }
}
