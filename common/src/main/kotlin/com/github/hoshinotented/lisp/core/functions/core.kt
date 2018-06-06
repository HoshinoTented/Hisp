@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.hoshinotented.lisp.core.functions

import com.github.hoshinotented.lisp.core.*

object DefineFunction : LispFunction(
	lispSymbol("defun"),
	lispList(
		lispSymbol("functionName"),
		lispSymbol("parameters"),
		lispSymbol("body")),
	emptyLispList, internalData) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = eval(args) {
		val name = args[parameters.values[0]] as LispSymbol
		val arguments = args[parameters.values[1]] as LispList
		val body = args[parameters.values[2]] as LispList

		LispFunction(name, arguments, body, args.data).apply {
			globals[name] = this
		}
	}
}

object SetQ : LispFunction(
	lispSymbol("setq"),
	lispList(
		lispSymbol("name"),
		lispSymbol("target")),
	emptyLispList, internalData) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = eval(args) {
		val name = args[parameters.values[0]]!!.cast<LispSymbol>()
		val target = args[parameters.values[1]]!!

		globals[name] = when (target) {
			is LispSymbol -> globals[target] ?: throw LispNoSuchFieldException(target, target.data)
			else -> target
		}

		name
	}
}

open class Compute(symbol : String, val action : Double.(Double) -> Double) : LispFunction(
	lispSymbol(symbol),
	lispList(
		lispSymbol("a"),
		lispSymbol("b")),
	emptyLispList, internalData) {

	final override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = eval(args) {
		val a = args[parameters.values[0]]!!
		val b = args[parameters.values[1]]!!

		val aValue = a.eval(globals, args).cast<LispNumber>()
		val bValue = b.eval(globals, args).cast<LispNumber>()

		lispNumber(action(aValue.number, bValue.number))
	}
}

object Add : Compute("+", Double::plus)
object Minus : Compute("-", Double::minus)
object Times : Compute("*", Double::times)
object Div : Compute("/", Double::div)

fun installCoreFunctions(globals : LispNameSpace) {
	listOf(
		DefineFunction,
		Add,
		Minus,
		Times,
		Div
	).install(globals)
}