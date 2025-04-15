package dev.hephaestus.glowcase.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class NotSoConstant<T> {
	private final Supplier<T> supplier;
	// Check for in case it gets loaded too early
	private final Function<T, Boolean> isValid;
	private T value;

	public NotSoConstant(Supplier<T> supplier) {
		this(supplier, ignored -> true);
	}

	public NotSoConstant(Supplier<T> supplier, Function<T, Boolean> isValid) {
		this.supplier = supplier;
		this.isValid = isValid;
	}

	public T get() {
		if (value == null || !isValid.apply(value)) {
			value = supplier.get();
		}

		return value;
	}

	public void dispose() {
		this.value = null;
	}
}
