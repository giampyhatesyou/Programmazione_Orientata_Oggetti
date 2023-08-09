package progetto_finale_39.parser.ast;

import progetto_finale_39.visitors.Visitor;

public interface AST {
	<T> T (Visitor<T> visitor);
}
