import TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NodesTest {

    @Test
    fun exec() {

        assertEquals(
            4,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(4)
                    ),
                    Node(Token(ASSIGNED_VAL, val_ = Val("a")))
                )
            ).exec().evaledInt
        )

        assertEquals(
            7,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(
                            PLUS,
                            Node(5),
                            Node(2)
                        )
                    ),
                    Node(Token(ASSIGNED_VAL, val_ = Val("a")))
                )
            ).exec().evaledInt
        )

        assertEquals(
            7,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(3)
                    ),
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("b"))),
                        Node(4)
                    ),
                    Node(
                        PLUS,
                        Node(
                            Token(ASSIGNED_VAL, val_ = Val("a"))
                        ),
                        Node(
                            Token(ASSIGNED_VAL, val_ = Val("b"))
                        )
                    )
                )
            ).exec().evaledInt
        )

        assertEquals(
            7,
            Nodes(
                listOf(
                    Node(3),
                    Node(7)
                )
            ).exec().evaledInt
        )

        assertEquals(
            5,
            Nodes(
                listOf(
                    Node(2),
                    Node(
                        RETURN, Node(3), Node(5)
                    ),
                    Node(7)
                )
            ).exec().evaledInt
        )

        assertEquals(
            7,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(3)
                    ),
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("b"))),
                        Node(4)
                    ),

                    Node(
                        RETURN, null, Node(
                            PLUS,
                            Node(
                                Token(ASSIGNED_VAL, val_ = Val("a"))
                            ),
                            Node(
                                Token(ASSIGNED_VAL, val_ = Val("b"))
                            )
                        )
                    ),
                    Node(
                        Token(ASSIGNED_VAL, val_ = Val("a"))
                    )
                )
            ).exec().evaledInt
        )

        assertEquals(
            3,
            Nodes(
                listOf(
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
                    ),
                    Node(
                        RETURN,
                        null,
                        Node(4)
                    )
                )
            ).exec().evaledInt
        )

        assertEquals(
            3,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(2)
                    ),
                    Node(
                        INCREASE,
                        Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                        Node(Token(NULL))
                    ),
                    Node(Token(ASSIGNED_VAL, val_ = Val("a")))
                )
            ).exec().evaledInt
        )

        assertEquals(
            3,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                        Node(3)
                    )
                )
            ).apply {
                exec()
            }.valMap["a"]
        )

        assertEquals(
            6,
            Nodes(
                listOf(
                    Node(
                        ASSIGN,
                        Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                        Node(2)
                    ),
                    Node(
                        WHILE,
                        Node(
                            NOT_EQUAL,
                            Node(
                                (Token(ASSIGNED_VAL, val_ = Val("a")))
                            ),
                            Node(6)
                        ),
                        Node(
                            INCREASE,
                            Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                            Node(NULL)
                        )
                    )
                )
            ).apply {
                exec()
            }.valMap["a"]
        )


    }
}