package house.thelittlemountaindev.islaamii;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import house.thelittlemountaindev.islaamii.R;

/**
 * Created by Charlie One on 12/7/2017.
 */

public class EveningFragment extends Fragment{

    public String text, counts;
    private TextView textViewzikr, textViewCount;

    public EveningFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_slide_2, container, false);

        text = getArguments().getString("zikrTextE");
        counts = getArguments().getString("zikrCountE");

        textViewzikr = (TextView) rootView.findViewById(R.id.tv_zikr);
        textViewCount = (TextView) rootView.findViewById(R.id.tv_count);

        textViewzikr.setText(text);
/*        if (Integer.parseInt(counts) > 0) {
            textViewCount.setText(counts);
        }
*/
        return rootView;
    }

}
