package com.github.winteryuki.nanoparsek

private infix fun <T> Parser<T>.or(other: Parser<T>): Parser<T> = Parser { s ->
    when (val result = parse(s)) {
        is ParseResult.Success -> result
        is ParseResult.Failure -> when (result.kind) {
            ParseResult.Failure.Kind.SOFT -> other.parse(s)
            ParseResult.Failure.Kind.HARD -> result
        }
    }
}

fun <T> or(vararg parsers: Parser<T>): Parser<T> = parsers.reduce(Parser<T>::or).stopping

fun <T> many(parser: Parser<T>): Parser<List<T>> = Parser { s ->
    var result = parser.parse(s)
    val results = mutableListOf<T>()
    var remaining = s
    while (result is ParseResult.Success) {
        results.add(result.result)
        remaining = result.remaining
        result = parser.parse(result.remaining)
    }
    ParseResult.Success(results, remaining)
}
