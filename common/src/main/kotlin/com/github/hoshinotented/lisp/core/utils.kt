package com.github.hoshinotented.lisp.core

import com.github.hoshinotented.lisp.parser.MetaData

val internalData = MetaData(-1)
val emptyLispList get() = lispList()
val emptyLispMap get() = lispNameSpace(emptyMap<LispSymbol, LispObject>().toMutableMap())

fun lispSymbol(name : String) = LispSymbol(name, internalData)
fun lispList(vararg elements : LispObject) = LispList(elements.toList(), internalData)
fun lispString(value : String) = LispString(value, internalData)
fun lispNameSpace(map : MutableMap<LispSymbol, LispObject>) = LispNameSpace(map, internalData)
fun lispNumber(value : Double) = LispNumber(value, internalData)