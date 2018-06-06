@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hoshinotented.lisp.parser

import com.github.hoshinotented.lisp.core.*

class LispParser(val lexer : LispLexer) {
	companion object {
		private val EOF = LispToken(LispTokenType.EOF, "", internalData)
	}

	private lateinit var tokens : List<LispToken>

	private var currentIndex = 0
	private val currentToken get() = tokens.getOrNull(currentIndex) ?: EOF
	private val currentData get() = MetaData(currentIndex)
	private val nextToken get() = tokens.getOrNull(currentIndex + 1) ?: EOF

	private fun next() {
		currentIndex++
	}

	fun startParse() : LispObject {
		tokens = lexer.startLex()
		currentIndex = 0

		return ArrayList<LispObject>().apply {
			while (nextToken.type != LispTokenType.EOF) {
				add(parseList())
				next()
			}
		}.run(::LispExecutable)
	}

	private fun parseToken() : LispObject {
		return when (currentToken.type) {
			LispTokenType.L_PAREN -> parseList()
			LispTokenType.QUOTE -> {
				next()

				if (currentToken.type != LispTokenType.EOF) {
					LispList(listOf(LispSymbol("quote", currentToken.data), parseToken()), currentToken.data)
				} else throw LispUnExpectedTokenException(LispTokenType.ANY, LispTokenType.EOF, currentData)
			}

			LispTokenType.SYMBOL -> LispSymbol(currentToken.text, currentToken.data)
			LispTokenType.STRING -> LispString(currentToken.text, currentToken.data)

			else -> throw RuntimeException("Internal Error")
		}
	}

	private fun parseList() : LispList {
		if (currentToken.type == LispTokenType.L_PAREN) {
			next()

			val elements = ArrayList<LispObject>()

			while (currentToken.type != LispTokenType.R_PAREN) {
				parseToken().run(elements::add)

				next()
			}

			return LispList(elements, currentToken.data)

		} else throw LispUnExpectedTokenException(LispTokenType.L_PAREN, currentToken.type, currentData)
	}
}