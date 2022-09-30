package com.nogravity.today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;
    FloatingActionButton taskDialog,delete;
    ArrayList<String> taskNames = new ArrayList<>();
    SharedPreferences myPrefs;
    TextView date_day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();


        setContentView(R.layout.activity_main);

        taskDialog = findViewById(R.id.taskdialog) ;
        delete = findViewById(R.id.delete);
        recyclerView = findViewById(R.id.recyclerview);
        date_day = findViewById(R.id.day_date);
        String date = new SimpleDateFormat("dd-MM-yyyy EEE",Locale.getDefault()).format(new Date());
        date_day.setText(date);



        LoadTask();

        taskDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.taskdialog);
                EditText task = dialog.findViewById(R.id.newtask);
                Button addTask = dialog.findViewById(R.id.addTask);

                addTask.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {

                        String taskData = task.getText().toString();
                        if(taskData.equals("")){
                            Toast.makeText(getApplicationContext(), "Task Cannot Be Empty", Toast.LENGTH_SHORT).show();
                        }else{
                            taskNames.add(taskNames.size(),taskData);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            adapter = new Adapter(MainActivity.this,taskNames);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            SaveTask();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete All Tasks ? ")
                        .setIcon(R.drawable.delete_icon)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (taskNames.size() == 0)
                                    Toast.makeText(MainActivity.this, "No Task Found", Toast.LENGTH_SHORT).show();
                            else

                            {
                                SharedPreferences Prefs = getSharedPreferences("CompletedTask", MODE_PRIVATE);
                                SharedPreferences.Editor editor = Prefs.edit();

                                int loop = Prefs.getInt("numberOfTask",0);

                                //for(int i = 0;i<loop;i++){
                                    editor.remove("checked");
                                //}

                                editor.apply();

                                taskNames.clear();
                                SaveTask();
                                adapter.notifyDataSetChanged();
                            }
                        }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();
            }
        });


    }

    void SaveTask(){
        myPrefs = getSharedPreferences("task",MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskNames);
        editor.putString("tasknames",json);
        editor.apply();



    }

    void LoadTask(){
        myPrefs = getSharedPreferences("task",MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPrefs.getString("tasknames",null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        taskNames = gson.fromJson(json,type);

        if(taskNames == null)
            taskNames = new ArrayList<>();

        adapter = new Adapter(this,taskNames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);


    }




}