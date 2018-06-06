package com.github.hoshinotented.lisp.parser

enum class LispTokenType {
	L_PAREN,
	R_PAREN,
	QUOTE,
	SYMBOL,
	STRING,
	EOF,
	ANY		//ONLY QUOTE
}

data class MetaData(val index : Int)

data class LispToken(val type : LispTokenType, val text : String, val data : MetaData) {
	override fun equals(other : Any?) : Boolean = (other as? LispToken)?.let { otherToken ->
		type == otherToken.type && text == otherToken.text
	} ?: false

	override fun hashCode() : Int {
		var result = type.hashCode()
		result = 31 * result + text.hashCode()
		return result
	}
}