# Strictly-Pretty
## A Java Implementation of Wadler and Lindig's pretty printer

A Java implementation of Scala's (now deprecated) text prettyprinting package using pattern matching. I'll update the repo as newer versions of java go on to improve pattern matching capabilities via deconstruction patterns, etc. [Here](https://www.scala-lang.org/api/2.12.8/scala/text/index.html) is a link to the docs for the original scala version.

Note: the algorithm currently relies on a hand rolled java immutable list class. It might be worth porting this version to one that uses standard (non-immutable) java.util.lists for a lighter weight implementation with less custom code to maintain and test.

## Tests
The repo includes some unit tests for the pretty printer and the immutable list. The pretty printer tests incorporate/are-takes-on some from existing implementation tests of this algorithm in scala, namely: [this one](https://github.com/erdeszt/scala-strictly-pretty) and [this one](https://github.com/weso/document).
