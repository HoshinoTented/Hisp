package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

abstract class Compare(name : String, val closure : (HispNumber, HispNumber) -> Boolean) : HispFunction(
	hispReference(name),
	hispList(hispReference("a"), hispReference("b")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val a = args[0].eval(namespace, emptyHispList)
		val b = args[1].eval(namespace, emptyHispList)
		val number = if (closure(a.cast(), b.cast())) 1.0 else 0.0

		return HispNumber(number, internalData)
	}
}

object Equals : Compare("=", { a, b -> a.number == b.number })
object NotEquals : Compare("!=", { a, b -> a.number != b.number })
object MoreThan : Compare(">", { a, b -> a.number > b.number })
object SmallThan : Compare("<", { a, b -> a.number < b.number })

fun installConditionPlugins(namespace : HispNameSpace) {
	listOf(
		Equals,
		NotEquals,
		MoreThan,
		SmallThan
	).install(namespace)
}