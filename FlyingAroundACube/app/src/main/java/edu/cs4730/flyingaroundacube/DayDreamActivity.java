package edu.cs4730.flyingaroundacube;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;

/*
 * This example separates the activity from the renderer so it is easier to figure what
 * is the activity part (like the trigger) versa the renderer code.
 */

public class DayDreamActivity extends GvrActivity {

    private Vibrator vibrator;
    private CardboardOverlayView overlayView;
    String TAG = "DayDreamActivity";
    private myStereoRenderer mStereoRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_ui);

        //setup the cardbardview and set the renderer for it.
        GvrView gvrView = (GvrView) findViewById(R.id.cardboard_view);
        mStereoRenderer = new myStereoRenderer();
        gvrView.setRenderer(mStereoRenderer);
        setGvrView(gvrView);

        //this is overlay code from google, that allows us to put text on the "screen" easily.
        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Welcome to the demo.");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    //add daydream controller code.


}
