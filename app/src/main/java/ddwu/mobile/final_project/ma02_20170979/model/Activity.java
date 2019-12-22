package ddwu.mobile.final_project.ma02_20170979.model;

import androidx.appcompat.app.AppCompatActivity;

public class Activity extends AppCompatActivity {
    private OnBackPressedListener onBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.onBackPressed();

        super.onBackPressed();
    }

    @FunctionalInterface
    public interface OnBackPressedListener {
        void onBackPressed();
    }
}
