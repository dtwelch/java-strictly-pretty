package prettyprint.immutableadts;

import prettyprint.TailCall;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.*;

/**
 * An immutable list class that uses pattern matching. A lot of this code has
 * been adapted from code in "Functional Programming in Java" by Pierre Saumont.
 *
 * @param <T> the type of entries contained in the list.
 */
public sealed interface IList<T> extends Iterable<T>
        permits NonEmptyList, EmptyList {

    // instance methods tied to the list type
    T head();

    IList<T> tail();

    default T get(int i) {
        if (i < 0 || i >= length()) { // O(n)
            throw new IndexOutOfBoundsException("index can't be negative");
        }
        return drop(i).head(); // O(n)
    }

    default IList<T> appendAll(@NotNull IList<T> o) {
        return switch (this) {
            case EmptyList<T> x -> o;
            case NonEmptyList<T> x ->
                    new NonEmptyList<>(x.head(), x.tail().appendAll(o));
        };
    }

    default IList<T> append(T x) {
        return appendAll(new NonEmptyList<>(x, EmptyList.emp()));
    }

    // returns a new list that's the same as this list but with the very last
    // element removed.
    // requires: |this| > 0
    default IList<T> init() {
        if (isEmpty()) {
            throw new UnsupportedOperationException(
                    "can't call init on empty list");
        }
        return reverse().tail().reverse();
    }

    default int length() {
        return foldRight(0, (x, y) -> y + 1);
    }

    // requires: n < |this list|
    default IList<T> drop(int n) {
        return drop_(this, n).eval();
    }

    static <A> TailCall<IList<A>> drop_(@NotNull IList<A> l, int n) {
        return switch (l) {
            case EmptyList<A> x -> TailCall.ret(l);
            case NonEmptyList<A> x -> TailCall.sus(() ->
                    n <= 0 ? TailCall.ret(l) : drop_(l.tail(), n - 1));
        };
    }

    default IList<T> dropWhile(@NotNull Predicate<T> p) {
        return dropWhile_(p, this).eval();
    }

    // todo: should get converted into a tail recursive version...
    private static <A> TailCall<IList<A>> dropWhile_(Predicate<A> p,
                                                     IList<A> list) {
        return switch (list) {
            case NonEmptyList<A> x &&
                    p.test(x.head()) -> TailCall.sus(() -> dropWhile_(p, x.tail()));
            default -> TailCall.ret(list);
        };
    }

    default IList<T> reverse() {
        return reverse_(of(), this).eval();
    }

    private static <A> TailCall<IList<A>> reverse_(IList<A> acc,
                                                   IList<A> list) {
        return switch (list) {
            case EmptyList<A> x -> TailCall.ret(acc);
            case NonEmptyList<A> x -> TailCall.sus(() -> reverse_(
                    new NonEmptyList<>(list.head(), acc), x.tail()));
        };
    }

    default <U> U foldLeft(@NotNull U identity,
                           @NotNull BiFunction<U, T, U> f) {
        return foldLeft_(this, identity,
                (U x) -> (T y) -> f.apply(x, y)).eval();
    }

    /*default <U> U foldLeft(@NotNull U identity,
                           @NotNull Function<U, Function<T, U>> f) {
        return foldLeft_(this, identity, f).eval();
    }*/

    // every method that ends in "_" is a helper method
    // uses TCE (tail call elimination)
    private static <T, U> TailCall<U> foldLeft_(IList<T> ts, U acc,
                                                Function<U, Function<T, U>> f) {
        return switch (ts) {
            case EmptyList<T> x -> TailCall.ret(acc);
            case NonEmptyList<T> x -> TailCall.sus(() -> foldLeft_(ts.tail(),
                    f.apply(acc).apply(ts.head()), f));
        };
    }

    /*default <U> U foldRight(@NotNull U identity,
                            @NotNull Function<T, Function<U, U>> f) {
        return foldRight_(identity, this.reverse(), identity, f).eval();
    }*/

    default <U> U foldRight(@NotNull U identity,
                            @NotNull BiFunction<T, U, U> f) {
        return foldRight_(identity,
                this.reverse(),
                (T x) -> (U y) -> f.apply(x, y)).eval();
    }

    private static <T, U> TailCall<U> foldRight_(U acc, IList<T> ts,
                                                 Function<T, Function<U, U>> f) {
        return switch (ts) {
            case EmptyList<T> x -> TailCall.ret(acc);
            case NonEmptyList<T> x -> TailCall.sus(
                    () -> foldRight_(f.apply(x.head()).apply(acc),
                            x.tail(), f));
        };
    }

    default IList<T> filter(Predicate<T> p) {
        return foldRight(of(), (hd, tl) ->
                p.test(hd) ? new NonEmptyList<>(hd, tl) : tl);
    }

    default <B> IList<B> flatMap(Function<T, IList<B>> f) {
        /* Java is unable to infer type of the second parameter for the
        second function */
        /* return foldRight(list(), h -> t -> f.apply(h).foldRight(t, x ->
        (List<B> y) -> new Cons<>(x, y))); */
        return foldRight(of(), (h, t) -> concat(f.apply(h), t));
    }

    default IList<T> cons(T a) {
        return new NonEmptyList<>(a, this);
    }

    default boolean isEmpty() {
        return false;
    }

    // scala-ish "companion" helper methods

    static int sum(IList<Integer> list) {
        return sum_(list).eval();
    }

    private static TailCall<Integer> sum_(IList<Integer> list) {
        return foldLeft_(list, 0, c -> (num -> c + num));
    }

    default void foreach(Consumer<T> consumer) {
        var these = this;
        while (!these.isEmpty()) {
            consumer.accept(these.head());
            these = these.tail();
        }
    }

    default <B> IList<B> map(Function<T, B> f) {
        return foldRight(of(), (h, t) -> new NonEmptyList<>(f.apply(h), t));
    }

    static <A> IList<A> of() {
        return EmptyList.emp();
    }

    /**
     * A static factory convenience method for constructing variadic lists of
     * the specified elements in {@code a}.
     * <p>
     * <b>Note:</b> The list also provides variant factory methods including:
     * <ul>
     *     <li>{@link #of()} for constructing empty (nil) immutable lists;</li>
     *     <li>{@link #of(List)} that accepts a mutable util list
     *     and returns an immutable version.</li>
     * </ul>
     *
     * @param a   variadic generic input array.
     * @param <A> the type of elements stored in the {@code input} and
     *            returned lists.
     * @return an immutable list.
     */
    @SafeVarargs
    static <A> IList<A> of(A... a) {
        IList<A> result = of();
        for (int i = a.length - 1; i >= 0; i--) {
            result = new NonEmptyList<>(a[i], result);
        }
        return result;
    }

    static <A> IList<A> of(List<A> input) {
        IList<A> result = of();
        for (int i = input.size() - 1; i >= 0; i--) {
            result = new NonEmptyList<>(input.get(i), result);
        }
        return result;
    }

    static <A, B> B foldRight(IList<A> list, B n,
                              BiFunction<A, B, B> f) {
        return list.foldRight(n, f);
    }

    static <A> IList<A> concat(IList<A> list1, IList<A> list2) {
        return foldRight(list1, list2, NonEmptyList::new);
    }

    // methods for equals, hashCode, and iterable

    @Override
    default Iterator<T> iterator() {
        final IList<T> that = this;
        return new Iterator<>() {
            IList<T> iterableList = that;

            @Override public boolean hasNext() {
                return !iterableList.isEmpty();
            }

            @Override public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final T result = iterableList.head();
                iterableList = iterableList.tail();
                return result;
            }
        };
    }

    static <V> boolean equalsHelper(IList<V> source, Object object) {
        if (object == source) {
            return true;
        } else if (source != null && object instanceof IList<?> o) {
            return o.length() == source.length() && areEqual(source, o);
        } else {
            return false;
        }
    }

    static boolean areEqual(Iterable<?> iterable1, Iterable<?> iterable2) {
        final Iterator<?> iter1 = iterable1.iterator();
        final Iterator<?> iter2 = iterable2.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            if (!Objects.equals(iter1.next(), iter2.next())) {
                return false;
            }
        }
        return iter1.hasNext() == iter2.hasNext();
    }

    // hashes the elements respecting their order
    static int hashOrdered(Iterable<?> iterable) {
        return hash(iterable, (acc, hash) -> acc * 31 + hash);
    }

    // hashes the elements regardless of their order
    // (would be good for an immutable set)
    static int hashUnordered(Iterable<?> iterable) {
        return hash(iterable, Integer::sum);
    }

    private static int hash(Iterable<?> iterable,
                            IntBinaryOperator accumulator) {
        if (iterable == null) {
            return 0;
        } else {
            int hashCode = 1;
            for (Object o : iterable) {
                hashCode = accumulator.applyAsInt(hashCode,
                        Objects.hashCode(o));
            }
            return hashCode;
        }
    }
}
