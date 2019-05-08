import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TokensTest {

    @Test
    fun parseAddTest() {
        assertEquals(
            6,
            parseAdd(
                listOf(
                    Token(TokenType.NUMBER, 2),
                    Token(TokenType.PLUS),
                    Token(TokenType.NUMBER, 5),
                    Token(TokenType.MINUS),
                    Token(TokenType.NUMBER, 1)

                )
            )
        )
    }
}