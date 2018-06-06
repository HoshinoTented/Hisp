@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.MetaData

abstract class HispObject(val data : MetaData) {
	inline fun <reified T : HispObject> cast() : T {
		return this as? T ?: throw HispUnExpectedTypeException(T::class, this::class, data)
	}

	abstract fun eval(namespace : HispNameSpace) : HispObject
}

open class HispExecutable(val objects : List<HispObject>) : HispObject(internalData) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		objects.forEach {
			it.eval(namespace)
		}

		return this
	}

	override fun hashCode() : Int = objects.hashCode()
	override fun equals(other : Any?) : Boolean = objects == (other as? HispExecutable)?.objects
	override fun toString() : String = objects.joinToString("\n") { it.toString() }

}

//Just a `parameterName -> value` map, DO NOT PUT IT INTO GLOBAL
open class HispNameSpace(val map : MutableMap<HispSymbol, HispObject>, val parent : HispNameSpace?, data : MetaData) : HispObject(data), MutableMap<HispSymbol, HispObject> by map {
	override fun get(key : HispSymbol) : HispObject? {
		return key.value.toDoubleOrNull()?.run {
			HispNumber(this, key.data)
		} ?: map[key] ?: parent?.get(key)
	}

	override fun put(key : HispSymbol, value : HispObject) : HispObject? = (parent ?: map).put(key, value)

	override fun remove(key : HispSymbol) : HispObject? {
		map.remove(key)
		parent?.remove(key)

		return null
	}

	override fun eval(namespace : HispNameSpace) : HispObject {
		TODO()
	}
}

// 这个应该被称为 `引用`
open class HispSymbol(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		var value : HispObject? = namespace[this]

		while (value is HispSymbol) {
			value = namespace.parent?.run(value::eval)
		}

		return value ?: throw HispNoSuchFieldException(this, data)
	}

	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispSymbol)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispString(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace) : HispObject = this
	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispString)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispList(val values : List<HispObject>, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		return if (values.isNotEmpty()) {
			val callName = values.first()
			if (callName is HispSymbol) {
				val obj = namespace[callName] ?: throw HispNoSuchFieldException(callName, data)
				if (obj is HispFunction) {
					obj.eval(holdParameters(namespace, obj, this))
				} else throw HispUnExpectedTypeException(HispFunction::class, obj::class, data)
			} else throw HispUnExpectedTypeException(HispSymbol::class, callName::class, data)
		} else this
	}

	override fun toString() : String = values.joinToString(" ", "(", ")") { it.toString() }
	final override fun equals(other : Any?) : Boolean = values == (other as? HispList)?.values
	final override fun hashCode() : Int = values.hashCode()
}

open class HispFunction(val name : HispSymbol, val parameters : HispList, val body : HispList, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		return body.eval(namespace)
	}

	override fun toString() : String = "($name $parameters (...))"
	final override fun equals(other : Any?) : Boolean = name == (other as? HispFunction)?.name
	final override fun hashCode() : Int = name.hashCode()
}

open class HispNumber(val number : Double, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace) : HispObject = this
	override fun toString() : String = number.toString()
	override fun equals(other : Any?) : Boolean = number == (other as? HispNumber)?.number
	override fun hashCode() : Int = number.hashCode()
}

fun holdParameters(namespace : HispNameSpace, function : HispFunction, values : HispList) : HispNameSpace {
	if (function.parameters.values.size == values.values.size - 1) {
		return HispNameSpace((function.parameters.values.map { it as HispSymbol } zip values.values.subList(1, values.values.size))
			.toMap()
			.toMutableMap(), namespace, values.data)
	} else throw HispFunctionArgumentsException(values.values.size - 1, function, values.data)
}