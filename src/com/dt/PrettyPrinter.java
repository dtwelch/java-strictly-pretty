package com.dt;

import com.dt.Docs.Doc;
import com.dt.immutableadts.EmptyList;
import com.dt.immutableadts.IList;
import com.dt.immutableadts.NonEmptyList;

public final class PrettyPrinter {

    public static final boolean DEBUG = false;

    private final StringBuilder writer;
    private final int width;

    public PrettyPrinter(int width, StringBuilder writer) {
        this.writer = writer;
        this.width = width;
    }

    public void format(Doc hd) {
        fmt_(0, IList.<FmtState>of().cons(
                new FmtState(0, false, new Docs.DocGroup(hd))));
    }

    private void fmt_(int k, IList<FmtState> states) {
        switch (states) {
            case EmptyList<FmtState> x -> {}
            case NonEmptyList<FmtState> z -> {
                FmtState s = z.head();
                if (s == null) {
                    return;
                }
                var zz = z.tail();
                switch (z.head().doc()) {
                    case Docs.DocNil n -> fmt_(k, zz);
                    case Docs.DocCons d -> {
                        var lst = zz.cons(new FmtState(s.i(), s.b(), d.tl()))
                                .cons(new FmtState(s.i(), s.b(), d.hd()));
                        if (DEBUG) {
                            System.out.println("DocCons fmt statelist: " + lst);
                        }

                        fmt_(k, lst);
                    }
                    case Docs.DocText d -> {
                        writer.append(d.text());
                        var lst = zz;
                        if (DEBUG) {
                            System.out.println("DocText fmt statelist: " + lst);
                        }

                        fmt_(k + d.text().length(), zz);
                    }
                    case Docs.DocNest d -> {
                        //case (i, b, DocNest(ii, d)) :: z =>
                        //    fmt(k, (i + ii, b, d) :: z)
                        IList<FmtState> lst = zz.cons(new FmtState(
                                s.i() + d.indent(), s.b(), d.doc()));
                        if (DEBUG) {
                            System.out.println("DocNest fmt statelist: " + lst);
                        }

                        fmt_(k, lst);
                    }
                    case Docs.DocRef d -> {
                        String txt = d.text(); //<- more potential computation happening here
                        writer.append(txt);
                        fmt_(k + txt.length(), zz);
                    }
                    case Docs.DocBreak d && s.b() -> {
                        writer.append("\n");
                        spaces(s.i());
                        var lst = zz;
                        if (DEBUG) {
                            System.out.println("DocBreak fmt statelist: " + lst);
                        }
                        fmt_(s.i(), zz);
                    }
                    case Docs.DocBreak d -> {
                        writer.append(" ");
                        fmt_(k + 1, z.tail());
                    }
                    case Docs.DocGroup d -> {
                        IList<FmtState> inputLst = zz.cons(new FmtState(s.i(), false, d.doc()));
                        /*
                        From Lindigs paper (in notes):

                        The following layout policy describes how to process a
                        group:
                        1. Print every optional line break of the current group
                           and all its subgroups as spaces. If the current group
                           then fits completely into the remaining space of
                           current line this is the layout of the group.
                        2. If the former fails every optional line break of the
                           current group is printed as a newline. Subgroups and
                           their line breaks, however, are considered
                           individually as they are reached by the pretty
                           printing process.
                         */
                        if (DEBUG) {
                            System.out.println(inputLst);
                        }
                        boolean fitsFlat = fits(width - k, inputLst);

                        var lst = zz.cons(new FmtState(s.i(), !fitsFlat, d.doc()));

                        if (DEBUG) {
                            System.out.println("DocGroup fmt statelist: " +
                                    inputLst + "\n\tfits flat ? " + fitsFlat +
                                    " new list: " + lst);
                        }
                        fmt_(k, zz.cons(new FmtState(s.i(), !fitsFlat, d.doc())));
                    }
                }
            }
        }
    }

    // make tail recursive
    private boolean fits(int w, IList<FmtState> fmtStates) {
        //throw new UnsupportedOperationException("CRASHING/recursing infinitely in fits(..)"); //on the: [(0, false, DocCons(DocCons(DocCons(DocCons(DocCons(DocText([ERROR]), DocText( )), DocRef(org.rsrg.met.naming.RefNodes$SourceInfoReference@4f6ee6e4)), DocText( )), DocText(mismatched input '<EOF>' expecting {';', '≜'})), null)), nil]  example
        return fits_(w, fmtStates);
    }

    private boolean fits_(int w, IList<FmtState> fmtStates) {
        // return ret(false);
        return switch (fmtStates) {
            case IList<FmtState> z && w < 0 -> false;
            case EmptyList<FmtState> z -> true;
            case NonEmptyList<FmtState> z -> {
                FmtState s = z.head();
                yield switch (z.head().doc()) {
                    case Docs.DocNil n -> fits_(w, z.tail());
                    case Docs.DocCons d -> {
                        var lst = z.tail()
                                .cons(new FmtState(s.i(), s.b(), d.tl()))
                                .cons(new FmtState(s.i(), s.b(), d.hd()));
                        if (DEBUG) {
                            System.out.println("fits(..) DocCons statelist: " + lst);
                        }
                        yield fits_(w, lst);
                    }
                    case Docs.DocText dt -> {
                        var lst = z.tail();
                        if (DEBUG) {
                            System.out.println("fits(..) DocText statelist: " + lst);
                        }
                        yield fits_(w - dt.text().length(), z.tail());
                    }
                    case Docs.DocNest d -> {
                        //case (i, b, DocNest(ii, d)) :: z =>
                        //          fits(w, (i + ii, b, d) :: z)
                        IList<FmtState> lst = z.tail().cons(new FmtState(
                                s.i() + d.indent(), s.b(), d.doc()));
                        if (DEBUG) {
                            System.out.println("fits(..) DocNest statelist: " + lst);
                        }
                        yield fits_(w, lst);
                    }
                    case Docs.DocRef dt -> {
                        var lst = z.tail();
                        //System.out.println("fits(..) DocText statelist: " + lst); // <- should really say DocRef (but in the interest of diffing)
                        yield fits_(w - dt.text().length(), z.tail());
                    }
                    case Docs.DocBreak d && !s.b() -> {
                        if (DEBUG) {
                            System.out.println("fits(..) DocBreak statelist: " + z.tail());
                        }
                        yield fits_(w - 1, z.tail());
                    }
                    case Docs.DocBreak d -> true;
                    case Docs.DocGroup d -> {
                        var lst = z.tail().cons(new FmtState(s.i(), false, d.doc()));
                        if (DEBUG) {
                            System.out.println("fits(..) DocGroup statelist: " + lst.toString());
                        }
                        yield fits_(w, lst);
                    }
                };
            }
        };
    }

    private void spaces(int n) {
        while (n >= 16) {
            writer.append("                ");
            n = n - 16;
        }
        if (n >= 8) {
            writer.append("        ");
            n = n - 8;
        }
        if (n >= 4) {
            writer.append("    ");
            n = n - 4;
        }
        if (n >= 2) {
            writer.append("  ");
            n = n - 2;
        }
        if (n == 1) {
            writer.append(" ");
        }
    }
}
