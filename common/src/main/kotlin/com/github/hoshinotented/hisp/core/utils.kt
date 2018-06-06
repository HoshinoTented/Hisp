package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.MetaData

val internalData = MetaData(-1)
val emptyHispList get() = hispList()
val emptyHispMap get() = hispNameSpace(emptyMap<HispSymbol, HispObject>().toMutableMap())

fun hispSymbol(name : String) = HispSymbol(name, internalData)
fun hispList(vararg elements : HispObject) = HispList(elements.toList(), internalData)
fun hispString(value : String) = HispString(value, internalData)
fun hispNameSpace(map : MutableMap<HispSymbol, HispObject>) = HispNameSpace(map, internalData)
fun hispNumber(value : Double) = HispNumber(value, internalData)