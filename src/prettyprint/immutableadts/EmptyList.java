package prettyprint.immutableadts;

public final class EmptyList<T> implements IList<T> {

    private static IList<?> instance = null;

    private EmptyList() {}

    @SuppressWarnings("unchecked")
    public static <T> IList<T> emp() {
        if (instance == null) {
            instance = new EmptyList<>();
        }
        return (IList<T>) instance;
    }

    @Override
    public T head() {
        throw new UnsupportedOperationException("no head on nil list");
    }

    @Override
    public IList<T> tail() {
        return emp();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public String toString() {
        return "List()";
    }

    @Override public boolean equals(Object o) {
        return IList.equalsHelper(this, o);
    }

    @Override public int hashCode() {
        return IList.hashOrdered(this);
    }

}
