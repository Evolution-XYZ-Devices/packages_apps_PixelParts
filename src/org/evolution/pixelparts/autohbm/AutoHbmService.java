package org.evolution.pixelparts.autohbm;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.preference.PreferenceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.evolution.pixelparts.Constants;
import org.evolution.pixelparts.utils.FileUtils;

public class AutoHbmService extends Service {

    private static boolean mAutoHbmActive = false;
    private ExecutorService mExecutorService;

    private SensorManager mSensorManager;
    Sensor mLightSensor;

    private SharedPreferences mSharedPrefs;

    public void activateLightSensorRead() {
        submit(() -> {
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(mSensorEventListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        });
    }

    public void deactivateLightSensorRead() {
        submit(() -> {
        mSensorManager.unregisterListener(mSensorEventListener);
        mAutoHbmActive = false;
        enableHbm(false);
        });
    }

    private void enableHbm(boolean enable) {
        if (enable) {
            FileUtils.writeValue(Constants.NODE_HBM, "1");
        } else {
            FileUtils.writeValue(Constants.NODE_HBM, "0");
        }
    }

    private boolean isCurrentlyEnabled() {
        String fileValue = FileUtils.getFileValue(Constants.NODE_HBM, "0");
        return fileValue.equals("1") ? true : false;
    }

    SensorEventListener mSensorEventListener = new SensorEventListener() {
        private boolean mCrossedThreshold = false;
        private long mCrossedThresholdTime = 0;
        private long mLastTriggerTime = 0;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            KeyguardManager km =
                    (KeyguardManager) getSystemService(getApplicationContext().KEYGUARD_SERVICE);
            boolean keyguardShowing = km.inKeyguardRestrictedInputMode();
            int luxThreshold = mSharedPrefs.getInt(Constants.KEY_AUTO_HBM_THRESHOLD, 20000);
            int timeToEnableHbm = mSharedPrefs.getInt(Constants.KEY_AUTO_HBM_ENABLE_TIME, 0);
            int timeToDisableHbm = mSharedPrefs.getInt(Constants.KEY_AUTO_HBM_DISABLE_TIME, 1);

            if (lux > luxThreshold) {
                if (!mCrossedThreshold) {
                    mCrossedThreshold = true;
                    mCrossedThresholdTime = System.currentTimeMillis();
                } else {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - mCrossedThresholdTime >= timeToEnableHbm * 1000 && (!mAutoHbmActive || !isCurrentlyEnabled()) && !keyguardShowing) {
                        mAutoHbmActive = true;
                        enableHbm(true);
                        mLastTriggerTime = currentTime;
                    }
                }
            } else {
                mCrossedThreshold = false;

                if (mAutoHbmActive) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - mLastTriggerTime >= timeToDisableHbm * 1000) {
                        mAutoHbmActive = false;
                        enableHbm(false);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do nothing
        }
    };

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                activateLightSensorRead();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                deactivateLightSensorRead();
            }
        }
    };

    @Override
    public void onCreate() {
        mExecutorService = Executors.newSingleThreadExecutor();
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isInteractive()) {
            activateLightSensorRead();
        }
    }

    private Future<?> submit(Runnable runnable) {
        return mExecutorService.submit(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isInteractive()) {
            deactivateLightSensorRead();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
