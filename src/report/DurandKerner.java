package report;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by noko on 2016/09/08.
 */
public class DurandKerner
{
    public static void testClient()
    {
        double[] as = {1,1,3,-5};
        Function<Complex, Complex> p = polynomial(as);
        Function<Complex, Complex> dp = polynomialDifferential(as);
        Complex[][] table = eaTable(
            1000,
            initializePoints(as, p),
            p,
            dp
        );

        for (int i = 0; i < table.length; i++)
        {
            System.out.println(Arrays.toString(table[i]));
        }
    }

    public static Function<Complex, Complex> polynomial(double[] as)
    {
        return (x)-> horner(doublesToComplexs(as), x);
    }
    public static Function<Complex, Complex> polynomialDifferential(double[] as)
    {
        double[] cs = new double[as.length-1];
        for (int i = 0; i < cs.length; i++)
        {
            int n = cs.length - i; // != 0
            cs[i] = as[i] * n;
        }
        Function<Complex, Complex> df = polynomial(cs);
        return df;
    }

    private static Complex[] doublesToComplexs(double[] xs)
    {
        Complex[] result = new Complex[xs.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = new Complex(xs[i]);
        }
        return result;
    }

    public static Complex horner(Complex[] as, Complex x)
    {
        Complex y = new Complex(0);
        for (int i = 0; i < as.length; i++)
        {
            y = y.mul(x).add(as[i]);
        }
        return y;
    }
    public static double horner(double[] as, double x)
    {
        double y = 0;
        for (int i = 0; i < as.length; i++)
        {
            y *= x + as[i];
        }
        return y;
    }

    public static Complex[][] dkTable(int count, Complex[] zs, Function<Complex, Complex> p)
    {
        Complex[][] table = new Complex[count][];
        table[0] = zs.clone();
        for (int k = 1; k < count; k++)
        {
            table[k] = new Complex[zs.length];
            for (int i = 0; i < zs.length; i++)
            {
                // z_i^(k-1)
                Complex z = table[k-1][i];
                Complex _multiplied = new Complex(1);
                for (int j = 0; j < zs.length; j++) if (i != j)
                {
                    _multiplied = _multiplied.mul(z.sub(table[k-1][j]));
                }
                // z_i^(k)
                table[k][i] = z.sub(p.apply(z).div(_multiplied));
            }
            if (isConverged(table[k], table[k-1], p))
            {
                return Arrays.copyOf(table, k-1);
            }
            if (k>=2 && isLoop(table[k], table[k-2]) && k < count-1) // ループが発生した時に値を変えて脱出させる
            {
                k++;
                table[k] = new Complex[zs.length];
                for (int i = 0; i < zs.length; i++)
                {
                    table[k][i] = table[k-1][i].add(table[k-3][i]).div(new Complex(2));
                }
            }
        }
        return table;
    }

    private static boolean isLoop(Complex[] xs, Complex[] zs)
    {
        for (int i = 0; i < xs.length; i++)
        {
            if (xs[i].sub(zs[i]).abs()<1.0e-10)
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isConverged(Complex[] xs, Complex[] ys, Function<Complex,Complex>p)
    {
        double sum = 0;
        for (int i = 0; i < xs.length; i++)
        {
            sum += Math.pow(p.apply(xs[i]).abs(), 2);
        }
        sum = Math.sqrt(sum);
        System.out.println(sum);
        return sum < 1.0e-9;
    }

    public static Complex[][] eaTable(int count, Complex[] zs, Function<Complex, Complex> p, Function<Complex, Complex> dp)
    {
        Complex[][] table = new Complex[count][];
        table[0] = zs.clone();
        for (int k = 1; k < count; k++)
        {
            table[k] = new Complex[zs.length];
            for (int i = 0; i < zs.length; i++)
            {
                // z_i^(k-1)
                Complex z = table[k-1][i];
                Complex sum = new Complex(0);
                for (int j = 0; j < zs.length; j++) if (i != j)
                {
                    sum = sum.add(new Complex(1.0).div(z.sub(table[k-1][j])));
                }
                // z_i^(k)
                table[k][i] = z.sub(p.apply(z).div(dp.apply(z).sub(p.apply(z).mul(sum))));
            }
            if (isConverged(table[k], table[k+1], p))
            {
                return Arrays.copyOf(table, k-1);
            }
        }
        return table;
    }

    public static Complex[] initializePoints(double[] as, Function<Complex, Complex> p)
    {
        Complex[] a = doublesToComplexs(as);
        int n = as.length-1;
        Complex[] result = new Complex[n];
        double pi = Math.PI;
        double r = getInitializeRadius(as);
        for (int j = 0; j < n; j++)
        {
            double deg = 2 * pi * (j - 3.0/4) / n;
            result[j] = a[1].div(new Complex(-n).mul(a[0])).add(new Complex(Math.cos(deg), Math.sin(deg)).mul(new Complex(r)));
        }
        return result;
    }

    private static double getInitializeRadius(double[] as)
    {
        double[] bs = new double[as.length];
        double a0 = as[0];
        bs[0] = 1;
        for (int i = 1; i < bs.length; i++)
        {
            bs[i] = -Math.abs(as[i]/a0);
        }
        Function<Double, Double> f = (x) -> horner(bs, x);
        double[] cs = new double[bs.length-1];
        for (int i = 0; i < cs.length; i++)
        {
            int n = cs.length - i; // != 0
            cs[i] = bs[i] * n;
        }
        Function<Double, Double> df = (x) -> horner(cs, x);

        double initialPoint = 0.3;
        int count = 0;
        while (count < 10)
        {
            double solution = NewtonMethod.exec(f, df, initialPoint);
            if (solution > 0)
            {
                return solution;
            }
            count++;
            initialPoint *= -1.1;
        }
        return 10;
    }
}
