
package lh.example.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActiveActivity extends Activity implements OnClickListener {

    private Alarm alarm;
    private MediaPlayer mediaPlayer;


    private Vibrator vibrator;
    private boolean alarmActive;
    private SensorManager mSensorManager;
    private ShakeListener mSensorListener;
    int shakeCount; //= getRandomInt();



    private String[] myString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_active);
        final TextView problemView = (TextView) findViewById(R.id.textView1);

        Resources res = getResources();
        myString = res.getStringArray(R.array.myArray);
        setShakeCount(getRandomInt());


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeListener();

        mSensorListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

            public void onShake() {
                setShakeCount(getShakeCount()- 1);

                Toast.makeText(AlarmActiveActivity.this, String.valueOf(shakeCount)+" Shakes Left", Toast.LENGTH_SHORT).show();

                Random ran = new Random();
                String ranText;
                ranText = myString[ran.nextInt(myString.length)];
                problemView.setText(ranText);

                if (!alarmActive) {
                    return;
                }
                if (isAnswerCorrect()) {
                    alarmActive = false;
                    if (vibrator != null)
                        vibrator.cancel();
                    try {
                        mediaPlayer.stop();
                    } catch (IllegalStateException ise) {
                        Toast.makeText(AlarmActiveActivity.this, "Illegal State Exception" + ise, Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mediaPlayer.release();

                    } catch (Exception e) {
                        Toast.makeText(AlarmActiveActivity.this, "Media Player error" + e, Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        System.exit(0);
                    }
                }


            }
        });


        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");

        this.setTitle(alarm.getAlarmName());


        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: "
                                + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException e) {
                            Toast.makeText(AlarmActiveActivity.this,"Illegal State Exception" + e, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {
                            Toast.makeText(AlarmActiveActivity.this,"Illegal State Exception" + e, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        startAlarm();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        alarmActive = true;
    }

    private void startAlarm() {

        if (alarm.getAlarmTonePath() != "") {
            mediaPlayer = new MediaPlayer();
            if (alarm.getVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 200, 200, 200};
                vibrator.vibrate(pattern, 0);
                Timer timer = new Timer();

                //Create a task which the timer will execute.  This should be an implementation of the TimerTask interface.
                //I have created an inner class below which fits the bill.
                MyTimer mt = new MyTimer();

                //We schedule the timer task to run after 1000 ms and continue to run every 1000 ms.
                timer.schedule(mt, 1000, 1000);

            }
            try {
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this,
                        Uri.parse(alarm.getAlarmTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                mediaPlayer.release();
                alarmActive = false;
            }
        }

    }

    /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
    @Override
    public void onBackPressed() {
        if (!alarmActive)
            super.onBackPressed();
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
        StaticWakeLock.lockOff(this);
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {
            Toast.makeText(AlarmActiveActivity.this, "Media Player error" + e, Toast.LENGTH_SHORT).show();
        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            Toast.makeText(AlarmActiveActivity.this, "Media Player error" + e, Toast.LENGTH_SHORT).show();
        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {
            Toast.makeText(AlarmActiveActivity.this, "Media Player error" + e, Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!alarmActive) {
            return;
        }
        if (isAnswerCorrect()) {
            alarmActive = false;
            if (vibrator != null)
                vibrator.cancel();
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ise) {
                Toast.makeText(AlarmActiveActivity.this, "Illegal State Exception" + ise, Toast.LENGTH_SHORT).show();
            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Toast.makeText(AlarmActiveActivity.this, "Media Player Error" + e, Toast.LENGTH_SHORT).show();
            }
            this.finish();
        }
    }

    private int getRandomInt() {
        int min = 4;
        int max = 10;
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private int getShakeCount(){
        return shakeCount;
    }

    private void setShakeCount(int shakeCount){
        this.shakeCount = shakeCount;
    }

    public boolean isAnswerCorrect() {
        boolean correct = false;
        try {
            correct = shakeCount == 0;
        }
        catch(Exception e){

        }
        return correct;
    }

    class MyTimer extends TimerTask {

        public void run() {


            runOnUiThread(new Runnable() {

                public void run() {
                    Random rand = new Random();

                    getWindow().getDecorView().setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256) ));
                }
            });
        }

    }
}