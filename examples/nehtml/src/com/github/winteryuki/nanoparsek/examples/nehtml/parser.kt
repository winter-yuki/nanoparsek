package com.github.winteryuki.nanoparsek.examples.nehtml

import com.github.winteryuki.nanoparsek.Parser
import com.github.winteryuki.nanoparsek.and
import com.github.winteryuki.nanoparsek.andL
import com.github.winteryuki.nanoparsek.andR
import com.github.winteryuki.nanoparsek.many
import com.github.winteryuki.nanoparsek.map
import com.github.winteryuki.nanoparsek.mb
import com.github.winteryuki.nanoparsek.or
import com.github.winteryuki.nanoparsek.satisfy
import com.github.winteryuki.nanoparsek.spaces
import com.github.winteryuki.nanoparsek.string
import com.github.winteryuki.nanoparsek.until

private val bodyOpening = Parser.string(Body.opening)
private val bodyClosing = Parser.string(Body.closing)

private val divOpening = Parser.string(Div.opening)
private val divClosing = Parser.string(Div.closing)

private val pOpening = Parser.string(P.opening)
private val pClosing = Parser.string(P.closing)

private val tag = or(bodyOpening, bodyClosing, divOpening, divClosing, pOpening, pClosing)

private val div: Parser<Div>
    get() = (divOpening andR Parser.spaces andR { body } andL Parser.spaces andL mb(divClosing)).map { Div(it) }

private val p: Parser<P> =
    or(
        pOpening andR Parser.spaces andR Parser.until(tag) andL Parser.spaces andL mb(pClosing),
        Parser.satisfy { it.isLetterOrDigit() }.map { fun(s: String): String = it + s } and
                Parser.until(tag) andL mb(pClosing),
    ).map { P(it.trimEnd()) }

private val body: Parser<List<Tag>>
    get() = many(
        or(
            div andL Parser.spaces,
            p andL Parser.spaces,
        )
    )

private val page: Parser<List<Tag>> =
    Parser.spaces andR mb(bodyOpening) andR Parser.spaces andR
            body andL Parser.spaces andL mb(bodyClosing) andL Parser.spaces

val documentParser: Parser<Document> = page.map { Document(Body(it)) }
