package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class TaskListActivity extends SingleFragmentActivity {

//    ??????
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

//    return z taskfragment na to
    @Override
    protected Fragment createFragment() {
        return new TaskListFragment();
    }
}