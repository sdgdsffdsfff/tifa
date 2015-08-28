package walker.basewf.common.utils;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.math.BigDecimal;
import java.util.Collections;


/**
 * 计算器
 *
 * @author wlq
 */
public class Calculator {

    public static void main(String[] args) {

        String exp = "5-(1+2)*1.2*#{x}+sin(0)";

        try {
            Evaluator evaluator = new Evaluator();
            evaluator.setVariables(Collections.singletonMap("x", Double.toString(2.1)));

            String returnRs = evaluator.evaluate(exp);
            System.out.println(Double.parseDouble(returnRs));

            exp = "#{x}";
            evaluator = new Evaluator();
            evaluator.setVariables(Collections.singletonMap("x", Double.toString(2.1)));

            returnRs = evaluator.evaluate(exp);
            System.out.println(Double.parseDouble(returnRs));

            returnRs = "0.545";

            BigDecimal resultBigDecimal = new BigDecimal(returnRs);
            System.out.println(resultBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));

            //String rs2= twoResult2("/","33","2");
            //System.out.println(rs2);

        } catch (EvaluationException e) {
            e.printStackTrace();
        }

    }
}
