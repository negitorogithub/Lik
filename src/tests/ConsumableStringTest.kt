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
    fun popIdentification() {
        assertEquals("abc123abc", "abc123abc".toConsumableString().popIdentification())
        assertEquals("abc12", "abc12 3abc".toConsumableString().popIdentification())
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

    @Test
    fun isFunExpression() {
        assertEquals(true, "fun a(".toConsumableString().isFunExpression())
        assertEquals(true, "fun abc123(".toConsumableString().isFunExpression())
        assertEquals(false, "funabc123(".toConsumableString().isFunExpression())
        assertEquals(false, "fun(".toConsumableString().isFunExpression())
        assertEquals(false, "fun abc123".toConsumableString().isFunExpression())
    }

    @Test
    fun isArgumentExpression() {
        assertEquals(true, "a)".toConsumableString().isArgumentExpression())
        assertEquals(true, "a,b,c)".toConsumableString().isArgumentExpression())
        assertEquals(true, " a , b , c )".toConsumableString().isArgumentExpression())
        assertEquals(true, " a12 , b34 , c56d )".toConsumableString().isArgumentExpression())
        assertEquals(true, ")".toConsumableString().isArgumentExpression())
    }

    @Test
    fun hasNextArgument() {
        assertEquals(false, ")".toConsumableString().hasNextArgument())
        assertEquals(true, "a)".toConsumableString().hasNextArgument())
        assertEquals(true, " a )".toConsumableString().hasNextArgument())
        assertEquals(true, "a,b)".toConsumableString().hasNextArgument())
        assertEquals(true, " a , b )".toConsumableString().hasNextArgument())
        assertEquals(true, " a , b , c  )".toConsumableString().hasNextArgument())
    }

}