package progetto_finale_39.parser.ast;
import static java.util.Objects.requireNonNull;
import progetto_finale_39.visitors.Visitor;

public class VectorLit implements Exp {
    private final Exp index;
    private final Exp dimension;
    
    public VectorLit(Exp index, Exp dimension) {
        this.index = requireNonNull(index);
        this.dimension = requireNonNull(dimension);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + index + "," + dimension + ")";
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitVector(index, dimension);
    }
}