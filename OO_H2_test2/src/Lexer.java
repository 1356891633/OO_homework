public class Lexer {
    private String expr;
    private int pos = 0;
    private String now;

    public void next() {
        if (pos == expr.length()) {
            return;
        }

        char c1 = expr.charAt(pos);
        String str3 = pos + 3 < expr.length() ? expr.substring(pos, pos + 3) : "Error";

        if (Character.isDigit(c1)) {
            now = getNumber();
        } else if (str3.equals("sin") || str3.equals("cos") || str3.equals("exp") ||
                str3.equals("sum")) {
            pos += 3;
            now = str3;
        } else if ("fgh,()+-*ix^".indexOf(c1) != -1) {
            pos += 1;
            now = String.valueOf(c1);
        }
    }

    public String peek() {
        return this.now;
    }

    public String getNumber() {
        StringBuilder num = new StringBuilder();
        while (pos < expr.length() && Character.isDigit(expr.charAt(pos))) {
            num.append(expr.charAt(pos));
            pos += 1;
        }
        return num.toString();
    }

    public Lexer(String expr) {
        this.expr = expr;
        this.next();
    }

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
