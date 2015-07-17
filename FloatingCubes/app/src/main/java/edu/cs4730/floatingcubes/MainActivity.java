package edu.cs4730.floatingcubes;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

/*
 * This example separates the activity from the renderer so it is easier to figure what
 * is the activity part (like the trigger) versa the renderer code.
 */

public class MainActivity extends CardboardActivity {

    private Vibrator vibrator;
    private CardboardOverlayView overlayView;
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_ui);

        //setup the cardbardview and set the renderer for it.
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new myStereoRenderer());
        setCardboardView(cardboardView);

        //this is overlay code from google, that allows us to put text on the "screen" easily.
        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Welcome to the demo.");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        overlayView.show3DToast("you triggered it.");

        // gives the user some feedback.
        vibrator.vibrate(50);
    }

}
