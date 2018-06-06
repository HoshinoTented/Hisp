package com.github.hoshinotented.lisp.parser

import com.github.hoshinotented.lisp.core.LispUnExpectedException

class LispLexer(val sourceCode : String) {
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

	private fun newToken(type : LispTokenType, strValue : String) = LispToken(type, strValue, currentData)
	private fun newToken(type : LispTokenType, char : Char) = LispToken(type, char.toString(), currentData)
	private fun unexcepted(excepted : Char, but : Char) = LispUnExpectedException(excepted.toString(), but.toString(), currentData)

	fun startLex() : List<LispToken> {
		currentIndex = 0

		val tokens = ArrayList<LispToken>()
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

	private fun lexString() : LispToken {
		if (currentChar == STR_QUOTE) {
			next()

			val token = newToken(LispTokenType.STRING, readFullString())

			if (currentChar == STR_QUOTE) {
				next()
				return token
			}
		}

		throw unexcepted('"', currentChar)
	}

	private fun lexSymbol() : LispToken = newToken(LispTokenType.SYMBOL, readFullSymbol())

	private fun lexList() : List<LispToken> {
		val tokens = ArrayList<LispToken>()
		if (currentChar == L_PAREN) {
			tokens.add(newToken(LispTokenType.L_PAREN, L_PAREN))

			while (currentChar != R_PAREN) {
				nextNotClear()
				when (currentChar) {
					L_PAREN -> lexList().run(tokens::addAll)
					QUOTE -> newToken(LispTokenType.QUOTE, QUOTE).run(tokens::add)
					STR_QUOTE -> lexString().run(tokens::add)
					else -> lexSymbol().run(tokens::add)
				}
			}

			tokens.add(newToken(LispTokenType.R_PAREN, R_PAREN))

			next()

			return tokens
		} else throw unexcepted(L_PAREN, currentChar)
	}
}