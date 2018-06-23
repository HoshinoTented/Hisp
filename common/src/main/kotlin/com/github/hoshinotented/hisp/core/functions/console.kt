@file:Suppress("unused")

package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

abstract class AbstractPutStr(name : String, val out : HispWriter) : HispFunction(
	hispReference(name),
	hispList(hispReference("str")),
	emptyHispList, internalData) {

	protected abstract fun append(str : CharSequence)

	private fun putStr(namespace : HispNameSpace, str : HispObject?) {
		str ?: throw HispNoSuchFieldException(hispReference("str"), data)

		when (str) {
			is HispList, is HispReference -> {
				putStr(namespace, str.eval(namespace, emptyHispList))
			}

			is HispString, is HispFunction, is HispNumber -> {
				append(str.toString())
			}
		}
	}

	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val str = args[0]

		putStr(namespace, str)

		return str
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

fun installConsolePlugins(namespace : HispNameSpace, out : HispWriter) {
	listOf(
		PutStr(out),
		PutStrLn(out)
	).install(namespace)
}