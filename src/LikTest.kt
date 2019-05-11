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
                Token(NUMBER, 123),
                Token(PLUS),
                Token(NUMBER, 22),
                Token(MULTIPLY),
                Token(NUMBER, 9)
            ),
            tokenize(" 123 +22  * 09 ")
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