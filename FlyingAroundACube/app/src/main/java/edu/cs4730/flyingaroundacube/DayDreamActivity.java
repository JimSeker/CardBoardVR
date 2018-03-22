package edu.cs4730.flyingaroundacube;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.controller.Controller;
import com.google.vr.sdk.controller.ControllerManager;
import com.google.vr.sdk.controller.Controller.ConnectionStates;
import com.google.vr.sdk.controller.ControllerManager.ApiStatus;
/*
 * This example separates the activity from the renderer so it is easier to figure what
 * is the activity part (like the trigger) versa the renderer code.
 */

public class DayDreamActivity extends GvrActivity {

    private Vibrator vibrator;
    private CardboardOverlayView overlayView;
    String TAG = "DayDreamActivity";
    private myStereoRenderer mStereoRenderer;

    // These two objects are the primary APIs for interacting with the Daydream controller.
    private ControllerManager controllerManager;
    private Controller controller;

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

        // Start the ControllerManager and acquire a Controller object which represents a single
        // physical controller. Bind our listener to the ControllerManager and Controller.
        EventListener listener = new EventListener();
        controllerManager = new ControllerManager(this, listener);
        controller = controllerManager.getController();
        controller.setEventListener(listener);


    }

    //add daydream controller code.
    // We receive all events from the Controller through this listener. In this example, our
    // listener handles both ControllerManager.EventListener and Controller.EventListener events.

    private class EventListener extends Controller.EventListener
        implements ControllerManager.EventListener {

        // The status of the overall controller API. This is primarily used for error handling since
        // it rarely changes.
        private String apiStatus;

        // The state of a specific Controller connection.
        private int controllerState = ConnectionStates.DISCONNECTED;

        @Override
        public void onApiStatusChanged(int state) {
            apiStatus = ApiStatus.toString(state);
            Log.d(TAG, "APIstatechanged to " + state);
        }

        @Override
        public void onConnectionStateChanged(int state) {
            controllerState = state;
            Log.d(TAG, "statechanged to " + state);
        }

        @Override
        public void onRecentered() {
            // In a real GVR application, this would have implicitly called recenterHeadTracker().
            // Most apps don't care about this, but apps that want to implement custom behavior when a
            // recentering occurs should use this callback.
            //controllerOrientationView.resetYaw();
        }

        @Override
        public void onUpdate() {
            controller.update();  //onUpdate tells you to update.  OnUpdate should update the controller itself, but it doesn't.  dumb.

            if (controllerState == ConnectionStates.DISCONNECTED) {
                Log.d(TAG, "State is Disconnected");
            }

            if (controller.clickButtonState) {
                overlayView.show3DToast("you trigged a move.");
                mStereoRenderer.move();
                // gives the user some feedback.
                vibrator.vibrate(50);
            }

        }
    }

}
