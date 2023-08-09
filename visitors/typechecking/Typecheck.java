package progetto_finale_39.visitors.typechecking;

import static progetto_finale_39.visitors.typechecking.AtomicType.*;

import progetto_finale_39.parser.ast.Exp;
import progetto_finale_39.environments.EnvironmentException;
import progetto_finale_39.environments.GenEnvironment;
import progetto_finale_39.parser.ast.*;
import progetto_finale_39.visitors.Visitor;



public class Typecheck implements Visitor<Type> {

	private final GenEnvironment<Type> env = new GenEnvironment<>();

    // useful to typecheck binary operations where operands must have the same type 
	private void checkBinOp(Exp left, Exp right, Type type) {
		type.checkEqual(left.accept(this));
		type.checkEqual(right.accept(this));
	}

	// static semantics for programs; no value returned by the visitor

	@Override
	public Type visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
		} catch (EnvironmentException e) { // undeclared variable
			throw new TypecheckerException(e);
		}
		return null;
	}

	// static semantics for statements; no value returned by the visitor

	@Override
	public Type visitAssignStmt(Variable var, Exp exp) {
		var found = env.lookup(var);
		found.checkEqual(exp.accept(this));
		return null;
	}

	@Override
	public Type visitPrintStmt(Exp exp) {
		exp.accept(this);
		return null;
	}

	@Override
	public Type visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	}

	@Override
	public Type visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		BOOL.checkEqual(exp.accept(this));
		thenBlock.accept(this);
		if (elseBlock != null)
			elseBlock.accept(this);
		return null;
	}

	@Override
	public Type visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// static semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Type visitEmptyStmtSeq() {
		return null;
	}

	@Override
	public Type visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// static semantics of expressions; a type is returned by the visitor

	//checkIntOrVect da semantic.ml per verificare che sia un int o un vector
	private AtomicType checkIntOrVect(Exp e) {
		var type = e.accept(this);
		if(INT.equals(type) || VECT.equals(type)) return (AtomicType) type;
		throw new TypecheckerException("Found " + type + ", expected INT or VECT");
	}
	
	//cambiando per poter gestire i vettori
	@Override
	public AtomicType visitAdd(Exp left, Exp right) {
		var found = checkIntOrVect(left);
		found.checkEqual(checkIntOrVect(right));
		return found;
	}

	@Override
	public AtomicType visitIntLiteral(int value) {
		return INT;
	}

	//cambiando per poter gestire i vettori
	@Override
	public AtomicType visitMul(Exp left, Exp right) {
		//voglio che siano INT o VECTOR, vanno bene anche diversi
		var found1 = checkIntOrVect(left);
		var found2 = checkIntOrVect(right);
		assert (INT.equals(found1) || VECT.equals(found1));
		assert (INT.equals(found2) || VECT.equals(found2));
		//se i due elementi sono uguali allora ritorno un INT
		if(found1.equals(found2))
			return INT;
		else	 //altrimenti ho un VECTOR
			return VECT;
	}

	@Override
	public AtomicType visitSign(Exp exp) {
		INT.checkEqual(exp.accept(this));
		return INT;
	}

	@Override
	public Type visitVariable(Variable var) {
		return env.lookup(var);
	}

	@Override
	public AtomicType visitNot(Exp exp) {
		BOOL.checkEqual(exp.accept(this));
		return BOOL;
	}

	@Override
	public AtomicType visitAnd(Exp left, Exp right) {
		checkBinOp(left, right, BOOL);
		return BOOL;
	}

	@Override
	public AtomicType visitBoolLiteral(boolean value) {
		return BOOL;
	}

	@Override
	public AtomicType visitEq(Exp left, Exp right) {
		left.accept(this).checkEqual(right.accept(this));
		return BOOL;
	}

	@Override
	public PairType visitPairLit(Exp left, Exp right) {
		return new PairType(left.accept(this), right.accept(this));
	}

	@Override
	public Type visitFst(Exp exp) {
		return exp.accept(this).getFstPairType();
	}

	@Override
	public Type visitSnd(Exp exp) {
		return exp.accept(this).getSndPairType();
	}

	@Override
	public AtomicType visitVector(Exp index, Exp dimension) {
		checkBinOp(index, dimension, INT);
		return VECT;
	}


	@Override
	public Type visitForEachStmt(Variable ident, Exp exp, Block block) {
		VECT.checkEqual(exp.accept(this));
		env.enterScope();
		env.dec(ident, INT);
		block.accept(this);
		env.exitScope();

		return null;
	}
}
