package house.thelittlemountaindev.islaamii;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Charlie One on 11/13/2017.
 */

public class MorningFragment extends Fragment {

    public String text, counts;
    private TextView textViewzikr, textViewCount;

    public MorningFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_slide, container, false);

        text = getArguments().getString("zikrText");
        counts = getArguments().getString("zikrCount");

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
