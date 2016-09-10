package report;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by noko on 2016/09/08.
 */
public class DurandKerner
{
    public static void main(String[] args)
    {
        double[][] table = zTable(
            20,
            new double[]{1,2,3,4,5,6,7,8,9,10},
            DurandKerner::randomP
        );

        for (int i = 0; i < table.length; i++)
        {
            System.out.println(Arrays.toString(table[i]));
        }
    }

    public static double randomP(double x)
    {
        return horner(new double[]{4,8,0,1,8,1,3,3,2,6}, x);
    }

    /**
     * ホーナー法による a[0]*x^n + a[1]*x^(n-1) + ... + a[n-2]*x^2 + a[n-1]*x^1 + a[n]*x^0 の計算
     * @param as {double[]} 係数の配列
     * @param x {double}
     * @return  {double} a[0]*x^n + a[1]*x^(n-1) + ... + a[n-2]*x^2 + a[n-1]*x^1 + a[n]*x^0
     */
    public static double horner(double[] as, double x)
    {
        double y = 0;
        for (int i = 0; i < as.length; i++)
        {
            y = y * x + as[i];
        }
        return y;
    }

    public static double[][] zTable(int count, double[] zs, Function<Double, Double> p)
    {
        double[][] table = new double[count][];
        table[0] = zs.clone();
        for (int k = 1; k < count; k++)
        {
            table[k] = new double[zs.length];
            for (int i = 0; i < zs.length; i++)
            {
                // z_i^(k-1)
                double z = table[k-1][i];
                double _multiplied = 1;
                for (int j = 0; j < zs.length; j++) if (i != j)
                {
                    _multiplied *= z - table[k-1][j];
                }
                // z_i^(k)
                table[k][i] = z - p.apply(z) / _multiplied;
            }
        }
        return table;
    }



}
