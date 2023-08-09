package progetto_finale_39.parser.ast;

import progetto_finale_39.visitors.Visitor;

public class EmptyStmtSeq extends EmptySeq<Stmt> implements StmtSeq {

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitEmptyStmtSeq();
	}
}
