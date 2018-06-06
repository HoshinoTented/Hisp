package com.github.hoshinotented.lisp.core.functions

actual class LispWriter(var str : String) {
	actual fun append(char : CharSequence) : LispWriter = apply {
		str += char
	}

	actual fun appendln() : LispWriter = append("\n")

	actual fun flush() {
		//NOP
	}
}