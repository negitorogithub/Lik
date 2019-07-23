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

        assertEquals(
            "3",
            parse(
                "if(2==2)" +
                        "return 3;" +
                        "return 4;"
            )
        )


        assertEquals(
            "4",
            parse(
                "a=2;" +
                        "b=34;" +
                        "if(a==b)" +
                        "return 3;" +
                        "return 4;"
            )
        )

        assertEquals(
            "4",
            parse(
                "a=2;" +
                        "a++;" +
                        "a++;" +
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

        assertEquals(
            listOf(
                Token(IF),
                Token(ROUND_BRACKET_OPEN),
                Token(2),
                Token(EQUAL),
                Token(2),
                Token(ROUND_BRACKET_CLOSE),
                Token(RETURN),
                Token(3),
                Token(SEMI_COLON),
                Token(RETURN),
                Token(4),
                Token(SEMI_COLON)
            ),
            tokenize("if(2==2)return 3;return 4;")
        )

        assertEquals(
            listOf(
                Token(FUN, funName = "isA"),
                Token(ROUND_BRACKET_OPEN),
                Token(ARGUMENTS, val_ = Val("b")),
                Token(ARGUMENTS, val_ = Val("c")),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("fun isA(b,c)")
        )

        assertEquals(
            listOf(
                Token(FUN, funName = "isA"),
                Token(ROUND_BRACKET_OPEN),
                Token(ARGUMENTS, val_ = Val("b")),
                Token(ARGUMENTS, val_ = Val("c")),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("fun isA( b , c )")
        )

        assertEquals(
            listOf(
                Token(FUN, funName = "isA"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("fun isA()")
        )

        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA()")
        )
        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123()")
        )

        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(ASSIGNED_VAL, val_ = Val("a")),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123(a)")
        )
        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(1),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123(1)")
        )
        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(ASSIGNED_VAL, val_ = Val("a")),
                Token(COMMA),
                Token(1),
                Token(COMMA),
                Token(3),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123( a , 1 , 3 )")
        )

        assertEquals(
            listOf(
                Token(FUN_CALL, funName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(FUN_CALL, funName = "getString"),
                Token(ROUND_BRACKET_OPEN),
                Token(1),
                Token(COMMA),
                Token(2),
                Token(ROUND_BRACKET_CLOSE),
                Token(COMMA),
                Token(3),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123( getString( 1 , 2 ) , 3 )")
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