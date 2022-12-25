package com.github.winteryuki.nanoparsek

fun interface Parser<out T> {
    fun parse(s: String): ParseResult<T>

    companion object {
        operator fun <T> invoke(value: T): Parser<T> =
            Parser { s -> ParseResult.Success(value, s) }
    }
}

sealed class ParseResult<out T> {
    data class Success<T>(val result: T, val remaining: String) : ParseResult<T>()

    data class Failure(val expected: String, val kind: Kind) : ParseResult<Nothing>() {
        /**
         * Helps to avoid parser exponential complexity by encouraging to write one-pass parsers.
         */
        enum class Kind {
            /**
             * Alternative semantics tries next parser.
             */
            SOFT,

            /**
             * Alternative parser should fail.
             */
            HARD
        }

        init {
            require(expected.isNotBlank())
        }
    }
}

fun <T, R> ParseResult<T>.map(f: (T) -> R): ParseResult<R> =
    when (this) {
        is ParseResult.Success -> ParseResult.Success(f(result), remaining)
        is ParseResult.Failure -> this
    }

fun <T, R> Parser<T>.map(f: (T) -> R): Parser<R> = Parser { s -> parse(s).map(f) }

inline infix fun <T> ParseResult<T>.orElse(block: (ParseResult.Failure) -> T): T =
    when (this) {
        is ParseResult.Success -> result
        is ParseResult.Failure -> block(this)
    }

inline infix fun <T> ParseResult<T>.orStop(block: (ParseResult.Failure) -> Nothing): ParseResult.Success<T> =
    when (this) {
        is ParseResult.Success -> this
        is ParseResult.Failure -> block(this)
    }

/**
 * Parser always fails in soft way. Can be used to write multiple-pass parsers.
 * Use carefully: it potentially leads to exponential parser complexity.
 */
val <T> Parser<T>.trying: Parser<T>
    get() = Parser { s ->
        when (val result = parse(s)) {
            is ParseResult.Success -> result
            is ParseResult.Failure -> result.copy(kind = ParseResult.Failure.Kind.SOFT)
        }
    }

val <T> Parser<T>.stopping: Parser<T>
    get() = Parser { s ->
        when (val result = parse(s)) {
            is ParseResult.Success -> result
            is ParseResult.Failure -> result.copy(kind = ParseResult.Failure.Kind.HARD)
        }
    }

val ParseResult.Failure.message: String
    get() = "{$expected} expected"
