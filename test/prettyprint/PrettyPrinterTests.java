package prettyprint;

import prettyprint.Docs.Doc;
import prettyprint.Docs.DocCons;
import prettyprint.Docs.DocText;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static prettyprint.DocFactory.*;
import static prettyprint.Docs.BRK;

public final class PrettyPrinterTests {

    @Test public void combineNilDocs() {
        // docNil() ::: docNil()
        Assertions.assertEquals("",
                mkHzListDoc(docNil(), docNil()).prettyPrint(1));
    }

    @Test public void nilBrkTest() {
        // docNil() :/: docNil()
        Assertions.assertEquals(" ", mkHzListDoc(docNil(), docBrk(),
                docNil()).prettyPrint(1));
    }

    @Test public void putTextOnSameLine() {
        // docText(a) ::: docText(b)
        Assertions.assertEquals("ab",
                mkHzListDoc(docTxt("a"), docTxt("b")).prettyPrint(1));
    }

    @Test public void breakDocIntoTwoLines() {
        // docText(a) :/: docText(b)
        Assertions.assertEquals("a\nb", mkHzListDoc(docTxt("a"), docBrk(),
                docTxt("b")).prettyPrint(1));
    }

    @Test public void breakDocSeveralLines() {
        // docText(a) :/: ... :/: docText(i)
        Assertions.assertEquals("a\nb\nc\nd\ne\nf\ngroup\nh\ni",
                mkHzJnListDoc(docBrk(), docTxt("a"), docTxt("b"), docTxt("c"
                        ), docTxt("d"), docTxt("e"), docTxt("f"), docTxt(
                                "group"),
                        docTxt("h"), docTxt("i")).prettyPrint(1));
    }

    @Test public void breakDocSeveralLinesDiffWidth() {
        // docText(a) ::: docText(b) :/:
        // docText(c) ::: docText(d) :/:
        // docText(e) ::: docText(f) :/:
        // docText(group) ::: docText(h) :/:
        // docText(i)
        Assertions.assertEquals("ab\ncd\nef\ngh\ni",
                mkHzListDoc(docTxt("a"), docTxt("b"), docBrk(),
                        docTxt("c"), docTxt("d"), docBrk(),
                        docTxt("e"), docTxt("f"), docBrk(),
                        docTxt("g"), docTxt("h"), docBrk(),
                        docTxt("i")).prettyPrint(2));
    }

    @Test public void combineGroupsSameLine() {
        // docGroup(docText(a)) ::: docGroup(docText(b))
        Assertions.assertEquals("ab", mkHzListDoc(docGroup(docTxt("a")),
                docGroup(docTxt("b"))).prettyPrint(1));
    }

    @Test public void combineGroupDocsNextLine() {
        // docGroup(a) :/: docGroup(b)
        Assertions.assertEquals("a\nb", mkHzListDoc(docGroup(docTxt("a")),
                docBrk(), docGroup(docTxt("b"))).prettyPrint(1));
    }

    @Test public void combineNestedDocsSameLine() {
        // docNest(1, docText(a)) ::: DocNest(1, DocText(b))
        Assertions.assertEquals("ab",
                mkHzListDoc(docNst(1, docTxt("a")),
                        docNst(1, docTxt("b"))).prettyPrint(1));
    }

    @Test public void combineNestedDocsNextLineWithoutBreak() {
        // docNest(1, docText(a)) :/: DocNest(1, DocText(b))
        Assertions.assertEquals("ab",
                mkHzListDoc(docNst(1, docTxt("a")),
                        docNst(1, docTxt("b"))).prettyPrint(1));
    }

    @Test public void combineNestedDocsNextLineWithBreak() {
        // docNest(1, docText(a)) :/: DocNest(1, DocText(b))
        Assertions.assertEquals("a\nb",
                mkHzListDoc(docNst(1, docTxt("a")),
                        docBrk(),
                        docNst(1, docTxt("b"))).prettyPrint(1));
    }

    // some sample err msgs

    @Test public void testSampleSyntaxErrorHzList() {
        Doc factoryDoc = DocFactory.mkHzListDoc(docTxt("[ERROR]"),
                docTxt(" "), mkHzListDoc(docTxt("Initial_CC:3:0"),
                        docTxt(":")), docTxt(" "), docTxt("mismatched input " +
                        "'<EOF>' expecting {';', " + "'≜'}"));

        Doc expectedDoc = new DocCons(new DocText("[ERROR]"),
                new DocCons(new DocText(" "),
                        new DocCons(new DocCons(new DocText("Initial_CC:3:0")
                                , new DocText(":")), new DocCons(new DocText(
                                " "), new DocText("mismatched" + " " +
                                "input '<EOF>' " + "expecting {';', " +
                                "'≜'}")))));
        Assertions.assertEquals(expectedDoc, factoryDoc);
        Assertions.assertEquals("[ERROR] Initial_CC:3:0: " + "mismatched " +
                        "input '<EOF>' expecting {';', '≜'}",
                factoryDoc.prettyPrint(40));
    }

    @Test public void shouldBreakUpErrorMsg() {
        Doc factoryDoc = DocFactory.mkHzListDoc(docTxt("[ERROR]"), docBrk(),
                mkHzListDoc(docTxt("Initial_CC:3:0"), docTxt(":")), docBrk(),
                docTxt("mismatched input '<EOF>' expecting {';', '≜'}"));

        Assertions.assertEquals("[ERROR]\n" + "Initial_CC:3:0:\n" +
                        "mismatched input '<EOF>' expecting {';', '≜'}",
                factoryDoc.prettyPrint(40),
                "routine test for breaking up a " +
                        "message on different " + "lines (width: 40 chars)");
    }

    // sample lang tests (from the lindig paper)

    private Doc cond, expr1, expr2, doc;

    @BeforeEach public void setup() {
        cond = binOpDoc("a", "==", "b");
        expr1 = binOpDoc("a", "<<", "2");
        expr2 = binOpDoc("a", "==", "b");
        doc = iteDoc(cond, expr1, expr2);
        int x = 0;
    }

    @Test public void testItePrintW33() {
        Assertions.assertEquals(
                "if a == b then a << 2 else a == b",
                doc.prettyPrint(33));
    }

    @Test public void testItePrintW32() {
        Assertions.assertEquals(
                "if a == b\n" +
                        "then a << 2\n" +
                        "else a == b",
                doc.prettyPrint(32));
    }

    @Test public void testItePrintW15() {
        Assertions.assertEquals(
                "if a == b\n" +
                        "then a << 2\n" +
                        "else a == b",
                doc.prettyPrint(15));
    }

    @Test public void testItePrintW10() {
        Assertions.assertEquals(
                "if a == b\n" +
                        "then\n" +
                        "  a << 2\n" +
                        "else\n" +
                        "  a == b",
                doc.prettyPrint(10));
    }

    @Test public void testItePrintW8() {
        Assertions.assertEquals(
                "if\n" +
                        "  a == b\n" +
                        "then\n" +
                        "  a << 2\n" +
                        "else\n" +
                        "  a == b",
                doc.prettyPrint(8));
    }

    @Test public void testItePrintW7() {
        Assertions.assertEquals(
                "if\n" +
                        "  a ==\n" +
                        "    b\n" +
                        "then\n" +
                        "  a <<\n" +
                        "    2\n" +
                        "else\n" +
                        "  a ==\n" +
                        "    b",
                doc.prettyPrint(7));
    }

    @Test public void testIteNestedInThenSmallWidth() {
        Doc e1 = iteDoc(cond, expr1, expr2);
        Doc e2 = binOpDoc("S", "o", "T");
        Doc c = binOpDoc("|S|", "<", "|T|");
        Doc ite1 = iteDoc(c, e1, e2);

        Assertions.assertEquals(
                "if |S| < |T|\n" +
                        "then\n" +
                        "  if a == b\n" +
                        "  then a << 2\n" +
                        "  else a == b\n" +
                        "else S o T",
                ite1.prettyPrint(15));
    }

    @Test public void testIteNestedInThenDiffWidths() {
        Doc e1 = iteDoc(cond, expr1, expr2);
        Doc e2 = binOpDoc("S", "o", "T");
        Doc c = binOpDoc("|S|", "<", "|T|");
        Doc ite1 = iteDoc(c, e1, e2);

        Assertions.assertEquals(
                "if |S| < |T|\n" +
                        "then if a == b then a << 2 else a == b\n" +
                        "else S o T",
                ite1.prettyPrint(50));

        Assertions.assertEquals(
                "if |S| < |T|\n" +
                        "then if a == b then a << 2 else a == b\n" +
                        "else S o T",
                ite1.prettyPrint(40));

        Assertions.assertEquals(
                "if |S| < |T|\n" +
                        "then\n" +
                        "  if a == b then a << 2 else a == b\n" +
                        "else S o T",
                ite1.prettyPrint(35));
    }

    private Doc binOpDoc(String l, String o, String r) {
        return docGroup(
                docNst(2,
                        docGroup(l, BRK, o), docBrk(), docTxt(r))
        );
    }

    private Doc iteDoc(Doc c, Doc e1, Doc e2) {
        return docGroup(
                docGroup(docNst(2, docTxt("if"), docBrk(), c)), docBrk(),
                docGroup(docNst(2, docTxt("then"), docBrk(), e1)), docBrk(),
                docGroup(docNst(2, docTxt("else"), docBrk(), e2)));
    }

    // ^|: (basically: delimits lhs and rhs with a linebreak doc iff lhs and
    // rhs is not docNil)
    /*private Doc ß(Doc lhs, Doc rhs) {
        if (lhs == docNil() || rhs == docNil()) {
            return docNil();
        }
        return mkHzJnListDoc(docBrk(), lhs, rhs);
    }*/

    /*
    def binOpDoc(left: String, op: String, right: String): Document = {
        g0(
          nest(
            2,
            g0(text(left) ^|: text(op)) ^|: text(right),
          )
        )
    }

    def iteDoc(c: Document, e1: Document, e2: Document): Document = {
        g0(
          g0(nest(2, text("if") ^|: c))
            ^|: g0(nest(2, text("then") ^|: e1))
            ^|: g0(nest(2, text("else") ^|: e2))
        )
    }
    */
}
