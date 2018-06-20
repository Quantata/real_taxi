package mobileapp.taxiproject;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button emergency = (Button) findViewById(R.id.emergency);
        TextView textResult = (TextView) findViewById(R.id.resultText);

        emergency.setOnClickListener(this);
        boolean isTaxi = false;


        if(isTaxi==true){
            emergency.setVisibility(View.INVISIBLE);
            textResult.setText("택시가 맞습니다!");
        }
        else{
            emergency.setVisibility(View.VISIBLE);
            textResult.setText("헉!택시가 아닙니다! 조심하세요!");
        }
    }

    public void onClick(View v){
        switch (v.getId()){

            case R.id.emergency:
                startActivity(new Intent("android.intent.action.DIAL",
                        Uri.parse("tel:112")));
                break;

            case R.id.backhome:
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
                onBackPressed();
                break;
        }
    }
}
