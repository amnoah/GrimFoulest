package ac.grim.grimac.utils.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.LinkedList;

@AllArgsConstructor
public final class EvictingList<T> extends LinkedList<T> {

    @Getter
    private final int maxSize;

    public EvictingList(Collection<? extends T> collection, int maxSize) {
        super(collection);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            removeFirst();
        }

        return super.add(t);
    }
}