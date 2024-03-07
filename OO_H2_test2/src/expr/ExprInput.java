package expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExprInput {
    private ExprInputMode mode = null;
    private List<String> lines;
    private int count = 0;
    private int listPos = 0;

    public ExprInput(ExprInputMode mode) {
        this.mode = mode;
        if (mode == ExprInputMode.NormalMode) {
        } else if (mode == ExprInputMode.ParsedMode) {
        } else {
        }

        this.parse();
    }

    public int getCount() {
        return this.mode == ExprInputMode.ParsedMode ? this.lines.size() : this.count;
    }

    public String readLine() {
        return this.listPos <= this.getCount() ? (String)this.lines.get(this.listPos++) : null;
    }

    private void parse() {
        Scanner scanner = new Scanner(System.in);
        this.lines = new ArrayList();
        int i;
        if (this.mode == ExprInputMode.ParsedMode) {
            this.count = scanner.nextInt();
            scanner.nextLine();

            for(i = 0; i < this.count; ++i) {
                this.lines.add(scanner.nextLine());
            }
        } else {
            this.count = scanner.nextInt();
            scanner.nextLine();

            for(i = 0; i <= this.count; ++i) {
                this.lines.add(scanner.nextLine());
            }
        }

    }
}
