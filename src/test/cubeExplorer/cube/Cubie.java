package cubeExplorer.cube;

import com.jogamp.opengl.math.Quaternion;

public class Cubie {

    private float[] relPos, cubieColor;
    private Quaternion cubieRotation;
    private Quaternion[] stickerRotations;
    private int[] cubieIndices;
    private float cubieSize;
    private int objectIndex;
    private int stickerCount;

    public Cubie(float x, float y, float z, float cubieSize, float[] cubieColor, int objectIndex) {
        this.relPos = new float[]{x, y, z};
        this.objectIndex = objectIndex;
        this.cubieRotation = new Quaternion();
        this.cubieColor = cubieColor;
        this.cubieSize = cubieSize;
    }

    // TODO Sticker immer nur wenn er sichtbar ist (if (z == 2) sticker auf +z)
    public void initStickers(int[] stickerColors) {
        for (int i = 0; i < 3; i++) {
            if (stickerColors[i] != -1) {
//                stickerRotations
                if (relPos[i] > 0) {

                }
            }
        }
        if (relPos[0] == 1) {

        }

//        for (int i = 0; i < 3; i++) {
//            if (relPos[i] == 1)
//                System.out.println(Arrays.toString(relPos));
//        }
    }

    public void updateLocalPosition(float[] offset) {
        // Rotate the localPos so that it matches the current rotation
        cubieRotation.rotateVector(relPos, 0, offset, 0);
        // Round the position and the actual position
        float[] eulerRotation = cubieRotation.toEuler(new float[3]);
        for (int i = 0; i < 3; i++) {
            relPos[i] = Math.round(relPos[i]);
            eulerRotation[i] = Math.round(eulerRotation[i] / (float)(Math.PI/2)) * (float)(Math.PI/2);
        }
        cubieRotation.setFromEuler(eulerRotation);
    }

    public void rotateCubie(Quaternion rotation) {
        cubieRotation.mult(rotation);
    }

    public int getObjectIndex() {
        return objectIndex;
    }

    public int getStickerCount() {
        return stickerCount;
    }

    public float[] getLocalPosition() {
        return relPos;
    }

    public Quaternion getRotation() {
        return cubieRotation;
    }

    public void setRotation(Quaternion rotation) {
        this.cubieRotation = rotation;
    }
}
