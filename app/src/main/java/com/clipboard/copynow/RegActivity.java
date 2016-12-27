package com.clipboard.copynow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegActivity extends BaseActivity implements View.OnClickListener{

    private static final String url = "http://yjham2002.woobi.co.kr/copynow/host.php?tr=107&";
    private EditText idf, mailf;
    private CheckBox ckbox;
    private Button yes, no;

    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.ok:
                String temp = idf.getText().toString();
                if(temp.indexOf("select")!=-1||temp.indexOf("union")!=-1||temp.indexOf("delete")!=-1||temp.indexOf("drop table")!=-1||temp.indexOf("DROP TABLE")!=-1||temp.indexOf("SELECT")!=-1||temp.indexOf("UNION")!=-1){
                    showToast("SQL Query와 유사한 내용은 입력할 수 없습니다.");
                    break;
                }
                temp = mailf.getText().toString();
                if(temp.indexOf("select")!=-1||temp.indexOf("union")!=-1||temp.indexOf("delete")!=-1||temp.indexOf("drop table")!=-1||temp.indexOf("DROP TABLE")!=-1||temp.indexOf("SELECT")!=-1||temp.indexOf("UNION")!=-1){
                    showToast("SQL Query와 유사한 내용은 입력할 수 없습니다.");
                    break;
                }
                if(idf.length()<=5) showToast("ID는 6글자 이상으로 설정하세요.");
                else if(mailf.length()<=5 || mailf.getText().toString().indexOf("@")==-1) showToast("정상적인 메일 주소를 입력하세요.");
                else if(!ckbox.isChecked()) showToast("메일 주소 수집에 동의하세요.");
                else if(mailf.getText().toString().indexOf(" ")!=-1 || idf.getText().toString().indexOf(" ")!=-1) showToast("공백없이 입력하세요.");
                else {
                    Communicator.getHttp(url + "id=" + idf.getText().toString() + "&mail=" + mailf.getText().toString(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            String jsonString = msg.getData().getString("jsonString");
                            if(jsonString.toString().equals("0")) {
                                showToast("Registered Successful.");
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }else{
                                showToast("Register Denied.");
                            }
                        }
                    });
                }
                break;
            case R.id.no:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idf = (EditText)findViewById(R.id.idText);
        mailf = (EditText)findViewById(R.id.mailText);
        yes = (Button)findViewById(R.id.ok);
        no = (Button)findViewById(R.id.no);
        ckbox = (CheckBox)findViewById(R.id.checkBox);

        ckbox.setChecked(false);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }
}