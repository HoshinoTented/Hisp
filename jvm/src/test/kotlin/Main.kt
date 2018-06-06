import com.github.hoshinotented.lisp.core.emptyLispMap
import com.github.hoshinotented.lisp.core.functions.LispWriter
import com.github.hoshinotented.lisp.core.functions.installConsoleFunctions
import com.github.hoshinotented.lisp.core.lispNameSpace
import com.github.hoshinotented.lisp.parser.LispLexer
import com.github.hoshinotented.lisp.parser.LispParser
import java.io.OutputStreamWriter

fun main(args : Array<String>) {
	val code = """
		(putStr "Hello world!")
	""".trimIndent()

	val namespace = lispNameSpace(mutableMapOf())
	val lexer = LispLexer(code)
	val parser = LispParser(lexer)
	val executable = parser.startParse()

	installConsoleFunctions(namespace, LispWriter(OutputStreamWriter(System.out)))

	executable.eval(namespace, emptyLispMap)
}