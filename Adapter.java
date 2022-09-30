package com.nogravity.today;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    Context context;
    ArrayList<String> Tasknames;

    ArrayList<Integer> checked = new ArrayList<>(100);

    public Adapter(Context context,  ArrayList<String> tasknames) {
        
        this.context = context;
        Tasknames = tasknames;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleritem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        SharedPreferences myPrefs = context.getSharedPreferences("CompletedTask", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();


  /*          if(!myPrefs.getString("checked" + position + "", "").equals("")){
                Toast.makeText(context, myPrefs.getString("checked" + position + "", "ff"), Toast.LENGTH_SHORT).show();
                holder.checkBox.setChecked(true);
                holder.tasknames.setPaintFlags(holder.tasknames.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }*/


        Gson gson = new Gson();
        String json = myPrefs.getString("checked",null);
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        checked = gson.fromJson(json,type);

        if(checked == null)
            checked = new ArrayList<>(100);

        if(checked.size()>position){
            if(checked.get(position) == position){
                holder.checkBox.setChecked(true);
                holder.tasknames.setPaintFlags(holder.tasknames.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }



        holder.tasknames.setText(Tasknames.get(position));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               if(holder.checkBox.isChecked()){

                   //editor.putInt("numberOfTask",myPrefs.getInt("numberOfTask",0)+1);
                   //editor.putString("checked"+position+"","checked");

                   checked.add(position,position);
                   Gson gson = new Gson();
                   String json = gson.toJson(checked);
                   editor.putString("checked",json);

                   holder.tasknames.setPaintFlags(holder.tasknames.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


               }else{

                   //editor.putInt("numberOfTask",myPrefs.getInt("numberOfTask",0)-1);

                   //editor.putString("checked"+position+"","");

                   checked.remove(position);
                   Gson gson = new Gson();
                   String json = gson.toJson(checked);
                   editor.putString("checked",json);

                   holder.tasknames.setPaintFlags(0);


               }

               editor.apply();


           }
       });

        holder.rlo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                SharedPreferences.Editor editor2 = myPrefs.edit();


                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete ? ")
                        .setIcon(R.drawable.delete_icon)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Tasknames.remove(position);
                                editor2.remove("checked"+position+"");
                                notifyItemRemoved(position);
                                editor2.apply();

                                SharedPreferences sharedPreferences = context.getSharedPreferences("task",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                Gson gson = new Gson();
                                String json = gson.toJson(Tasknames);
                                editor.putString("tasknames",json);
                                editor.apply();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });


                builder.show();

                return true;


            }
        });







    }

    @Override
    public int getItemCount() {
        return Tasknames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tasknames;
        CheckBox checkBox;
        RelativeLayout rlo;

        FloatingActionButton uncheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tasknames = itemView.findViewById(R.id.taskname);
            checkBox = itemView.findViewById(R.id.checkbox);
            rlo = itemView.findViewById(R.id.rlo);
            uncheck = itemView.findViewById(R.id.uncheck);

        }
    }


}
