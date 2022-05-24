package com.LEGENO.SaveTheTime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
class menuListviewAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<String> m_oData = null;
    private int nListCnt = 0;

    public menuListviewAdapter(Context context,ArrayList<String> Group) {
        m_oData = Group;
        nListCnt = m_oData.size();
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        Log.d("Lisst", "adapter Receive :\n" + m_oData.toString());
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.menu_listview, parent, false);
        }

        TextView GroupName = (TextView) convertView.findViewById(R.id.menulisttext);
        ImageView Image = (ImageView) convertView.findViewById(R.id.groupImage);
        GroupName.setText(m_oData.get(position));
        Image.setImageResource(R.drawable.ic_group);
        Image.setColorFilter(Color.parseColor("#846243"), PorterDuff.Mode.SRC_IN);

        return convertView;
    }

    public void notifyDataSetChanged(ArrayList<String> gList) {
    }

}
