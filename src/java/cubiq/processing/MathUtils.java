package cubiq.processing;

public class MathUtils {

    /**
     * Function to calculate a ease in/out animation
     * Source: http://gizma.com/easing
     * @param t current time
     * @param b start value
     * @param c change in value
     * @param d duration
     * @return Eased value
     */
    public static float easeInOut(float t, float b, float c, float d) {
        t /= d/2;
        if (t < 1) return c/2*t*t*t + b;
        t -= 2;
        return c/2*(t*t*t + 2) + b;
    }

    /**
     * Function to calculate a ease out animation
     * Source: http://gizma.com/easing/
     * @param t current time
     * @param b start value
     * @param c change in value
     * @param d duration
     * @return Eased value
     */
    public static float easeOut(float t, float b, float c, float d) {
        t /= d;
        t--;
        return c*(t*t*t + 1) + b;
    }

    /**
     * Rotate Vector
     * Source: https://stackoverflow.com/questions/31225062/rotating-a-vector-by-angle-and-axis-in-java
     * @param vec
     * @param axis
     * @param theta
     * @return
     */
    public static float[] rotateVector(float[] vec, float[] axis, float theta){
        float x, y, z;
        float u, v, w;
        x=vec[0];y=vec[1];z=vec[2];
        u=axis[0];v=axis[1];w=axis[2];;
        double xPrime = u*(u*x + v*y + w*z)*(1d - Math.cos(theta))
                + x*Math.cos(theta)
                + (-w*y + v*z)*Math.sin(theta);
        double yPrime = v*(u*x + v*y + w*z)*(1d - Math.cos(theta))
                + y*Math.cos(theta)
                + (w*x - u*z)*Math.sin(theta);
        double zPrime = w*(u*x + v*y + w*z)*(1d - Math.cos(theta))
                + z*Math.cos(theta)
                + (-v*x + u*y)*Math.sin(theta);
        return new float[]{(float)xPrime, (float)yPrime, (float)zPrime};
    }
}
