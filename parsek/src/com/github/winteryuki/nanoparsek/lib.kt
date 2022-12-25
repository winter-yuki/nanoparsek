package com.github.winteryuki.nanoparsek

fun Parser.Companion.string(prefix: String): Parser<String> = Parser { s ->
    if (s.startsWith(prefix)) {
        ParseResult.Success(prefix, s.substring(prefix.length))
    } else {
        ParseResult.Failure(prefix, ParseResult.Failure.Kind.SOFT)
    }
}

fun Parser.Companion.satisfy(
    expected: String = "match predicate",
    predicate: (Char) -> Boolean
): Parser<Char> = Parser { s ->
    val c = s.firstOrNull()
    if (c != null && predicate(c)) {
        ParseResult.Success(c, s.substring(1))
    } else {
        ParseResult.Failure(expected, ParseResult.Failure.Kind.SOFT)
    }
}

fun Parser.Companion.until(parser: Parser<*>): Parser<String> = Parser { s ->
    s.indices.forEach { i ->
        val suffix = s.substring(i)
        if (parser.parse(suffix) is ParseResult.Success) {
            return@Parser ParseResult.Success(s.substring(0, i), suffix)
        }
    }
    ParseResult.Success(s, "")
}

val Parser.Companion.unit: Parser<Unit>
    get() = Parser { s -> ParseResult.Success(Unit, s) }

val Parser<*>.unit: Parser<Unit>
    get() = map {}

val Parser.Companion.spaces: Parser<Unit>
    get() = Parser { s -> ParseResult.Success(Unit, s.trimStart()) }

fun <T> Parser<T>.between(before: Parser<*>, after: Parser<*>): Parser<T> = Parser { s1 ->
    val (_, s2) = before.parse(s1) orStop { return@Parser it }
    val (x, s3) = parse(s2) orStop { return@Parser it.copy(kind = ParseResult.Failure.Kind.HARD) }
    val (_, s4) = after.parse(s3) orStop { return@Parser it.copy(kind = ParseResult.Failure.Kind.HARD) }
    ParseResult.Success(x, s4)
}

fun mb(parser: Parser<*>): Parser<Unit> = or(parser.trying.unit, Parser.unit)
