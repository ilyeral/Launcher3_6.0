package com.android.launcher3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.launcher3.model.BackupsInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BackupsActivity extends AppCompatActivity {
    ListView backupsList;
    private List<BackupsInfo> backupsInfoList = new ArrayList<BackupsInfo>();
    BaseAdapter baseAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backups);
        TextView t=(TextView)findViewById(R.id.title);
        t.setText("备份");
        init();
        accessBackupInfoRequest("");
    }
    void init(){
        backupsList=(ListView)findViewById(R.id.backups_list);
        backupsList.setAdapter( baseAdapter=new BaseAdapter(){
            //返回多少条记录
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return backupsInfoList.size();
            }
            //每一个item项，返回一次界面
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = null;
                //布局不变，数据变

                //如果缓存为空，我们生成新的布局作为1个item
                if(convertView==null){
                    Log.i("info:", "没有缓存，重新生成"+position);
                    LayoutInflater inflater = BackupsActivity.this.getLayoutInflater();
                    //因为getView()返回的对象，adapter会自动赋给ListView
                    view = inflater.inflate(R.layout.backups_item, null);
                }else{
                    Log.i("info:", "有缓存，不需要重新生成"+position);
                    view = convertView;
                }
                final BackupsInfo backupsInfo = backupsInfoList.get(position);
                TextView tv_title = (TextView)view.findViewById(R.id.title);
                tv_title.setText(backupsInfo.getTitle());

                TextView tv_datetime = (TextView)view.findViewById(R.id.summary);
                tv_datetime.setText(backupsInfo.getTime());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("icon", null);
                        bundle.putInt("age", 40);
                        bundle.putFloat("weight", 70.4f);
                        intent.putExtras(bundle);
                        intent.setClass(BackupsActivity.this, SettingsActivityBackups.class);
                        BackupsActivity.this.startActivity(intent);
                    }
                });
                return view;
            }

            @Override
            public Object getItem(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }

        } );
    }
    public void BackUpInfo(View v){
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入消息")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        saveBackupInfoRequest(editText.getText().toString());
                        Log.e("request","title:"+editText.getText().toString());
                    }
                }).setNegativeButton("取消",null).show();
    }
    public void saveBackupInfoRequest(String title){
        if(title==null)return;
        SimpleDateFormat   formatter   =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String   time   =   formatter.format(curDate);
        String backupInfo=BackupInfoUtil.BackupIconInfo();
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("title",title);//传递键值对参数
        formBody.add("time",time);//传递键值对参数
        formBody.add("backupInfo",backupInfo);//传递键值对参数
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://192.168.43.140:8080/index/aaa")
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.sendEmptyMessage(1);
            }
        });
    }
    public void accessBackupInfoRequest(String user){
        final String[] backupInfos = {null};
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://192.168.43.140:8080/index/bbb")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                List<BackupsInfo> list=gson.fromJson(response.body().string(),new TypeToken<ArrayList<BackupsInfo>>(){}.getType());
                Log.e("backupInfos","list:"+list.toString());
                backupsInfoList.clear();
                backupsInfoList.addAll(list);
                Log.e("backupInfos","list:"+backupsInfoList.size());
                mHandler.sendEmptyMessage(3);
            }
        });
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "备份失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT).show();
                    accessBackupInfoRequest("");
                    break;
                case 3:
                    baseAdapter.notifyDataSetInvalidated();
                    break;
                default:
                    break;
            }
        }
    };
}
