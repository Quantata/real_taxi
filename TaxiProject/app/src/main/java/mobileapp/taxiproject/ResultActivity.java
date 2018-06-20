package mobileapp.taxiproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    boolean isTaxi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Button emergency = (Button) findViewById(R.id.emergency);
        TextView textResult = (TextView) findViewById(R.id.resultText);
        ImageView resultView = (ImageView) findViewById(R.id.resultView);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra("uri");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            resultView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        emergency.setOnClickListener(this);

        if(isTaxi==true){
            emergency.setVisibility(View.INVISIBLE);
            textResult.setText("택시가 맞습니다!\n안심하고 타세요!!");
        }
        else{
            emergency.setVisibility(View.VISIBLE);
            textResult.setText("택시가 아닙니다!\n조심하세요!!");
        }
    }

    public void onClick(View v){
        switch (v.getId()){

            case R.id.emergency:
                startActivity(new Intent("android.intent.action.DIAL",
                        Uri.parse("tel:112")));
                break;
        }
    }
}
