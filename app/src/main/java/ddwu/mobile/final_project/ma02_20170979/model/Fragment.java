package ddwu.mobile.final_project.ma02_20170979.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

public abstract class Fragment extends androidx.fragment.app.Fragment {
    @FunctionalInterface
    protected interface ActivityEnabledListener {
        void onActivityEnabled(Activity activity);
    }

    @FunctionalInterface
    protected interface ContextEnabledListener {
        void onContextEnabled(Context context);
    }

    protected ActivityEnabledListener activityEnabledListener;
    protected ContextEnabledListener contextEnabledListener;

    protected void getAvailableActivity(ActivityEnabledListener listener) {
        if (getActivity() == null)
            activityEnabledListener = listener;
        else
            listener.onActivityEnabled((Activity) getActivity());
    }

    protected void getAvailableContext(ContextEnabledListener listener) {
        if (getContext() == null)
            contextEnabledListener = listener;
        else
            listener.onContextEnabled(getContext());
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (activityEnabledListener != null) {
            activityEnabledListener.onActivityEnabled((Activity) context);
            activityEnabledListener = null;
        }

        if (contextEnabledListener != null) {
            contextEnabledListener.onContextEnabled(context);
            contextEnabledListener = null;
        }
    }

    protected void runOnUiThread(Runnable runnable) {
        getAvailableActivity(activity -> activity.runOnUiThread(runnable));
    }

    protected View rootView;

    protected View onCreateView(int layout, @NonNull LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(layout, container, false);

        initializeView();

        return rootView;
    }

    protected abstract void initializeView();

    protected void replace(@IdRes int container, Fragment fragment, boolean addToBackStack) {
        getAvailableActivity(activity -> {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(container, fragment);
            if (addToBackStack)
                transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        });
    }

    protected void clearLayout(@IdRes int container) {
        ((ViewGroup) rootView.findViewById(container)).removeAllViews();
    }

    protected void finish() {
        getAvailableActivity(Activity::onBackPressed);
    }
}