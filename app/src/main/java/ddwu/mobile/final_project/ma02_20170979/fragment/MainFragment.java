package ddwu.mobile.final_project.ma02_20170979.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;

public class MainFragment extends Fragment {
    public MainFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_main, inflater, container);
    }


    @Override
    protected void initializeView() {
        rootView.findViewById(R.id.btn_start_timer).setOnClickListener(v ->
                replace(R.id.layout_main, new ExerciseEditFragment(), true)
        );

        rootView.findViewById(R.id.btn_my_exercise).setOnClickListener(v ->
                replace(R.id.layout_main, new ExerciseListFragment(), true)
        );

        rootView.findViewById(R.id.btn_do_exercise).setOnClickListener(v ->
                replace(R.id.layout_main, new MapFragment(), true)
        );

        rootView.findViewById(R.id.btn_show_calendar).setOnClickListener(v ->
                replace(R.id.layout_main, new TodayExerciseFragment(), true)
        );
    }
}
