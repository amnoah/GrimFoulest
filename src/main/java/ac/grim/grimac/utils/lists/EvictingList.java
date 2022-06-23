package ac.grim.grimac.utils.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;

// https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/util/EvictingList.java
@AllArgsConstructor
public final class EvictingList<T> extends LinkedList<T> {

    @Getter
    private final int maxSize;

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= getMaxSize()) {
            removeFirst();
        }

        return super.add(t);
    }
}