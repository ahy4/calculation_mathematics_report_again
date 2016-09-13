package report;

import java.util.function.Function;

/**
 * Created by noko on 2016/09/13.
 */
public class NewtonMethod
{
    public static double exec(Function<Double, Double> f, Function<Double, Double> df)
    {
        return legacyNewtonMethod(f, df, 0.3);
    }

    private static double legacyNewtonMethod(Function<Double, Double> f, Function<Double, Double> df, double initialPoint)
    {
        double eps = 1.0e-12;
        double x0 = initialPoint;
        int i = 1;
        while (Math.abs(f.apply(x0)) >= eps && i <= 300) {
            x0 = x0 - f.apply(x0) / df.apply(x0);
            i++;
        }
        if (i == 301 || Double.isNaN(x0))
        {
            return legacyNewtonMethod(f, df, -initialPoint*1.1);
        }
        return x0;
    }
}
