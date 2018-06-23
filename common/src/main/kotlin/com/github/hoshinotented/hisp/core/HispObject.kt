@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.MetaData

abstract class HispObject(val data : MetaData) {
	inline fun <reified T : HispObject> cast() : T {
		return this as? T ?: throw HispUnExpectedTypeException(T::class, this::class, data)
	}

	abstract fun eval(namespace : HispNameSpace, args : HispList) : HispObject
}

open class HispExecutable(val objects : List<HispObject>) : HispObject(internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		objects.forEach {
			it.eval(namespace, args)
		}

		return this
	}

	override fun hashCode() : Int = objects.hashCode()
	override fun equals(other : Any?) : Boolean = objects == (other as? HispExecutable)?.objects
	override fun toString() : String = objects.joinToString("\n") { it.toString() }

}

open class HispNameSpace(val map : MutableMap<HispReference, HispObject>, val parent : HispNameSpace?, data : MetaData) : HispObject(data), MutableMap<HispReference, HispObject> by map {
	override fun get(key : HispReference) : HispObject? {
		return key.value.toDoubleOrNull()?.run {
			HispNumber(this, key.data)
		} ?: map[key] ?: parent?.get(key)
	}

	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		throw UnsupportedOperationException("HispNameSpace::eval can not be called")
	}
}

// 这个应该被称为 `引用`
open class HispReference(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		return namespace[this] ?: throw HispNoSuchFieldException(this, data)
	}

	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispReference)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispString(val value : String, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject = this
	override fun toString() : String = value
	final override fun equals(other : Any?) : Boolean = value == (other as? HispString)?.value
	final override fun hashCode() : Int = value.hashCode()
}

open class HispList(val values : List<HispObject>, data : MetaData) : HispObject(data), List<HispObject> by values {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val function = values.first().eval(namespace, args)
		if (function is HispFunction) {
			return function.eval(namespace, HispList(values.drop(1), data))
		} else throw HispUnExpectedTypeException(HispFunction::class, function::class, data)
	}

	override fun toString() : String = values.joinToString(" ", "(", ")") { it.toString() }
	final override fun equals(other : Any?) : Boolean = values == (other as? HispList)?.values
	final override fun hashCode() : Int = values.hashCode()
}

open class HispFunction(
	val name : HispReference,
	val parameters : HispList,
	val body : HispList,
	data : MetaData
) : HispObject(data) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val realValues = args.map { (it as? HispReference)?.eval(namespace, args) ?: it }
		val localNameSpace = (parameters.values.map { it as HispReference } zip realValues).toMap().toMutableMap()

		return body.eval(HispNameSpace(localNameSpace, namespace, internalData), args)
	}

	override fun toString() : String = "($name $parameters (...))"
	final override fun equals(other : Any?) : Boolean = name == (other as? HispFunction)?.name
	final override fun hashCode() : Int = name.hashCode()
}

open class HispNumber(val number : Double, data : MetaData) : HispObject(data) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject = this
	override fun toString() : String = number.toString()
	override fun equals(other : Any?) : Boolean = number == (other as? HispNumber)?.number
	override fun hashCode() : Int = number.hashCode()
}