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
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = eval(args) {
		val name = args[parameters.values[0]] as HispSymbol
		val arguments = args[parameters.values[1]] as HispList
		val body = args[parameters.values[2]] as HispList

		HispFunction(name, arguments, body, args.data).apply {
			globals[name] = this
		}
	}
}

object SetQ : HispFunction(
	hispSymbol("setq"),
	hispList(
		hispSymbol("name"),
		hispSymbol("target")),
	emptyHispList, internalData) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = eval(args) {
		val name = args[parameters.values[0]]!!.cast<HispSymbol>()
		val target = args[parameters.values[1]]!!

		globals[name] = when (target) {
			is HispSymbol -> globals[target] ?: throw HispNoSuchFieldException(target, target.data)
			else -> target
		}

		name
	}
}

open class Compute(symbol : String, val action : Double.(Double) -> Double) : HispFunction(
	hispSymbol(symbol),
	hispList(
		hispSymbol("a"),
		hispSymbol("b")),
	emptyHispList, internalData) {

	final override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = eval(args) {
		val a = args[parameters.values[0]]!!
		val b = args[parameters.values[1]]!!

		val aValue = a.eval(globals, args).cast<HispNumber>()
		val bValue = b.eval(globals, args).cast<HispNumber>()

		hispNumber(action(aValue.number, bValue.number))
	}
}

object Add : Compute("+", Double::plus)
object Minus : Compute("-", Double::minus)
object Times : Compute("*", Double::times)
object Div : Compute("/", Double::div)

fun installCoreFunctions(globals : HispNameSpace) {
	listOf(
		DefineFunction,
		Add,
		Minus,
		Times,
		Div
	).install(globals)
}