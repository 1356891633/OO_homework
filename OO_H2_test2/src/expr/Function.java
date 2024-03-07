package expr;

public class Function {
    private final String[] parameters;
    private final String fun;

    public Function(String[] parameters, String fun) {
        this.parameters = parameters;
        this.fun = fun.replace("**", "^");
    }

    public String getFun() {
        return fun;
    }

    public String[] getParameters() {
        return parameters;
    }
}
