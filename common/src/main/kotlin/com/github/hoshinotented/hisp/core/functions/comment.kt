package com.github.hoshinotented.hisp.core.functions

import com.github.hoshinotented.hisp.core.*

object Comment : HispFunction(
	hispReference("#"),
	hispList(hispReference("comment")),
	emptyHispList, internalData
) {
	override fun eval(namespace : HispNameSpace, args : HispList) : HispObject {
		//DO NOTHING!!
		return hispString("COMMENT")
	}
}

fun installCommentPlugins(namespace : HispNameSpace) {
	listOf(Comment).install(namespace)
}