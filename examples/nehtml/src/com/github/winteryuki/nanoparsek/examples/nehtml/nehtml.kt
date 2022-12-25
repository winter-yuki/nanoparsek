package com.github.winteryuki.nanoparsek.examples.nehtml

data class Document(val body: Body = Body()) {
    fun pretty(): String = body.pretty(0).toString()
}

abstract class TagInfo(name: String) {
    val opening: String = "<$name>"
    val closing: String = "</$name>"
}

sealed class Tag {
    abstract val info: TagInfo
}

data class Body(val tags: List<Tag> = emptyList()) : Tag() {
    constructor(vararg tags: Tag) : this(tags.toList())

    override val info: TagInfo = Companion

    companion object : TagInfo("body")
}

data class P(val text: String = "") : Tag() {
    override val info: TagInfo = Companion

    companion object : TagInfo("p")
}

data class Div(val tags: List<Tag> = emptyList()) : Tag() {
    constructor(vararg tags: Tag) : this(tags.toList())

    override val info: TagInfo = Companion

    companion object : TagInfo("div")
}

private fun Tag.pretty(level: Int, indentationSize: Int = 4): StringBuilder =
    StringBuilder().apply {
        val indent = " ".repeat(indentationSize)
        val spaces = indent.repeat(level)
        appendLine("$spaces${info.opening}")
        when (this@pretty) {
            is Body -> tags.forEach { append(it.pretty(level + 1)) }
            is Div -> tags.forEach { append(it.pretty(level + 1)) }
            is P -> text.split('\n').forEach { appendLine("$spaces$indent$it") }
        }
        appendLine("$spaces${info.closing}")
    }
