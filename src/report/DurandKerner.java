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
        double[] as = {1,0,-3,0};
        Function<Complex, Complex> p = polynomial(as);
        Function<Complex, Complex> dp = polynomialDifferential(as);
        Complex[][] table = dkaTable(
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
        }
        return table;
    }

    public static Complex[][] dkaTable(int count, Complex[] zs, Function<Complex, Complex> p, Function<Complex, Complex> dp)
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
        }
        return table;
    }

    public static Complex[] initializePoints(double[] as, Function<Complex, Complex> p)
    {
        Complex[] a = doublesToComplexs(as);
        int n = as.length-1;
        Complex[] result = new Complex[n];
        double pi = Math.PI;
        double r = 10000;
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

        return NewtonMethod.exec(f, df);
    }
}
