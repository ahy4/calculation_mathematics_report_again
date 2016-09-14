package report;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Created by noko on 2016/09/08.
 */
public class Main
{
    public static void main(String[] args)
    {
        double[] as ={2,7,5,-9,-5};
        Function<Complex, Complex> p = SolveAlgorithm.polynomial(as);
        Function<Complex, Complex> dp = SolveAlgorithm.polynomialDifferential(as);
        Complex[][] table = SolveAlgorithm.dkTable(
            10000,
            SolveAlgorithm.initializePoints(as, p),
            p
//            ,dp
        );
        // eaTableも同様に使用できる


        System.out.println(Arrays.deepToString(table));

    }

}
