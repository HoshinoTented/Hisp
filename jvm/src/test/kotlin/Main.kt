import com.github.hoshinotented.hisp.core.HispWriter
import com.github.hoshinotented.hisp.core.functions.installCommentPlugins
import com.github.hoshinotented.hisp.core.functions.installConsolePlugins
import com.github.hoshinotented.hisp.core.functions.installCorePlugins
import com.github.hoshinotented.hisp.core.hispNameSpace
import com.github.hoshinotented.hisp.parser.HispLexer
import com.github.hoshinotented.hisp.parser.HispParser
import java.io.OutputStreamWriter

fun main(args : Array<String>) {
	val code = """
		(set a 1)
		(set b 2)
		(defun add (a b)
			(+ a b))

		(putStrLn (add a b))

		(del putStrLn)
		(# "putStr will be invalid")
		(putStr b)
	""".trimIndent()

	val namespace = hispNameSpace(mutableMapOf())
	val lexer = HispLexer(code)
	val parser = HispParser(lexer)
	val tokens = lexer.startLex()
	val executable = parser.startParse(tokens)

	installCorePlugins(namespace)
	installCommentPlugins(namespace)
	installConsolePlugins(namespace, HispWriter(OutputStreamWriter(System.out)))

	println("Tokens: $tokens")
	executable.eval(namespace)
}