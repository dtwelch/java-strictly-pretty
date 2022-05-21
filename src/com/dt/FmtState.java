package com.dt;

import com.dt.Docs.Doc;

// i = indentation, b = linebreak, doc=current working doc
record FmtState(int i, boolean b, Doc doc) {

    @Override public String toString() {
        return String.format("(%d,%s,%s)", i, b, doc.toString());
    }
}
