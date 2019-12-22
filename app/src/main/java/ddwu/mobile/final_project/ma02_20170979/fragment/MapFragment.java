package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;

public class MapFragment extends Fragment {
    public MapFragment() {}


    private GoogleMap map;
    private Marker currentMarker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private Location currentLocation;
    private LatLng currentPosition;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location location;

    private volatile boolean isRecording = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_map, inflater, container);
    }


    @Override
    protected void initializeView() {
        getAvailableActivity(activity -> {
            SupportMapFragment fragment =
                    ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

            fragment.getMapAsync(googleMap -> {
                map = googleMap;
                setDefaultLocation();
                startLocationUpdates();

                LatLng seoul = new LatLng(37.56, 126.97);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(seoul);

                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLng(seoul));
                map.animateCamera(CameraUpdateFactory.zoomTo(10));
            });
        });

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        FloatingActionButton startBtn = rootView.findViewById(R.id.fab_start);
        Recorder recorder = new Recorder();
        startBtn.setOnClickListener(v -> {
            if (isRecording) {
                // TODO: Stop recording
                startBtn.setImageResource(R.drawable.baseline_play_arrow_white_24);
                isRecording = false;
                getAvailableActivity(activity -> {
                    JSONObject record = JSONUtil.parse(SharedPreferencesUtil.get(activity, "record", JSONUtil.getSharedPreferencesDefaultValue()));
                    long end = System.currentTimeMillis();
                    Date date = new Date(end);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    String formatted = format.format(date);

                    recorder.record.put("end", System.currentTimeMillis());
                    recorder.record.put("distance", recorder.distance);
                    recorder.record.put("length", recorder.i);
                    record.put(formatted, recorder.record.toJSONString());
                    SharedPreferencesUtil.edit(activity, "record", record.toJSONString());
                });
            } else {
                // TODO: Start recording
                startBtn.setImageResource(R.drawable.baseline_stop_white_24);
                isRecording = true;
                recorder.start();
            }
        });
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager =
                (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                map.setMyLocationEnabled(true);

        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            super.onLocationResult(result);

            List<Location> locationList = result.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = "현재위치";
                setCurrentLocation(location, markerTitle, "");

                currentLocation = location;
            }
        }
    };

    private void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null)
            currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions options = new MarkerOptions();
        options.position(currentLatLng);
        options.title(markerTitle);
        options.snippet(markerSnippet);
        options.draggable(false);

        currentMarker = map.addMarker(options);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        map.moveCamera(cameraUpdate);
    }

    private void setDefaultLocation() {
        LatLng defaultLocation = new LatLng(37.56, 126.97);

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions marker = markerBuilder(defaultLocation, "위치정보 오류", "", false);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentMarker = map.addMarker(marker);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
    }

    private MarkerOptions markerBuilder(LatLng latLng, String title, String snippet, boolean draggable) {
        return new MarkerOptions().position(latLng).title(title).snippet(snippet).draggable(draggable);
    }

    private void showDialogForLocationServiceSetting() {
        getAvailableActivity(activity -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(R.string.dialog_content);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.dialog_positive, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
            });
            builder.setNegativeButton(R.string.dialog_negative, (dialog, which) -> {
                dialog.cancel();
            });
            builder.create().show();
        });
    }

    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private class Recorder extends Thread {
        float distance = 0.0f;
        LatLng lastLatLng = null;
        JSONObject record;
        int i = 0;

        @Override
        public void run() {
            record = new JSONObject();
            record.put("start", System.currentTimeMillis());

            while (isRecording) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (lastLatLng != null) {
                    distance += getDistance(lastLatLng, latLng);
                }

                lastLatLng = latLng;

                MarkerOptions markerOptions = markerBuilder(new LatLng(location.getLatitude(), location.getLongitude()), "", "", false);
                runOnUiThread(() -> map.addMarker(markerOptions));

                JSONObject point = new JSONObject();
                point.put("lat", latLng.latitude);
                point.put("lng", latLng.longitude);

                record.put(i++, point.toJSONString());

                try {
                    sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private float getDistance(LatLng a, LatLng b) {
            float lat_a = (float) a.latitude;
            float lng_a = (float) a.longitude;
            float lat_b = (float) b.latitude;
            float lng_b = (float) b.longitude;

            double earthRadius = 3958.75;
            double latDiff = Math.toRadians(lat_b-lat_a);
            double lngDiff = Math.toRadians(lng_b-lng_a);
            double d1 = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                    Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                            Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
            double c = 2 * Math.atan2(Math.sqrt(d1), Math.sqrt(1-d1));
            double distance = earthRadius * c;

            int meterConversion = 1609;

            return (float) (distance * meterConversion);
        }
    }

    /*
    private class RecordIndicator extends Thread {
        private String lastRecord;

        @Override
        public void run() {
            while (isRecording) {
                if (binder == null) {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                try {
                    String asJsonString = binder.getRecord();

                    if (lastRecord != null && lastRecord.equals(asJsonString)) {
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        continue;
                    }

                    lastRecord = asJsonString;
                    JSONObject parsed = JSONUtil.parse(asJsonString);
                    long length = (long) parsed.get("length");
                    String content = (String) parsed.get(String.valueOf(length - 1));
                    parsed = JSONUtil.parse(content);
                    double lat = (double) parsed.get("lat");
                    double lng = (double) parsed.get("lng");
                    long timeStamp = (long) parsed.get("time_stamp");

                    MarkerOptions options = markerBuilder(
                            new LatLng(lat, lng), "", "", false);
                    map.addMarker(options);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    */
}
