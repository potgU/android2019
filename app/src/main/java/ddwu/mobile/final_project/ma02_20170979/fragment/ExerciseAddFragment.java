package ddwu.mobile.final_project.ma02_20170979.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONObject;

import ddwu.mobile.final_project.ma02_20170979.R;
import ddwu.mobile.final_project.ma02_20170979.model.Fragment;
import ddwu.mobile.final_project.ma02_20170979.util.JSONUtil;
import ddwu.mobile.final_project.ma02_20170979.util.SharedPreferencesUtil;

public class ExerciseAddFragment extends Fragment {
    public ExerciseAddFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_exercise_add, inflater, container);
    }


    // TODO: to SharedPreferences
    private final String[] exerciseTypes = { "전체", "맨몸", "기구" };
    private final String[] exerciseParts = { "전체", "하체", "등" };


    // Index of each arrays: exerciseTypes, exerciseParts
    private int currentExerciseType = 0, currentExercisePart = 0;


    @Override
    protected void initializeView() {
        getAvailableContext(context -> {
            /* Spinner exerciseTypeSpinner = rootView.findViewById(R.id.spinner_exercise_type);
            exerciseTypeSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, exerciseTypes));
            exerciseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setTypeFilter(exerciseTypes[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    adapter.setTypeFilter(null);
                }
            });

            Spinner exercisePartSpinner = rootView.findViewById(R.id.spinner_exercise_part);
            exercisePartSpinner.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, exerciseParts));
            exercisePartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter.setPartFilter(exerciseParts[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    adapter.setPartFilter(null);
                }
            }); */

            RecyclerView recyclerView = rootView.findViewById(R.id.list_add_exercise);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new ContentAdapter());
        });
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        CardView wrappingCardView;
        TextView categoryTextView, nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wrappingCardView = itemView.findViewById(R.id.card_exercise_add);
            categoryTextView = itemView.findViewById(R.id.tv_exercise_category);
            nameTextView = itemView.findViewById(R.id.tv_exercise_name);
        }
    }


    private class Content {
        String type, part, name;

        Content(String type, String part, String name) {
            this.type = type;
            this.part = part;
            this.name = name;
        }
    }


    private class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final Content[] defaultContents = {
                new Content("맨몸", "하체", "런지"),
                new Content("맨몸", "하체", "스쿼트"),
                new Content("기구", "등", "풀업")
        };

        private Content[] contents;


        ContentAdapter() {
            contents = new Content[defaultContents.length];
            System.arraycopy(defaultContents, 0, contents, 0, defaultContents.length);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.card_exercise_add, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Content content = contents[position];
            holder.wrappingCardView.setOnClickListener(v -> getAvailableActivity(activity -> {
                String existing = SharedPreferencesUtil.get(activity, "exercises", JSONUtil.getSharedPreferencesDefaultValue());
                JSONObject parsed = JSONUtil.parse(existing);
                long length = (long) parsed.remove("length");
                parsed.put(String.valueOf(length), content.name);
                parsed.put("length", ++length);

                SharedPreferencesUtil.edit(activity, "exercises", parsed.toJSONString());
                clearLayout(R.id.fragment_exercise_add);
                replace(R.id.layout_main, new ExerciseEditFragment(), false);
            }));
            holder.categoryTextView.setText(content.type + "/" + content.part);
            holder.nameTextView.setText(content.name);
        }

        @Override
        public int getItemCount() {
            return (contents != null) ? contents.length : 0;
        } /*

        void setTypeFilter(String type) {
            if (type != null) {
                ArrayList<Content> filtered = new ArrayList<>();
                for (Content c : contents)
                    if (c.type.equals(type))
                        filtered.add(c);
                filtered.toArray(contents);
            } else {
                contents = defaultContents;
            }

            notifyDataSetChanged();
        }

        void setPartFilter(String part) {
            Log.d("MyExerciseTimer", "content.length :: " + contents.length);
            for (int i = 0; i < contents.length; i++) {
                Log.d("MyExerciseTimer", "content[" + i + "] part=" + contents[i].part + ", type=" + contents[i].type + ", name=" + contents[i].name);
            }


            if (part != null) {
                ArrayList<Content> filtered = new ArrayList<>();
                for (Content c : contents) {
                    Log.d("MyExerciseTimer", "Debug: c == null :: " + (c == null));
                    assert c != null;
                    if (c.part.equals(part))
                        filtered.add(c);
                }
                filtered.toArray(contents);
            } else {
                contents = defaultContents;
            }

            notifyDataSetChanged();
        } */
    }
}
