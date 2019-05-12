import TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TokensTest {

    @Test
    fun parseAddTest() {
        assertEquals(
            6,
            parseAdd(
                listOf(
                    Token(NUMBER, 2),
                    Token(PLUS),
                    Token(NUMBER, 5),
                    Token(MINUS),
                    Token(NUMBER, 1)

                )
            )
        )
    }

    @Test
    fun parseTest() {
        assertEquals(
            Node(
                PLUS,
                Node(1),
                Node(
                    MULTIPLY,
                    Node(2),
                    Node(3)
                )
            ),
            Tokens(
                listOf(
                    Token(1),
                    Token(PLUS),
                    Token(2),
                    Token(MULTIPLY),
                    Token(3)
                )
            ).parse()
        )

        assertEquals(
            Node(
                PLUS,
                Node(
                    MULTIPLY,
                    Node(1),
                    Node(2)
                ),
                Node(3)
            ),
            Tokens(
                listOf(
                    Token(1),
                    Token(MULTIPLY),
                    Token(2),
                    Token(PLUS),
                    Token(3)
                )
            ).parse()
        )

        assertEquals(
            Node(
                MULTIPLY,
                Node(5),
                Node(
                    PLUS,
                    Node(2),
                    Node(3)
                )
            ),
            Tokens(
                listOf(
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE)
                )
            ).parse()
        )

        assertEquals(
            Node(
                MULTIPLY,
                Node(
                    MINUS,
                    Node(0),
                    Node(5)
                ),
                Node(
                    PLUS,
                    Node(2),
                    Node(3)
                )
            )
            ,
            Tokens(
                listOf(
                    Token(MINUS),
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE)
                )
            ).parse()
        )


    }


}