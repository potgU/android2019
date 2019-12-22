package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;

public class RouteFragment extends Fragment {
    public RouteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_route, inflater, container);
    }

    @Override
    protected void initializeView() {
        Bundle bundle = getArguments();
        assert bundle != null;
        String asString = bundle.getString("record");
        JSONObject record = JSONUtil.parse(asString);
        assert record != null;

        SupportMapFragment fragment =
                ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        assert fragment != null;

        fragment.getMapAsync(googleMap -> {

            ArrayList<LatLng> points = new ArrayList<>();

            long length = (long) record.get("length");
            for (int i = 0; i < length; i++) {
                JSONObject point = JSONUtil.parse((String) record.get(String.valueOf(i)));
                assert point != null;

                float lat = (float)(double) point.get("lat");
                float lng = (float)(double) point.get("lng");
                LatLng latLng = new LatLng(lat, lng);
                points.add(latLng);

                MarkerOptions markerOptions = markerBuilder(latLng, String.valueOf(i + 1), "", false);
                googleMap.addMarker(markerOptions);
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(points.size() / 2)));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        });

        float distance = (float)(double) record.get("distance");
        ((TextView) rootView.findViewById(R.id.tv_distance)).setText(distance + "m");

        long startTime = (long) record.get("start");
        long endTime = (long) record.get("end");
        long elapsed = endTime - startTime;
        int ss = (int) (elapsed / 1000);
        int mm = ss / 60;
        int hh = mm / 60;
        mm -= hh * 60;
        ss -= hh * 3600 + mm * 60;
        String time = String.format("%02d:%02d:%02d", hh, mm, ss);
        Log.v("MyExerciseTimer", "startTime=" + startTime + ", endTime=" + endTime + ", elapsed=" + elapsed + ", time=" + time);
        ((TextView) rootView.findViewById(R.id.tv_time)).setText(time);

        // 한시간 걷기 = 158kcal
        String[] splited = time.split(":");
        int inSecond = Integer.parseInt(splited[0]) * 3600 + Integer.parseInt(splited[1]) * 60 + Integer.parseInt(splited[2]);
        float calorie = 158 * inSecond / 3600;
        ((TextView) rootView.findViewById(R.id.tv_calorie)).setText(calorie + "kcal");
    }

    private MarkerOptions markerBuilder(LatLng latLng, String title, String snippet, boolean draggable) {
        return new MarkerOptions().position(latLng).title(title).snippet(snippet).draggable(draggable);
    }
}
