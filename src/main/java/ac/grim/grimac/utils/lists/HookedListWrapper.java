package ac.grim.grimac.utils.lists;

import org.jetbrains.annotations.NotNull;

import java.util.*;

// https://github.com/ThomasOM/Pledge/blob/master/src/main/java/dev/thomazz/pledge/util/collection/HookedListWrapper.java
@SuppressWarnings({"unchecked"})
public abstract class HookedListWrapper<T> extends ListWrapper<T> {
    public HookedListWrapper(List<T> base) {
        super(base);
    }

    // We can use the List#size call to execute some code
    public abstract void onIterator();

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return base.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        onIterator();
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        return base.toArray();
    }

    @Override
    public boolean add(T o) {
        return base.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return base.remove(o);
    }

    @Override
    public boolean addAll(@NotNull Collection c) {
        return base.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection c) {
        return base.addAll(index, c);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public T get(int index) {
        return base.get(index);
    }

    @Override
    public T set(int index, T element) {
        return base.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        base.add(index, element);
    }

    @Override
    public T remove(int index) {
        return base.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return base.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return base.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return base.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return base.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return base.subList(fromIndex, toIndex);
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        return base.retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        return base.removeAll(c);
    }

    @Override
    public boolean containsAll(@NotNull Collection c) {
        return new HashSet<>(base).containsAll(c);
    }

    @Override
    public Object[] toArray(Object @NotNull [] a) {
        return base.toArray(a);
    }
}