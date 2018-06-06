@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.MetaData

abstract class HispObject(val data : MetaData) {
	inline fun <reified T : HispObject> cast() : T {
		return this as? T ?: throw HispUnExpectedTypeException(T::class, this::class, data)
	}

	abstract fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject
}

open class HispExecutable(val objects : List<HispObject>) : HispObject(internalData) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject {
		objects.forEach {
			it.eval(globals, args)
		}

		return this
	}

	override fun hashCode() : Int = objects.hashCode()
	override fun equals(other : Any?) : Boolean = objects == (other as? HispExecutable)?.objects
	override fun toString() : String = objects.joinToString("\n") { it.toString() }

}

//Just a `parameterName -> value` map, DO NOT PUT IT INTO GLOBAL
open class HispNameSpace(val map : MutableMap<HispSymbol, HispObject>, data : MetaData) : HispObject(data), MutableMap<HispSymbol, HispObject> by map {
	override fun get(key : HispSymbol) : HispObject? {
		return key.value.toDoubleOrNull()?.run {
			HispNumber(this, key.data)
		} ?: map[key]
	}

	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject {
		TODO()
	}
}

open class HispSymbol(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = args[this] ?: globals[this]
	?: throw HispNoSuchFieldException(this, data)

	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispSymbol)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispString(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = this
	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispString)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispList(val values : List<HispObject>, data : MetaData) : HispObject(data) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject {
		return if (values.isNotEmpty()) {
			val callName = values.first()
			if (callName is HispSymbol) {
				val obj = args[callName] ?: globals[callName] ?: throw HispNoSuchFieldException(callName, data)
				if (obj is HispFunction) {
					obj.eval(globals, args.takeIf { it.isNotEmpty() } ?: holdParameters(obj, this))
				} else throw HispUnExpectedTypeException(HispFunction::class, obj::class, data)
			} else throw HispUnExpectedTypeException(HispSymbol::class, callName::class, data)
		} else this
	}

	override fun toString() : String = values.joinToString(" ", "(", ")") { it.toString() }
	final override fun equals(other : Any?) : Boolean = values == (other as? HispList)?.values
	final override fun hashCode() : Int = values.hashCode()
}

open class HispFunction(val name : HispSymbol, val parameters : HispList, val body : HispList, data : MetaData) : HispObject(data) {
	protected inline fun eval(args : HispNameSpace, action : () -> HispObject) : HispObject {
		if (args.size == parameters.values.size) {
			return action()
		} else throw HispFunctionArgumentsException(args.size, this, args.data)
	}

	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = eval(args) {
		body.eval(globals, args)
	}

	override fun toString() : String = "($name $parameters (...))"
	final override fun equals(other : Any?) : Boolean = name == (other as? HispFunction)?.name
	final override fun hashCode() : Int = name.hashCode()
}

open class HispNumber(val number : Double, data : MetaData) : HispObject(data) {
	override fun eval(globals : HispNameSpace, args : HispNameSpace) : HispObject = this
	override fun toString() : String = number.toString()
	override fun equals(other : Any?) : Boolean = number == (other as? HispNumber)?.number
	override fun hashCode() : Int = number.hashCode()
}

fun holdParameters(function : HispFunction, values : HispList) : HispNameSpace {
	if (function.parameters.values.size == values.values.size - 1) {
		return HispNameSpace((function.parameters.values.map { it as HispSymbol } zip values.values.subList(1, values.values.size))
			.toMap()
			.toMutableMap(), values.data)
	} else throw HispFunctionArgumentsException(values.values.size - 1, function, values.data)
}