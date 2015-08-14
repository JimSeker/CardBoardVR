# CardBoardVR
These examples are for Android's CardBoard.  They are built with AndroidStudio in java. For more information see https://developers.google.com/cardboard<BR>

<b>CardBoardSample</b> is Google's example code, but setup for studio correctly.<BR>

<b>FloatingCube</b> is a floating cube in space.  It is based on the <a href="https://github.com/JimSeker/opengl/tree/master/OpenGL30Cube">OpenGL30Cube example</a>.  This example separates the classes to make it easier to see the CardBoardActivity and the StereoRenderer.  The cube and color class are the same as the opengl30cube example.  The CardboardOverlayView is borrowed from the CardBoardSample code.<BR>

<b>FloatingCubes</b> is based on FloatCube, except there are 8 cubes, which each turn differently.  Included the Floor code from CardBardSample in Floor.java with the necessary parameters from the renderer are send to the draw function.  Since GLES30 is backward compatible to 2.0, no changes where made to the shader/vertex code.

<b>FlyingAroundACube</b> is based on FloatingCube.  When you "click/touch" you will move (mostly) in the direction the phone is facing.

These are example code for University of Wyoming, Cosc 4730 Mobile Programming course.  All examples are for Android.
