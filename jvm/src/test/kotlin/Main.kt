import com.github.hoshinotented.hisp.core.HispWriter
import com.github.hoshinotented.hisp.core.emptyHispMap
import com.github.hoshinotented.hisp.core.functions.installConsoleFunctions
import com.github.hoshinotented.hisp.core.functions.installCoreFunctions
import com.github.hoshinotented.hisp.core.hispNameSpace
import com.github.hoshinotented.hisp.parser.HispLexer
import com.github.hoshinotented.hisp.parser.HispParser
import java.io.OutputStreamWriter

fun main(args : Array<String>) {
	val code = """
		(defun sayHelloWorld () (putStrLn "Hello world!"))
		(sayHelloWorld)
	""".trimIndent()

	val namespace = hispNameSpace(mutableMapOf())
	val lexer = HispLexer(code)
	val parser = HispParser(lexer)
	val executable = parser.startParse()

	installCoreFunctions(namespace)
	installConsoleFunctions(namespace, HispWriter(OutputStreamWriter(System.out)))

	executable.eval(namespace, emptyHispMap)
}