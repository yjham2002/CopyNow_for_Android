package com.clipboard.copynow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnActivity extends BaseActivity implements View.OnClickListener{

    private Button encode, decode, back;
    private EditText key, text;

    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public String fillText(){
        StringBuffer temp = new StringBuffer(key.getText().toString());
        if(temp.length() < 32)
            for(int i = 0; 32 != temp.length(); i++) temp.append("0");
        return temp.toString();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button2:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.decode:
                if(key.length() < 5) {
                    showToast("6자리 이상의 키를 입력하세요.");
                    break;
                }
                try {
                    text.setText(new AES256Cipher(fillText()).AES_Decode(text.getText().toString()));
                }catch(javax.crypto.BadPaddingException a){
                    showToast("올바르지 않은 키입니다.");
                }
                catch(Exception e){
                    Log.e("CP", e.toString());
                }
                break;
            case R.id.encode:
                if(key.length() < 5){
                    showToast("6자리 이상의 키를 입력하세요.");
                    break;
                }
                try {
                    text.setText(new AES256Cipher(fillText()).AES_Encode(text.getText().toString()));
                }catch(Exception e){
                    Log.e("CP", e.toString());
                }
                break;
            default: break;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enc);

        key = (EditText)findViewById(R.id.key);
        text = (EditText)findViewById(R.id.text);

        encode = (Button)findViewById(R.id.encode);
        decode = (Button)findViewById(R.id.decode);
        back = (Button)findViewById(R.id.button2);

        encode.setOnClickListener(this);
        decode.setOnClickListener(this);
        back.setOnClickListener(this);
    }
}