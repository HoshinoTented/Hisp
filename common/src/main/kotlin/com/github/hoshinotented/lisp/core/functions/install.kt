package com.github.hoshinotented.lisp.core.functions

import com.github.hoshinotented.lisp.core.LispFunction
import com.github.hoshinotented.lisp.core.LispNameSpace

fun <T : LispFunction> List<T>.install(namespace : LispNameSpace) {
	map {
		it.name to it
	}.let { namespace.putAll(it) }
}