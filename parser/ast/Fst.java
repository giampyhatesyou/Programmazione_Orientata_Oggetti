package progetto_finale_39.parser.ast;

import progetto_finale_39.visitors.Visitor;

public class Fst extends UnaryOp {

	public Fst(Exp exp) {
		super(exp);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitFst(exp);
	}
}
