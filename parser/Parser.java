package progetto_finale_39.parser;

import progetto_finale_39.parser.ast.Prog;

public interface Parser extends AutoCloseable {

	Prog parseProg() throws ParserException;

}