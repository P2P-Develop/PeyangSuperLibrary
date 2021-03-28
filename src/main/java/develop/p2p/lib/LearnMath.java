package develop.p2p.lib;

import java.util.Arrays;

/**
 * Neural network functions
 * @author Potato1682
 */
public class LearnMath
{
    /**
     * Sigmoid function
     */
    public static double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }

    /**
     * Sigmoid function (deliver)
     */
    public static double sigmoidDef(double x)
    {
        return sigmoid(x) * (1 - sigmoid(x));
    }

    /**
     * Swish function
     */
    public static double swish(double x)
    {
        return x * sigmoid(x);
    }

    /**
     * Swish function (deliver)
     */
    public static double swishDef(double x)
    {
        return swish(x) + (sigmoid(x) * (1 - swish(x)));
    }

    /**
     * Step function
     */
    public static double step(double x)
    {
        return x >= 0 ? 1 : 0;
    }

    /**
     * Rectified Linear Unit function (ReLU)
     */
    public static double relu(double x)
    {
        return x * Math.max(x, 0);
    }

    /**
     * ReLU function (deliver)
     */
    public static double reluDef(double x)
    {
        return 1 * x > 0 ? 1 : 0;
    }

    /**
     * Leaky ReLU function (LReLU)
     */
    public static double lrelu(double x)
    {
        return x >= 0 ? x : 0.01 * x;
    }

    /**
     * Leaky ReLU function (LReLU)
     */
    public static double lrelu(double x, double alpha)
    {
        return x >= 0 ? x : alpha * x;
    }

    /**
     * LReLU function
     */
    public static double lreluDef(double x)
    {
        return x >= 0 ? 1 : 0.01;
    }

    /**
     * LReLU function
     */
    public static double lreluDef(double x, double alpha)
    {
        return x >= 0 ? alpha : 0.01;
    }

    /**
     * Exponential Linear Unit function (ELU) / Parametric ReLU function (PReLU)
     */
    public static double elu(double x)
    {
        return x > 0 ? x : 1 * (Math.exp(x) - 1);
    }

    /**
     * Exponential Linear Unit function (ELU) / Parametric ReLU function (PReLU)
     */
    public static double elu(double x, double alpha)
    {
        return x > 0 ? x : alpha * (Math.exp(x) - 1);
    }

    /**
     * ELU function (deliver) / PReLU function (deliver)
     */
    public static double eluDef(double x)
    {
        return x > 0 ? 1 : elu(x) + 1;
    }

    /**
     * ELU function (deliver) / PReLU function (deliver)
     */
    public static double eluDef(double x, double alpha)
    {
        return x > 0 ? 1 : elu(x, alpha) + alpha;
    }

    /**
     * Scaled ELU function (SELU)
     */
    public static double selu(double x, double scale, double alpha)
    {
        return scale * x > 0 ? x : alpha * (Math.exp(x) - 1);
    }

    /**
     * SELU function (deliver)
     */
    public static double seluDef(double x, double scale, double alpha)
    {
        return scale * x > 0 ? 1 : alpha * Math.exp(x);
    }

    /**
     * Hyperbolic tangent function (tanH)
     */
    public static double tanH(double x)
    {
        return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
    }

    /**
     * tanH function (deliver)
     */
    public static double tanHDef(double x)
    {
        return 1 - (Math.pow(tanH(x), 2));
    }

    /**
     * Softplus function
     */
    public static double softplus(double x)
    {
        return Math.log(1 + Math.exp(x));
    }

    /**
     * Softplus function (deliver)
     */
    public static double softplusDef(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }

    /**
     * Omega function (Required by Mish function)
     */
    public static double omega(double x)
    {
        return 4 * (x + 1) + 4 * Math.exp(2 * x) + Math.exp(3 * x) + Math.exp(x) * (4 * x + 6);
    }

    /**
     * Delta function (Required by Mish function)
     */
    public static double delta(double x)
    {
        return 2 * Math.exp(x) + Math.exp(2 * x) + 2;
    }

    /**
     * Mish function
     */
    public static double mish(double x)
    {
        return x * tanH(softplus(x));
    }

    /**
     * Mish function
     */
    public static double mishDef(double x)
    {
        return Math.exp(x) * omega(x) / Math.pow(delta(x), 2);
    }

    /**
     * Identity function
     */
    public static double identity(double x)
    {
        return x;
    }

    /**
     * Identiry function (deliver)
     */
    public static double identityDef()
    {
        return 1;
    }

    /**
     * Softmax function
     */
    public static double[] softmax(double[] x)
    {
        double[] value = Arrays.stream(x).map(y -> Math.exp(y - (Arrays.stream(x).max().getAsDouble()))).toArray();
        return Arrays.stream(value).map(p -> p / Arrays.stream(value).sum()).toArray();
    }

    /**
     * Softmax function
     */
    public static double[][] softmax(double[][] x)
    {
        double[][] result = new double[x.length][];
        Arrays.setAll(result, i -> softmax(x[i]));
        return result;
    }
}
