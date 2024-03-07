import expr.ExprInput;
import expr.ExprInputMode;
import expr.Expr;
import expr.Function;

import java.util.HashMap;

public class MainClass {
    public static void main(String[] args) {
        ExprInput scanner = new ExprInput(ExprInputMode.NormalMode);
        // 获取自定义函数个数
        int cnt = scanner.getCount();

        // 读入自定义函数
        HashMap<String, Function> functions = new HashMap<>();
        for (int i = 0; i < cnt; i++) {
            String tmp = scanner.readLine();
            tmp = tmp.replaceAll("[ \t]", "");
            String[] parts = tmp.split("=");

            // 函数名: f,g,h
            String name = Character.toString(parts[0].charAt(0));
            // 含有的形参列表, 形参只有x,y,z, 可以用下面方法直接去掉括号
            // 此处考虑形参列表顺序可能不是 (x,y,z)，eg.f(z,x,y)
            String[] parameters = parts[0].replaceAll("[fgh()]", "").split(",");
            // 原始形参表达式
            String fun = parts[1];

            Function function = new Function(parameters, fun);
            functions.put(name, function);
        }

        // 读入最后一行表达式
        String expr = scanner.readLine().replaceAll("[ \t]", "");
        expr = expr.replace("**", "^");

        // 表达式括号展开相关的逻辑
        Lexer lexer = new Lexer(expr);
        Parser parser = new Parser(lexer, functions);
        Expr res = parser.parseExpr();
        System.out.println(res.toString());
//        exp(x)*exp(exp(x))+exp(x)*exp((exp(1)+exp(2)-exp(2)))^2-exp(2)
    }
}
