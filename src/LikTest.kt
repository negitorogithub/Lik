import TokenType.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class LikTest {

    @Test
    fun parseTest() {

    }

    @Test
    fun tokenizeTest() {

        assertEquals(
            listOf(
                Token(123),
                Token(PLUS),
                Token(22),
                Token(MULTIPLY),
                Token(9)
            ),
            tokenize(" 123 +22  * 09 ")
        )

        assertEquals(
            listOf(
                Token(123),
                Token(GREATER_THAN_OR_EQUAL),
                Token(22),
                Token(MULTIPLY),
                Token(9),
                Token(EQUAL),
                Token(3),
                Token(LESS_THAN)
            ),
            tokenize("  123>= 22  * 09 == 3 < ")
        )
    }

    @Test
    fun numberList2numberTest() {
        assertEquals(12, numberList2number(listOf("1", "2")))
        assertEquals(2, numberList2number(listOf("0", "2")))
        assertEquals(2, numberList2number(listOf("02")))
        assertEquals(0, numberList2number(listOf("0")))
        assertEquals(90080, numberList2number(listOf("090", "080")))
        assertThrows<java.lang.NumberFormatException> { numberList2number(listOf("a", "b")) }
        assertThrows<java.lang.NumberFormatException> { numberList2number(listOf()) }
    }
}