@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.hisp.parser

import com.github.hoshinotented.hisp.core.*

class HispParser(val lexer : HispLexer) {
	companion object {
		private val EOF = HispToken(HispTokenType.EOF, "", internalData)
	}

	private lateinit var tokens : List<HispToken>

	private var currentIndex = 0
	private val currentToken get() = tokens.getOrNull(currentIndex) ?: EOF
	private val currentData get() = MetaData(currentIndex)
	private val nextToken get() = tokens.getOrNull(currentIndex + 1) ?: EOF

	private fun next() {
		currentIndex++
	}

	fun startParse(tokens : List<HispToken>) : HispObject {
		this.tokens = tokens
		currentIndex = 0

		return ArrayList<HispObject>().apply {
			while (nextToken.type != HispTokenType.EOF) {
				add(parseList())
				next()
			}
		}.run(::HispExecutable)
	}

	fun startParse() : HispObject = startParse(lexer.startLex())

	private fun parseToken() : HispObject {
		return when (currentToken.type) {
			HispTokenType.L_PAREN -> parseList()
			HispTokenType.QUOTE -> {
				next()

				if (currentToken.type != HispTokenType.EOF) {
					HispList(listOf(HispSymbol("quote", currentToken.data), parseToken()), currentToken.data)
				} else throw HispUnExpectedTokenException(HispTokenType.ANY, HispTokenType.EOF, currentData)
			}

			HispTokenType.SYMBOL -> HispSymbol(currentToken.text, currentToken.data)
			HispTokenType.STRING -> HispString(currentToken.text, currentToken.data)

			else -> throw RuntimeException("Internal Error")
		}
	}

	private fun parseList() : HispList {
		if (currentToken.type == HispTokenType.L_PAREN) {
			next()

			val elements = ArrayList<HispObject>()

			while (currentToken.type != HispTokenType.R_PAREN) {
				parseToken().run(elements::add)

				next()
			}

			return HispList(elements, currentToken.data)

		} else throw HispUnExpectedTokenException(HispTokenType.L_PAREN, currentToken.type, currentData)
	}
}