package cubeExplorer.cube;

import com.jogamp.opengl.math.Quaternion;

public class Sticker {

    private Quaternion rotation;
    private int color;


    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
