package com.taskpal.taskpal;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public NotificationCompat.Builder mBuilder;
    public NotificationManager notificationManager;
    private Button addTaskButton;
    private ImageButton sunButton;
    private Button menuButton;
    private TextView nameText;
    private TextView monthText2;
    private TextView dayText;
    private TextView timeText1;
    private TextView timeText2;
    private TextView timeText3;
    private TextView timeText4;
    private TextView timeText5;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        notificationsSetup();
        addTaskButton = findViewById(R.id.addTaskButton);
        sunButton = findViewById(R.id.sunButton);
        menuButton = findViewById(R.id.menuButton);
        nameText = findViewById(R.id.nameText);
        monthText2 = findViewById(R.id.monthText2);
        dayText = findViewById(R.id.dayText);
        timeText1 = findViewById(R.id.timeText1);
        timeText2 = findViewById(R.id.timeText2);
        timeText3 = findViewById(R.id.timeText3);
        timeText4 = findViewById(R.id.timeText4);
        timeText5 = findViewById(R.id.timeText5);
        drawerLayout = findViewById(R.id.drawer_layout);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        monthText2.setText(dateTime);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd");
        Date date2 = new Date();
        String dateTime2 = dateFormat2.format(date2);
        dayText.setText(dateTime2);

        nameText.setText(PreferencesActivity.NAMETEXT);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();

                        //Add code here to change the UI based on the selected input
                        switch (menuItem.getItemId()) {
                            case R.id.nav_add_task:
                                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
                                return true;
                            case R.id.nav_preferences:
                                startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
                                return true;
                            case R.id.nav_calendar:
                                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                                return true;
                            default:
                                return true;
                        }
                    }
                }
        );

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.notify(1, mBuilder.build());
                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
            }
        });

        sunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.END);
            }
        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                ){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    123);
        }
    }
    @TargetApi(26)
    public void notificationsSetup() {
        // Set up notification channel
        CharSequence name = "check-ins";
        String description = "Check in with user after each task";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("check_ins_id", name, importance);
        channel.setDescription(description);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        // Create an explicit intent for notification
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(this, "check_ins_id")
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Task Check-in")
                .setContentText("Did you complete TASK_NAME_HERE?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }
}
