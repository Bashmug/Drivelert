package com.example.akarshan.drivelert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cuboid.cuboidcirclebutton.CuboidButton;

import java.text.DateFormat;
import java.util.Date;

public class EndActivity extends AppCompatActivity {
    CuboidButton proceedButton;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        proceedButton = (CuboidButton) findViewById(R.id.button_proceed);
        statusTextView = (TextView) findViewById(R.id.textView_status);



        proceedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent next = new Intent(EndActivity.this, MainActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(next);
                EndActivity.this.finish();
                return false;
            }
        });

    }
}
