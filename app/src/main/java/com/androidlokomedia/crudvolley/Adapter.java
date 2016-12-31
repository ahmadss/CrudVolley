package com.androidlokomedia.crudvolley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ahmad on 28/12/2016.
 */
public class Adapter extends BaseAdapter {

    private Context context;
    private List<Data> datas;
    private LayoutInflater inflater;

    public Adapter(Context context, List<Data> dataList) {
        this.context = context;
        this.datas = dataList;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) convertView = inflater.inflate(R.layout.list_row, null);

            TextView id = (TextView)convertView.findViewById(R.id.id);
            TextView email = (TextView)convertView.findViewById(R.id.email);
            TextView password = (TextView)convertView.findViewById(R.id.password);

            Data data = datas.get(position);

            id.setText(Integer.toString(data.getId()));
            email.setText(data.getEmail());
            password.setText(data.getPassword());

        return convertView;
    }
}
