package tests

import Token
import TokenType.*
import Val
import numberList2number
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tokenize

internal class LikTest {


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

                Token(RETURN),
                Token(4)

            ),
            tokenize("if(2==2)return 3 return 4")
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
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA()")
        )
        assertEquals(
            listOf(
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123()")
        )

        assertEquals(
            listOf(
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(ASSIGNED_VAL, val_ = Val("a")),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123(a)")
        )
        assertEquals(
            listOf(
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(1),
                Token(ROUND_BRACKET_CLOSE)
            ),
            tokenize("isA123(1)")
        )
        assertEquals(
            listOf(
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA123"),
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
                Token(CLASS_OR_FUN_CALL, classOrFunName = "isA123"),
                Token(ROUND_BRACKET_OPEN),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "getString"),
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
        assertEquals(
            listOf(
                Token(FUN, funName = "fac"),
                Token(ROUND_BRACKET_OPEN),
                Token(ARGUMENTS, val_ = Val("n")),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(IF),
                Token(ROUND_BRACKET_OPEN),
                Token(ASSIGNED_VAL, val_ = Val("n")),
                Token(EQUAL),
                Token(1),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(1),

                Token(CURLY_BRACKET_CLOSE),
                Token(RETURN),
                Token(ASSIGNED_VAL, val_ = Val("n")),
                Token(MULTIPLY),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "fac"),
                Token(ROUND_BRACKET_OPEN),
                Token(ASSIGNED_VAL, val_ = Val("n")),
                Token(MINUS),
                Token(1),
                Token(ROUND_BRACKET_CLOSE),

                Token(CURLY_BRACKET_CLOSE),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "fac"),
                Token(ROUND_BRACKET_OPEN),
                Token(5),
                Token(ROUND_BRACKET_CLOSE)

            ),
            tokenize("fun fac(n){if(n == 1){return 1} return n*fac(n-1)} fac(5)")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "a"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class a(){}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "a"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class a(){\n}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE),
                Token(NOT_ASSIGNED_VAL, val_ = Val("a")),
                Token(DECLARE_AND_ASSIGN_VAL),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE)

            ),
            tokenize("class A(){} val a=A()")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "a"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE),
                Token(FUN, funName = "b"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE),
                Token(FUN, funName = "main"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "b"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),

                Token(CLASS_OR_FUN_CALL, classOrFunName = "a"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),

                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class a(){} fun b(){} fun main(){b()a()}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "a"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(NOT_ASSIGNED_VAL, val_ = Val("b")),
                Token(DECLARE_AND_ASSIGN_VAL),
                Token(42),

                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class a(){val b=42}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(CURLY_BRACKET_CLOSE),
                Token(FUN, funName = "main"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(DOT),
                Token(ASSIGNED_VAL, val_ = Val("member")),

                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class A(){} fun main(){return A().member}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(FUN, funName = "get42"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(42),

                Token(CURLY_BRACKET_CLOSE),
                Token(CURLY_BRACKET_CLOSE),
                Token(FUN, funName = "main"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(DOT),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "get42"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),

                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class A(){fun get42(){return 42}} fun main(){return A().get42()}")
        )

        assertEquals(
            listOf(
                Token(CLASS, className = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ARGUMENTS, val_ = Val("n")),
                Token(ROUND_BRACKET_CLOSE),
                Token(CURLY_BRACKET_OPEN),
                Token(FUN, funName = "get42"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(TYPE_OF_FUN, typeOfFun = "Int"),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(ASSIGNED_VAL, val_ = Val("n")),

                Token(CURLY_BRACKET_CLOSE),
                Token(CURLY_BRACKET_CLOSE),
                Token(FUN, funName = "main"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(TYPE_OF_FUN, typeOfFun = "Int"),
                Token(CURLY_BRACKET_OPEN),
                Token(RETURN),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "A"),
                Token(ROUND_BRACKET_OPEN),
                Token(ROUND_BRACKET_CLOSE),
                Token(DOT),
                Token(CLASS_OR_FUN_CALL, classOrFunName = "get42"),
                Token(ROUND_BRACKET_OPEN),
                Token(42),
                Token(ROUND_BRACKET_CLOSE),

                Token(CURLY_BRACKET_CLOSE)
            ),
            tokenize("class A(n){fun get42():Int{return n}} fun main():Int{return A().get42(42)}")
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