package mobileapp.taxiproject;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);

        ImageView taxiIv = (ImageView)findViewById(R.id.taxiIv);
        taxiIv.setBackground(new ShapeDrawable(new OvalShape()));
        taxiIv.setClipToOutline(true);
        taxiIv.bringToFront();


    }
}
