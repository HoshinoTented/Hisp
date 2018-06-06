package com.github.hoshinotented.hisp.parser

import com.github.hoshinotented.hisp.core.HispUnExpectedException

class HispLexer(val sourceCode : String) {
	companion object {
		const val EOF = '\u0000'
		const val L_PAREN = '('
		const val R_PAREN = ')'
		const val QUOTE = '\''
		const val STR_QUOTE = '"'
		const val SYMBOL = "$L_PAREN$R_PAREN$EOF$QUOTE$STR_QUOTE"
	}

	private var currentIndex = 0
	private val currentChar get() = sourceCode.getOrNull(currentIndex) ?: EOF
	private val currentData get() = MetaData(currentIndex)

	private fun next() {
		currentIndex++
	}

	/**
	 * 前进到下一个非空字符
	 */
	private fun nextNotClear() {
		do {
			next()
		} while (currentChar <= ' ' && currentChar != EOF)
	}

	private fun newToken(type : HispTokenType, strValue : String) = HispToken(type, strValue, currentData)
	private fun newToken(type : HispTokenType, char : Char) = HispToken(type, char.toString(), currentData)
	private fun unexcepted(excepted : Char, but : Char) = HispUnExpectedException(excepted.toString(), but.toString(), currentData)

	fun startLex() : List<HispToken> {
		currentIndex = 0

		val tokens = ArrayList<HispToken>()
		while(currentChar != EOF) {
			if (currentChar <= ' ') nextNotClear()
			lexList().run(tokens::addAll)
		}

		return tokens
	}

	private fun readFullSymbol() : String = buildString {
		while (currentChar > ' ' && currentChar !in SYMBOL) {
			append(currentChar)
			next()
		}
	}

	private fun readFullString() : String = buildString {
		while (currentChar != STR_QUOTE && currentChar != EOF) {
			append(currentChar)
			next()
		}
	}

	private fun lexString() : HispToken {
		if (currentChar == STR_QUOTE) {
			next()

			val token = newToken(HispTokenType.STRING, readFullString())

			if (currentChar == STR_QUOTE) {
				next()
				return token
			}
		}

		throw unexcepted('"', currentChar)
	}

	private fun lexSymbol() : HispToken = newToken(HispTokenType.SYMBOL, readFullSymbol())

	private fun lexList() : List<HispToken> {
		val tokens = ArrayList<HispToken>()
		if (currentChar == L_PAREN) {
			tokens.add(newToken(HispTokenType.L_PAREN, L_PAREN))

			while (currentChar != R_PAREN) {
				nextNotClear()
				when (currentChar) {
					L_PAREN -> lexList().run(tokens::addAll)
					QUOTE -> newToken(HispTokenType.QUOTE, QUOTE).run(tokens::add)
					STR_QUOTE -> lexString().run(tokens::add)
					else -> lexSymbol().takeIf { it.text != "" }?.run(tokens::add)
				}
			}

			tokens.add(newToken(HispTokenType.R_PAREN, R_PAREN))

			next()

			return tokens
		} else throw unexcepted(L_PAREN, currentChar)
	}
}