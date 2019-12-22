package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;
import ddwu.mobile.final_project.ma02_20170979.util.ViewUtil;

public class ExerciseListFragment extends Fragment {
    public ExerciseListFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_exercise_list, inflater, container);
    }


    @Override
    protected void initializeView() {
        rootView.findViewById(R.id.btn_add_exercise_item).setOnClickListener(v -> {
            Fragment next = new ExerciseEditFragment();
            Bundle argument = new Bundle();
            argument.putString("from", "ExerciseListFragment");
            next.setArguments(argument);
            replace(R.id.layout_main, next, true);
        });

        ((ListView) rootView.findViewById(R.id.list_exercise_preset)).setAdapter(new ContentAdapter());
    }

    private class Content {
        String name;
        JSONObject innerContent;

        Content(String name, JSONObject innerContent) {
            this.name = name;
            this.innerContent = innerContent;
        }
    }

    private class ViewHolder {
        View rootView;
        CardView wrappingCardView;
        TextView nameTextView;
        ImageView deleteImageView;

        ViewHolder(View view) {
            rootView = view;

            wrappingCardView = view.findViewById(R.id.card_exercise_list);
            nameTextView = view.findViewById(R.id.tv_preset_name);
            deleteImageView = view.findViewById(R.id.image_delete);
        }
    }

    private class ContentAdapter extends BaseAdapter {
        private ArrayList<Content> contents;
        private ArrayList<ViewHolder> viewHolders;

        ContentAdapter() {
            contents = new ArrayList<>();

            getAvailableActivity(activity -> {
                String presetString = SharedPreferencesUtil.get(activity, "preset", JSONUtil.getSharedPreferencesDefaultValue());
                JSONObject presets = JSONUtil.parse(presetString);
                assert presets != null;
                long length = (long) presets.get("length");

                for (int i = 0; i < length; i++) {
                    String presetContent = (String) presets.get(String.valueOf(i));
                    JSONObject content = JSONUtil.parse(presetContent);
                    assert content != null;

                    String name = (String) content.get("name");
                    contents.add(new Content(name, content));
                }
            });
        }


        @Override
        public int getCount() {
            return (contents != null) ? contents.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return (contents != null) ? contents.get(position) : null;
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

            View view = LayoutInflater.from(getContext()).inflate(R.layout.card_exercise_list, parent, false);
            ViewHolder holder = new ViewHolder(view);
            holder.nameTextView.setText(contents.get(position).name);
            holder.deleteImageView.setOnClickListener(v -> {
                String existing = SharedPreferencesUtil.get(getActivity(), "preset", JSONUtil.getSharedPreferencesDefaultValue());
                JSONObject parsed = JSONUtil.parse(existing);
                assert parsed != null;

                parsed.remove(String.valueOf(position));
                long length = (long) parsed.remove("length");
                parsed.put("length", length - 1);
                SharedPreferencesUtil.edit(getActivity(), "preset", parsed.toJSONString());

                contents.remove(position);
                notifyDataSetChanged();
            });

            viewHolders.add(holder);

            return view;
        }
    }
}
