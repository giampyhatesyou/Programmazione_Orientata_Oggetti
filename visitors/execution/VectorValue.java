package progetto_finale_39.visitors.execution;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;


public class VectorValue implements Value {

    private final int[] elements;
    private final Value idx;
    private final Value dim;

    public VectorValue(Value index, Value dimension) {

        try {
            //assert (index.toInt() >= dimension.toInt() || index.toInt() < 0);
            //assert (dimension.toInt() <= 0);

            this.idx = requireNonNull(checkValue(index));
            this.dim = requireNonNull(checkValue(dimension));
            this.elements= new int[dim.toInt()];

            for (int i = 0; i < dimension.toInt(); i++)
                elements[i] = 0;
            elements[idx.toInt()] = 1;
        } catch (Exception e) {
            throw new InterpreterException(e);
        }
    }

    private VectorValue(int[] elements) {
        this.elements = requireNonNull(elements);
        this.idx = new IntValue(0);
        this.dim = new IntValue(elements.length);
    }

    public Value getDimension() {
        return dim;
    }

    public Value getElement(int i) {
        return new IntValue(elements[i]);
    }

    private Value checkValue(Value index){
        if(index instanceof IntValue) return index;

        throw new InterpreterException("Expected IntValue");
    }

    public boolean checkDimension(VectorValue v) {
        return this.dim.toInt() == v.dim.toInt();
    }

    @Override
    public VectorValue toVect(){
        return this;
    }
    
    public IntValue mul(VectorValue v){
        if(!checkDimension(v)) throw new InterpreterException("Vectors must have the same dimension");
        var size = getDimension().toInt();
        var aux = 0;

        for (var i=0; i < size; ++i)
            aux += this.getElement(i).toInt() * v.getElement(i).toInt();

        return new IntValue(aux);
    }


    public VectorValue mul(IntValue v){
        var size = getDimension().toInt();
        int [] newVec = new int[size];
        for (var i=0; i < size; ++i)
            newVec[i] = this.getElement(i).toInt() * v.toInt();
        return new VectorValue(newVec);
    }

    //vector * vector = int
    public VectorValue sum(VectorValue v){
        if(!checkDimension(v)) throw new InterpreterException("Vectors must have the same dimension");
        var size = getDimension().toInt();
        int [] newVec = new int[size];
        for (var i=0; i < size; ++i)
            newVec[i] = this.getElement(i).toInt() + v.getElement(i).toInt();
        return new VectorValue(newVec);
    }

    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof VectorValue ot)
            return Arrays.equals(elements, ot.elements);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]);
            if(i != elements.length - 1)
                sb.append(";");
        }
        sb.append("]");
        return sb.toString();
    }

    public int hashCode() {
        return hash(Arrays.hashCode(elements));
    }
}
