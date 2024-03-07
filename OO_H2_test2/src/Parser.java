import expr.Expr;
import expr.Variable;
import expr.Factor;
import expr.Function;
import expr.Term;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Parser {
    private final Lexer lexer;
    private final HashMap<String, Function> functions;

    public Parser(Lexer lexer, HashMap<String, Function> functions) {
        this.lexer = lexer;
        this.functions = functions;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();

        int sign = getNumSign();
        expr.addTerm(parseTerm(sign));

        while ("+-".contains(lexer.peek())) {
            sign = getNumSign();
            expr.addTerm(parseTerm(sign));
        }
        return expr;
    }

    public Term parseTerm(int sign) {
        Term term = new Term(sign);
        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {
        int exp = 1;
        if (lexer.peek().equals("sum")) {
            int start = lexer.getPos() - 3;
            getSum(start);
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            return expr;
        } else if ("fgh".contains(lexer.peek())) {
            int start = lexer.getPos() - 1;
            getFun(start, functions.get(lexer.peek()));
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            return expr;
        } else if (lexer.peek().equals("sin") || lexer.peek().equals("cos") || lexer.peek().equals("exp")) {
            final StringBuilder varName = new StringBuilder(lexer.peek());
            lexer.next();
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                exp = getNumSign() * Integer.parseInt(lexer.peek());
                lexer.next();
            }
            String res = expr.toString().contains("+") || expr.toString().contains("-") ||
                    expr.toString().contains("*") ?
                    varName + "((" + expr + "))" : varName + "(" + expr + ")";
            return expr.toString().equals("0") ? (varName.toString().equals("exp") ?
                    (exp == 0 ? new Variable("x", 0, BigInteger.ONE) :
                            new Variable("x", 0, BigInteger.ZERO)) :
                    new Variable("x", 0, BigInteger.ONE)) : new Variable(res, exp, BigInteger.ONE);
        } else if (lexer.peek().equals("x")) {             // 变量因子
            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                exp = getNumSign() * Integer.parseInt(lexer.peek());
                lexer.next();
            }
            return new Variable("x", exp, BigInteger.ONE);
        } else if (lexer.peek().equals("(")) {      // 表达式因子
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                int num = getNumSign() * Integer.parseInt(lexer.peek());
                lexer.next();
               if(expr.toString().contains("+")||expr.toString().contains("-")||expr.toString().contains("*")){
                   expr.setTerms(expr.calcPow(num,1));
               }else {
                   expr.setTerms(expr.calcPow(num,0));
               }
            }
            return expr;
        } else {                                    // 数字
            int sign = getNumSign();
            BigInteger num = new BigInteger(lexer.peek()).multiply(BigInteger.valueOf(sign));
            lexer.next();
            return new Variable("x", 0, num);
        }
    }

    private int getNumSign() {
        int sign = 1;
        while ("+-".contains(lexer.peek())) {
            sign = lexer.peek().equals("-") ? -sign : sign;
            lexer.next();
        }
        return sign;
    }

    private String[] getSumParameters(String[] oldParameters) {
        String[] res = new String[3];
        int cnt = 0;
        for (int i = 0; i < oldParameters.length; ) {
            StringBuilder sb = new StringBuilder();
            if (oldParameters[i].contains("sum") ||
                    "fgh".indexOf(oldParameters[i].charAt(0)) != -1) {
                int flag = 0;
                do {
                    for (int j = 0; j < oldParameters[i].length(); j++) {
                        flag = oldParameters[i].charAt(j) == '(' ? flag + 1 :
                                oldParameters[i].charAt(j) == ')' ? flag - 1 : flag;
                    }
                    sb.append(oldParameters[i]);
                    sb.append(flag != 0 ? "," : "");
                    i++;
                } while (flag != 0);
                res[cnt++] = sb.toString();
            } else {
                res[cnt++] = oldParameters[i++];
            }
        }
        return res;
    }

    private void getFun(int start, Function function) {
        StringBuilder sb = new StringBuilder();
        int flag = 0;
        int end;
        lexer.next();
        do {
            if (lexer.peek().equals("(")) {
                flag += 1;
            } else if (lexer.peek().equals(")")) {
                flag -= 1;
            }
            sb.append(lexer.peek());
            end = lexer.getPos();
            lexer.next();
        } while (flag != 0);

        String[] parameters = function.getParameters();
        String[] realParameters = sb.substring(1, sb.length() - 1).split(",");
        if (sb.toString().contains("sum") || sb.toString().contains("f")
                || sb.toString().contains("g") || sb.toString().contains("h")) {
            realParameters = getSumParameters(realParameters);
        }

        String tmp = "(" + function.getFun() + ")";

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals("x")) {
                tmp = tmp.replace("x", "(" + realParameters[i] + ")");
                if(tmp.contains("e(x)p")){
                    tmp = tmp.replace("e(x)p", "exp");
                }
                if(tmp.contains("e(1)p")){
                    tmp = tmp.replace("e(1)p", "exp");
                }
                if(tmp.contains("e(exp(x))p")){
                    tmp = tmp.replace("e(exp(x))p", "exp");
                }

                break;
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].equals("x")) {
                tmp = tmp.replace(parameters[i], "(" + realParameters[i] + ")");

            }
        }

        StringBuilder newExpr = new StringBuilder(lexer.getExpr());
        newExpr.replace(start, end, tmp);

        lexer.setExpr(newExpr.toString());
        lexer.setPos(start);
        lexer.next();
    }

    private void getSum(int start) {
        int flag = 0;
        int end;
        lexer.next();
        StringBuilder sb = new StringBuilder();
        do {
            if (lexer.peek().equals("(")) {
                flag += 1;
            } else if (lexer.peek().equals(")")) {
                flag -= 1;
            }
            sb.append(lexer.peek());
            end = lexer.getPos();
            lexer.next();
        } while (flag != 0);

        String[] parameters = sb.substring(1, sb.length() - 1).split(",");
        sb.delete(0, sb.length());
        BigInteger s = new BigInteger(parameters[1]);
        BigInteger e = new BigInteger(parameters[2]);
        String basicTerm = parameters[3];

        basicTerm = basicTerm.replace("sin", "$");
        if (s.compareTo(e) <= 0) {
            sb.append(basicTerm.replace("i", "(" + s + ")"));
        }
        for (BigInteger i = s.add(BigInteger.ONE); i.compareTo(e) <= 0; i = i.add(BigInteger.ONE)) {
            sb.append("+");
            sb.append(basicTerm.replace("i", "(" + i + ")"));
        }

        sb = new StringBuilder(sb.toString().replace("$", "sin"));
        StringBuilder newExpr = new StringBuilder(lexer.getExpr());
        if (sb.length() != 0) {
            //  newExpr.replace(start, end,   sb + ")");
            newExpr.replace(start, end, "(" + sb + ")");
        } else {
            newExpr.replace(start, end, "0");
        }

        lexer.setExpr(newExpr.toString());
        lexer.setPos(start);
        lexer.next();
    }
}
