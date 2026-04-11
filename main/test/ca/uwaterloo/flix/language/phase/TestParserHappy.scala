package ca.uwaterloo.flix.language.phase

import ca.uwaterloo.flix.TestUtils
import ca.uwaterloo.flix.util.Options
import org.scalatest.funsuite.AnyFunSuite

/**
  * Note that CompilerSuite and LibrarySuite covers the positive testing of the parser well.
  */
class TestParserHappy extends AnyFunSuite with TestUtils {
  test("DetectRecord.01") {
    val input =
      """
        |pub def foo(): { x = Int32 } = {
        |    // This is a comment
        |    x = 1000
        |}
        |""".stripMargin
    val result = check(input, Options.TestWithLibNix)
    expectSuccess(result)
  }

  test("ExtensibleType.01") {
    val input =
      """
        |pub def foo(): #| A(Int32) |# = ???
        |""".stripMargin
    val result = check(input, Options.TestWithLibNix)
    expectSuccess(result)
  }

  test("FlatStm.ThreeStatements") {
    // Test that a three-statement chain (like MutList.push) compiles correctly.
    val input =
      """
        |def f(): Unit \ IO = checked_ecast(())
        |def g(): Unit \ IO = checked_ecast(())
        |def main(): Int32 \ IO = {
        |    f();
        |    g();
        |    42
        |}
        |""".stripMargin
    val result = check(input, Options.TestWithLibMin)
    expectSuccess(result)
  }
}
