@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.lisp.core

import com.github.hoshinotented.lisp.parser.MetaData

abstract class LispObject(val data : MetaData) {
	inline fun <reified T : LispObject> cast() : T {
		return this as? T ?: throw LispUnExpectedTypeException(T::class, this::class, data)
	}

	abstract fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject
}

open class LispExecutable(val objects : List<LispObject>) : LispObject(internalData) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject {
		objects.forEach {
			it.eval(globals, args)
		}

		return this
	}

	override fun hashCode() : Int = objects.hashCode()
	override fun equals(other : Any?) : Boolean = objects == (other as? LispExecutable)?.objects
	override fun toString() : String = objects.joinToString("\n") { it.toString() }

}

//Just a `parameterName -> value` map, DO NOT PUT IT INTO GLOBAL
open class LispNameSpace(val map : MutableMap<LispSymbol, LispObject>, data : MetaData) : LispObject(data), MutableMap<LispSymbol, LispObject> by map {
	override fun get(key : LispSymbol) : LispObject? {
		return key.value.toDoubleOrNull()?.run {
			LispNumber(this, key.data)
		} ?: map[key]
	}

	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject {
		TODO()
	}
}

open class LispSymbol(val value : String, data : MetaData) : LispObject(data) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = args[this] ?: globals[this]
	?: throw LispNoSuchFieldException(this, data)

	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? LispSymbol)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class LispString(val value : String, data : MetaData) : LispObject(data) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = this
	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? LispString)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class LispList(val values : List<LispObject>, data : MetaData) : LispObject(data) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject {
		return if (values.isNotEmpty()) {
			val callName = values.first()
			if (callName is LispSymbol) {
				val obj = args[callName] ?: globals[callName] ?: throw LispNoSuchFieldException(callName, data)
				if (obj is LispFunction) {
					obj.eval(globals, args.takeIf { it.isNotEmpty() } ?: holdParameters(obj, this))
				} else throw LispUnExpectedTypeException(LispFunction::class, obj::class, data)
			} else throw LispUnExpectedTypeException(LispSymbol::class, callName::class, data)
		} else this
	}

	override fun toString() : String = values.joinToString(" ", "(", ")") { it.toString() }
	final override fun equals(other : Any?) : Boolean = values == (other as? LispList)?.values
	final override fun hashCode() : Int = values.hashCode()
}

open class LispFunction(val name : LispSymbol, val parameters : LispList, val body : LispList, data : MetaData) : LispObject(data) {
	protected inline fun eval(args : LispNameSpace, action : () -> LispObject) : LispObject {
		if (args.size == parameters.values.size) {
			return action()
		} else throw LispFunctionArgumentsException(args.size, this, args.data)
	}

	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = eval(args) {
		body.eval(globals, args)
	}

	override fun toString() : String = "($name $parameters (...))"
	final override fun equals(other : Any?) : Boolean = name == (other as? LispFunction)?.name
	final override fun hashCode() : Int = name.hashCode()
}

open class LispNumber(val number : Double, data : MetaData) : LispObject(data) {
	override fun eval(globals : LispNameSpace, args : LispNameSpace) : LispObject = this
	override fun toString() : String = number.toString()
	override fun equals(other : Any?) : Boolean = number == (other as? LispNumber)?.number
	override fun hashCode() : Int = number.hashCode()
}

fun holdParameters(function : LispFunction, values : LispList) : LispNameSpace {
	if (function.parameters.values.size == values.values.size - 1) {
		return LispNameSpace((function.parameters.values.map { it as LispSymbol } zip values.values.subList(1, values.values.size))
			.toMap()
			.toMutableMap(), values.data)
	} else throw LispFunctionArgumentsException(values.values.size, function, values.data)
}