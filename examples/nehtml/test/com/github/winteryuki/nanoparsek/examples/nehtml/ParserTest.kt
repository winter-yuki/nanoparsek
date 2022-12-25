package com.github.winteryuki.nanoparsek.examples.nehtml

import com.github.winteryuki.nanoparsek.ParseResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ParserTest {
    @Test
    fun `empty doc`() {
        val actual = documentParser.parse("")
        val expected = ParseResult.Success(Document(), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `only spaces`() {
        val actual = documentParser.parse("         ")
        val expected = ParseResult.Success(Document(), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `opening body`() {
        val actual = documentParser.parse("  <body>  ")
        val expected = ParseResult.Success(Document(Body()), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `ending body`() {
        val actual = documentParser.parse("   <body>   </body>  ")
        val expected = ParseResult.Success(Document(Body()), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `single div`() {
        val actual = documentParser.parse("   <body>  \t<div>  </body>  ")
        val expected = ParseResult.Success(Document(Body(Div())), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `closing div`() {
        val actual = documentParser.parse("   <body>  \t<div>  </div>  </body>  ")
        val expected = ParseResult.Success(Document(Body(Div())), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `p only`() {
        val text = "hello\n world"
        val actual = documentParser.parse("  $text  \n ")
        val expected = ParseResult.Success(Document(Body(P(text))), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `p in half body`() {
        val text = "hello\n world"
        val actual = documentParser.parse(" <body> $text  \n ")
        val expected = ParseResult.Success(Document(Body(P(text))), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `p in body`() {
        val text = "hello\n world"
        val actual = documentParser.parse(" <body> $text \t</body> \n ")
        val expected = ParseResult.Success(Document(Body(P(text))), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `nested div opening`() {
        val actual = documentParser.parse("   <body>  \t<div> <div>  </div>  </body>  ")
        val expected = ParseResult.Success(Document(Body(Div(Div()))), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `nested divs`() {
        val actual = documentParser.parse(
            """
            <body>
                <div>
                    <div>  </div>
                    <div>
                        <div> </div>
                    </div>
            </body>
            """.trimIndent()
        )
        val body = Body(
            Div(
                Div(),
                Div(Div())
            )
        )
        val expected = ParseResult.Success(Document(body), "")
        assertEquals(expected, actual)
    }

    @Test
    fun `nested ps`() {
        val text1 = "text!"
        val text2 = "hello"
        val text3 = "hello world!"
        val actual = documentParser.parse(
            """
            <body>
                <div>
                    <p> $text1
                    <div>  </div>
                    <div>
                        <div> <p> $text2 </p> </div>
                    </div>
                    $text3
            </body>
            """.trimIndent()
        )
        val body = Body(
            Div(
                P(text1),
                Div(),
                Div(Div(P(text2))),
                P(text3)
            )
        )
        val expected = ParseResult.Success(Document(body), "")
        assertEquals(expected, actual)
    }
}
