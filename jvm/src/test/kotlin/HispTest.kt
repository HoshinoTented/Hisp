@file:Suppress("LocalVariableName", "unused")

import com.github.hoshinotented.hisp.core.*
import com.github.hoshinotented.hisp.core.HispWriter
import com.github.hoshinotented.hisp.core.functions.installConsoleFunctions
import com.github.hoshinotented.hisp.core.functions.installCoreFunctions
import com.github.hoshinotented.hisp.parser.HispLexer
import com.github.hoshinotented.hisp.parser.HispParser
import com.github.hoshinotented.hisp.parser.HispToken
import com.github.hoshinotented.hisp.parser.HispTokenType
import org.junit.Test
import java.io.OutputStreamWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class HispTest {
	companion object {
		private val code = """
		(defun add (a b)
			(+ a b))

		(putStr (add 1 2))
	""".trimIndent()
	}

	@Test
	fun lexer() {
		fun newToken(type : HispTokenType, char : Char) = HispToken(type, char.toString(), internalData)
		fun newToken(type : HispTokenType, strValue : String) = HispToken(type, strValue, internalData)

		val L_PAREN = newToken(HispTokenType.L_PAREN, HispLexer.L_PAREN)
		val R_PAREN = newToken(HispTokenType.R_PAREN, HispLexer.R_PAREN)

		fun symbol(value : String) = newToken(HispTokenType.SYMBOL, value)
		fun string(value : String) = newToken(HispTokenType.STRING, value)

		val lexer = HispLexer(code)
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
		val lexer = HispLexer(code)
		val parser = HispParser(lexer)
		val executable = parser.startParse() as HispExecutable

		assertEquals(
			HispExecutable(
				listOf(
					hispList(
						hispSymbol("defun"),
						hispSymbol("add"),
						hispList(
							hispSymbol("a"), hispSymbol("b")
						),

						hispList(
							hispSymbol("+"), hispSymbol("a"), hispSymbol("b")
						)
					),

					hispList(
						hispSymbol("putStr"),
						hispList(hispSymbol("add"), hispSymbol("1"), hispSymbol("2"))
					)
				)
			), executable
		)
	}

	@Test
	fun execute() {
		val namespace = emptyHispMap
		val lexer = HispLexer(code)
		val parser = HispParser(lexer)
		val executable = parser.startParse()
		val out = StringWriter()

		installCoreFunctions(namespace)
		installConsoleFunctions(namespace, HispWriter(out))

		executable.eval(namespace, emptyHispMap)

		assertEquals("3.0", out.toString())
	}
}
