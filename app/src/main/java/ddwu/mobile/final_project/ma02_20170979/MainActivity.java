package ddwu.mobile.final_project.ma02_20170979;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.HashSet;

import ddwu.mobile.final_project.ma02_20170979.fragment.MainFragment;
import ddwu.mobile.final_project.ma02_20170979.model.Activity;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private boolean needRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_main, new MainFragment())
                .commit();

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]))
                Snackbar.make(findViewById(R.id.layout_main), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("확인", view -> ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)).show();
            else
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean checkResult = true;

            // 요청한 모든 권한이 승인되었는지 확인
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false;
                    break;
                }
            }

            // 하나 이상의 권한이 거부되었을 때 SnackBar 표시 후 종료
            if (!checkResult) {
                Snackbar.make(findViewById(R.id.layout_main), R.string.snackbar_permission_not_granted,
                        Snackbar.LENGTH_INDEFINITE).setAction(R.string.snackbar_ok, view -> finish()).show();
            }
        }
    }
}
