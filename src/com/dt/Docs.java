package com.dt;

public interface Docs {

    String BRK = ":/:",  JN = ":::";

    sealed interface Doc {

        int DEFAULT_WIDTH = 80;
        /* in DocFactory
        def :::(doc: Document): Document = DocCons(doc, this)
        def :::(doc: String): Document = DocCons(DocText(doc), this)
        def :/:(doc: Document): Document = doc ::: DocBreak ::: this
        def :/:(doc: String): Document = doc ::: DocBreak ::: this
        */

        default String prettyPrint(int width) {
            return prettyPrint(width, new StringBuilder());
        }

        default String prettyPrint() {
            return prettyPrint(DEFAULT_WIDTH, new StringBuilder());
        }

        // format(width: Int, writer: Writer) -- in the tapl-scala project
        // NOTE: HListDoc in Arend is basically DocCons for us...
        default String prettyPrint(int width, StringBuilder writer) {
            PrettyPrinter p = new PrettyPrinter(width, writer);
            p.format(this);
            return writer.toString();
        }
    }

    final class DocNil implements Doc {
        public static final DocNil INSTANCE = new DocNil();

        private DocNil() { }

        @Override public String toString() { return "DocNil"; }
    }

    final class DocBreak implements Doc {
        public static final Doc INSTANCE = new DocBreak();
        private DocBreak() { }

        @Override public String toString() { return "DocBreak"; }
    }

    record DocNest(int indent, Doc doc) implements Doc {
        @Override public String toString() {
            return String.format("DocNest(%d,%s)", indent, doc);
        }
    }

    record DocGroup(Doc doc) implements Doc {
        @Override public String toString() { return "DocGroup(" + doc + ")"; }
    }

    record DocText(String text) implements Doc {
        @Override public String toString() { return "DocText(" + text + ")";  }
    }

    record DocCons(Doc hd, Doc tl) implements Doc {
        @Override public String toString() {
            return String.format("DocCons(%s,%s)", hd, tl);
        }
    }
}

