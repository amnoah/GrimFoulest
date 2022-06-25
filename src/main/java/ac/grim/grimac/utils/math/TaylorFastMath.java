package ac.grim.grimac.utils.math;

// Found in BetterFPS 1.2.0
// TODO: Implement this somewhere
public class TaylorFastMath {

    private static final float SIN_TO_COS = 1.5707964f;

    public static float sin(float rad) {
        double x2 = rad * rad;
        double x3 = x2 * rad;
        double x4 = x2 * x3;
        double x5 = x2 * x4;
        double x6 = x2 * x5;
        double x7 = x2 * x6;
        double x8 = x2 * x7;
        double x9 = x2 * x8;
        double x10 = x2 * x9;
        double val = rad;
        val -= x3 * 0.16666666666666666;
        val += x4 * 0.008333333333333333;
        val -= x5 * 1.984126984126984E-4;
        val += x6 * 2.7557319223985893E-6;
        val -= x7 * 2.505210838544172E-8;
        val += x8 * 1.6059043836821613E-10;
        val -= x9 * 7.647163731819816E-13;
        val += x10 * 2.8114572543455206E-15;
        return (float) val;
    }

    public static float cos(float rad) {
        return sin(rad + SIN_TO_COS);
    }
}
