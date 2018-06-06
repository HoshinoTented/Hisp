@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")

package com.github.hoshinotented.lisp.core

import com.github.hoshinotented.lisp.parser.LispTokenType
import com.github.hoshinotented.lisp.parser.MetaData
import kotlin.reflect.KClass

open class LispException(message : String, val data : MetaData) : Exception(message)

// Lex
open class LispLexException(message : String, data : MetaData) : LispException("Syntax Error (data: $data): $message", data)
open class LispUnExpectedException(val excepted : String, val but : String, data : MetaData) : LispLexException("excepted: $excepted, but got: $but", data)

// Parse
open class LispParseException(message : String, data : MetaData) : LispException("Parse Error (data: $data): $message", data)
open class LispUnExpectedTokenException(val excepted : LispTokenType, val but : LispTokenType, data : MetaData) : LispParseException("excepted: $excepted, but got: $but", data)

//AST
open class LispRuntimeException(message: String, data : MetaData) : LispException("Runtime Error (data: $data)", data)
open class LispFunctionArgumentsException(val inputCount : Int, val function : LispFunction, data : MetaData) : LispRuntimeException("Need ${function.parameters.values.size} argument(s), but got $inputCount", data)
open class LispUnExpectedTypeException(val excepted : KClass<*>, val but : KClass<*>, data : MetaData) : LispRuntimeException("Type not match, excepted: $excepted, but got: $but", data)
open class LispNoSuchFieldException(val name : LispSymbol, data : MetaData) : LispRuntimeException("No field found by name: $name", data)