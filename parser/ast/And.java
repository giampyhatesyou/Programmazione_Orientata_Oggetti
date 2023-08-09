package progetto_finale_39.parser.ast;

import progetto_finale_39.visitors.Visitor;

public class And extends BinaryOp {
	public And(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitAnd(left, right);
	}
}
