@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.HispTokenType
import com.github.hoshinotented.hisp.parser.MetaData
import kotlin.reflect.KClass

open class HispException(message : String, val data : MetaData) : Exception(message)

// Lex
open class HispLexException(message : String, data : MetaData) : HispException("Syntax Error (data: $data): $message", data)
open class HispUnExpectedException(val excepted : String, val but : String, data : MetaData) : HispLexException("excepted: $excepted, but got: $but", data)

// Parse
open class HispParseException(message : String, data : MetaData) : HispException("Parse Error (data: $data): $message", data)
open class HispUnExpectedTokenException(val excepted : HispTokenType, val but : HispTokenType, data : MetaData) : HispParseException("excepted: $excepted, but got: $but", data)

//AST
open class HispRuntimeException(message: String, data : MetaData) : HispException("Runtime Error (data: $data): $message", data)
open class HispFunctionArgumentsException(val inputCount : Int, val function : HispFunction, data : MetaData) : HispRuntimeException("Need ${function.parameters.values.size} argument(s), but got $inputCount", data)
open class HispUnExpectedTypeException(val excepted : KClass<*>, val but : KClass<*>, data : MetaData) : HispRuntimeException("Type not match, excepted: $excepted, but got: $but", data)
open class HispNoSuchFieldException(val name : HispSymbol, data : MetaData) : HispRuntimeException("No field found by name: $name", data)