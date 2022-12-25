package com.github.winteryuki.nanoparsek.examples.nehtml

import com.github.winteryuki.nanoparsek.ParseResult
import com.github.winteryuki.nanoparsek.message
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

fun main(args: Array<String>) {
    val (src, dst) = args.map { Path(it) }
    val text = src.readText()
    when (val parsed = documentParser.parse(text)) {
        is ParseResult.Failure -> println("Parsing failed: ${parsed.message}")
        is ParseResult.Success -> dst.writeText(parsed.result.pretty())
    }
}
