package edu.cs4730.flyingaroundacube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/*
 * This example separates the activity from the renderer so it is easier to figure what
 * is the activity part (like the trigger) versa the renderer code.
 */

public class MainActivity extends Activity {

   String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CardBoardActivity.class);
                // Set the request code to any code you like, you can identify the
                // callback via this code
                startActivity(intent);
            }
        });

        findViewById(R.id.day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DayDreamActivity.class);
                // Set the request code to any code you like, you can identify the
                // callback via this code
                startActivity(intent);
                //daydreamApi.
            }
        });
    }


}
