# Strictly-Pretty
## A Java Implementation of Wadler and Lindig's pretty printer

A Java implementation of Scala's (now deprecated) text prettyprinting package using pattern matching. I'll update the repo as newer versions of java go improve pattern matching capabilities via deconstruction patterns, etc. [Here](https://www.scala-lang.org/api/2.12.8/scala/text/index.html) is a link to the docs for the original scala version.

Note: the algorithm currently relies on a hand rolled java immutable list class. It it might be worth porting this version to one that uses normal (non-immutable) java.util.lists for a lighter weight implementation with less custom code to maintain and test.

## Tests
The repo includes some unit tests for the pretty printer and the immutable list. the pretty printer tests incorporate/are takes on some from existing implemntations in scala, such as: [this one](https://github.com/erdeszt/scala-strictly-pretty) and [this one](https://github.com/weso/document).
