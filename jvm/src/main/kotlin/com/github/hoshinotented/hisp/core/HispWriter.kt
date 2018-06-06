package com.github.hoshinotented.hisp.core

import com.github.hoshinotented.hisp.core.HispWriter
import java.io.Flushable
import java.io.Writer

actual class HispWriter(val writer : Writer) : Appendable by writer, Flushable by writer {
	actual override fun append(char : CharSequence) : HispWriter {
		writer.append(char)
		return this
	}

	actual override fun flush() = writer.flush()

	actual fun appendln() : HispWriter = append("\n")
}