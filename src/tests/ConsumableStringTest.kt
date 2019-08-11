package tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import toConsumableString

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
            false,
            "val a=".toConsumableString().isAssignExpression()
        )

        assertEquals(
            false,
            "val abc = ".toConsumableString().isAssignExpression()
        )

        assertEquals(
            false,
            "val abc ==".toConsumableString().isAssignExpression()
        )

        assertEquals(
            true,
            "a=".toConsumableString().isAssignExpression()
        )

        assertEquals(
            true,
            "vala=".toConsumableString().isAssignExpression()
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
    fun isClassExpression() {
        assertEquals(true, "class a".toConsumableString().isClassExpression())
        assertEquals(true, "class abc123(){".toConsumableString().isClassExpression())
        assertEquals(false, "classa".toConsumableString().isClassExpression())
        assertEquals(false, "classabc123(){".toConsumableString().isClassExpression())
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