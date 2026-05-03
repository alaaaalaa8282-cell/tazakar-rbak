package com.alaaeltaweel.thikrallah.Notification;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alaaeltaweel.thikrallah.MainActivity;
import com.alaaeltaweel.thikrallah.R;
import com.alaaeltaweel.thikrallah.ThikrMediaPlayerService;

public class AthanScreenActivity extends AppCompatActivity {

    private static final int AUTO_DISMISS_DELAY = 10 * 60 * 1000;
    private static final int SLIDESHOW_INTERVAL = 30 * 1000; // 30 ثانية

    private Handler autoHandler = new Handler();
    private Handler slideshowHandler = new Handler();
    private String dataType;
    private ImageView fatherBgView;
    private int currentPhotoIndex = 0;

    // قائمة صور والدك رحمه الله
    private int[] photos = {
        R.drawable.father_bg,
        R.drawable.father_bg2,
        R.drawable.father_bg3,
        R.drawable.father_bg4,
        R.drawable.father_bg5,
        R.drawable.father_bg6,
        R.drawable.father_bg7
    };

    private BroadcastReceiver athanCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopAthanAndClose();
        }
    };

    private Runnable slideshowRunnable = new Runnable() {
        @Override
        public void run() {
            currentPhotoIndex = (currentPhotoIndex + 1) % photos.length;
            changePhotoWithAnimation(photos[currentPhotoIndex]);
            slideshowHandler.postDelayed(this, SLIDESHOW_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            );
        }

        setContentView(R.layout.activity_athan_screen);

        dataType = getIntent().getStringExtra("com.alaaeltaweel.thikrallah.datatype");

        fatherBgView = findViewById(R.id.father_bg);
        TextView prayerNameText = findViewById(R.id.prayer_name_text);
        TextView athanText = findViewById(R.id.athan_text);
        Button stopButton = findViewById(R.id.stop_athan_button);

        String prayerName = getPrayerName(dataType);
        prayerNameText.setText(prayerName);
        athanText.setText("حان وقت صلاة " + prayerName);

        stopButton.setOnClickListener(v -> stopAthanAndClose());

        // ابدأ الـ slideshow بعد 30 ثانية
        slideshowHandler.postDelayed(slideshowRunnable, SLIDESHOW_INTERVAL);

        playAthan();

        autoHandler.postDelayed(this::stopAthanAndClose, AUTO_DISMISS_DELAY);
    }

    private void changePhotoWithAnimation(final int newPhotoRes) {
        AlphaAnimation fadeOut = new AlphaAnimation(0.18f, 0f);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation a) {}
            @Override public void onAnimationRepeat(Animation a) {}
            @Override
            public void onAnimationEnd(Animation a) {
                fatherBgView.setImageResource(newPhotoRes);
                AlphaAnimation fadeIn = new AlphaAnimation(0f, 0.18f);
                fadeIn.setDuration(1000);
                fatherBgView.startAnimation(fadeIn);
                fatherBgView.setAlpha(0.18f);
            }
        });
        fatherBgView.startAnimation(fadeOut);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(athanCompleteReceiver,
                new IntentFilter("com.alaaeltaweel.thikrallah.ATHAN_COMPLETE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(athanCompleteReceiver);
        } catch (IllegalArgumentException e) {
            // في حالة مش مسجل أصلاً
        }
    }

    private String getPrayerName(String dataType) {
        if (dataType == null) return "الصلاة";
        switch (dataType) {
            case MainActivity.DATA_TYPE_ATHAN1: return "الفجر";
            case MainActivity.DATA_TYPE_ATHAN2:
                java.util.Calendar cal = java.util.Calendar.getInstance();
                if (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY) {
                    return "الجمعة";
                }
                return "الظهر";
            case MainActivity.DATA_TYPE_ATHAN3: return "العصر";
            case MainActivity.DATA_TYPE_ATHAN4: return "المغرب";
            case MainActivity.DATA_TYPE_ATHAN5: return "العشاء";
            default: return "الصلاة";
        }
    }

    private void playAthan() {
        Bundle data = new Bundle();
        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_PLAY);
        data.putString("com.alaaeltaweel.thikrallah.datatype", dataType);
        data.putBoolean("isUserAction", false);

        Intent intent = new Intent(this, ThikrService.class).putExtras(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopAthanAndClose() {
        Bundle data = new Bundle();
        data.putInt("ACTION", ThikrMediaPlayerService.MEDIA_PLAYER_STOP);
        data.putString("com.alaaeltaweel.thikrallah.datatype", dataType);
        Intent stopMedia = new Intent(this, ThikrMediaPlayerService.class).putExtras(data);
        startService(stopMedia);

        Intent stopThikr = new Intent(this, ThikrService.class);
        stopService(stopThikr);

        slideshowHandler.removeCallbacksAndMessages(null);
        autoHandler.removeCallbacksAndMessages(null);
        finish();
    }

    @Override
    protected void onDestroy() {
        slideshowHandler.removeCallbacksAndMessages(null);
        autoHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
