package com.clipboard.copynow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/copynow/host.php?tr=106&id=";
    private static final String inurl = "http://yjham2002.woobi.co.kr/copynow/host.php?tr=109";
    private ListView mainList;
    private ProgressBar pbar;
    private Button insert, logout, what;
    private ListViewAdapter mListAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private EditText input;

    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                Intent i2 = new Intent(MainActivity.this, EnActivity.class);
                startActivity(i2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.button:
                String temp = input.getText().toString();
                if(input.length() < 5){
                    showToast("5자 이상 내용을 입력하세요.");
                    break;
                }
                if(temp.indexOf("select")!=-1||temp.indexOf("union")!=-1||temp.indexOf("delete")!=-1||temp.indexOf("drop table")!=-1||temp.indexOf("DROP TABLE")!=-1||temp.indexOf("SELECT")!=-1||temp.indexOf("UNION")!=-1){
                    showToast("SQL Query와 유사한 내용은 입력할 수 없습니다.");
                    break;
                }
                Calendar cal = Calendar.getInstance();
                String dateSet = Integer.toString(cal.get(Calendar.YEAR))+"-"+Integer.toString(cal.get(Calendar.MONTH)+1)+"-"+
                        Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+" "+Integer.toString(cal.get(Calendar.HOUR_OF_DAY))+":"+Integer.toString(cal.get(Calendar.MINUTE))+":"+Integer.toString(cal.get(Calendar.SECOND));
                HashMap<String, String> map = new HashMap<>();
                map.put("id", prefs.getString("ID", "#"));
                map.put("date", dateSet);
                map.put("cont", input.getText().toString());
                new Communicator().postHttp(inurl, map, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        input.setText("");
                        loadList();
                    }
                });
                break;
            case R.id.button2:
                editor.putBoolean("auto", false);
                editor.putString("ID", "#");
                editor.commit();
                showToast("Logged out");
                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default: break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText)findViewById(R.id.editText);
        mainList = (ListView)findViewById(R.id.listView);
        pbar = (ProgressBar)findViewById(R.id.pbar);
        insert = (Button)findViewById(R.id.button);
        logout = (Button)findViewById(R.id.button2);
        what = (Button)findViewById(R.id.button1);
        mListAdapter = new ListViewAdapter(this);

        mainList.setEmptyView(findViewById(R.id.textView));
        mainList.setAdapter(mListAdapter);
        insert.setOnClickListener(this);
        logout.setOnClickListener(this);
        what.setOnClickListener(this);
        prefs = getSharedPreferences("cpnow", MODE_PRIVATE);
        editor = prefs.edit();

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final ListData mData = mListAdapter.mListData.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("클립보드에 복사하시겠습니까?");
                builder.setCancelable(true);
                builder
                        .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setClipboard(getApplicationContext(), mData.content);
                                showToast("복사됨");
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void setClipboard(Context context,String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadList();
    }

    public void loadList(){
        pbar.setVisibility(View.VISIBLE);
        Communicator comm = new Communicator();
        comm.getHttp(url+ prefs.getString("ID", "#"), new Handler() {
            public void handleMessage(Message msg) {
                String jsonString = msg.getData().getString("jsonString");
                mListAdapter.mListData.clear();
                try {
                    JSONArray json_arr = new JSONArray(jsonString);
                    for (int i = json_arr.length() - 1; i >= 0; i--) {
                        JSONObject json_list = json_arr.getJSONObject(i);
                        mListAdapter.addItem(json_list.getInt("pkey"), json_list.getString("contents"), json_list.getString("dates"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mListAdapter.dataChange();
                    pbar.setVisibility(View.GONE);
                }
            }
        });
    }

    public boolean mFlag;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0){
                mFlag=false;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            if(!mFlag) {
                Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                mFlag = true;
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
