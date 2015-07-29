
package lh.example.alarm;

import android.content.Context;
import android.os.PowerManager;

public class StaticWakeLock {
	private static PowerManager.WakeLock wl = null;

	public static void lockOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		//Object flags;
		if (wl == null)
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MATH_ALARM");
		wl.acquire();
	}

	public static void lockOff(Context context) {

		try {
			if (wl != null)
				wl.release();
		} catch (Exception e) {

		}
	}
}