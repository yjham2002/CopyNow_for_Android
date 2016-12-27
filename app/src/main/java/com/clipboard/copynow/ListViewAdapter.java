package com.clipboard.copynow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private static final String url = "http://yjham2002.woobi.co.kr/copynow/host.php?tr=110&id=";
    public Context mContext = null;
    public ArrayList<ListData> mListData = new ArrayList<>();

    public ListViewAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }
    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(int idx, String content, String date){
        ListData addInfo = new ListData();
        addInfo.idx = idx;
        addInfo.content = content;
        addInfo.date = date;
        mListData.add(addInfo);
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.delete = (Button)convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final ListData mData = mListData.get(position);
        holder.content.setText(mData.content);
        holder.date.setText(TIME_MAXIMUM.relative(mData.date));
        holder.delete.setFocusable(false);
        holder.delete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Communicator.getHttp(url + mData.idx, new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(mContext instanceof MainActivity){
                            ((MainActivity)mContext).loadList();
                            ((MainActivity)mContext).showToast("삭제됨");
                        }
                    }
                });
            }
        });
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.push_out);
        anim.setDuration(300);
        convertView.startAnimation(anim);

        return convertView;
    }

    private class ViewHolder {
        public TextView content;
        public TextView date;
        public Button delete;
    }

}