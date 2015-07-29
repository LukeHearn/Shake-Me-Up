
package lh.example.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmActiveBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);
		
		StaticWakeLock.lockOn(context);
		Bundle bundle = intent.getExtras();
		final Alarm alarm = (Alarm) bundle.getSerializable("alarm");

		Intent mathAlarmAlertActivityIntent;

		mathAlarmAlertActivityIntent = new Intent(context, AlarmActiveActivity.class);

		mathAlarmAlertActivityIntent.putExtra("alarm", alarm);

		mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(mathAlarmAlertActivityIntent);
	}

}
