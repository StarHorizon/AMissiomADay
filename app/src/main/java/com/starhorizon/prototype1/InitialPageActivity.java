package com.starhorizon.prototype1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;


public class InitialPageActivity extends ActionBarActivity {

    private String taskName;
    private Button buttonCheck;
    private EditText editTextTaskContent;
    //public final static String EXTRA_MESSAGE="com.starhorizon.prototype1.MESSAGE";

    protected static SharedPreferences SP_task;
    protected static final String SP_string_data = "DATA";
    protected static final String SP_string_taskName="NAME";
    protected static final String SP_string_taskDate="DATE";
    protected static final String SP_string_taskStatus = "STATUS";
    protected static final String SP_string_taskProcess = "PROCESS";
    protected static String taskDate;
    protected static boolean taskSet;  // true for set7，對應 SP_string_taskStatus
    protected static boolean taskDone;  // true for done，對應 SP_string_taskProcess

    private void clearAllSharedPreferences(){
        taskSet = false;
        taskDone = false;
        taskDate = "";
        taskName = "";
        saveTaskNameAndDate();
        saveTaskStatusAndProcess();
    }

    private void gotoOtherPageIfNecessary(){
        String currentDate=getDate();
        if(! taskDate.equals("")){
            // 同一天
            if(currentDate.equals(taskDate)){
                // 任務已設置
                if(taskSet){
                    // 任務未完成
                    if(!taskDone){
                        // 前往第二個頁面
                        Intent intent=new Intent(InitialPageActivity.this,SecondPageActivity.class);
                        startActivity(intent);
                        InitialPageActivity.this.finish();
                    }
                    // 任務已完成
                    else{
                        // 前往第三個頁面
                        Intent intent=new Intent(InitialPageActivity.this,ThirdPageActivity.class);
                        startActivity(intent);
                        InitialPageActivity.this.finish();
                    }
                }
            }
            // 不同天
            else{
                // 重設任務設置狀態與進度
                taskSet=false;
                taskDone=false;
                saveTaskStatusAndProcess();
            }
        }
    }

    private void saveTaskNameAndDate(){
        SP_task=getSharedPreferences(SP_string_data, 0);
        SP_task.edit()
                .putString(SP_string_taskName, taskName)
                .putString(SP_string_taskDate,taskDate)
                .apply();
        Log.d("Task Date set to:", taskDate);
    }

    private void saveTaskStatusAndProcess(){
        SP_task=getSharedPreferences(SP_string_data, 0);
        SP_task.edit()
                .putBoolean(SP_string_taskStatus, taskSet)
                .putBoolean(SP_string_taskProcess, taskDone)
                .apply();
    }

    private void showTaskInfo(){
        Log.d("Test", "Current Date: "+getDate());
        if(! taskDate.equals("")) Log.d("Test","Task Date:"+taskDate);
        else Log.d("Test","Task Date: none");

        if(taskSet) Log.d("Test","Task Status: Task has been set.");
        else Log.d("Test","Task Status: Task has NOT been set.");

        if(taskDone) Log.d("Test","Task Process: Task has been done.");
        else Log.d("Test","Task Process: Task has NOT been done.");
    }

    private void readTaskInfo(){
        SP_task=getSharedPreferences(SP_string_data, 0);
        taskDate=SP_task.getString(SP_string_taskDate,"");
        taskSet=SP_task.getBoolean(SP_string_taskStatus, false);
        taskDone=SP_task.getBoolean(SP_string_taskProcess, false);
    }

    private String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date(System.currentTimeMillis());
        return formatter.format(currentDate);
    }

    private void setButtons(){
        buttonCheck.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 儲存 task 狀態
                taskSet = true;
                taskDone = false;
                taskDate = getDate();
                taskName = editTextTaskContent.getText().toString();
                saveTaskNameAndDate();
                saveTaskStatusAndProcess();

                // 前往第二個頁面
                Intent intent = new Intent(InitialPageActivity.this, SecondPageActivity.class);
                startActivity(intent);
                InitialPageActivity.this.finish();
            }
        });
    }

    private void findViews(){
        buttonCheck=(Button)findViewById(R.id.button);
        editTextTaskContent=(EditText)findViewById(R.id.editText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        readTaskInfo();
        showTaskInfo();
        gotoOtherPageIfNecessary();

        findViews();
        setButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_initial_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            clearAllSharedPreferences();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
