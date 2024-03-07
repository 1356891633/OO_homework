package expr;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public class Variable implements Factor {
    private final LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger>
            variable = new LinkedHashMap<>();

    public Variable(String varName, int exp, BigInteger coef) {
        LinkedHashMap<String, Integer> var = new LinkedHashMap<>();
        var.put(varName, exp);
        variable.put(var, coef);
    }

    @Override
    public LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> getTerms() {
        return variable;
    }
}
