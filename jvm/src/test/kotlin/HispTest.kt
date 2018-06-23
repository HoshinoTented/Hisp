@file:Suppress("LocalVariableName", "unused")

import com.github.hoshinotented.hisp.core.*
import com.github.hoshinotented.hisp.core.functions.installCommentPlugins
import com.github.hoshinotented.hisp.core.functions.installConsolePlugins
import com.github.hoshinotented.hisp.core.functions.installCorePlugins
import com.github.hoshinotented.hisp.core.functions.installMathPlugins
import com.github.hoshinotented.hisp.parser.HispLexer
import com.github.hoshinotented.hisp.parser.HispParser
import com.github.hoshinotented.hisp.parser.HispToken
import com.github.hoshinotented.hisp.parser.HispTokenType
import org.junit.Test
import java.io.StringWriter
import kotlin.test.assertEquals

class HispTest {
	companion object {
		private val code = """
		(set a 1)
		(set b 2)
		(defun add (a b)
			(+ a b))

		(putStrLn (add a b))
		(# "putStr will be invalid")
		(putStr b)
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
				symbol("set"),
				symbol("a"),
				symbol("1"),
				R_PAREN,

				L_PAREN,
				symbol("set"),
				symbol("b"),
				symbol("2"),
				R_PAREN,

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
				symbol("putStrLn"),

				L_PAREN,
				symbol("add"),
				symbol("a"),
				symbol("b"),
				R_PAREN,

				R_PAREN,

				L_PAREN,
				symbol("#"),
				string("putStr will be invalid"),
				R_PAREN,

				L_PAREN,
				symbol("putStr"),
				symbol("b"),
				R_PAREN
			),
			lexer.startLex()
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
						hispReference("set"),
						hispReference("a"),
						hispReference("1")
					),

					hispList(
						hispReference("set"),
						hispReference("b"),
						hispReference("2")
					),

					hispList(
						hispReference("defun"),
						hispReference("add"),
						hispList(
							hispReference("a"), hispReference("b")
						),

						hispList(
							hispReference("+"), hispReference("a"), hispReference("b")
						)
					),

					hispList(
						hispReference("putStrLn"),
						hispList(hispReference("add"), hispReference("a"), hispReference("b"))
					),

					hispList(
						hispReference("#"),
						hispString("putStr will be invalid")
					),

					hispList(
						hispReference("putStr"),
						hispReference("b")
					)
				)
			), executable
		)
	}

	@Test
	fun execute() {
		val namespace = emptyHispNamespace
		val lexer = HispLexer(code)
		val parser = HispParser(lexer)
		val executable = parser.startParse()
		val out = StringWriter()

		installCorePlugins(namespace)
		installMathPlugins(namespace)
		installCommentPlugins(namespace)
		installConsolePlugins(namespace, HispWriter(out))

		executable.eval(namespace, emptyHispList)

		assertEquals("3.0\n2.0", out.toString())
	}
}
