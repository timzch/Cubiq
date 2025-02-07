package cubiq.cube;

public class Sticker {
    float[] color, verticesPosColor;
    int[] pos, indices;

    public Sticker(int color, int[] position, int orientation) {
        initIndices();
        this.pos = position;
        this.color = convert2Rgb(color);
        verticesPosColor = addColor(initVertices(orientation));
    }

    private void initIndices() {
        indices = new int[] {0, 1, 2, 3};
    }

    /**
     * 0, 1, 2
     * @param orientation
     * @return
     */
    private float[] initVertices(int orientation) {
        float[] posVertices = new float[12];
        switch (orientation) {
            case 0:
                if (pos[0] < 0) {
                    posVertices = new float[]{
                            -0.51f, 0.45f, -0.45f,
                            -0.51f, -0.45f, -0.45f,
                            -0.51f, 0.45f, 0.45f,
                            -0.51f, -0.45f, 0.45f
                    };
                }
                else {
                    posVertices = new float[]{
                            0.51f, 0.45f, 0.45f,
                            0.51f, -0.45f, 0.45f,
                            0.51f, 0.45f, -0.45f,
                            0.51f, -0.45f, -0.45f
                    };
                }
                break;
            case 1:
                if (pos[1] < 0) {
                    posVertices = new float[]{
                            -0.45f, -0.51f, 0.45f,
                            0.45f, -0.51f, 0.45f,
                            -0.45f, -0.51f, -0.45f,
                            0.45f, -0.51f, -0.45f
                    };
                }
                else {
                    posVertices = new float[]{
                            0.45f, 0.51f, -0.45f,
                            -0.45f, 0.51f, -0.45f,
                            0.45f, 0.51f, 0.45f,
                            -0.45f, 0.51f, 0.45f
                    };
                }
                break;
            case 2:
                if (pos[2] < 0) {
                    posVertices = new float[]{
                            0.45f, 0.45f, -0.51f,
                            0.45f, -0.45f, -0.51f,
                            -0.45f, 0.45f, -0.51f,
                            -0.45f, -0.45f, -0.51f
                    };
                }
                else {
                    posVertices = new float[]{
                            -0.45f, 0.45f, 0.51f,
                            -0.45f, -0.45f, 0.51f,
                            0.45f, 0.45f, 0.51f,
                            0.45f, -0.45f, 0.51f
                    };
                }
        }
        return posVertices;
    }

    private float[] addColor(float[] input) {
        float[] output = new float[24];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(input, i*3, output, i*3, 3);
            System.arraycopy(color, 0, output, i*3+3, 3);
        }
        return output;
    }

    private float[] convert2Rgb(int normColor) {
        switch (normColor) {
            case 0:
                return new float[]{1.0f, 1.0f, 1.0f};
            case 1:
                return new float[]{0.0f, 0.62f, 0.33f};
            case 2:
                return new float[]{0.86f, 0.26f, 0.18f};
            case 3:
                return new float[]{1.0f, 0.42f, 0.0f};
            case 4:
                return new float[]{0.24f, 0.51f, 0.96f};
            case 5:
                return new float[]{0.99f, 0.8f, 0.03f};
            default:
                return null;
        }
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getVerticesPosColor() {
        return verticesPosColor;
    }
}
