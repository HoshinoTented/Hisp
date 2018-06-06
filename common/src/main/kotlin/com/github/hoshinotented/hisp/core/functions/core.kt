@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

object DefineFunction : HispFunction(
	hispSymbol("defun"),
	hispList(
		hispSymbol("functionName"),
		hispSymbol("parameters"),
		hispSymbol("body")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		val name = namespace[parameters.values[0]] as HispSymbol
		val arguments = namespace[parameters.values[1]] as HispList
		val body = namespace[parameters.values[2]] as HispList

		return HispFunction(name, arguments, body, namespace.data).apply {
			namespace[name] = this
		}
	}
}

object SetQ : HispFunction(
	hispSymbol("setq"),
	hispList(
		hispSymbol("name"),
		hispSymbol("target")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		val name = namespace[parameters.values[0]]!!.cast<HispSymbol>()
		val target = namespace[parameters.values[1]]!!

		namespace[name] = when (target) {
			is HispSymbol -> namespace[target] ?: throw HispNoSuchFieldException(target, target.data)
			else -> target
		}

		return name
	}
}

open class Compute(symbol : String, val action : Double.(Double) -> Double) : HispFunction(
	hispSymbol(symbol),
	hispList(
		hispSymbol("a"),
		hispSymbol("b")),
	emptyHispList, internalData) {

	final override fun eval(namespace : HispNameSpace) : HispObject {
		val a = namespace[parameters.values[0]]!!
		val b = namespace[parameters.values[1]]!!

		val aValue = a.eval(namespace).cast<HispNumber>()
		val bValue = b.eval(namespace).cast<HispNumber>()

		return hispNumber(action(aValue.number, bValue.number))
	}
}

object Add : Compute("+", Double::plus)
object Minus : Compute("-", Double::minus)
object Times : Compute("*", Double::times)
object Div : Compute("/", Double::div)

fun installCoreFunctions(globals : HispNameSpace) {
	listOf(
		DefineFunction,
		SetQ,
		Add,
		Minus,
		Times,
		Div
	).install(globals)
}