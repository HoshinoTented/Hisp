package com.github.hoshinotented.hisp.parser

enum class HispTokenType {
	L_PAREN,
	R_PAREN,
	QUOTE,
	SYMBOL,
	STRING,
	EOF,
	ANY		//ONLY QUOTE
}

data class MetaData(val index : Int)

data class HispToken(val type : HispTokenType, val text : String, val data : MetaData) {
	override fun equals(other : Any?) : Boolean = (other as? HispToken)?.let { otherToken ->
		type == otherToken.type && text == otherToken.text
	} ?: false

	override fun hashCode() : Int {
		var result = type.hashCode()
		result = 31 * result + text.hashCode()
		return result
	}
}