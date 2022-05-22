package prettyprint.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static prettyprint.immutableadts.EmptyList.emp;
import static prettyprint.immutableadts.IList.of;

public final class ImmutableListTests {

    // list factory tests / toString tests

    @Test public void stringifyNonEmp() {
        Assertions.assertEquals(of(1, 2, 3).toString(), "List(1, 2, 3)");
    }

    @Test public void stringifyEmp() {
        Assertions.assertEquals(emp().toString(), "List()");
    }

    // append tests

    @Test public void appendElementToEmp() {
        final var expected = of(1);
        final var actual = EmptyList.<Integer>emp().append(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test public void appendNullElementToEmp() {
        final var expected = of((Integer) null);
        final var actual = emp().append(null);
        Assertions.assertEquals(expected, actual);
    }

    @Test public void appendElementToNonEmp() {
        final IList<Integer> expected = of(1, 2, 3);
        final IList<Integer> actual = of(1, 2).append(3);
        Assertions.assertEquals(expected, actual);
    }

    @Test public void throwErrorOnAppendAllNull() {
        // noinspection ConstantConditions
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> emp().appendAll(null));
    }

    @Test public void appendEmpToEmp() {
        final var expected = emp();
        final var actual = emp().appendAll(emp());
        Assertions.assertEquals(expected, actual);
    }

    @Test public void appendNonEmpToEmp() {
        final var expected = of(1, 2, 3);
        final var actual = emp().appendAll(of(1, 2, 3));
        Assertions.assertEquals(expected, actual);
    }

    @Test public void appendAllEmpToNonEmp() {
        final var expected = of(1, 2, 3);
        final var actual = of(1, 2, 3).appendAll(emp());
        Assertions.assertEquals(expected, actual);
    }

    @Test public void appendNonEmpToNonEmp() {
        final var expected = of(1, 2, 3, 4, 5, 6);
        final var actual = of(1, 2, 3).appendAll(of(4, 5, 6));
        Assertions.assertEquals(expected, actual);
    }

    @Test public void emptySameTest() {
        Assertions.assertEquals(emp().appendAll(emp()), emp());
    }

    @Test public void sameListWhenEmptyAppendAllNonEmp() {
        final var lst = of(1, 2, 3);
        Assertions.assertEquals(EmptyList.<Integer>emp().appendAll(lst), lst);
    }

    @Test public void sameListWhenNonEmptyAppendAllEmp() {
        final var lst = of(1, 2, 3);
        Assertions.assertEquals(lst.appendAll(emp()), lst);
    }

    // drop while

    @Test public void dropWhileNoneOnEmp() {
        Assertions.assertEquals(emp(), emp().dropWhile(ignored -> true));
    }

    @Test public void dropWhileNoneIfPredicateIsFalse() {
        Assertions.assertEquals(of(1, 2, 3), of(1, 2, 3).dropWhile(i -> false));
    }

    @Test public void dropWhileAllIfPredicateIsTrue() {
        final var actual = of(1, 2, 3).dropWhile(x -> true);
        Assertions.assertEquals(actual, emp());
    }

    @Test public void dropWhileAccordingToPredicate() {
        final var expected = of(2, 3);
        final var actual = of(1, 2, 3).dropWhile(i -> i < 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test public void dropWhileAndNotTruncate() {
        Assertions.assertEquals(of(2, 3),
                of(1, 2, 3).dropWhile(i -> i % 2 == 1));
    }

    // flatMap

    @Test public void flatMapEmp() {
        Assertions.assertEquals(emp(), emp().flatMap(v -> of(v, 0)));
    }

    @Test public void flatMapNonEmp() {
        Assertions.assertEquals(of(1, 0, 2, 0, 3, 0),
                of(1, 2, 3).flatMap(v -> of(v, 0)));
    }

    // foldLeft

    @Test public void foldLeftEmp() {
        Assertions.assertEquals("", emp().foldLeft("", (xs, x) -> xs + x));
    }

    @Test public void throwWhenFoldLeftNullOperator() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> emp().foldLeft(null, (x, y) -> x));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> emp().foldLeft(null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> emp().foldLeft(0, null));
    }

    @Test public void foldLeftNonEmp() {
        Assertions.assertEquals("!abc", of("a", "b", "c")
                .foldLeft("!", (xs, x) -> xs + x));
    }

    // foldRight

    @Test public void foldRightEmp() {
        Assertions.assertEquals("", EmptyList.<String>emp().foldRight("",
                (String x, String xs) -> x + xs));
    }

    @Test public void throwWhenFoldRightNullOperator() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EmptyList.<String>emp().foldRight(null, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EmptyList.<String>emp().foldRight(0, null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EmptyList.<String>emp().foldRight(null, (x, y) -> x));
    }

    @Test public void foldRightNonEmp() {
        Assertions.assertEquals("abc!", of("a", "b", "c")
                .foldRight("!", (x, xs) -> x + xs));
    }

    // is empty

    @Test public void recognizeEmp() {
        Assertions.assertTrue(emp().isEmpty());
    }

    @Test public void recognizeNonEmp() {
        Assertions.assertFalse(of(1).isEmpty());
    }

    // init

    @Test public void throwWhenInitOfEmp() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> emp().init());
    }

    @Test public void getInitOfNonEmp() {
        Assertions.assertEquals(of(1, 2), of(1, 2, 3).init());
    }

    // drop

    @Test public void dropNoneOnEmp() {
        Assertions.assertEquals(emp(), emp().drop(1));
    }

    @Test public void dropNoneIfCountIsNegative() {
        Assertions.assertEquals(of(1, 2, 3), of(1, 2, 3).drop(-1));
    }

    @Test public void dropAsExpectedIfCountIsLessThanSize() {
        Assertions.assertEquals(of(3), of(1, 2, 3).drop(2));
    }

    @Test public void dropAllIfCountExceedsSize() {
        Assertions.assertEquals(emp(), of(1, 2, 3).drop(4));
    }

    @Test public void returnSameInstanceWhenDropZeroCount() {
        final var lst = of(1, 2, 3);
        Assertions.assertEquals(lst, lst.drop(0));
    }

    @Test public void returnSameInstanceWhenDropNegativeCount() {
        final var lst = of(1, 2, 3);
        Assertions.assertSame(lst, lst.drop(-1));
        Assertions.assertEquals(lst, lst.drop(-1));
    }

    @Test public void shouldReturnSameInstanceWhenEmptyDropOne() {
        final var empty = emp();
        Assertions.assertEquals(empty, empty.drop(1));
    }

    // filter

    @Test public void shouldFilterExistingElements() {
        Assertions.assertEquals(of(1), of(1, 2, 3).filter(i -> i == 1));
        Assertions.assertEquals(of(2), of(1, 2, 3).filter(i -> i == 2));
        Assertions.assertEquals(of(3), of(1, 2, 3).filter(i -> i == 3));
        Assertions.assertEquals(of(1, 2, 3), of(1, 2, 3).filter(x -> true));
    }

    @Test public void shouldFilterNonExistingElements() {
        Assertions.assertEquals(emp(),
                EmptyList.<Integer>emp().filter(x -> x == 0));
        Assertions.assertEquals(emp(), of(1, 2, 3).filter(i -> i == 0));
    }

    @Test public void shouldReturnSameInstanceWhenFilteringEmptyTraversable() {
        final var empty = emp();
        Assertions.assertSame(empty, empty.filter(x -> true));
    }

    // iterator

    @Test public void shouldNotHasNextWhenEmpIterator() {
        Assertions.assertFalse(emp().iterator().hasNext());
    }

    @Test public void shouldThrowOnNextWhenEmpIterator() {
        Assertions.assertThrows(NoSuchElementException.class,
                () -> emp().iterator().next());
    }

    @Test public void shouldIterateFirstElementOfNonEmp() {
        Assertions.assertEquals(1, of(1, 2, 3).iterator().next());
    }

    @Test public void shouldIterateFirstElementOfNonEmpFully() {
        var lstIter = of(1, 2, 3).iterator();
        Assertions.assertTrue(lstIter.hasNext());

        Assertions.assertEquals(1, lstIter.next());
        Assertions.assertTrue(lstIter.hasNext());
        Assertions.assertEquals(2, lstIter.next());
        Assertions.assertTrue(lstIter.hasNext());
        Assertions.assertEquals(3, lstIter.next());
        Assertions.assertFalse(lstIter.hasNext());
    }

    @Test public void shouldThrowWhenCallingNextTooOftenOnNonEmptyIterator() {
        final Iterator<Integer> iterator = of(1).iterator();
        iterator.next();
        Assertions.assertThrows(NoSuchElementException.class, iterator::next);
    }

    // get

    @Test public void shouldThrowWhenGetWithNegativeIndexOnEmp() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> emp().get(-1));
    }

    @Test public void shouldThrowWhenGetWithNegativeIndexOnNonEmp() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> of(1).get(-1));
    }

    @Test public void shouldThrowWhenGetOnEmp() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> emp().get(0));
    }

    @Test public void shouldThrowWhenGetWithTooBigIndexOnNonEmp() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> of(1).get(1));
    }

    @Test public void shouldGetFirstElement() {
        Assertions.assertEquals(11, of(11, 2, 3).get(0));
    }

    @Test public void shouldGetLastElement() {
        Assertions.assertEquals(33, of(11, 22, 33).get(2));
    }

    @Test public void shouldGetMidElement() {
        Assertions.assertEquals(22, of(11, 22, 33).get(1));
        Assertions.assertEquals(11, of(11, 22, 33).get(0));
    }
}
