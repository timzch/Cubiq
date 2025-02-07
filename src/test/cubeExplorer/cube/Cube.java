package cubeExplorer.cube;

import com.jogamp.opengl.math.Quaternion;
import cubiq.processing.MathUtils;

import java.util.Arrays;
import java.util.List;

public class Cube {

    private final float CUBIE_SIZE = 1;
    private final float[] CUBIE_COLOR = {0, 0, 0};
    private int cubeLayers;
    private int totalCubies;
    private Cubie[][][] cubies;
    private Quaternion cubeRotation;
    private float[] cubieVertices, stickerVertices;
    private int[] cubieIndices, stickerIndices;
    private int[][][][] cubieStickerColors;

    public Cube(int cubeLayers, List<int[][]> colorScheme) {
        this.cubeLayers = cubeLayers;
        this.cubeRotation = new Quaternion();
        createCubieVertices();
        createCubieIndices();
        createStickerVertices();
        createStickerIndices();
        assignStickerColors(colorScheme);
        initCubies();
    }

    private void initCubies() {
        totalCubies = (int)Math.pow(cubeLayers, 3);
        cubies = new Cubie[cubeLayers][cubeLayers][cubeLayers];
        // Offset, to center the cube in the scene
        float cubePosOffset = (cubeLayers - CUBIE_SIZE) / 2f;
        // Create the cubies with
        for (int x = 0, index = 0; x < cubeLayers; x++) {
            for (int y = 0; y < cubeLayers; y++) {
                for (int z = 0; z < cubeLayers; z++, index++) {
                    Cubie cubie = new Cubie(x - cubePosOffset, y - cubePosOffset, z - cubePosOffset,
                            CUBIE_SIZE, CUBIE_COLOR, index);
                    cubie.initStickers(cubieStickerColors[x][y][z]);
                    cubies[x][y][z] = cubie;
                }
            }
        }
    }

    private void assignStickerColors(List<int[][]> colorScheme) {
        cubieStickerColors = new int[3][3][3][3];

        cubieStickerColors[0][0][0] = new int[] {colorScheme.get(4)[2][2], colorScheme.get(0)[0][2], colorScheme.get(3)[0][0]};
        cubieStickerColors[0][0][1] = new int[] {           -1           , colorScheme.get(0)[1][2], colorScheme.get(3)[1][0]};
        cubieStickerColors[0][0][2] = new int[] {colorScheme.get(2)[0][2], colorScheme.get(0)[2][2], colorScheme.get(3)[2][0]};
        cubieStickerColors[0][1][0] = new int[] {colorScheme.get(4)[2][1], colorScheme.get(0)[0][1],            -1           };
        cubieStickerColors[0][1][1] = new int[] {           -1           , colorScheme.get(0)[1][1],            -1           };
        cubieStickerColors[0][1][2] = new int[] {colorScheme.get(2)[0][1], colorScheme.get(0)[2][1],            -1           };
        cubieStickerColors[0][2][0] = new int[] {colorScheme.get(4)[2][0], colorScheme.get(0)[0][0], colorScheme.get(1)[0][2]};
        cubieStickerColors[0][2][1] = new int[] {           -1           , colorScheme.get(0)[1][0], colorScheme.get(1)[1][2]};
        cubieStickerColors[0][2][2] = new int[] {colorScheme.get(2)[0][0], colorScheme.get(0)[2][0], colorScheme.get(1)[2][2]};
        cubieStickerColors[1][0][0] = new int[] {colorScheme.get(4)[1][2],            -1           , colorScheme.get(3)[0][1]};
        cubieStickerColors[1][0][1] = new int[] {           -1           ,            -1           , colorScheme.get(3)[1][1]};
        cubieStickerColors[1][0][2] = new int[] {colorScheme.get(2)[1][2],            -1           , colorScheme.get(3)[2][1]};
        cubieStickerColors[1][1][0] = new int[] {colorScheme.get(4)[1][1],            -1           ,            -1           };
        cubieStickerColors[1][1][2] = new int[] {colorScheme.get(2)[1][1],            -1           ,            -1           };
        cubieStickerColors[1][2][0] = new int[] {colorScheme.get(4)[1][0],            -1           , colorScheme.get(1)[0][1]};
        cubieStickerColors[1][2][1] = new int[] {           -1           ,            -1           , colorScheme.get(1)[1][1]};
        cubieStickerColors[1][2][2] = new int[] {colorScheme.get(2)[1][0],            -1           , colorScheme.get(1)[2][1]};
        cubieStickerColors[2][0][0] = new int[] {colorScheme.get(4)[0][2], colorScheme.get(5)[2][2], colorScheme.get(3)[0][2]};
        cubieStickerColors[2][0][1] = new int[] {           -1           , colorScheme.get(5)[1][2], colorScheme.get(3)[1][2]};
        cubieStickerColors[2][0][2] = new int[] {colorScheme.get(2)[2][2], colorScheme.get(5)[0][2], colorScheme.get(3)[2][2]};
        cubieStickerColors[2][1][0] = new int[] {colorScheme.get(4)[0][1], colorScheme.get(5)[2][1],            -1           };
        cubieStickerColors[2][1][1] = new int[] {           -1           , colorScheme.get(5)[1][1],            -1           };
        cubieStickerColors[2][1][2] = new int[] {colorScheme.get(2)[2][1], colorScheme.get(5)[0][1],            -1           };
        cubieStickerColors[2][2][0] = new int[] {colorScheme.get(4)[0][0], colorScheme.get(5)[2][0], colorScheme.get(1)[0][0]};
        cubieStickerColors[2][2][1] = new int[] {           -1           , colorScheme.get(5)[1][0], colorScheme.get(1)[1][0]};
        cubieStickerColors[2][2][2] = new int[] {colorScheme.get(2)[2][0], colorScheme.get(5)[0][0], colorScheme.get(1)[2][0]};
    }

    private void createCubieVertices() {
        cubieVertices = new float[24];
        float[] a = {CUBIE_SIZE / 2, CUBIE_SIZE / 2, CUBIE_SIZE / 2};
        for (int i = 0; i < 8; i++) {
            System.arraycopy(a, 0, cubieVertices, i * 3, 3);
            if (i % 2 != 0) a[0] *= -1;
            if (i == 3) a[1] *= -1;
            a[2] *= -1;
        }
    }

    private void createCubieIndices() {
        cubieIndices = new int[] {4, 6, 5, 7, 3, 6, 2, 4, 0, 5, 1, 3, 0, 2};
    }

    private void createStickerVertices() {
        float so = (CUBIE_SIZE / 2f) + 0.01f; // The offset from the stickers to the cubie center (sticker center offset)
        float hso = (CUBIE_SIZE / 2f) - 0.05f; // Half the width of the outer sticker (half sticker outer)
        float hsi = (CUBIE_SIZE / 2f) - 0.15f; // Half the width of the inner sticker (half sticker inner)

        stickerVertices = new float[] {
                -hsi, hsi, so, -hsi, -hsi, so, hsi, -hsi, so, hsi, hsi, so, // Inner rectangle
                -hso, hsi, so, -hso, -hsi, so,                              // Left rectangle
                -hsi, -hso, so, hsi, -hso, so,                              // Bottom rectangle
                hso, -hsi, so, hso, hsi, so,                                // Right rectangle
                hsi, hso, so, -hsi, hso, so,                                // Top rectangle
                -0.38f, 0.45f, so, -0.40f, 0.44f, so, -0.42f, 0.43f, so,    // Top-left fan
                -0.43f, 0.42f, so, -0.44f, 0.40f, so, -0.45f, 0.38f, so,
                -0.45f, -0.38f, so, -0.44f, -0.40f, so, -0.43f, -0.42f, so, // Bottom-left fan
                -0.42f, -0.43f, so, -0.40f, -0.44f, so, -0.38f, -0.45f, so,
                0.38f, -0.45f, so, 0.40f, -0.44f, so, 0.42f, -0.43f, so,    // Bottom-right fan
                0.43f, -0.42f, so, 0.44f, -0.40f, so, 0.45f, -0.38f, so,
                0.45f, 0.38f, so, 0.44f, 0.40f, so, 0.43f, 0.42f, so,       // Top-right fan
                0.42f, 0.43f, so, 0.40f, 0.44f, so, 0.38f, 0.45f, so
        };
    }

    private void createStickerIndices() {
        stickerIndices = new int[] {
                0, 1, 3, 2, 4, 5, 0, 1, 1, 6, 2, 7, 3, 2, 9, 8, 11, 0, 10, 3, 0, 11, 12, 13, 14, 15, 16, 17, 4,
                1, 5, 18, 19, 20, 21, 22, 23, 6, 2, 7, 24, 25, 26, 27, 28, 29, 8, 3, 9, 30, 31, 32, 33, 34, 35, 10
        };
    }

    public void updateCubieRelPos() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    cubies[x][y][z].updateLocalPosition(new float[]{x-1, y-1, z-1});
                }
            }
        }
    }

    /**
     * axis ->   0 = x, 1 = y, 2 = z,
     * layer ->  -1 or 1 on the relative axis,
     * amount -> < 0 -> counter clockwise, > 0 -> clockwise
     */
    public void rotateLayer(int axis, int layer, Quaternion amount) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    Cubie cubie = cubies[x][y][z];
                    if (cubie.getLocalPosition()[axis] == layer)
                        cubie.rotateCubie(amount);
                }
            }
        }
    }

    private float[] rotateStickerVertices(int index) {
        switch (index) {
            case 1:
                // Rotate x 90
                float[] axis = {0, 0, 0};
                float amount = (float)(Math.PI/2);
                float[] vertex = new float[3];
                float[] rotatedVertices = stickerVertices.clone();
                float[] rotatedVector;
                for (int i = 0; i < rotatedVertices.length/3; i++) {
                    System.arraycopy(rotatedVertices, i*3, vertex, 0, 3);
                    rotatedVector = MathUtils.rotateVector(vertex, axis, amount);
                    for (int j = 0; j < 3; j++) {
                        rotatedVector[j] = Math.round(rotatedVector[j] * 100f) / 100f;
                    }
                    System.arraycopy(rotatedVector, 0, rotatedVertices, i*3, 3);
                }
                System.out.println(Arrays.toString(stickerVertices));
                System.out.println(Arrays.toString(rotatedVertices));
                return rotatedVertices;
            case 2:
                // Rotate y -90
                return stickerVertices;
            case 3:
                // Rotate x -90
                return stickerVertices;
            case 4:
                // Rotate y 90
                return stickerVertices;
            case 5:
                // Rotate y 180
                return stickerVertices;
        }
        return stickerVertices;
    }

    public float[] getStickerVertices(int index) {
        return rotateStickerVertices(index);
    }

    public float[] getCubieVertices() {
        return cubieVertices;
    }

    public int[] getCubieIndices() {
        return cubieIndices;
    }

    public Cubie getCubie(int xIndex, int yIndex, int zIndex) {
        return cubies[xIndex][yIndex][zIndex];
    }

    public int getCubeLayers() {
        return cubeLayers;
    }

    public Quaternion getCubieRotation(int xIndex, int yIndex, int zIndex) {
        return cubies[xIndex][yIndex][zIndex].getRotation();
    }

    public void setStickerVertices(float[] stickerVertices) {
        this.stickerVertices = stickerVertices;
    }

    public int[] getStickerIndices() {
        return stickerIndices;
    }

    public void setStickerIndices(int[] stickerIndices) {
        this.stickerIndices = stickerIndices;
    }
}
