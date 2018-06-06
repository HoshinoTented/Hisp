package com.github.hoshinotented.hisp.core

actual class HispWriter(var str : String) {
	actual fun append(char : CharSequence) : HispWriter = apply {
		str += char
	}

	actual fun appendln() : HispWriter = append("\n")

	actual fun flush() {
		//NOP
	}
}