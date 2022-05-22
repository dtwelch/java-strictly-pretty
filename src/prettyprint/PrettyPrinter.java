package prettyprint;

import prettyprint.immutableadts.EmptyList;
import prettyprint.immutableadts.IList;
import prettyprint.immutableadts.NonEmptyList;

public final class PrettyPrinter {

    public static final boolean DEBUG = false;

    private final StringBuilder writer;
    private final int width;

    public PrettyPrinter(int width, StringBuilder writer) {
        this.writer = writer;
        this.width = width;
    }

    public void format(Docs.Doc hd) {
        fmt_(0, IList.<FmtState>of().cons(
                new FmtState(0, false, new Docs.DocGroup(hd))));
    }

    private void fmt_(int k, IList<FmtState> states) {
        switch (states) {
            case EmptyList<FmtState> x -> { }
            case NonEmptyList<FmtState> z -> {
                FmtState s = z.head();
                if (s == null) { return; }
                var tl = z.tail();
                switch (z.head().doc()) {
                    case Docs.DocNil n -> fmt_(k, tl);
                    case Docs.DocCons d -> fmt_(k, tl.cons(
                            new FmtState(s.i(), s.b(), d.tl())).cons(
                            new FmtState(s.i(), s.b(), d.hd())));
                    case Docs.DocText d -> {
                        writer.append(d.text());
                        fmt_(k + d.text().length(), tl);
                    }
                    case Docs.DocNest d -> fmt_(k, tl.cons(new FmtState(
                            s.i() + d.indent(), s.b(), d.doc())));
                    case Docs.DocBreak d && s.b() -> {
                        writer.append("\n");
                        spaces(s.i());
                        fmt_(s.i(), tl);
                    }
                    case Docs.DocBreak d -> {
                        writer.append(" ");
                        fmt_(k + 1, z.tail());
                    }
                    case Docs.DocGroup d -> {
                        boolean fitsFlat = fits(width - k,
                                tl.cons(new FmtState(s.i(), false, d.doc())));
                        fmt_(k, tl.cons(new FmtState(s.i(),
                                !fitsFlat, d.doc())));
                    }
                }
            }
        }
    }

    // todo make tail recursive (might be already; use tce here)..
    private boolean fits(int w, IList<FmtState> fmtStates) {
        return fits_(w, fmtStates);
    }

    private boolean fits_(int w, IList<FmtState> fmtStates) {
        return switch (fmtStates) {
            case IList<FmtState> z && w < 0 -> false;
            case EmptyList<FmtState> z -> true;
            case NonEmptyList<FmtState> z -> {
                FmtState s = z.head();
                yield switch (z.head().doc()) {
                    case Docs.DocNil n -> fits_(w, z.tail());
                    case Docs.DocCons d -> fits_(w, z.tail()
                            .cons(new FmtState(s.i(), s.b(), d.tl()))
                            .cons(new FmtState(s.i(), s.b(), d.hd())));
                    case Docs.DocText dt ->
                            fits_(w - dt.text().length(), z.tail());
                    case Docs.DocNest d -> fits_(w, z.tail().cons(new FmtState(
                            s.i() + d.indent(), s.b(), d.doc())));
                    case Docs.DocBreak d && !s.b() -> fits_(w - 1, z.tail());
                    case Docs.DocBreak d -> true;
                    case Docs.DocGroup d ->
                            fits_(w, z.tail().cons(new FmtState(s.i(),
                                    false, d.doc())));
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
