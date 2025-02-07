package cubeExplorer.io;


import com.jogamp.opengl.math.VectorUtil;

public class RayTriangleIntersection {

    private float[] edge0 = new float[3];
    private float[] edge1 = new float[3];
    private float[] tvec = new float[3];
    private float[] pvec = new float[3];
    private float[] qvec = new float[3];

    private final float EPSILON = 0.000001f;

    public float intersectTriangle(float[] rayOrigin, float[] rayDirection, float[] vert0, float[] vert1, float[] vert2) {
        // Find vectors for two edges sharing vert0
        // TODO Zwei edges des triangles
        VectorUtil.subVec3(edge0, vert1, vert0);
        VectorUtil.subVec3(edge1, vert2, vert0);

        // Begin calculating determinant -- also used to calculate U parameter
        // TODO Vektor, der senkrecht auf der Ebene, die rayDirection und edge1 aufspannen, steht
        VectorUtil.crossVec3(pvec, rayDirection, edge1);

        // If determinant is near zero, ray lies in plane of triangle
        // TODO Determinante bilden
        float det = VectorUtil.dotVec3(edge0, pvec);
        if (det > -EPSILON && det < EPSILON)
            return 0;

        float invDet = 1.0f / det;

        // Calculate distance from vert0 to ray origin
        VectorUtil.subVec3(tvec, rayOrigin, vert0);

        // Calculate U parameter and test bounds
        float u = VectorUtil.dotVec3(tvec, pvec) * invDet;
        if (u < 0.0f || u > 1.0f)
            return 0;

        // Prepare to test V parameter
        VectorUtil.crossVec3(qvec, tvec, edge0);

        // Calculate V parameter and test bounds
        float v = VectorUtil.dotVec3(rayDirection, qvec) * invDet;
        if (v < 0.0f || (u + v) > 1.0f)
            return 0;

        // Calculate t, ray intersects triangle

        return VectorUtil.dotVec3(edge1, qvec) * invDet;
    }
}