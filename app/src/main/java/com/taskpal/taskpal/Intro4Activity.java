package com.taskpal.taskpal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Intro4Activity extends AppCompatActivity {

    private ImageButton next;
    private ImageButton prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro4);
        next = findViewById(R.id.next4);
        prev = findViewById(R.id.previous4);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intro4Activity.this, Intro5Activity.class));
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intro4Activity.this, Intro3Activity.class));
            }
        });
    }
}
