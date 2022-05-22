package prettyprint.immutableadts;

import prettyprint.TailCall;

public final class NonEmptyList<T> implements IList<T> {

    private final T head;
    private final IList<T> tail;

    public NonEmptyList(T head, IList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public IList<T> tail() {
        return tail;
    }

    @Override
    public String toString() {
        return String.format("List(%s)",
                toString_(new StringBuilder(), this).eval());
    }

    static <T> TailCall<StringBuilder> toString_(StringBuilder acc,
                                                 IList<T> lst) {
        return switch (lst) {
            case EmptyList<T> x -> TailCall.ret(acc);
            case NonEmptyList<T> x -> TailCall.sus(() ->
                    toString_(acc.append(x.head())
                                    .append(x.tail() != EmptyList.emp() ? ", " : ""),
                            x.tail()));
        };
    }

    @Override public boolean equals(Object o) {
        return IList.equalsHelper(this, o);
    }

    @Override public int hashCode() {
        return IList.hashOrdered(this);
    }

}
