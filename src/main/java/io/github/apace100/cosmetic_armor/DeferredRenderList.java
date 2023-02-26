package io.github.apace100.cosmetic_armor;

import java.util.List;
import java.util.function.Supplier;

public interface DeferredRenderList {
	List<Supplier<Boolean>> deferredRenderList();
}
