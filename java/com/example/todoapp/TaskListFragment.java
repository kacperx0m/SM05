package com.example.todoapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class TaskListFragment extends Fragment {

    public static final String KEY_EXTRA_TASK_ID = "tasklistfragment.task_id";
    private boolean subtitleVisible;
    private static final String SUBTITILE = "subtitle_visible";
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
//    public static final int new_task = 1000008; // dodane czy dziala?
//    public static final int show_subtitle = 1000005;
//    przed 31 dzialalo


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        updateView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(SUBTITILE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.new_task == item.getItemId()) {
            Task task = new Task();
            TaskStorage.getInstance().addTask(task);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(TaskListFragment.KEY_EXTRA_TASK_ID, task.getId());
            startActivity(intent);
            return true;
        }
        else if (R.id.show_subtitle == item.getItemId()) {
            subtitleVisible = !subtitleVisible;
            getActivity().invalidateOptionsMenu();
            updateSubtitle();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Task task;
        private TextView nameTextView;
        private TextView dateTextView;
        private ImageView iconImageView;
        private CheckBox checkBox;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);

            nameTextView = itemView.findViewById(R.id.task_item_name);
            dateTextView = itemView.findViewById(R.id.task_item_date);
            iconImageView = itemView.findViewById(R.id.miniature);
            checkBox = itemView.findViewById(R.id.task_checkbox);
        }

        public void bind(Task task) {
            this.task = task;
            nameTextView.setText(task.getName());
            dateTextView.setText(task.getDate().toString());
            if (task.getCategory().equals(Category.HOME)) {
                iconImageView.setImageResource(R.drawable.ic_house);
            } else {
                iconImageView.setImageResource(R.drawable.ic_studies);
            }
            checkBox.setChecked(task.isDone());
            if (checkBox.isChecked()){
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(KEY_EXTRA_TASK_ID, task.getId());
            startActivity(intent);
        }

        // ????
        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> tasks;

        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            Task task = tasks.get(position);

            CheckBox checkBox = holder.getCheckBox();
            checkBox.setChecked(tasks.get(position).isDone());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    tasks.get(holder.getBindingAdapterPosition()).setDone(isChecked));

            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITILE, subtitleVisible);
    }

    private void updateView() {
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();
        if (adapter == null) {
            adapter = new TaskAdapter(tasks);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
//        wywolanie tej metody wywala aplikacje pkt 35
        updateSubtitle();
    }


    public void updateSubtitle() {
        TaskStorage taskStorage = TaskStorage.getInstance();
        List<Task> tasks = taskStorage.getTasks();
        int todoTasksCount = 0;
        for (Task task: tasks) {
            if (!task.isDone()) {
                todoTasksCount++;
            }
        }
        String subtitle = getString(R.string.subtitle_format, todoTasksCount);
        if (!subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null && appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
        }
    }
}
