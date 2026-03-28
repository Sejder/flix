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

  test("DeepStatementChain.01") {
    // Regression test: 1000 statement expressions must not cause stack overflow.
    val n = 1000
    val stmts = (1 to n).map(_ => "f()").mkString("; ")
    val input = s"def f(): Unit \\ IO = checked_ecast(())\ndef foo(): Int32 \\ IO = { $stmts; 123 }"
    val result = check(input, Options.TestWithLibMin)
    expectSuccess(result)
  }

  test("DeepStatementChain.02") {
    // Regression test: deep statement chain inside a let-binding.
    val n = 1000
    val stmts = (1 to n).map(_ => "f()").mkString("; ")
    val input = s"def f(): Unit \\ IO = checked_ecast(())\ndef foo(): Int32 \\ IO = { let _x = 42; $stmts; 123 }"
    val result = check(input, Options.TestWithLibMin)
    expectSuccess(result)
  }
}
