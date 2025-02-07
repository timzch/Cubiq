package cubiq.io;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;
import cubiq.cube.Cube;
import cubiq.processing.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class InteractionHandler implements MouseListener {

    private final int MOVE_DISTANCE_THRESHOLD = 5;
    private final int FAST_ROTATION_CLICK_SPEED = 7;
    private final int CUBE_SNAP_BACK_SPEED = 23;
    private final float CUBE_ROTATION_SPEED = 0.15f;
    private final float INTERSECTION_EPSILON = 0.000001f;
    private final float[] camPos;
    private float[] pMatrix;
    private float[] mvMatrix;
    private final float deviceWidth;
    private final float deviceHeight;
    private List<Quaternion> actualQuats, pressedQuats, snapToQuats, releasedQuats;
    private int mousePressedX, mousePressedY, windowWidth, pressedFrameCount, pressedFrame;
    private float rotatedSincePress, snapBackDiff;
    private boolean swingingBack, mousePressed;
    private int snapBackFrameCount, mouseDraggedDirection; // -1 -> no direction; 0 -> x; 1 -> y; 2 -> z
    private Cube cube;

    private final int[][][] clickGrid = new int[3][3][3];

    public InteractionHandler(float[] camPos, float deviceWidth, float deviceHeight) {
        mousePressed = false;
        swingingBack = false;
        actualQuats = new ArrayList<>();
        pressedQuats = new ArrayList<>();
        snapToQuats = new ArrayList<>();
        releasedQuats = new ArrayList<>();
        snapBackFrameCount = 0;
        mouseDraggedDirection = -1;
        this.camPos = camPos;
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseEvent.BUTTON1 && !swingingBack) {
            mousePressed = true;
            pressedFrame = pressedFrameCount;

            // Store the position of the mouse when it was pressed
            mousePressedX = mouseEvent.getX();
            mousePressedY = mouseEvent.getY();

            pressedQuats.clear();
            actualQuats.clear();

            // Check where the mouse was pressed
//            System.out.println(cubiePressedDistance(cube.getCubieBoundingBox(0)));
//            for (int i = 0; i < cube.getTotalCubies(); i++) {
//                Quaternion cubieQuat = cube.getCubieRotation(i);
                // Load the cubie quaternions in actual quads
//                actualQuats.add(cubieQuat);
                // Store the angles of the cube when the mouse was pressed in an quaternion
//                pressedQuats.add(new Quaternion(cubieQuat));
//            }
//          Quaternion cubieQuat = cube.getCubieRotation(i);
//          // Load the cubie quaternions in actual quads
//          actualQuats.add(cubieQuat);
//          // Store the angles of the cube when the mouse was pressed in an quaternion
//          pressedQuats.add(new Quaternion(cubieQuat));
//            System.out.println("shortest: " + Math.round(shortestDistance));
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        // If the mouse travelled enough to trigger a direction
        if (mouseDraggedDirection != -1) {
            float nextSnapAngle;
            float[] directionAxis = new float[3];
            directionAxis[mouseDraggedDirection] = 1f;

            // Reset the animation frame counter
            snapBackFrameCount = 0;

            // Round the angle that the cube has been rotated since the mouse was pressed to 90°
            nextSnapAngle = (float)(Math.round(rotatedSincePress / (Math.PI/2)) * (Math.PI/2));

            // If the mouse was released shortly after it was pressed, increase or decrease the rotation by 90°
            if (pressedFrameCount < pressedFrame + FAST_ROTATION_CLICK_SPEED) {
                // Increase or decrease the rotation by 90°
                if (rotatedSincePress > 0)
                    nextSnapAngle += Math.PI/2;
                else
                    nextSnapAngle -= Math.PI/2;
            }

            // Calculate the angle the cube must rotate to reach the next step
            snapBackDiff = rotatedSincePress - nextSnapAngle;

            snapToQuats.clear();
            releasedQuats.clear();

            for (int i = 0; i < actualQuats.size(); i++) {
                // Create quaternions with the rounded angle
                snapToQuats.add(new Quaternion().setFromAngleNormalAxis(nextSnapAngle, directionAxis));
                snapToQuats.get(i).mult(pressedQuats.get(i));
                // Save the released rotation quaternion
                releasedQuats.add(new Quaternion(actualQuats.get(i)));
            }
            // Update every local cubie location
//            cube.updateLocalPos();

            // Reset the direction
            mouseDraggedDirection = -1;

            pressedFrameCount = 0;

            swingingBack = true;
        }
        mousePressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (mousePressed) {
            int mouseMovedX = Math.abs(mousePressedX - mouseEvent.getX());
            int mouseMovedY = Math.abs(mousePressedY - mouseEvent.getY());
            Quaternion stepRotQuat = new Quaternion();

            // Process in which direction the mouse moved
            if (mouseDraggedDirection == -1) {
                if (mouseMovedX > MOVE_DISTANCE_THRESHOLD || mouseMovedY > MOVE_DISTANCE_THRESHOLD) {
                    if (mouseMovedX > mouseMovedY)
                        mouseDraggedDirection = 1;
                    else {
                        if (mousePressedX < windowWidth / 2)
                            mouseDraggedDirection = 2;
                        else
                            mouseDraggedDirection = 0;
                    }
                }
            }
            else {
                // Differentiation whether the X or Y axis is used for the calculation
                if (mouseDraggedDirection == 1) {
                    float mouseMoved = (float)Math.toRadians(mouseEvent.getX() - mousePressedX);
                    rotatedSincePress = mouseMoved * CUBE_ROTATION_SPEED;
                    stepRotQuat.setFromAngleNormalAxis(rotatedSincePress, new float[]{0, 1, 0});
                }
                else {
                    float mouseMoved = (float)Math.toRadians(mouseEvent.getY() - mousePressedY);
                    rotatedSincePress = mouseMoved * CUBE_ROTATION_SPEED;
                    if (mouseDraggedDirection == 2)
                        stepRotQuat.setFromAngleNormalAxis(rotatedSincePress, new float[]{0, 0, 1});
                    else
                        stepRotQuat.setFromAngleNormalAxis(rotatedSincePress, new float[]{1, 0, 0});
                }
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) {
    }

    public void nextFrame() {
        if (mousePressed)
            pressedFrameCount++;
        else if (swingingBack)
            snapBack();
    }

    private void snapBack() {
        snapBackFrameCount++;

        float animationLength = Math.round(Math.abs(snapBackDiff * CUBE_SNAP_BACK_SPEED));

        if (animationLength != 0 && snapBackFrameCount < animationLength) {
            for (int i = 0; i < actualQuats.size(); i++) {
                actualQuats.get(i).setSlerp(releasedQuats.get(i), snapToQuats.get(i), MathUtils.easeOut(snapBackFrameCount, 0, 1, animationLength));
            }
        }
        else {
            for (int i = 0; i < actualQuats.size(); i++) {
                actualQuats.get(i).set(snapToQuats.get(i));
            }
            swingingBack = false;
        }
    }
/*
    private void RotateSide() {
        animationFrameCount++;

        float animationLength = Math.round(Math.abs(Math.PI/2 * rotationSpeed));

        if (animationLength != 0 && animationFrameCount < animationLength) {
            for (int i = 0; i < actualQuats.size(); i++) {
                actualQuats.get(i).setSlerp(releasedQuats.get(i), snapToQuats.get(i), EaseUtils.easeOut(animationFrameCount, 0, 1, animationLength));
            }
        }
        else {
            for (int i = 0; i < actualQuats.size(); i++) {
                actualQuats.get(i).set(snapToQuats.get(i));
            }
            swingingBack = false;
        }
    }

 */

    private float cubiePressedDistance(float[] boundingBoxVertices) {
        for (int i = 0; i < 12; i++) {
            float[][] triangle = new float[3][3];
            for (int j = 0; j < 3; j++) {
                System.arraycopy(boundingBoxVertices, i*9+j*3, triangle[j], 0, 3);
            }
            float triangleDistance = intersectTriangle(triangle);
            System.out.println(triangleDistance);
        }
        return 0;
    }

    /**
     * Calculates a ray from the camera through the mouse.
     * @return The normalized direction of the ray.
     */
    private float[] rayThroughMouse() {
        // 2D normalised device coordinates
        float x = (2.0f * mousePressedX) / deviceWidth - 1.0f;
        float y = 1.0f - (2.0f * mousePressedY) / deviceHeight;

        // 4D homogeneous clip coordinates
        float[] rayClip = {x, y, -1, 1};

        // 4D camera coordinates
        float[] inversePMatrix = FloatUtil.invertMatrix(pMatrix, new float[16]);
        float[] rayEye = multMat4Vec4(inversePMatrix, rayClip);
        rayEye = new float[] {rayEye[0], rayEye[1], -1, 0};

        // 4D World Coordinates
        float[] inverseMvMatrix = FloatUtil.invertMatrix(mvMatrix, new float[16]);
        float[] rayWorld = multMat4Vec4(inverseMvMatrix, rayEye);

        // Normalize the vector
        return VectorUtil.normalizeVec3(rayWorld);
    }

    private float intersectTriangle(float[][] triangle) {
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];
        float[] tvec = new float[3];
        float[] pvec = new float[3];
        float[] qvec = new float[3];

        float[] rayDirection = rayThroughMouse();

        // Find vectors for two edges sharing vert0
        VectorUtil.subVec3(edge1, triangle[1], triangle[0]);
        VectorUtil.subVec3(edge2, triangle[2], triangle[0]);

        // Begin calculating determinant -- also used to calculate U parameter
        VectorUtil.crossVec3(pvec, rayDirection, edge2);

        // If determinant is near zero, ray lies in plane of triangle
        float det = VectorUtil.dotVec3(edge1, pvec);
        if (det > -INTERSECTION_EPSILON && det < INTERSECTION_EPSILON)
            return 0;

        float invDet = 1.0f / det;

        // Calculate distance from vert0 to ray origin
        VectorUtil.subVec3(tvec, camPos, triangle[0]);

        // Calculate U parameter and test bounds
        float u = VectorUtil.dotVec3(tvec, pvec) * invDet;
        if (u < 0.0f || u > 1.0f)
            return 0;

        // Prepare to test V parameter
        VectorUtil.crossVec3(qvec, tvec, edge1);

        // Calculate V parameter and test bounds
        float v = VectorUtil.dotVec3(rayDirection, qvec) * invDet;
        if (v < 0.0f || (u + v) > 1.0f)
            return 0;

        return VectorUtil.dotVec3(edge1, qvec) * invDet;
    }

    public static float[] multMat4Vec4(float[] inMat, float[] inVec) {
        float[] outVec = new float[4];
        outVec[0] = inVec[0] * inMat[0] + inVec[1] * inMat[4] + inVec[2] * inMat[8] + inVec[3] * inMat[12];
        outVec[1] = inVec[0] * inMat[1] + inVec[1] * inMat[5] + inVec[2] * inMat[9] + inVec[3] * inMat[13];
        outVec[2] = inVec[0] * inMat[2] + inVec[1] * inMat[6] + inVec[2] * inMat[10] + inVec[3] * inMat[14];
        outVec[3] = inVec[0] * inMat[3] + inVec[1] * inMat[7] + inVec[2] * inMat[11] + inVec[3] * inMat[15];
        return outVec;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setProjectionMatrix(float[] pMatrix) {
        this.pMatrix = pMatrix;
    }

    public void setModelviewMatrix(float[] mvMatrix) {
        this.mvMatrix = mvMatrix;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }
}
