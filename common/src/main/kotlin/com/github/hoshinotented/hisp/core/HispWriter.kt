package com.github.hoshinotented.hisp.core

expect class HispWriter {
	fun append(char : CharSequence) : HispWriter
	fun appendln() : HispWriter
	fun flush()
}