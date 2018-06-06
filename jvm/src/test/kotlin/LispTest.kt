import com.github.hoshinotented.lisp.core.*
import com.github.hoshinotented.lisp.core.functions.LispWriter
import com.github.hoshinotented.lisp.core.functions.installConsoleFunctions
import com.github.hoshinotented.lisp.core.functions.installCoreFunctions
import com.github.hoshinotented.lisp.parser.LispLexer
import com.github.hoshinotented.lisp.parser.LispParser
import com.github.hoshinotented.lisp.parser.LispToken
import com.github.hoshinotented.lisp.parser.LispTokenType
import org.junit.Test
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class LispTest {
	companion object {
		private val code = """
		(defun add (a b)
			(+ a b))

		(putStr (add 1 2))
	""".trimIndent()
	}

	@Test
	fun lexer() {
		fun newToken(type : LispTokenType, char : Char) = LispToken(type, char.toString(), internalData)
		fun newToken(type : LispTokenType, strValue : String) = LispToken(type, strValue, internalData)

		val L_PAREN = newToken(LispTokenType.L_PAREN, LispLexer.L_PAREN)
		val R_PAREN = newToken(LispTokenType.R_PAREN, LispLexer.R_PAREN)

		fun symbol(value : String) = newToken(LispTokenType.SYMBOL, value)
		fun string(value : String) = newToken(LispTokenType.STRING, value)

		val lexer = LispLexer(code)
		assertEquals(
			listOf(
				L_PAREN,
				symbol("defun"),
				symbol("add"),

				L_PAREN,
				symbol("a"),
				symbol("b"),
				R_PAREN,

				L_PAREN,
				symbol("+"),
				symbol("a"),
				symbol("b"),
				R_PAREN,
				R_PAREN,

				L_PAREN,
				symbol("putStr"),

				L_PAREN,
				symbol("add"),
				symbol("1"),
				symbol("2"),
				R_PAREN,
				R_PAREN
			), lexer.startLex()
		)
	}

	@Test
	fun parse() {
		val lexer = LispLexer(code)
		val parser = LispParser(lexer)
		val executable = parser.startParse() as LispExecutable

		assertEquals(
			LispExecutable(
				listOf(
					lispList(
						lispSymbol("defun"),
						lispSymbol("add"),
						lispList(
							lispSymbol("a"), lispSymbol("b")
						),

						lispList(
							lispSymbol("+"), lispSymbol("a"), lispSymbol("b")
						)
					),

					lispList(
						lispSymbol("putStr"),
						lispList(lispSymbol("add"), lispSymbol("1"), lispSymbol("2"))
					)
				)
			), executable
		)
	}

	@Test
	fun execute() {
		val namespace = emptyLispMap
		val lexer = LispLexer(code)
		val parser = LispParser(lexer)
		val executable = parser.startParse()
		val out = StringWriter()

		installCoreFunctions(namespace)
		installConsoleFunctions(namespace, LispWriter(OutputStreamWriter(System.out)))

		executable.eval(namespace, emptyLispMap)

		assertEquals("3.0", out.toString())
	}
}
