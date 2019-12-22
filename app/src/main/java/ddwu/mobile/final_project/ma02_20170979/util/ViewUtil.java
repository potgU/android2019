package ddwu.mobile.final_project.ma02_20170979.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUtil {
    public static int dp2px(Context ctx, float dp) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context ctx, float px) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static boolean isEmpty(EditText... editTexts) {
        for (EditText editText : editTexts)
            if (editText == null || editText.getText().toString().isEmpty())
                return true;

        return false;
    }

    public static boolean isEmpty(TextView... textViews) {
        for (TextView textView : textViews)
            if (textView == null || textView.getText().toString().isEmpty())
                return true;

        return false;
    }

    public static String getText(TextView textView, String def) {
        return isEmpty(textView) ? def : textView.getText().toString();
    }

    public static int getText(TextView textView, int def) {
        return isEmpty(textView) ? def : Integer.parseInt(textView.getText().toString());
    }

    public static String getText(EditText editText, String def) {
        return isEmpty(editText) ? def : editText.getText().toString();
    }

    public static int getText(EditText editText, int def) {
        return isEmpty(editText) ? def : Integer.parseInt(editText.getText().toString());
    }
}
