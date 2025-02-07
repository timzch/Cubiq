package cubeExplorer.processing;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.PMVMatrix;
import cubeExplorer.cube.Cube;
import cubeExplorer.io.InteractionHandler;
import cubeExplorer.model.Model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Observable;
import java.util.Observer;

import static com.jogamp.opengl.GL.*;

public class Renderer implements GLEventListener, Observer {

    private Model model;
    private final GLWindow glWindow;
    private Cube cube;
    private InteractionHandler interactionHandler;
    private int DEVICE_WIDTH = 1920;
    private int DEVICE_HEIGHT = 1080;
    private float[] CAM_POS = new float[] {-9.5f, 6.1f, 9.5f};
    private int counter = 0;

    private ShaderProgram shaderBlack;
    private ShaderProgram[] stickerShaders;

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Names of vertex array objects
    private int[] vboName;	// Names of vertex buffer objects
    private int[] iboName;	// Names of index buffer objects

    // Declaration for using the projection-model-view matrix tool
    PMVMatrix pmvMatrix;


    public Renderer() {
        Display jfxNewtDisplay = NewtFactory.createDisplay(null, false);
        Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));

        // Enable FSAA (full screen antialiasing)
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);

        glWindow = GLWindow.create(screen, caps);
    }

    private void startRenderer() {
        NewtCanvasJFX glCanvas = new NewtCanvasJFX(glWindow);

        glCanvas.setWidth(DEVICE_WIDTH);
        glCanvas.setHeight(DEVICE_HEIGHT);
        model.getRendererPane().getChildren().add(glCanvas);

        FPSAnimator animator = new FPSAnimator(glWindow, 60, true);
        animator.start();

        interactionHandler = new InteractionHandler(CAM_POS, DEVICE_WIDTH, DEVICE_HEIGHT);

        glWindow.addMouseListener(interactionHandler);
        glWindow.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        cube = new Cube(3, model.getColorScheme());

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        // TODO Auf cubeLAyers anpassen
        int noOfObjects = 7;
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

        initShaders(gl);

        // Initialize cubie
        initCubie(gl);
        initStickers(gl);
        // END: Preparing scene

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
        gl.glClearColor(0.04f, 0.07f, 0.12f, 1.0f);
    }

    private void initShaders(GL3 gl) {
        shaderBlack = new ShaderProgram(gl);
        shaderBlack.loadShaderAndCreateProgram(
                getClass().getResource("/shaders/").getPath().replace("%20", " "),
                "Basic.vert", "Black.frag");

        stickerShaders = new ShaderProgram[6];
        for (int i = 0; i < 6; i++) {
            stickerShaders[i] = new ShaderProgram(gl);
            stickerShaders[i].loadShaderAndCreateProgram(
                    getClass().getResource("/shaders/").getPath().replace("%20", " "),
                    "Basic.vert", i + ".frag");
        }
    }

    private void initStickers(GL3 gl) {
        // 1 because cubie is 0
        for (int i = 0; i < 6; i++) {
            gl.glBindVertexArray(vaoName[i+1]);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i+1]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, 108 * Float.BYTES,
                    FloatBuffer.wrap(cube.getStickerVertices(i)), GL.GL_STATIC_DRAW);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i+1]);
            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, 56 * Integer.BYTES,
                    IntBuffer.wrap(cube.getStickerIndices()), GL.GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(0);
            gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * Float.BYTES, 0);
        }
    }

    private void initCubie(GL3 gl) {
        gl.glBindVertexArray(vaoName[0]);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cube.getCubieVertices().length * 4L,
                FloatBuffer.wrap(cube.getCubieVertices()), GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[0]);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cube.getCubieIndices().length * 4L,
                IntBuffer.wrap(cube.getCubieIndices()), GL.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * Float.BYTES, 0);
    }

    private void displaySticker(GL3 gl, int color) {
        for (int i = 1; i < 7; i++) {
            gl.glUseProgram(stickerShaders[color].getShaderProgramID());
            gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
            gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
            gl.glBindVertexArray(vaoName[i]);
            // Draw the five rectangles
            for (int j = 0; j < 5; j++) {
                gl.glDrawElements(GL.GL_TRIANGLE_STRIP, 4, GL.GL_UNSIGNED_INT, j * 4 * Integer.BYTES);
            }
            // Draw the four corner fans
            for (int j = 0; j < 4; j++) {
                gl.glDrawElements(GL.GL_TRIANGLE_FAN, 9, GL.GL_UNSIGNED_INT, 20 * Integer.BYTES + j * 9 * Integer.BYTES);
            }
        }
    }

    private void displayCubie(GL3 gl) {
        gl.glUseProgram(shaderBlack.getShaderProgramID());
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoName[0]);
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cube.getCubieIndices().length, GL.GL_UNSIGNED_INT, 0);
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
        pmvMatrix.gluPerspective(30f, aspectRatio, 0.1f, 1000f);

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
        pmvMatrix.gluLookAt(CAM_POS[0], CAM_POS[1], CAM_POS[2], 0f, 0f, 0f, 0f, 1.0f, 0f);

        // Set matrices in the interaction handler
        float[] pMatrix = new float[16];
        System.arraycopy(pmvMatrix.glGetPMvMatrixf().array(), 0, pMatrix, 0, 16);
        interactionHandler.setProjectionMatrix(pMatrix);

        float[] mvMatrix = new float[16];
        System.arraycopy(pmvMatrix.glGetPMvMatrixf().array(), 16, mvMatrix, 0, 16);
        interactionHandler.setModelviewMatrix(mvMatrix);

        cube.rotateLayer(0, 1, new Quaternion().setFromEuler(0.0174533f, 0, 0));
        counter++;
        if (counter == 90) {
            cube.updateCubieRelPos();
            counter = 0;
        }

//        displayTestIntersectionObject(gl);
        for (int x = 0; x < cube.getCubeLayers(); x++) {
            for (int y = 0; y < cube.getCubeLayers(); y++) {
                for (int z = 0; z < cube.getCubeLayers(); z++) {
                    pmvMatrix.glPushMatrix();
                    // Cubie rotation
//                    pmvMatrix.glRotate(interactionHandler.getActualQuat());
                    pmvMatrix.glRotate(cube.getCubieRotation(x, y, z));
                    // Cubie translation
                    pmvMatrix.glTranslatef(x-1, y-1, z-1);
                    // Display Cubie
                    displayCubie(gl);
                    displaySticker(gl, 4);
                    pmvMatrix.glPopMatrix();
                }
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderBlack.deleteShaderProgram();
        for (int i = 0; i < 6; i++) {
            stickerShaders[i].deleteShaderProgram();
        }

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
            case "startRenderer":
                startRenderer();
                break;
        }
    }

    public void initModel(Model model) {
        this.model = model;
    }
}
