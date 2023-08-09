package progetto_finale_39.visitors.execution;

import java.io.PrintWriter;

import progetto_finale_39.environments.EnvironmentException;
import progetto_finale_39.environments.GenEnvironment;
import progetto_finale_39.parser.ast.*;
import progetto_finale_39.visitors.Visitor;

import static java.util.Objects.requireNonNull;

public class Execute implements Visitor<Value> {
	private final GenEnvironment<Value> env = new GenEnvironment<>();
	private final PrintWriter printWriter; // output stream used to print values

	public Execute() {
		printWriter = new PrintWriter(System.out, true);
	}

	public Execute(PrintWriter printWriter) {
		this.printWriter = requireNonNull(printWriter);
	}

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitMyLangProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
			// possible runtime errors
			// EnvironmentException: undefined variable
		} catch (EnvironmentException e) {
			throw new InterpreterException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor

	@Override
	public Value visitAssignStmt(Variable var, Exp exp) {
		env.update(var, exp.accept(this));
		return null;
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		printWriter.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitVarStmt(Variable var, Exp exp) {
		env.dec(var, exp.accept(this));
		return null;
	}

	@Override
	public Value visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		if (exp.accept(this).toBool())
			thenBlock.accept(this);
		else if (elseBlock != null)
			elseBlock.accept(this);
		return null;
	}

	@Override
	public Value visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Value visitEmptyStmtSeq() {
		return null;
	}

	@Override
	public Value visitNonEmptyStmtSeq(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// dynamic semantics of expressions; a value is returned by the visitor
	public Value genAdd(Value left,Value right){
		if(left instanceof IntValue && right instanceof IntValue)
			return new IntValue(left.toInt() + right.toInt());
		if(!(left instanceof VectorValue && right instanceof VectorValue))
			throw new InterpreterException("Operands of + must be both integers or both vectors");

		return ((VectorValue)left).sum((VectorValue)right);
	}

	public Value genMul(Value left,Value right){
		if(left instanceof IntValue && right instanceof IntValue) // int * int
			return new IntValue(left.toInt() * right.toInt());

		if(left instanceof IntValue && right instanceof VectorValue) // int * vector
			return ((VectorValue)right).mul((IntValue)left);

		if(left instanceof VectorValue && right instanceof IntValue) // vector * int
			return ((VectorValue)left).mul((IntValue)right);

		if(!(left instanceof VectorValue && right instanceof VectorValue)) // not vector * vector
			throw new InterpreterException("Operands of * must integers or vectors");
		//se sono arrivato a questo punto sono per forza due vettori

		return ((VectorValue)left).mul((VectorValue)right);	//vector * vector
	}

	@Override
	public Value visitAdd(Exp left, Exp right) {
		return genAdd(left.accept(this), right.accept(this));
	}

	@Override
	public Value visitMul(Exp left, Exp right) {
		return genMul(left.accept(this), right.accept(this));
	}

	@Override
	public IntValue visitIntLiteral(int value) {
		return new IntValue(value);
	}

	@Override
	public IntValue visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).toInt());
	}

	@Override
	public Value visitVariable(Variable var) {
		return env.lookup(var);
	}

	@Override
	public BoolValue visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).toBool());
	}

	@Override
	public BoolValue visitAnd(Exp left, Exp right) {
		return new BoolValue(left.accept(this).toBool() && right.accept(this).toBool());
	}

	@Override
	public BoolValue visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	}

	@Override
	public BoolValue visitEq(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public PairValue visitPairLit(Exp left, Exp right) {
		return new PairValue(left.accept(this), right.accept(this));
	}

	@Override
	public Value visitFst(Exp exp) {
		return exp.accept(this).toPair().getFstVal();
	}

	@Override
	public Value visitSnd(Exp exp) {
		return exp.accept(this).toPair().getSndVal();
	}

    @Override
	public VectorValue visitVector(Exp exp1, Exp exp2) {
		return new VectorValue(exp1.accept(this), exp2.accept(this));
	}


	public Value visitForEachStmt(Variable IDENT, Exp exp, Block body){
		var vector = exp.accept(this).toVect();
		env.enterScope();

		env.dec(IDENT, new IntValue(-1));

		for (var i =0; i < vector.getDimension().toInt(); i++){
			env.update(IDENT, vector.getElement(i));
			body.accept(this);
		}
		env.exitScope();
		return null;
	}
}
