package progetto_finale_39.visitors.execution;

public class IntValue extends AtomicValue<Integer> {

	public IntValue(Integer value) {
		super(value);
	}

	@Override
	public int toInt() {
		return value;
	}

}