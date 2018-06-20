package mobileapp.taxiproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    Button detect;
    Button gallery;
    Button camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       detect = (Button) findViewById(R.id.detect);
       gallery = (Button) findViewById(R.id.gallery);
       camera = (Button) findViewById(R.id.camera);


//        detect.setOnClickListener((View.OnClickListener) this);
//        gallery.setOnClickListener((View.OnClickListener) this);
//        camera.setOnClickListener((View.OnClickListener)this);


        startActivity(new Intent(this, SplashActivity.class));

        detect.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                startActivity(intent);

            }
        });




    }


}
