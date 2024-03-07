package expr;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Expr implements Factor {
    private LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> terms;

    public Expr() {
        this.terms = new LinkedHashMap<>();
    }

    public void setTerms(LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> terms) {
        this.terms = terms;
    }

    public LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> getTerms() {
        return terms;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        BigInteger constant = new BigInteger("0");
        for (LinkedHashMap<String, Integer> varName: terms.keySet()) {
            BigInteger coef = terms.get(varName);
            boolean isConstant = true;
            for (String var: varName.keySet()) {
                if (!varName.get(var).equals(0)) {
                    isConstant = false;
                }
            }
            if (isConstant) {
                constant = constant.add(coef);
                continue;
            }
            if (coef.equals(BigInteger.ZERO)) {
                continue;
            } else if (coef.compareTo(BigInteger.ZERO) > 0) {
                res.append("+");
            }

            if (coef.equals(BigInteger.ONE) || coef.equals(BigInteger.valueOf(-1))) {
                if (coef.equals(BigInteger.valueOf(-1))) {
                    res.append("-");
                }
            } else {
                res.append(coef);
                res.append("*");
            }
            boolean first = true;
            for (String var: varName.keySet()) {
                if (!varName.get(var).equals(0)) {
                    if (!first) {
                        res.append("*");
                    } else {
                        first = false;
                    }
                    res.append(var);
                    if (!varName.get(var).equals(1)) {
                        if(res.charAt(res.length()-2) == '1') {
                            res.insert(res.length()-2,varName.get(var));
                            res.deleteCharAt(res.length()-2);
                        }else {
                            res.append("^");
                            res.append(varName.get(var));
                        }
                    }

                }
            }
        }

        if (!constant.equals(BigInteger.ZERO)) {
            if (constant.compareTo(BigInteger.ZERO) > 0) {
                res.append("+");
                res.append(constant);
            } else {
                res.append(constant);
            }
        }
        if (res.length() == 0) {
            return "0";
        }

        // 头部 "+" 优化掉
        while (res.charAt(0) == '+') {
            res = new StringBuilder(res.substring(1, res.length()));
        }

        return res.toString();
    }

    public void addTerm(Term term) {
        Iterator<Factor> it = term.getFactor().iterator();
        Factor factor = it.next();

        LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> tmp = new LinkedHashMap<>();
        for (LinkedHashMap<String, Integer> varName: factor.getTerms().keySet()) {
            tmp.put(varName, factor.getTerms().get(varName));
        }

        BigInteger sign = BigInteger.valueOf(term.getSign());
        while (it.hasNext()) {
            factor = it.next();
            tmp = multi(tmp, factor.getTerms(),1);
        }
        for (LinkedHashMap<String, Integer> varName: tmp.keySet()) {
            tmp.replace(varName, sign.multiply(tmp.get(varName)));
        }

        for (LinkedHashMap<String, Integer> varName: tmp.keySet()) {
            if (terms.containsKey(varName)) {
                terms.replace(varName, tmp.get(varName).add(terms.get(varName)));
            } else {
                terms.put(varName, tmp.get(varName));
            }
        }
    }

    private LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> multi(
        LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> op1,
        LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> op2,int type) {
        // Pattern : a * x ** b or a * sin/cos(m*x**n) ** b
        LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> res = new LinkedHashMap<>();

        for (LinkedHashMap<String, Integer> var1: op1.keySet()) {
            BigInteger b1 = op1.get(var1);
            for (LinkedHashMap<String, Integer> var2: op2.keySet()) {
                BigInteger b2 = op2.get(var2);
                LinkedHashMap<String, Integer> varName = new LinkedHashMap<>();
                for (String varName1: var1.keySet()) {
                    if (var2.containsKey(varName1)) {
                        int i = var1.get(varName1) + var2.get(varName1);
                        if(varName1.contains("exp") && type == 1) {
                            varName1 = varName1.substring(0, varName1.length() - 2) + i + "*" + varName1.substring((varName1.length() - 2));
                            varName.put(varName1, 1);
                        }else {
                            varName.put(varName1, var1.get(varName1) + var2.get(varName1));
                        }
                    } else {
                        varName.put(varName1, var1.get(varName1));
                    }
                }
                for (String varName2: var2.keySet()) {
                    if (!var1.containsKey(varName2)) {
                       varName.put(varName2, var2.get(varName2));
                    }
                }
                if (res.containsKey(varName)) {
                    res.put(varName, res.get(varName).add(b1.multiply(b2)));
                } else {
                    res.put(varName, b1.multiply(b2));
                }
            }
        }

        return res;
    }

    public LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> calcPow(int num,int type) {
        LinkedHashMap<LinkedHashMap<String, Integer>, BigInteger> newTerms = new LinkedHashMap<>();
        if (num == 0) {
            LinkedHashMap<String, Integer> varName = new LinkedHashMap<>();
            varName.put("x", 0);
            newTerms.put(varName, BigInteger.ONE);
            return newTerms;
        }

        for (LinkedHashMap<String, Integer> varName: terms.keySet()) {
            newTerms.put(varName, terms.get(varName));
        }

        for (int i = 1; i < num; i++) {
            newTerms = multi(newTerms, terms,type);
        }

        return newTerms;
    }

}
