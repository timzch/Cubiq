package cubiq.renderer;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.PMVMatrix;
import cubiq.cube.Cube;
import cubiq.io.InteractionHandler;
import cubiq.models.GuiModel;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_LESS;

public class CubeRenderer implements GLEventListener, Observer {

    private GuiModel guiModel;
    private final GLWindow glWindow;
    private Cube cube;
    private InteractionHandler interactionHandler;
    private int deviceWidth;
    private int deviceHeight;
    private float[] camPos;
    private List<int[][]> colorScheme;
    private float fovy;

    private ShaderProgram shaderProgram;

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Names of vertex array objects
    private int[] vboName;	// Names of vertex buffer objects
    private int[] iboName;	// Names of index buffer objects

    // Declaration for using the projection-model-view matrix tool
    PMVMatrix pmvMatrix;


    public CubeRenderer() {
        Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
        Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));

        // Enable FSAA (full screen antialiasing)
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);

        glWindow = GLWindow.create(screen, caps);
    }

    private void startRenderer(AnchorPane renderPane, List<int[][]> colorScheme) {
        this.colorScheme = colorScheme;
        NewtCanvasJFX glCanvas = new NewtCanvasJFX(glWindow);

        glCanvas.setWidth(deviceWidth);
        glCanvas.setHeight(deviceHeight);
        Platform.runLater(() -> {
            renderPane.getChildren().add(glCanvas);
            FPSAnimator animator = new FPSAnimator(glWindow, 60, true);
            animator.start();
        });

        interactionHandler = new InteractionHandler(camPos, deviceWidth, deviceHeight);

        glWindow.addMouseListener(interactionHandler);
        glWindow.addGLEventListener(this);
    }

//    private void initObject(float[] vertices, int[] indices, int index) {
//        gl.glBindVertexArray(vaoName[index]);
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[index]);
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.length * 4L,
//                FloatBuffer.wrap(vertices), GL.GL_STATIC_DRAW);
//        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[index]);
//        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * 4L,
//                IntBuffer.wrap(indices), GL.GL_STATIC_DRAW);
//        gl.glEnableVertexAttribArray(0);
//        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
//        gl.glEnableVertexAttribArray(1);
//        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
//    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 81;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoName = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        // Initialize cubie
        cube = new Cube(3, colorScheme);
        cube.initCubies(gl, vaoName, vboName, iboName);

        // Shader program
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(
                getClass().getResource("/shaders/").getPath().replace("%20", " "),
                "Basic.vert", "Black.frag");


        interactionHandler.setCube(cube);


        // Create object for projection-model-view matrix calculation.
        pmvMatrix = new PMVMatrix();

        // Switch on depth test
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Switch on back face culling
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_FILL);

        // Set background color of the GLCanvas.
        gl.glClearColor(0.098f, 0.106f, 0.114f, 1.0f);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x x-coordinate of the viewport
     * @param y y-coordinate of the viewport
     * @param width width of the viewport
     * @param height height of the viewport
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // Set the viewport to the entire window
        gl.glViewport(0, 0, width, height);
        // Set the window size in the interactionHandler
        interactionHandler.setWindowWidth(width);

        // Switch the pmv-tool to perspective projection
        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        // Reset projection matrix to identity
        pmvMatrix.glLoadIdentity();

        // Calculate projection matrix
        //      Parameters:
        //          fovy (field of view), aspect ratio,
        //          zNear (near clipping plane), zFar (far clipping plane)
        float aspectRatio = (float)width / (float)height;
        pmvMatrix.gluPerspective(fovy, aspectRatio, 0.1f, 1000f);

        // Switch to model-view transform
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        // Clear color and depth buffer
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Tell the interactionHandler that the program has run one frame further
        interactionHandler.nextFrame();

        // Apply view transform using the PMV-Tool
        // Camera positioning is steered by the interaction handler
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluLookAt(camPos[0], camPos[1], camPos[2], 0f, 0f, 0f, 0f, 1.0f, 0f);

        // Set matrices in the interaction handler
        float[] pMatrix = new float[16];
        System.arraycopy(pmvMatrix.glGetPMvMatrixf().array(), 0, pMatrix, 0, 16);
        interactionHandler.setProjectionMatrix(pMatrix);

        float[] mvMatrix = new float[16];
        System.arraycopy(pmvMatrix.glGetPMvMatrixf().array(), 16, mvMatrix, 0, 16);
        interactionHandler.setModelviewMatrix(mvMatrix);

        displayCubies(gl);
    }

    private void displayCubies(GL3 gl) {
        cube.updateVerticesBuffer(gl, vboName);
        for (int i = 0; i < cube.getTotalCubies(); i++) {
            gl.glUseProgram(shaderProgram.getShaderProgramID());
            // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
            gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
            gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
            gl.glBindVertexArray(vaoName[i]);

            // Draws the elements in the order defined by the index buffer object (IBO)
            gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cube.getCubieIndices(i).length, GL.GL_UNSIGNED_INT, 0);

            for (int j = 0; j < cube.getCubie(i).getTotalStickers(); j++) {
                gl.glUseProgram(shaderProgram.getShaderProgramID());
                // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
                gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
                gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
                gl.glBindVertexArray(vaoName[j]);

                // Draws the elements in the order defined by the index buffer object (IBO)
                gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cube.getCubie(i).getStickerIndices(j).length, GL.GL_UNSIGNED_INT, 0);
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDeleteVertexArrays(1, vaoName,0);
        gl.glDeleteBuffers(1, vboName, 0);

        System.exit(0);
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "renderCubeExplorer":
                deviceWidth = 1800;
                deviceHeight = 900;
                camPos = new float[] {-9.5f, 6.1f, 9.5f};
                fovy = 30f;
                startRenderer(guiModel.getRendererPaneExplorer(), null);
                break;
            case "renderCubeSolver":
                deviceWidth = 1800;
                deviceHeight = 600;
                camPos = new float[] {-15f, 4.6f, 5f};
                fovy = 20f;
                startRenderer(guiModel.getRendererPaneSolver(), guiModel.getColorScheme());
                break;
            case "nextSolveStep":
                cube.rotateLayer(guiModel.getActualSolveStep());
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
