import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TokenTest {
    @Test
    fun constructorTest() {
        assertThrows<IllegalArgumentException> { Token(TokenType.NUMBER) }
        assertThrows<IllegalArgumentException> { Token(TokenType.PLUS, 2) }
    }
}