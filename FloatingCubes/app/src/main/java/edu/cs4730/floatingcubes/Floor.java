package edu.cs4730.floatingcubes;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Seker on 7/17/2015.
 */
public class Floor {

    private int mProgramObject;
    private int mMVPMatrixHandle;

    private int floorPositionParam;
    private int floorNormalParam;
    private int floorColorParam;
    private int floorModelParam;
    private int floorModelViewParam;
    private int floorModelViewProjectionParam;
    private int floorLightPosParam;
    private static final int COORDS_PER_VERTEX = 3;



    private FloatBuffer floorVertices;
    private FloatBuffer floorColors;
    private FloatBuffer floorNormals;

    public float[] FLOOR_COORDS = new float[] {
            200f, 0, -200f,
            -200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, -200f,
            -200f, 0, 200f,
            200f, 0, 200f,
    };

    public float[] FLOOR_NORMALS = new float[] {
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };

    public float[] FLOOR_COLORS = new float[] {
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
            0.0f, 0.3398f, 0.9023f, 1.0f,
    };
    //fragment shader
    String grid_fragment =
        "precision mediump float;       \n"
        + "varying vec4 v_Color;          \n"
        + "varying vec3 v_Grid;           \n"
        + "void main() {                  \n"
        + "  float depth = gl_FragCoord.z / gl_FragCoord.w; // Calculate world-space distance. \n"
        + "  if ((mod(abs(v_Grid.x), 10.0) < 0.1) || (mod(abs(v_Grid.z), 10.0) < 0.1)) {       \n"
        + "     gl_FragColor = max(0.0, (90.0-depth) / 90.0) * vec4(1.0, 1.0, 1.0, 1.0)        \n"
        + "     + min(1.0, depth / 90.0) * v_Color;                                            \n"
        + "  } else {                         \n"
        + "    gl_FragColor = v_Color;        \n"
        + " }                                 \n"
        + "}                                  \n" ;

    //vertex shader
    String light_vertex =
        "uniform mat4 u_Model;              \n" +
        "uniform mat4 u_MVP;                \n" +
        "uniform mat4 u_MVMatrix;           \n" +
        "uniform vec3 u_LightPos;           \n" +
        "attribute vec4 a_Position;         \n" +
        "attribute vec4 a_Color;            \n" +
        "attribute vec3 a_Normal;           \n" +
        "varying vec4 v_Color;              \n" +
        "varying vec3 v_Grid;               \n" +

        "void main() {                                                         \n" +
        "   v_Grid = vec3(u_Model * a_Position);                               \n" +
        "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n" +
        "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n" +
        "   float distance = length(u_LightPos - modelViewVertex);             \n" +
        "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n" +
        "   float diffuse = max(dot(modelViewNormal, lightVector), 0.5);         \n" +
        "   diffuse = diffuse * (1.0 / (1.0 + (0.00001 * distance * distance))); \n" +
        "   v_Color = a_Color * diffuse;                                         \n" +
        "   gl_Position = u_MVP * a_Position;                                    \n" +
        "}                                                                       \n";

    String TAG = "Floor";

    public Floor() {
        // make a floor
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        floorVertices = bbFloorVertices.asFloatBuffer();
        floorVertices.put(FLOOR_COORDS);
        floorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        floorNormals = bbFloorNormals.asFloatBuffer();
        floorNormals.put(FLOOR_NORMALS);
        floorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        floorColors = bbFloorColors.asFloatBuffer();
        floorColors.put(FLOOR_COLORS);
        floorColors.position(0);

        //now setup the shaders and program object
        int vertexShader = myStereoRenderer.LoadShader(GLES30.GL_VERTEX_SHADER, light_vertex);
        int gridShader = myStereoRenderer.LoadShader(GLES30.GL_FRAGMENT_SHADER, grid_fragment);
        int programObject;
        int[] linked = new int[1];

        // Create the program object
        programObject = GLES30.glCreateProgram();

        if (programObject == 0) {
            Log.e(TAG, "So some kind of error, but what?");
            return;
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, gridShader);

        // Link the program
        GLES30.glLinkProgram(programObject);

        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject));
            GLES30.glDeleteProgram(programObject);
            return;
        }

        // Store the program object
        mProgramObject = programObject;
    }



    /**
     * Draw the floor.
     *
     * <p>This feeds in data for the floor into the shader. Note that this doesn't feed in data about
     * position of the light, so if we rewrite our code to draw the floor first, the lighting might
     * look strange.
     */
    public void drawFloor(float[] modelViewProjection, float[] modelFloor, float[] modelView, float[] lightPosInEyeSpace ) {
        GLES30.glUseProgram(mProgramObject);
        floorModelParam = GLES30.glGetUniformLocation(mProgramObject, "u_Model");
        floorModelViewParam = GLES30.glGetUniformLocation(mProgramObject, "u_MVMatrix");
        floorModelViewProjectionParam = GLES30.glGetUniformLocation(mProgramObject, "u_MVP");
        floorLightPosParam = GLES30.glGetUniformLocation(mProgramObject, "u_LightPos");

        floorPositionParam = GLES30.glGetAttribLocation(mProgramObject, "a_Position");
        floorNormalParam = GLES30.glGetAttribLocation(mProgramObject, "a_Normal");
        floorColorParam = GLES30.glGetAttribLocation(mProgramObject, "a_Color");

        GLES30.glEnableVertexAttribArray(floorPositionParam);
        GLES30.glEnableVertexAttribArray(floorNormalParam);
        GLES30.glEnableVertexAttribArray(floorColorParam);

        // Set ModelView, MVP, position, normals, and color.
        GLES30.glUniform3fv(floorLightPosParam, 1, lightPosInEyeSpace, 0);
        GLES30.glUniformMatrix4fv(floorModelParam, 1, false, modelFloor, 0);
        GLES30.glUniformMatrix4fv(floorModelViewParam, 1, false, modelView, 0);
        GLES30.glUniformMatrix4fv(floorModelViewProjectionParam, 1, false,modelViewProjection, 0);
        GLES30.glVertexAttribPointer(floorPositionParam, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, floorVertices);
        GLES30.glVertexAttribPointer(floorNormalParam, 3, GLES30.GL_FLOAT, false, 0, floorNormals);
        GLES30.glVertexAttribPointer(floorColorParam, 4, GLES30.GL_FLOAT, false, 0, floorColors);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);


    }


}
