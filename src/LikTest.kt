import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class LikTest {

    @Test
    fun parseTest() {

    }

    @Test
    fun tokenizeTest() {

    }

    @Test
    fun numberList2numberTest() {
        assertEquals(numberList2number(listOf("1", "2")), 12)
        assertEquals(numberList2number(listOf("0", "2")), 2)
        assertEquals(numberList2number(listOf("02")), 2)
        assertEquals(numberList2number(listOf("0")), 0)
        assertEquals(numberList2number(listOf("090", "080")), 90080)
        assertThrows<java.lang.NumberFormatException> { numberList2number(listOf("a", "b")) }
        assertThrows<java.lang.NumberFormatException> { numberList2number(listOf()) }
    }
}