package prettyprint;

import java.util.function.Supplier;

/**
 * A class to simulate tail call elimination (TCE) for recursive methods to
 * limit stack-related memory issues on large inputs.
 *
 * <b>Note:</b>  this can only be used if the method's recursive call is the very
 * last statement in the method. If not, the method must be re-written in a
 * tail recursive way before this class can be used.
 *
 * @param <T>
 */
public abstract class TailCall<T> {

    public abstract TailCall<T> resume();

    public abstract T eval();

    public abstract boolean isSuspend();

    private TailCall() {
    }

    private static final class Return<T> extends TailCall<T> {

        private final T t;

        private Return(T t) {
            this.t = t;
        }

        @Override public T eval() {
            return t;
        }

        @Override public boolean isSuspend() {
            return false;
        }

        @Override public TailCall<T> resume() {
            throw new IllegalStateException("Return has no resume");
        }
    }

    private static final class Suspend<T> extends TailCall<T> {

        private final Supplier<TailCall<T>> resume;

        private Suspend(Supplier<TailCall<T>> resume) {
            this.resume = resume;
        }

        @Override
        public T eval() {
            TailCall<T> tailRec = this;
            while(tailRec.isSuspend()) {
                tailRec = tailRec.resume();
            }
            return tailRec.eval();
        }

        @Override
        public boolean isSuspend() {
            return true;
        }

        @Override
        public TailCall<T> resume() {
            return resume.get();
        }
    }

    public static <T> Return<T> ret(T t) {
        return new Return<>(t);
    }

    public static <T> Suspend<T> sus(Supplier<TailCall<T>> s) {
        return new Suspend<>(s);
    }
}