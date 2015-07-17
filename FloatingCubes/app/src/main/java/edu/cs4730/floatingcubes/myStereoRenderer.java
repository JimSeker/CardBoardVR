package edu.cs4730.floatingcubes;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * This is implementation of the Render, using Cardboards StereoRenderer.
 * Code was used from the cardboardsample and combined with the opengl30Cube.
 * The cube and mycolor code is unchanged from the opengl30Cube example.
 *
 */
public class myStereoRenderer implements CardboardView.StereoRenderer{
    private static String TAG = "StereoRenderer";
    private float mAngle = 0.4f;  //spin of the cube.

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;

    public Cube mCube;
    public Cube mCube2;
    public Floor mFloor;

    private float objectDistance = 6f;
    private float floorDepth = 20f;

    private float[] mRotationMatrix;
    private float[] mRotationMatrixCube2;
    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelview;
    private float[] mMVPMatrix;

    private float[] modelFloor;


    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 2.0f, 0.0f, 1.0f };

    private final float[] lightPosInEyeSpace = new float[4];

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    public static int LoadShader(int type, String shaderSrc) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES30.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc);

        // Compile the shader
        GLES30.glCompileShader(shader);

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, "Erorr!!!!");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {

        //rotate the cube, mangle is how fast, x,y,z which directions it rotates.
        Matrix.rotateM(mRotationMatrix, 0, mAngle, 0.7f, 0.7f, 1.0f);
        //rotate cube2, mangle is how fast, x,y,z which directions it rotates.
        Matrix.rotateM(mRotationMatrixCube2, 0, -mAngle, 1.0f, 0.5f, 0.5f);

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(headView, 0);
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // combine the model with the view matrix to create the modelview matreix
        Matrix.multiplyMM(modelview, 0, view, 0, mRotationMatrix, 0);

        // combine the model-view with the projection matrix
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);

        //finally draw the cube with the full Model-view-projection matrix.
        mCube.draw(mMVPMatrix);

        //now create the mvp matrix for cube2 and then draw it.
        // combine the model with the view matrix to create the modelview matreix
        Matrix.multiplyMM(modelview, 0, view, 0, mRotationMatrixCube2, 0);

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);
        mCube2.draw(mMVPMatrix);

        //now calculate for the floor
        Matrix.multiplyMM(modelview, 0, view, 0, modelFloor, 0);

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, modelview, 0);
        mFloor.drawFloor(mMVPMatrix, modelFloor, modelview, lightPosInEyeSpace);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        //no clue, example code was blank here.
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG, "onSurfaceChanged");  //should not happen, set landscape in the manifest file.
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        mRotationMatrix = new float[16];
        mRotationMatrixCube2 = new float[16];
        camera = new float[16];
        view = new float[16];
        mMVPMatrix = new float[16];
        modelview = new float[16];
        headView = new float[16];
        modelFloor = new float[16];

        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
        //initialize the cube code for drawing.
        mCube = new Cube();
        // Object first appears directly in front of user.
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.translateM(mRotationMatrix, 0, 0, 0, -objectDistance);
        //Don't think I need cube2, but I need to matrix to place the second cube.
        mCube2 = new Cube();
        Matrix.setIdentityM(mRotationMatrixCube2, 0);
        //                                         X      Y  Z
        Matrix.translateM(mRotationMatrixCube2, 0, -2.0f, 0, -objectDistance);

        //floor object
        mFloor = new Floor();
        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.

    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }
}
