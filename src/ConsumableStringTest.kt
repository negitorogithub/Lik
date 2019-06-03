import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ConsumableStringTest {

    @Test
    fun consume() {
        val consumableStr = "abc".toConsumableString()
        assertEquals(true, consumableStr.consume("a"))
        assertEquals("bc", consumableStr.innerString)
    }

    @Test
    fun popNumber() {
        val consumableString = "123abc".toConsumableString()
        assertEquals("123", consumableString.popNumber())
        assertEquals("abc", consumableString.innerString)
    }

    @Test
    fun startWithNumber() {
        assertEquals(true, "123abc".toConsumableString().startWithNumber())
    }

    @Test
    fun isAssignExpression() {
        assertEquals(
            true,
            "a=".toConsumableString().isAssignExpression()
        )

        assertEquals(
            true,
            "abc = ".toConsumableString().isAssignExpression()
        )

        assertEquals(
            false,
            "abc ==".toConsumableString().isAssignExpression()
        )
    }


}