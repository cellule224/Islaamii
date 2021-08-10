package house.thelittlemountaindev.islaamii;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Charlie One on 12/7/2017.
 */

public class OnBoardActivity extends AppCompatActivity {

    private boolean animationStarted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboard);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus || animationStarted) {
            return;
        }else {
            //animate();
            startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
            finish();
        }
        super.onWindowFocusChanged(hasFocus);

    }

    private void animate() {
        final ImageView imageView = findViewById(R.id.iv_onboard_logo);

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 1.2f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(15000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                imageView.setScaleX(animatedValue);
                imageView.setScaleY(animatedValue);
            }

        });

        animator.start();

        ViewGroup viewGroup = findViewById(R.id.onboarding_container);

        ViewCompat.animate(imageView)
                .translationY(500)
                .setStartDelay(300)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator(1.2f))
                .start();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof TextView)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((300 * i) + 500)
                        .setDuration(500);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((300 * i) + 500)
                        .setDuration(300);
            }
            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }

        //startActivity(new Intent(OnBoardActivity.this, MainActivity.class));
    }
}
