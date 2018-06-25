package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.parser.MetaData

val internalData = MetaData(-1)
val emptyHispList get() = hispList()
val emptyHispNamespace get() = hispNameSpace(emptyMap<HispReference, HispObject>().toMutableMap())

fun hispReference(name : String) = HispReference(name, internalData)
fun hispList(vararg elements : HispObject) = HispList(elements.toList(), internalData)
fun hispList(elements : List<HispObject>) = HispList(elements, internalData)
fun hispString(value : String) = HispString(value, internalData)
fun hispNameSpace(map : MutableMap<HispReference, HispObject>) = HispNameSpace(map, null, internalData)
fun hispNumber(value : Double) = HispNumber(value, internalData)