package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;

public class TodayExerciseFragment extends Fragment {
    public TodayExerciseFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_today_exercise, inflater, container);
    }

    @Override
    protected void initializeView() {
        getAvailableActivity(activity -> {
            JSONObject record = JSONUtil.parse(SharedPreferencesUtil.get(activity, "record", JSONUtil.getSharedPreferencesDefaultValue()));
            Log.v("MyExerciseTimer", record.toJSONString());

            CalendarView calendarView = rootView.findViewById(R.id.calendar);
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                String asString = year + "/" + (month + 1) + "/" + dayOfMonth;
                String singleRecordString = (String) record.get(asString);

                Log.v("MyExerciseTimer", asString + "::" + singleRecordString);

                if (singleRecordString == null)
                    return;

                Log.v("MyExerciseTimer", singleRecordString);

                Fragment next = new RouteFragment();
                Bundle arguments = new Bundle();
                arguments.putString("record", singleRecordString);
                next.setArguments(arguments);
                replace(R.id.layout_main, next, true);
            });
        });
    }
}
