package prettyprint;

import prettyprint.Docs.Doc;
import prettyprint.Docs.DocCons;
import prettyprint.Docs.DocNil;
import prettyprint.Docs.DocText;

import java.util.*;
import java.util.stream.Stream;

public final class DocFactory {

    private DocFactory() {}

    // mkHzJnListDoc = horizontal join list doc
    public static Doc mkHzJnListDoc(Doc delimiterDoc, Doc... docs) {
        return mkHzJnListDoc(delimiterDoc, Arrays.stream(docs).toList());
    }

    // mkHzJnListDoc = horizontal join list doc
    public static Doc mkHzJnListDoc(Doc delimiterDoc, List<Doc> docs) {
        List<Doc> result = new ArrayList<>();
        boolean first = true;
        for (Doc doc : docs) {
            if (doc != DocNil.INSTANCE) {
                if (first) {
                    result.add(doc);
                    first = false;
                } else {
                    result.add(delimiterDoc);
                    result.add(doc);
                }
            }
        }
        return mkHzListDoc(result);
    }

    public static Doc mkHzListDoc(List<Doc> docs) {
        Doc[] array = new Doc[docs.size()];
        return mkHzListDoc(docs.toArray(array));
    }

    // note: this will look for a special string: :/: and will use it to
    // implicitly put breaks in
    public static Doc mkHzListDoc(String... docs) {
        return mkHzListDoc(Stream.of(docs)
                .map(x -> x.equals(":/:") ? docBrk() : docTxt(x))
                .toList());
    }

    public static Doc mkHzListDoc(Doc... docs) {
        if (docs.length == 0) {
            return DocNil.INSTANCE;
        }
        if (docs.length == 1) {
            return docs[0];
        }
        List<Doc> docList = new LinkedList<>(Arrays.asList(docs));

        ListIterator<Doc> docIter = docList.listIterator(docList.size());
        Doc result = docIter.previous();

        // DocCons(DocText([ERROR]),DocCons(DocText( ),DocCons(DocText(Prelude:3:0),DocText(:))))
        //sDoc1: DocCons(DocText([ERROR]),DocCons(DocText( ),DocText(moo)))
        //sDoc2: DocCons(DocCons(DocText([ERROR]),DocText( )),DocText(moo))

        while (docIter.hasPrevious()) {
            Doc prev2 = docIter.previous();
            result = new DocCons(prev2, result);
        }
        return result;
    }

    // Doc util library factory methods

    public static Doc docTxt(String s) {
        if (s == null) {
            throw new IllegalArgumentException(
                    "can't construct a text doc w/ null text");
        }
        return new DocText(s);
    }

    public static Doc docNst(int i, Doc s) {
        return new Docs.DocNest(i, s);
    }

    public static Doc docNst(int i, Doc ... docs) {
        return new Docs.DocNest(i, mkHzListDoc(docs));
    }

    public static Doc docNil() {
        return DocNil.INSTANCE;
    }

    // :/:
    public static Doc docBrk() {
        return Docs.DocBreak.INSTANCE;
    }

    /**
     * A group, whose components will either be printed with all breaks rendered
     * as spaces, or with all breaks rendered as line breaks.
     */
    public static Doc docGroup(Doc d) {
        return new Docs.DocGroup(d);
    }

    public static Doc docGroup(Doc ... docs) {
        return new Docs.DocGroup(mkHzListDoc(docs));
    }

    public static Doc docGroup(String ... docs) {
        return new Docs.DocGroup(mkHzListDoc(docs));
    }
}
