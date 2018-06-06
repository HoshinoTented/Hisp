@file:Suppress("unused")

package com.github.hoshinotented.lisp.core.functions

import com.github.hoshinotented.lisp.core.*

expect class LispWriter {
	fun append(char : CharSequence) : LispWriter
	fun appendln() : LispWriter
	fun flush()
}

abstract class AbstractPutStr(name : String, val out : LispWriter) : LispFunction(
	lispSymbol(name),
	lispList(lispSymbol("str")),
	emptyLispList, internalData) {

	protected abstract fun append(str : CharSequence)

	private fun putStr(globals : LispNameSpace, str : LispObject?) {
		str ?: throw LispNoSuchFieldException(lispSymbol("str"), data)

		when (str) {
			is LispSymbol -> {
				putStr(globals, globals[str]?.eval(globals, emptyLispMap))
			}

			is LispList -> {
				putStr(globals, str.eval(globals, emptyLispMap))
			}

			is LispString, is LispFunction, is LispNumber -> {
				append(str.toString())
			}
		}
	}

	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = eval(args) {
		val str = args[lispSymbol("str")]!!

		putStr(globals, str)

		str
	}
}

class PutStr(out : LispWriter) : AbstractPutStr("putStr", out) {
	override fun append(str : CharSequence) {
		out.append(str)
		out.flush()
	}
}

class PutStrLn(out : LispWriter) : AbstractPutStr("putStrLn", out) {
	override fun append(str : CharSequence) {
		out.append(str).appendln()
		out.flush()
	}
}

fun installConsoleFunctions(namespace : LispNameSpace, out : LispWriter) {
	listOf(
		PutStr(out),
		PutStrLn(out)
	).install(namespace)
}