package progetto_finale_39.parser.ast;

import progetto_finale_39.visitors.Visitor;
import static java.util.Objects.requireNonNull;

public class ForEachStmt implements Stmt {

    protected  Variable ident;
    protected  Exp exp;
    private final Block body;

	public ForEachStmt(Variable ident, Exp exp, Block body) {
		this.ident = requireNonNull(ident);
		this.exp = requireNonNull(exp);
        this.body=requireNonNull(body);
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + ident + "," + exp + "," + body + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForEachStmt(ident, exp, body);
	}
}
