package house.thelittlemountaindev.islaamii;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Charlie One on 12/2/2017.
 */

public class AlarmActivity extends AppCompatActivity {
    AnimationDrawable mAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

    Button btn_dismiss = findViewById(R.id.btn_dissmis);
    btn_dismiss.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

     /*   LinearLayout linearLayout = findViewById(R.id.ll_background);
        linearLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.animated_bg));
        mAnimation = (AnimationDrawable) linearLayout.getBackground();
        linearLayout.post(new Starter());
    */
    }

    class Starter implements Runnable {
        public void run() {
            mAnimation.setEnterFadeDuration(800);
            mAnimation.setExitFadeDuration(800);
            mAnimation.start();
        }
    }
}
