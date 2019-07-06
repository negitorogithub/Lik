import TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NodeTest {

    @Test
    fun evalTest() {

        assertEquals(
            7,
            Node(
                PLUS,
                Node(1),
                Node(
                    MULTIPLY,
                    Node(2),
                    Node(3)
                )
            ).eval().evaledInt
        )

        assertEquals(
            -25,
            Node(
                MULTIPLY,
                Node(
                    MINUS,
                    Node(0),
                    Node(5)
                ),
                Node(
                    PLUS,
                    Node(2),
                    Node(3)
                )
            ).eval().evaledInt
        )

        assertEquals(
            3,
            Node(
                DIVIDE,
                Node(10),
                Node(3)
            ).eval().evaledInt
        )


        assertEquals(
            true,
            Node(
                EQUAL,
                Node(10),
                Node(
                    PLUS,
                    Node(
                        DIVIDE,
                        Node(
                            MINUS,
                            Node(0),
                            Node(5)
                        ),
                        Node(
                            PLUS,
                            Node(3),
                            Node(
                                MULTIPLY,
                                Node(1),
                                Node(2)
                            )
                        )
                    ),
                    Node(11)
                )
            ).eval().evaledBool
        )


        assertEquals(
            false,
            Node(
                GREATER_THAN,
                Node(10),
                Node(10)
            ).eval().evaledBool
        )

        assertEquals(
            false,
            Node(
                LESS_THAN_OR_EQUAL,
                Node(5),
                Node(4)
            ).eval().evaledBool
        )

        assertEquals(
            false,
            Node(
                LESS_THAN,
                Node(4),
                Node(4)
            ).eval().evaledBool
        )

        assertEquals(
            true,
            Node(
                EQUAL,
                Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                Node(3)
            ).apply { valMap["a"] = 3 }.eval().evaledBool
        )

        assertEquals(
            3,
            Node(
                IF,
                Node(
                    EQUAL,
                    Node(2),
                    Node(2)
                ),
                Node(
                    RETURN,
                    null,
                    Node(3)
                )
            ).eval().evaledInt
        )

        assertEquals(
            Node(
                Token(FUN, funName = "a"),
                Node(
                    ARGUMENTS,
                    arguments = mutableListOf(
                        Val("b"),
                        Val("c")
                    )
                ),
                Node(
                    RETURN,
                    null,
                    Node(3)
                )
            ),
            Node(
                Token(FUN, funName = "a"),
                Node(
                    ARGUMENTS,
                    arguments = mutableListOf(
                        Val("b"),
                        Val("c")
                    )
                ),
                Node(
                    RETURN,
                    null,
                    Node(3)
                )
            ).apply { eval() }.funMap["a"]
        )
    }
}