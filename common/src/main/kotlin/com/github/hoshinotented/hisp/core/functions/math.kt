package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

abstract class Compute(symbol : String, val action : Double.(Double) -> Double) : HispFunction(
	hispReference(symbol),
	hispList(
		hispReference("a"),
		hispReference("b")),
	emptyHispList, internalData) {

	final override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val a = args[0].eval(namespace, emptyHispList).cast<HispNumber>()
		val b = args[1].eval(namespace, emptyHispList).cast<HispNumber>()

		return hispNumber(action(a.number, b.number))
	}
}

object Add : Compute("+", Double::plus)
object Minus : Compute("-", Double::minus)
object Times : Compute("*", Double::times)
object Div : Compute("/", Double::div)
object Mod : Compute("%", Double::rem)

fun installMathPlugins(namespace : HispNameSpace) {
	listOf(
		Add,
		Minus,
		Times,
		Div,
		Mod
	).install(namespace)
}