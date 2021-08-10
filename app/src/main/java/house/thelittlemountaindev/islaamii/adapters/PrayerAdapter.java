package house.thelittlemountaindev.islaamii.adapters;

/**
 * Created by Charlie One on 10/21/2017.
 */

import java.util.ArrayList;
import house.thelittlemountaindev.islaamii.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PrayerAdapter extends BaseAdapter{

    ArrayList<String> prayerTimes;
    ArrayList<String> prayerNames;
    private Context context;

    public PrayerAdapter(Context context, ArrayList<String> prayerNames, ArrayList<String> prayerTimes) {
        super();
        this.context = context;
        this.prayerTimes = prayerTimes;
        this.prayerNames = prayerNames;

        // TODO Auto-generated constructor stub
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return prayerNames.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return prayerNames.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.prayer_list_item, parent, false);

        TextView prName = (TextView) row.findViewById(R.id.prayerName);
        TextView prTime = (TextView) row.findViewById(R.id.prayerTime);

        //Get the prayer Time array Object
        ArrayList<String> times = prayerTimes;
        ArrayList<String> names = prayerNames;

        //Set Text in listView
        prTime.setText(times.get(position));
        prName.setText(names.get(position));
/*
        LinearLayout linearLayout = (LinearLayout) row.findViewById(R.id.ll_hidden_prayertimes);
        linearLayout.setVisibility(View.VISIBLE);
*/
        return row;
    }


}
