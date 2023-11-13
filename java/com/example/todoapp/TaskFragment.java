package com.example.todoapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity; //ten import

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TaskFragment extends Fragment {

    private static final String ARG_TASK_ID = "task_id";
    private EditText nameField;
    private EditText dateField;
    private CheckBox doneCheckBox;
    private Task task;
    private final Calendar calendar = Calendar.getInstance();
    private Spinner spinner;
//    private static final int simple_spinner_item ;

    public TaskFragment() {}

    public static TaskFragment newInstance(UUID taskId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TASK_ID, taskId);
        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);
        return taskFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        task = TaskStorage.getInstance().getTask(taskId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        dateField = view.findViewById(R.id.task_date);
        nameField = view.findViewById(R.id.task_name);
        doneCheckBox = view.findViewById(R.id.task_done);
        spinner = view.findViewById(R.id.spinner_category);
        nameField.setText(task.getName());
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                task.getName();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                task.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                task.getName();
            }
        });

//        Category[] values = Category.values();
//        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, values);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

//        spinner.setAdapter(new ArrayAdapter<Category>(this, R.layout.simple_spinner_item, Category.values()));

        spinner.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, Category.values()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                task.setCategory(Category.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(task.getCategory().ordinal());

        DatePickerDialog.OnDateSetListener date = (view12, year, month, day) -> {
          calendar.set(Calendar.YEAR, year);
          calendar.set(Calendar.MONDAY, month);
          calendar.set(Calendar.DAY_OF_MONTH, day);
          setupDateFieldValue(calendar.getTime());
          task.setDate(calendar.getTime());
        };
        dateField.setOnClickListener(view1 ->
                new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .show());
        setupDateFieldValue(task.getDate());


        doneCheckBox.setChecked(task.isDone());
        doneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setDone(isChecked));

        return view;
    }

    private void setupDateFieldValue(Date date) {
        Locale locale = new Locale("pl", "PL");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
        dateField.setText(dateFormat.format(date));
    }

}
