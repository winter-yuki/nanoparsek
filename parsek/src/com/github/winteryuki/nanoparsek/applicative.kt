package com.github.winteryuki.nanoparsek

infix fun <T, R> Parser<T>.andL(next: Parser<R>): Parser<T> = andL { next }

infix fun <T, R> Parser<T>.andL(next: () -> Parser<R>): Parser<T> = map { fun(_: R): T = it } and next

infix fun <T, R> Parser<T>.andR(next: Parser<R>): Parser<R> = andR { next }

infix fun <T, R> Parser<T>.andR(next: () -> Parser<R>): Parser<R> = map { fun(x: R): R = x } and next

infix fun <T, R> Parser<(T) -> R>.and(next: Parser<T>): Parser<R> = and { next }

/**
 * [next] parser is lazy value to avoid infinite mutual recursion during parser construction.
 */
infix fun <T, R> Parser<(T) -> R>.and(next: () -> Parser<T>): Parser<R> = Parser { s1 ->
    val (f, s2) = parse(s1) orStop { return@Parser it }
    val (x, s3) = next().parse(s2) orStop { return@Parser it.copy(kind = ParseResult.Failure.Kind.HARD) }
    ParseResult.Success(f(x), s3)
}
