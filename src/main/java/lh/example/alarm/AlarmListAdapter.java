
package lh.example.alarm;

import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class AlarmListAdapter extends BaseAdapter {

	private AlarmActivity alarmActivity;
	private List<Alarm> alarms = new ArrayList<Alarm>();



	public AlarmListAdapter(AlarmActivity alarmActivity) {
		this.alarmActivity = alarmActivity;

	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if (null == view)
			view = LayoutInflater.from(alarmActivity).inflate(
					R.layout.alarm_list_element, null);

		Alarm alarm = (Alarm) getItem(position);

		 CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_alarm_active);
		 checkBox.setChecked(alarm.getAlarmActive());
		 checkBox.setTag(position);
		 checkBox.setOnClickListener(alarmActivity);
		 
		TextView alarmTimeView = (TextView) view
				.findViewById(R.id.textView_alarm_time);
		alarmTimeView.setText(alarm.getAlarmTimeString());

		
			TextView alarmDaysView = (TextView) view
					.findViewById(R.id.textView_alarm_days);
			alarmDaysView.setText(alarm.getRepeatDaysString());
		

		return view;
	}



	public void setMathAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

}
