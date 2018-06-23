@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

object DefineFunction : HispFunction(
	hispReference("defun"),
	hispList(
		hispReference("functionName"),
		hispReference("parameters"),
		hispReference("body...")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val name = args[0] as HispReference
		val parameters = args[1] as HispList
		val body = args[2] as HispList
		return HispFunction(name, parameters, body, data).apply {
			namespace[name] = this
		}
	}
}

object SetQ : HispFunction(
	hispReference("set"),
	hispList(
		hispReference("name"),
		hispReference("target")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val name = args[0] as HispReference
		val target = args[1]
		val realValue = when (target) {
			is HispReference -> target.eval(namespace, emptyHispList)
			else -> target
		}

		namespace[name] = realValue

		return realValue
	}
}

object DelQ : HispFunction(
	hispReference("del"),
	hispList(
		hispReference("name")
	),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val name = args[0]
		val value = name.eval(namespace, emptyHispList)

		namespace.remove(name)

		return value
	}
}

fun installCorePlugins(globals : HispNameSpace) {
	listOf(
		DefineFunction,
		SetQ,
		DelQ
	).install(globals)
}