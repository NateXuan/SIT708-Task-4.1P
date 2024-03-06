package com.example.sit708_task_4_1p;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
//import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText editTextWorkoutDuration, editTextRestDuration;
    private Button startButton, stopButton;
    private TextView timerTextView;
    private ProgressBar timerProgressBar;
    private CountDownTimer countDownTimer;
    private boolean isWorkoutTime = true;
    private long timeLeftInMillis; // time remaining
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }

        editTextWorkoutDuration = findViewById(R.id.editTextWorkoutDuration);
        editTextRestDuration = findViewById(R.id.editTextRestDuration);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        timerTextView = findViewById(R.id.timerTextView);
        timerProgressBar = findViewById(R.id.timerProgressBar);

        startButton.setOnClickListener(v -> {
            long workoutDuration = Long.parseLong(editTextWorkoutDuration.getText().toString()) * 1000;
            isWorkoutTime = true;
            startTimer(workoutDuration);
        });

        stopButton.setOnClickListener(v -> {
            stopTimer();
            showNotification("Workout Stopped", "You've stopped the session.");
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//
//            }
//        }
//    }

    private void startTimer(long duration) {
        timeLeftInMillis = duration;
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                updateProgressBar(duration);
            }

            @Override
            public void onFinish() {
                if (isWorkoutTime) {
                    long restDuration = Long.parseLong(editTextRestDuration.getText().toString()) * 1000;
                    isWorkoutTime = false;
                    startTimer(restDuration);
                } else {
                    showNotification("Workout Complete", "Good job! You've finished your session.");
                    isWorkoutTime = true; // Reset for the next start
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void updateProgressBar(long totalTime) {
        int progress = (int) (100 * (totalTime - timeLeftInMillis) / totalTime);
        timerProgressBar.setProgress(progress);
    }

    private void showNotification(String title, String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "workout_timer_channel";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence channelName = "Workout Timer Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }
}
