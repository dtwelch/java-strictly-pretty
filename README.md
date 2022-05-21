# Strictly-Pretty
## A Java Implementation of Wadler and Lindig's pretty printer

A Java implementation of Scala's (now deprecated) text prettyprinting package using pattern matching. I'll update the repo as newer versions of java go improve pattern matching capabilities via deconstruction patterns, etc.

Note: the algorithm currently relies on a hand rolled java immutable list class. It it might be worth porting this version to one that uses normal (non-immutable) java.util.lists for a lighter weight implementation with less custom code to maintain and test.

## Tests
The repo includes some unit tests combined from [some existing]() repos providing [scala]() tests written in scala.
