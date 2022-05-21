package com.dt.immutableadts;

import org.rsrg.resolve.utils.TailCall;

import static org.rsrg.resolve.utils.TailCall.ret;
import static org.rsrg.resolve.utils.TailCall.sus;
import static org.rsrg.resolve.utils.immutableadts.list.EmptyList.emp;

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
            case EmptyList<T> x -> ret(acc);
            case NonEmptyList<T> x -> sus(() ->
                    toString_(acc.append(x.head())
                                    .append(x.tail() != emp() ? ", " : ""),
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
