import com.github.hoshinotented.hisp.core.HispWriter
import com.github.hoshinotented.hisp.core.emptyHispList
import com.github.hoshinotented.hisp.core.functions.*
import com.github.hoshinotented.hisp.core.hispNameSpace
import com.github.hoshinotented.hisp.parser.HispLexer
import com.github.hoshinotented.hisp.parser.HispParser
import java.io.OutputStreamWriter

/*
*         if(b==0) return a;
	return a % b == 0 ? b : GCD(b, a % b);*/

/*
		(defun sum (a)
			(if (= a 2)
				1
				(+ 1 (sum (+ a 1)))))

		(putStrLn (sum 0))*/

fun main(args : Array<String>) {
	val code = """
		(defun gcd (a b)
			(if (= b 0)
				a
				(if (= (% a b) 0)
					b
					(gcd b (% a b))
				)
			)
		)

		(putStrLn (gcd 25 15))
	""".trimIndent()

	val namespace = hispNameSpace(mutableMapOf())
	val lexer = HispLexer(code)
	val parser = HispParser(lexer)
	val tokens = lexer.startLex()
	val executable = parser.startParse(tokens)

	installCorePlugins(namespace)
	installMathPlugins(namespace)
	installConditionPlugins(namespace)
	installProcessControlPlugins(namespace)
	installCommentPlugins(namespace)
	installConsolePlugins(namespace, HispWriter(OutputStreamWriter(System.out)))

	println("Tokens: $tokens")
	executable.eval(namespace, emptyHispList)
}