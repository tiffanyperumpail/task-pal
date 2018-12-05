package com.taskpal.taskpal;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
    private int yesIndex = 0;
    private RadioButton yesButton;
    private RadioButton noButton;
    private TextView taskText;

    private static final String[] yesMessages = {
            "Amazing! Remember, you are so much more than just your work.",
            "Great job! You are valuable in a way that words cannot describe.",
            "Fantastic! Today is full of endless possibilities.",
            "Wonderful! Remember, you are worthy of happiness.",
            "Magnificent! Remember, no matter how difficult things get, there is an opportunity for better."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        taskText = findViewById(R.id.taskText);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = yesMessages[yesIndex % 5];
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("You did it!")
                        .setMessage(message)
                        .setIcon(R.drawable.app_logo)
                        .show();
                yesIndex++;
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startMillis2 = System.currentTimeMillis();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis2);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
            }
        });
    }
}
