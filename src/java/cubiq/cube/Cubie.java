package cubiq.cube;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.VectorUtil;
import cubiq.processing.MathUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Cubie {

    private final float CUBIE_SIZE = 1;
    private float[] CUBIE_COLOR = {0.01f, 0.01f, 0.01f};
    private float[] verticesPosColor, verticesPos, localPos;
    private int[] indices;
    private boolean debugCubie = false;
    private List<Sticker> stickers;


    public Cubie(float x, float y, float z) {
        localPos = new float[] {x, y, z};
        verticesPos = new float[24];
        stickers = new ArrayList<>();
        createVertices();
        translateVertices(new float[] {x, y, z});
        createIndices();
    }

    public void initStickers(int[] cubieStickers, int[] position, GL3 gl, int[] vaoName, int[] vboName, int[] iboName) {
        for (int i = 0; i < cubieStickers.length; i++) {
            stickers.add(new Sticker(cubieStickers[i], position, i));
            gl.glBindVertexArray(vaoName[i]);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
            gl.glBufferData(GL.GL_ARRAY_BUFFER, stickers.get(i).getVerticesPosColor().length * 4L,
                    FloatBuffer.wrap(stickers.get(i).getVerticesPosColor()), GL.GL_STATIC_DRAW);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i]);
            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, stickers.get(i).getIndices().length * 4L,
                    IntBuffer.wrap(stickers.get(i).getIndices()), GL.GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(0);
            gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 0);
            gl.glEnableVertexAttribArray(1);
            gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        }
    }

    private void createVertices() {
        verticesPosColor = new float[48];
        float[] a = {CUBIE_SIZE / 2, CUBIE_SIZE / 2, CUBIE_SIZE / 2};
        for (int i = 0; i < 8; i++) {
            System.arraycopy(a, 0, verticesPosColor, i * 6, 3);
            System.arraycopy(a, 0, verticesPos, i * 3, 3);
            System.arraycopy(CUBIE_COLOR, 0, verticesPosColor, i * 6 + 3, 3);
            if (i % 2 != 0) a[0] *= -1;
            if (i == 3) a[1] *= -1;
            a[2] *= -1;
        }
    }

    private void updateVerticesPosColor() {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(verticesPos, i*3, verticesPosColor, i * 6, 3);
        }
    }

    public float[] getBoundingBox() {
        float[] boundingBoxVertices = new float[108];
        // Triangle strip to triangles
        for (int i = 0, counter = 0; i < indices.length - 2; i++) {
            for (int j = 0; j < 3; j++, counter++) {
                System.arraycopy(verticesPos, indices[i] + j, boundingBoxVertices, counter * 3, 3);
            }
        }
        return boundingBoxVertices;
    }

    public void updateLocalAxis(float amount, float[] axis) {
        localPos = MathUtils.rotateVector(localPos, axis, amount);
        for (int i = 0; i < 3; i++) {
            localPos[i] = Math.round(localPos[i]);
        }
    }

    public void rotateAroundAxis(float amount, float[] axis) {
        float[] vertex = new float[3];
        float[] rotatedVertex;
        // Rotate all vertexes
        for (int i = 0; i < 8; i++) {
            System.arraycopy(verticesPos, i*3, vertex, 0, 3);
            rotatedVertex = MathUtils.rotateVector(vertex, axis, amount);
            System.arraycopy(rotatedVertex, 0, verticesPos, i*3, 3);
        }
        updateVerticesPosColor();
    }

    private void translateVertices(float[] translation) {
        for (int i = 0; i < 8; i++) {
            float[] temp = new float[3];
            System.arraycopy(verticesPos, i*3, temp, 0, 3);
            VectorUtil.addVec3(temp, temp, translation);
            System.arraycopy(temp, 0, verticesPos, i*3, 3);
        }
        updateVerticesPosColor();
    }

    public int getTotalStickers() {
        return stickers.size();
    }

    private void createIndices() {
        indices = new int[] {4, 6, 5, 7, 3, 6, 2, 4, 0, 5, 1, 3, 0, 2};
    }

    public float[] getVerticesPosColor() {
        return verticesPosColor;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getLocalPosition() {
        return localPos;
    }

    public float[] getStickerVertices(int index) {
        return stickers.get(index).getVerticesPosColor();
    }

    public int[] getStickerIndices(int index) {
        return stickers.get(index).getIndices();
    }
}
