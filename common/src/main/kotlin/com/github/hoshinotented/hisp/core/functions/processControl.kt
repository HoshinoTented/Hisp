package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

object IfBlock : HispFunction(
	hispReference("if"),
	hispList(
		hispReference("condition"),
		hispReference("block"),
		hispReference("elseBlock")),
	emptyHispList, internalData) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		val condition = args[0]
		val block = args[1]
		val elseBlock = args[2]

		return if (condition.eval(namespace, emptyHispList).cast<HispNumber>().number != 0.0) {
			block.eval(namespace, emptyHispList)
		} else elseBlock.eval(namespace, emptyHispList)
	}
}

fun installProcessControlPlugins(namespace : HispNameSpace) {
	listOf(IfBlock).install(namespace)
}