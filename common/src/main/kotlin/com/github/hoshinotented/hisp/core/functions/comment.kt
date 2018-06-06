package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

object Comment : HispFunction(
	hispSymbol("#"),
	hispList(hispSymbol("comment")),
	emptyHispList, internalData
) {
	override fun eval(namespace : HispNameSpace) : HispObject {
		//DO NOTHING!!
		return hispString("COMMENT")
	}
}

fun installCommentPlugins(namespace : HispNameSpace) {
	listOf(Comment).install(namespace)
}