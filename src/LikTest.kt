import TokenType.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class LikTest {

    @Test
    fun parseTest() {
        assertEquals(
            "13",
            parse(
                "a = 4;" +
                        "b = 3;" +
                        "a*b+(a-b);"
            )
        )
        assertEquals(
            "1",
            parse(
                "a = 5;" +
                        "b = 3;" +
                        "a*b+(a-b);" +
                        "return a/b;" +
                        "return a;"
            )
        )

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

        assertEquals(
            listOf(
                Token(ASSIGNED_VAL, val_ = Val("returna"))
            ),
            tokenize("returna")
        )

        assertEquals(
            listOf(
                Token(RETURN),
                Token(ASSIGNED_VAL, val_ = Val("a"))
            ),
            tokenize("return a")
        )

        assertEquals(
            listOf(
                Token(RETURN)
            ),
            tokenize("return  ")
        )

    }

    @Test
    fun numberList2numberTest() {
        assertEquals(12, numberList2number(listOf("1", "2")))
        assertEquals(2, numberList2number(listOf("0", "2")))
        assertEquals(2, numberList2number(listOf("02")))
        assertEquals(0, numberList2number(listOf("0")))
        assertEquals(90080, numberList2number(listOf("090", "080")))
        assertThrows<NumberFormatException> { numberList2number(listOf("a", "b")) }
        assertThrows<NumberFormatException> { numberList2number(listOf()) }
    }
}