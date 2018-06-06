@file:Suppress("unused")

package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

abstract class AbstractPutStr(name : String, val out : HispWriter) : HispFunction(
	hispSymbol(name),
	hispList(hispSymbol("str")),
	emptyHispList, internalData) {

	protected abstract fun append(str : CharSequence)

	private fun putStr(globals : HispNameSpace, str : HispObject?) {
		str ?: throw HispNoSuchFieldException(hispSymbol("str"), data)

		when (str) {
			is HispSymbol -> {
				putStr(globals, globals[str]?.eval(globals, emptyHispMap))
			}

			is HispList -> {
				putStr(globals, str.eval(globals, emptyHispMap))
			}

			is HispString, is HispFunction, is HispNumber -> {
				append(str.toString())
			}
		}
	}

	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = eval(args) {
		val str = args[hispSymbol("str")]!!

		putStr(globals, str)

		str
	}
}

class PutStr(out : HispWriter) : AbstractPutStr("putStr", out) {
	override fun append(str : CharSequence) {
		out.append(str)
		out.flush()
	}
}

class PutStrLn(out : HispWriter) : AbstractPutStr("putStrLn", out) {
	override fun append(str : CharSequence) {
		out.append(str).appendln()
		out.flush()
	}
}

fun installConsoleFunctions(namespace : HispNameSpace, out : HispWriter) {
	listOf(
		PutStr(out),
		PutStrLn(out)
	).install(namespace)
}