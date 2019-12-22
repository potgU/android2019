package ddwu.mobile.final_project.ma02_20170979.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;

public class TimerFragment extends Fragment {
    public TimerFragment() {}


    private ArrayList<Exercise> exercises;
    private int restTime;
    private TextView nameTextView, setTextView, timerTextView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_timer, inflater, container);
    }


    @Override
    protected void initializeView() {
        exercises = new ArrayList<>();
        getAvailableActivity(activity -> {
            String fromSharedPreferences = SharedPreferencesUtil.get(activity, "exercises", "");
            JSONObject parsed = JSONUtil.parse(fromSharedPreferences);
            long length = (long) parsed.get("length");

            for (int i = 0; i < length; i++) {
                String name = (String) parsed.get(String.valueOf(i));
                int times = SharedPreferencesUtil.get(activity, name + "_times", 3);

                exercises.add(new Exercise(name, times, 10));
            }
        });

        restTime = getArguments().getInt("rest_time");
        Timer timer = new Timer();
        timer.start();

        nameTextView = rootView.findViewById(R.id.tv_current_exercise);
        setTextView = rootView.findViewById(R.id.tv_set_indicator);
        timerTextView = rootView.findViewById(R.id.tv_timer);

        rootView.findViewById(R.id.fragment_timer).setOnClickListener(v -> timer.onScreenTouched());
    }


    private class Exercise {
        String name;
        int set, seconds;

        Exercise(String name, int set, int seconds) {
            this.name = name;
            this.set = set;
            this.seconds = seconds;
        }
    }


    private class Timer extends Thread {
        int currentIndex = 0;
        volatile int state = 1;

        @Override
        public void run() {
            int set = 1;
            for (currentIndex = 0; currentIndex < exercises.size(); currentIndex++) {
                Log.d("MyExerciseTimer", "exercises[" + currentIndex + "]: name=" + exercises.get(currentIndex).name);

                int finalSet = set;
                runOnUiThread(() -> {
                    // 운동 이름 설정
                    nameTextView.setText(exercises.get(currentIndex).name);
                    // 현재 세트 설정
                    setTextView.setText(String.format("%d/%d 세트", finalSet, exercises.get(currentIndex).set));
                });

                // 운동 걸리는 시간 ms
                long inMillis = exercises.get(currentIndex).seconds * 1000;
                // 운동 시작 시각
                long start = System.currentTimeMillis();
                // 시작 후 지난 시간
                long elapsed = 0;
                // 지난 시간 < 전체 시간인 동안
                while (elapsed < inMillis) {
                    // 화면 touch 시간 정지가 발생했을 때 그만큼 시간이 흐르지 않은 것처럼 보여야 함
                    // 정지 시작 시각 기록
                    long waitStart = System.currentTimeMillis();
                    // 정지되지 않았다면 지난 시각에 더할 필요가 없음
                    boolean waited = false;
                    // 정상 작동(=1)이 아닌 동안
                    while (state != 1) {
                        // waited flag 활성화
                        waited = true;
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (waited)
                        // 시간이 멈춰있었다면 운동 시작 시각에 정지된 시간만큼 더함
                        start += System.currentTimeMillis() - waitStart;

                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 남은 시간은 ((전체 시간) - (흐른 시간)) / 1000
                    int leftInSeconds = (int) ((inMillis - elapsed) / 1000);
                    runOnUiThread(() -> timerTextView.setText(String.format("%d초", leftInSeconds)));
                    // elapsed 초기화
                    elapsed = System.currentTimeMillis() - start;
                }

                if (set < exercises.get(currentIndex).set) {
                    set++;
                    currentIndex--;
                }

                if (currentIndex < exercises.size() - 1) {
                    // 마지막 세트가 아니면 휴식, 세트 진행과 같은 내용이지만 정지 없음
                    start = System.currentTimeMillis();
                    elapsed = 0;
                    inMillis = restTime * 1000;

                    runOnUiThread(() -> {
                        nameTextView.setText(R.string.tv_current_exercise_rest);
                        setTextView.setText("");
                    });

                    while (elapsed < inMillis) {
                        int leftInSeconds = (int) ((inMillis - elapsed) / 1000);
                        runOnUiThread(() -> timerTextView.setText(String.format("%d초", leftInSeconds)));

                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        elapsed = System.currentTimeMillis() - start;
                    }
                }
            }
        }

        void onScreenTouched() {
            state *= -1;
        }
    }
}
