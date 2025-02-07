package cubiq.cube;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import cubiq.processing.MathUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Cube {

    private final int cubeLayersCount;
    private Cubie[] cubies;
    private int totalCubies;
    private List<int[][]> colorScheme;
    private int[][][][] cubieStickers;
    int currentCycle = 0;
    float totalAmount = 0;

    public Cube(int cubeLayersCount, List<int[][]> colorScheme) {
        this.cubeLayersCount = cubeLayersCount;
        this.colorScheme = colorScheme;
        assignStickerColors();
    }

    public void initCubies(GL3 gl, int[] vaoName, int[] vboName, int[] iboName) {
        if (colorScheme == null) {
            // TODO Scene render handle
        }
        totalCubies = (int)Math.pow(cubeLayersCount, 3);
        cubies = new Cubie[totalCubies];
        // Offset, to center the cube in the scene
        float cubePosOffset = (cubeLayersCount - 1) / 2f;
        for (int x = 0, c = 0; x < cubeLayersCount; x++) {
            for (int y = 0; y < cubeLayersCount; y++) {
                for (int z = 0; z < cubeLayersCount; z++, c++) {
                    Cubie cubie = new Cubie(x - cubePosOffset, y - cubePosOffset, z - cubePosOffset);
//                    cubie.initStickers(cubieStickers[x][y][z], new int[] {x, y, z}, gl, vaoName, vboName, iboName);
                    cubies[c] = cubie;
                    gl.glBindVertexArray(vaoName[c]);
                    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[c]);
                    gl.glBufferData(GL.GL_ARRAY_BUFFER, cubies[c].getVerticesPosColor().length * 4L,
                            FloatBuffer.wrap(cubies[c].getVerticesPosColor()), GL.GL_STATIC_DRAW);
                    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[c]);
                    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubies[c].getIndices().length * 4L,
                            IntBuffer.wrap(cubies[c].getIndices()), GL.GL_STATIC_DRAW);
                    gl.glEnableVertexAttribArray(0);
                    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
                    gl.glEnableVertexAttribArray(1);
                    gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
                }
            }
        }
    }

    public void updateVerticesBuffer(GL3 gl, int[] vboName) {
        for (int i = 0; i < totalCubies; i++) {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, cubies[i].getVerticesPosColor().length * 4L,
                    FloatBuffer.wrap(cubies[i].getVerticesPosColor()), GL.GL_STATIC_DRAW);
        }
    }

    private List<int[][]> generateDefaultScheme() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

            }
        }
        return null;
    }

    public void rotateLayer(String rotation) {
        String layer = rotation.substring(0, 1);
        float amount = (float)(Math.PI/2);
        if (rotation.contains("'"))
            amount *= -1f;
        if (rotation.contains("2"))
            amount *= 2;
        switch (layer) {
            case "U":
                animate(-amount, new int[] {1, 1}, new float[] {0, 1, 0});
                break;
            case "D":
                animate(amount, new int[] {1, -1}, new float[] {0, 1, 0});
                break;
            case "L":
                animate(amount, new int[] {2, -1}, new float[] {0, 0, 1});
                break;
            case "R":
                animate(-amount, new int[] {2, 1}, new float[] {0, 0, 1});
                break;
            case "F":
                animate(amount, new int[] {0, -1}, new float[] {1, 0, 0});
                break;
            case "B":
                animate(-amount, new int[] {0, 1}, new float[] {1, 0, 0});
                break;
        }
    }

    private void animate(float amount, int[] layer, float[] axis) {
        currentCycle = 0;
        totalAmount = 0;
        List<Cubie> rotateCubiesList = new ArrayList<>();
        for (int i = 0; i < totalCubies; i++) {
            if (cubies[i].getLocalPosition()[layer[0]] == layer[1]) {
                rotateCubiesList.add(cubies[i]);
            }
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(new Duration(3), e -> {
                    float frameAmount = MathUtils.easeInOut(currentCycle, 0, amount, 100);
                    for (Cubie cubie: rotateCubiesList) {
                        cubie.rotateAroundAxis(frameAmount - totalAmount, axis);
                    }
                    totalAmount = frameAmount;
                    currentCycle++;
                }));
        timeline.setCycleCount(100);
        timeline.setOnFinished(e -> {
            for (Cubie cubie: rotateCubiesList) {
                cubie.updateLocalAxis(amount, axis);
            }
        });
        timeline.play();
    }

    private void assignStickerColors() {
        cubieStickers = new int[3][3][3][3];

        cubieStickers[0][0][0] = new int[] {colorScheme.get(0)[0][2], colorScheme.get(3)[0][0], colorScheme.get(4)[2][2]};
        cubieStickers[0][0][1] = new int[] {colorScheme.get(0)[1][2], colorScheme.get(3)[1][0]};
        cubieStickers[0][0][2] = new int[] {colorScheme.get(0)[2][2], colorScheme.get(3)[2][0], colorScheme.get(2)[0][2]};
        cubieStickers[0][1][0] = new int[] {colorScheme.get(0)[0][1],            0,             colorScheme.get(4)[2][1]};
        cubieStickers[0][1][1] = new int[] {colorScheme.get(0)[1][1]};
        cubieStickers[0][1][2] = new int[] {colorScheme.get(0)[2][1],            0,             colorScheme.get(2)[0][1]};
        cubieStickers[0][2][0] = new int[] {colorScheme.get(0)[0][0], colorScheme.get(1)[0][2], colorScheme.get(4)[2][0]};
        cubieStickers[0][2][1] = new int[] {colorScheme.get(0)[1][0], colorScheme.get(1)[1][2]};
        cubieStickers[0][2][2] = new int[] {colorScheme.get(0)[2][0], colorScheme.get(1)[2][2], colorScheme.get(2)[0][0]};
        cubieStickers[1][0][0] = new int[] {           0,             colorScheme.get(3)[0][1], colorScheme.get(4)[1][2]};
        cubieStickers[1][0][1] = new int[] {           0,             colorScheme.get(3)[1][1]};
        cubieStickers[1][0][2] = new int[] {           0,             colorScheme.get(3)[2][1], colorScheme.get(2)[1][2]};
        cubieStickers[1][1][0] = new int[] {           0,                        0,             colorScheme.get(4)[1][1]};

        cubieStickers[1][1][2] = new int[] {           0,                        0,             colorScheme.get(2)[1][1]};
        cubieStickers[1][2][0] = new int[] {           0,             colorScheme.get(1)[0][1], colorScheme.get(4)[1][0]};
        cubieStickers[1][2][1] = new int[] {           0,             colorScheme.get(1)[1][1]};
        cubieStickers[1][2][2] = new int[] {           0,             colorScheme.get(1)[2][1], colorScheme.get(2)[1][0]};
        cubieStickers[2][0][0] = new int[] {colorScheme.get(5)[2][2], colorScheme.get(3)[0][2], colorScheme.get(4)[0][2]};
        cubieStickers[2][0][1] = new int[] {colorScheme.get(5)[1][2], colorScheme.get(3)[1][2]};
        cubieStickers[2][0][2] = new int[] {colorScheme.get(5)[0][2], colorScheme.get(3)[2][2], colorScheme.get(2)[2][2]};
        cubieStickers[2][1][0] = new int[] {colorScheme.get(5)[2][1],            0,             colorScheme.get(4)[0][1]};
        cubieStickers[2][1][1] = new int[] {colorScheme.get(5)[1][1]};
        cubieStickers[2][1][2] = new int[] {colorScheme.get(5)[0][1],            0,             colorScheme.get(2)[2][1]};
        cubieStickers[2][2][0] = new int[] {colorScheme.get(5)[2][0], colorScheme.get(1)[0][0], colorScheme.get(4)[0][0]};
        cubieStickers[2][2][1] = new int[] {colorScheme.get(5)[1][0], colorScheme.get(1)[1][0]};
        cubieStickers[2][2][2] = new int[] {colorScheme.get(5)[0][0], colorScheme.get(1)[2][0], colorScheme.get(2)[2][0]};
    }

    public float[] getCubieBoundingBox(int index) {
        return cubies[index].getBoundingBox();
    }

    public int[] getCubieIndices(int index) {
        return cubies[index].getIndices();
    }

    public int getCubeLayersCount() {
        return cubeLayersCount;
    }

    public float[] getCubiePosition(int qbIndex) {
        return cubies[qbIndex].getLocalPosition();
    }

    public int getTotalCubies() {
        return totalCubies;
    }

    public Cubie getCubie(int i) {
        return cubies[i];
    }
}
