package cubeExplorer.io;

import cubeExplorer.io.RayTriangleIntersection;

public class TEST {
    public static void main(String[] args) {
        RayTriangleIntersection rayTriangleIntersection = new RayTriangleIntersection();

        float[] rayOrigin = new float[] {0, 10, 0};
        float[] rayDirection = new float[] {0, 0, -1};
        float[] vert0 = new float[] {-20, 20, -20};
        float[] vert1 = new float[] {20, 20, -20};
        float[] vert2 = new float[] {0, 0, -20};
        float intersects = rayTriangleIntersection.intersectTriangle(rayOrigin, rayDirection, vert0, vert1, vert2);
        System.out.println(intersects);
    }
}
