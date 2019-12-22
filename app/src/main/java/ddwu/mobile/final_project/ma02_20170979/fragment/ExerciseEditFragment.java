package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.DndListView;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;
import ddwu.mobile.final_project.ma02_20170979.util.ViewUtil;

public class ExerciseEditFragment extends Fragment {
    public ExerciseEditFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_exercise_edit, inflater, container);
    }

    private FloatingActionButton expandBtn, addExerciseBtn, startTimerBtn;
    private boolean isVisible = false;

    private boolean fromListFragment = false;

    @Override
    protected void initializeView() {
        getAvailableActivity(activity -> activity.setOnBackPressedListener(() -> clearLayout(R.id.fragment_exercise_edit)));

        Bundle bundle = getArguments();
        if (bundle != null) {
            String from = bundle.getString("from");
            assert from != null;
            fromListFragment = from.equals("ExerciseListFragment");
        }

        // TODO: ListView implementation
        DndListView listView = rootView.findViewById(R.id.list_editable_exercise);
        ContentAdapter adapter = new ContentAdapter();
        listView.setAdapter(adapter);

        EditText restTimeEdit = rootView.findViewById(R.id.edit_rest_time);

        // expandBtn 클릭 시 나머지 FloatingActionButton 보이는 상태 switch
        expandBtn = rootView.findViewById(R.id.fab_expand);
        expandBtn.setOnClickListener(v -> switchFloatingActionButtonVisibility());

        addExerciseBtn = rootView.findViewById(R.id.fab_add_exercise);
        addExerciseBtn.setOnClickListener(v ->
                replace(R.id.layout_main, new ExerciseAddFragment(), false)
        );

        startTimerBtn = rootView.findViewById(R.id.fab_start_timer);
        startTimerBtn.setOnClickListener(v -> {
            if (!fromListFragment) {
                if (adapter.contents.size() != 0) {
                    Fragment next = new TimerFragment();
                    Bundle arguments = new Bundle();
                    arguments.putInt("rest_time", Integer.parseInt(restTimeEdit.getText().toString()));
                    next.setArguments(arguments);
                    replace(R.id.layout_main, next, true);
                }
            } else {
                if (adapter.contents.size() != 0) {
                    JSONObject preset = new JSONObject();

                    for (int i = 0; i < adapter.contents.size(); i++) {
                        JSONObject content = new JSONObject();
                        content.put("name", adapter.contents.get(i).name);
                        content.put("times", adapter.contents.get(i).times);
                        preset.put(i, content.toJSONString());
                    }

                    preset.put("length", adapter.contents.size());

                    getAvailableActivity(activity -> {
                        String allList = SharedPreferencesUtil.get(activity, "preset", JSONUtil.getSharedPreferencesDefaultValue());
                        JSONObject parsed = JSONUtil.parse(allList);
                        assert parsed != null;

                        long length = (long) parsed.get("length");
                        parsed.put(length, preset.toJSONString());
                        parsed.put("length", length + 1);
                        SharedPreferencesUtil.edit(activity, "preset", parsed.toJSONString());
                    });
                }
            }
        });

        rootView.findViewById(R.id.btn_add_rest_time).setOnClickListener(v ->
                // 휴식 시간 [+] 버튼 클릭 시 시간 5 증가
                // EditText 비었으면 31 설정
                restTimeEdit.setText(ViewUtil.isEmpty(restTimeEdit) ? String.valueOf(31) : String.valueOf(Integer.parseInt(restTimeEdit.getText().toString()) + 5))
        );

        rootView.findViewById(R.id.btn_sub_rest_time).setOnClickListener(v ->
                // 휴식 시간 [-] 버튼 클릭 시 시간 5 감소
                // EditText 비었으면 29 설정
                restTimeEdit.setText(ViewUtil.isEmpty(restTimeEdit) ? String.valueOf(29) : String.valueOf(Integer.parseInt(restTimeEdit.getText().toString()) - 5))
        );


    }

    private void switchFloatingActionButtonVisibility() {
        if (isVisible) {
            addExerciseBtn.hide();
            startTimerBtn.hide();
            expandBtn.setImageResource(R.drawable.baseline_keyboard_arrow_down_white_24);

            isVisible = false;
        } else {
            addExerciseBtn.show();
            startTimerBtn.show();
            expandBtn.setImageResource(R.drawable.baseline_keyboard_arrow_up_white_24);

            isVisible = true;
        }
    }

    private class Content {
        String name;
        int times;

        Content(String name, int times) {
            this.name = name;
            this.times = times;
        }
    }

    private class ViewHolder {
        View rootView;
        CardView wrappingCardView;
        TextView nameTextView, timesTextView;
        ImageView dragImageView, addImageView, subImageView, deleteImageView;

        ViewHolder(View view) {
            rootView = view;
            wrappingCardView = view.findViewById(R.id.card_exercise_edit);
            nameTextView = view.findViewById(R.id.tv_exercise_name);
            timesTextView = view.findViewById(R.id.tv_times);
            dragImageView = view.findViewById(R.id.image_drag);
            addImageView = view.findViewById(R.id.image_add_times);
            subImageView = view.findViewById(R.id.image_sub_times);
            deleteImageView = view.findViewById(R.id.image_delete);
        }
    }

    private class ContentAdapter extends BaseAdapter {
        ArrayList<Content> contents;
        ArrayList<ViewHolder> viewHolders;

        ContentAdapter() {
            contents = new ArrayList<>();
            viewHolders = new ArrayList<>();

            getAvailableActivity(activity -> {
                if (!fromListFragment) {
                    String existing = SharedPreferencesUtil.get(activity, "exercises", JSONUtil.getSharedPreferencesDefaultValue());
                    JSONObject parsed = JSONUtil.parse(existing);
                    long length = (long) parsed.get("length");

                    for (int i = 0; i < length; i++) {
                        String name = (String) parsed.get(String.valueOf(i));
                        int times = SharedPreferencesUtil.get(activity, name + "_times", 3);
                        contents.add(new Content(name, times));
                    }


                    for (Content content : contents) {
                        SharedPreferencesUtil.edit(activity, content.name + "_times", content.times);
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return (contents != null) ? contents.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return contents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (viewHolders.size() > position && viewHolders.get(position) != null) {
                return viewHolders.get(position).rootView;
            }

            View view = LayoutInflater.from(getContext()).inflate(R.layout.card_exercise_edit, parent, false);
            ViewHolder holder = new ViewHolder(view);
            // 반복 횟수 증가, 감소
            holder.addImageView.setOnClickListener(v -> {
                int times = ViewUtil.getText(holder.timesTextView, -1) + 1;
                holder.timesTextView.setText(String.valueOf(times));
                SharedPreferencesUtil.edit(getActivity(), contents.get(position).name + "_times", times);
            });
            holder.subImageView.setOnClickListener(v -> {
                int times = ViewUtil.getText(holder.timesTextView, -1) - 1;
                holder.timesTextView.setText(String.valueOf(times));
                SharedPreferencesUtil.edit(getActivity(), contents.get(position).name + "_times", times);
            });
            holder.nameTextView.setText(contents.get(position).name);
            holder.timesTextView.setText(String.valueOf(SharedPreferencesUtil.get(getActivity(), contents.get(position).name + "_times", 99)));
            holder.deleteImageView.setOnClickListener(v -> {
                String existing = SharedPreferencesUtil.get(getActivity(), "exercises", JSONUtil.getSharedPreferencesDefaultValue());
                JSONObject parsed = JSONUtil.parse(existing);
                parsed.remove(position + 1);
                long length = (long) parsed.remove("length");
                parsed.put("length", length - 1);
                SharedPreferencesUtil.edit(getActivity(), "exercises", parsed.toJSONString());
                SharedPreferencesUtil.edit(getActivity(), contents.get(position).name + "_times", 3);

                contents.remove(position);
                notifyDataSetChanged();
            });

            viewHolders.add(holder);

            return view;
        }
    }
}
