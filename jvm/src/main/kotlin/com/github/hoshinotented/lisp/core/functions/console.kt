package com.github.hoshinotented.lisp.core.functions

import java.io.Flushable
import java.io.Writer

actual class LispWriter(val writer : Writer) : Appendable by writer, Flushable by writer {
	actual override fun append(char : CharSequence) : LispWriter {
		writer.append(char)
		return this
	}

	actual override fun flush() = writer.flush()

	actual fun appendln() : LispWriter = append("\n")
}