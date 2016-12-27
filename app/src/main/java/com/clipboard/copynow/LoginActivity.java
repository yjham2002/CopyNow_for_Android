package com.clipboard.copynow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String pid;
    private boolean pbl;
    private static final String url = "http://yjham2002.woobi.co.kr/copynow/host.php?tr=108&";
    private EditText id;
    private Button login, sign;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button3:
                String temp = id.getText().toString();
                if(temp.indexOf("select")!=-1||temp.indexOf("union")!=-1||temp.indexOf("delete")!=-1||temp.indexOf("drop table")!=-1||temp.indexOf("DROP TABLE")!=-1||temp.indexOf("SELECT")!=-1||temp.indexOf("UNION")!=-1){
                    showToast("SQL Query와 유사한 내용은 입력할 수 없습니다.");
                    break;
                }
                if (id.length() <= 5) {showToast("ID를 입력하세요."); break;}
                Communicator.getHttp(url + "id=" + id.getText().toString(), new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String jsonString = msg.getData().getString("jsonString");
                        if (jsonString.toString().equals("1")) {
                            showToast("Logged In as " + id.getText().toString());
                            editor.putBoolean("auto", true);
                            editor.putString("ID", id.getText().toString());
                            editor.commit();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }else showToast("Access Denied.");
                    }
                });
                break;
            case R.id.button4:
                Intent ii = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(ii);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default: break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText)findViewById(R.id.editText2);
        login = (Button)findViewById(R.id.button3);
        sign = (Button)findViewById(R.id.button4);

        login.setOnClickListener(this);
        sign.setOnClickListener(this);

        prefs = getSharedPreferences("cpnow", MODE_PRIVATE);
        editor = prefs.edit();
        pbl = prefs.getBoolean("auto", false);
        pid = prefs.getString("ID", "#");

        if(pbl){
            showToast("Logged In as " + pid);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
