package com.taskpal.taskpal;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.joda.time.DateMidnight;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.taskpal.taskpal.CalendarActivity.CALENDAR_EVENTS;
import static com.taskpal.taskpal.CalendarActivity.REQUEST_ACCOUNT_PICKER;
import static com.taskpal.taskpal.CalendarActivity.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.taskpal.taskpal.CalendarActivity.REQUEST_PERMISSION_GET_ACCOUNTS;

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
    private View line1;
    private View line2;
    private View line3;
    private View line4;
    private List<TextView> timeTexts;
    private List<View> lines;
    private DrawerLayout drawerLayout;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CALENDAR_EVENTS, CalendarScopes.CALENDAR};
    private com.google.api.services.calendar.Calendar service = null;
    private static final String PREF_ACCOUNT_NAME = "accountName";

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
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);
        drawerLayout = findViewById(R.id.drawer_layout);

        timeTexts = new ArrayList<>();
        timeTexts.add(timeText1);
        timeTexts.add(timeText2);
        timeTexts.add(timeText3);
        timeTexts.add(timeText4);
        timeTexts.add(timeText5);

        lines = new ArrayList<>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);

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
                                long startMillis2 = System.currentTimeMillis();
                                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                                builder.appendPath("time");
                                ContentUris.appendId(builder, startMillis2);
                                Intent intent = new Intent(Intent.ACTION_VIEW)
                                        .setData(builder.build());
                                startActivity(intent);
                                return true;
                            case R.id.nav_tutorial:
                                intent = new Intent(MainActivity.this, IntroActivity.class);
                                intent.putExtra("flag", "A");
                                startActivity(intent);
                            default:
                                return true;
                        }
                    }
                }
        );

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notificationManager.notify(1, mBuilder.build());
                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
            }
        });

        sunButton.setOnClickListener(new View.OnClickListener() {
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

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.END);
            }
        });

        //initiates google login
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //gets next five tasks within a 24 hour period
        getResultsFromApi();
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

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            try {
                chooseAccount();
            } catch (Exception e) {}
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        }
        try {
            Log.d("Insert", "Sets up service");
            //this will try to retrieve the next 5 events. it calls getDataFromAPI at the end
            new MainActivity.MakeRequestTask(mCredential).execute();
        } catch (Exception e) {
            //if anything fails, hide the Up Next section
            populateList(new ArrayList<Event>());
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            Log.d("Insert", "HasPermissions");
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                Log.d("Insert", "HasName");
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                Log.d("Insert", "NoName");
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            Log.d("Insert", "RequestPermissions");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Exception mLastError = null;
        private boolean FLAG = false;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("TaskPal")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                populateList(new ArrayList<Event>());
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    Log.d("Auth", "AuthIO");
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            CalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    //mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }

    /**
     * Fetch a list of the next 5 events from the primary calendar, within 24 hours of the current time.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private void getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime tfh = new DateTime(System.currentTimeMillis() + 86400000);
        List<String> eventStrings = new ArrayList<String>();
        Events events = service.events().list("primary")
                .setMaxResults(5)
                .setTimeMin(now)
                .setTimeMax(tfh)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        populateList(items);
    }

    private void populateList(List<Event> events) {
        for (int i = 0; i < 5; i++) {
            TextView text = timeTexts.get(i);
            if (i < events.size()) {
                //handle event
                Event event = events.get(i);
                String name = event.getSummary();
                if (name.length() > 12) {
                    name = name.substring(0, 11) + "...";
                }
                String start = event.getStart().getDateTime().toStringRfc3339().substring(11,16);
                String txt = start + "   " + name;
                text.setText(txt);
            } else {
                //display none
                text.setVisibility(View.GONE);
                if (i > 0) {
                    View line = lines.get(i - 1);
                    line.setVisibility(View.GONE);
                }
            }
        }
    }
}
