package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.HispFunction
import com.github.hoshinotented.hisp.core.HispNameSpace

fun <T : HispFunction> List<T>.install(namespace : HispNameSpace) {
	map {
		it.name to it
	}.let { namespace.putAll(it) }
}