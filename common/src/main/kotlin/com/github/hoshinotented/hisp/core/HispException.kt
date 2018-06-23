@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.HispTokenType
import com.github.hoshinotented.hisp.parser.MetaData
import kotlin.reflect.KClass

interface UnExpectedException<T> {
	val expected : T
	val but : T
}

open class HispException(message : String, val data : MetaData) : Exception(message)

// Lex
open class HispLexException(message : String, data : MetaData) : HispException("Syntax Error (data: $data): $message", data)
open class HispUnExpectedException(val excepted : String, val but : String, data : MetaData) : HispLexException("excepted: $excepted, but got: $but", data)

// Parse
open class HispParseException(message : String, data : MetaData) : HispException("Parse Error (data: $data): $message", data)
open class HispUnExpectedTokenException(val excepted : HispTokenType, val but : HispTokenType, data : MetaData) : HispParseException("excepted: $excepted, but got: $but", data)

//AST
open class HispRuntimeException(message: String, data : MetaData) : HispException("Runtime Error (data: $data): $message", data)
open class HispNoSuchFunctionException(override val expected : HispList, override val but : HispList, data : MetaData) : HispRuntimeException("excepted: ${expected.values} argument(s), but got ${but.values}", data), UnExpectedException<HispList>
open class HispUnExpectedTypeException(val excepted : KClass<*>, val but : KClass<*>, data : MetaData) : HispRuntimeException("Type not match, excepted: ${excepted.simpleName}, but got: ${but.simpleName}", data)
open class HispNoSuchFieldException(val name : HispReference, data : MetaData) : HispRuntimeException("No field found by name: $name", data)