import TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TokensTest {

    @Test
    fun parseTest() {

        assertEquals(
            Node(
                PLUS,
                Node(1),
                Node(
                    MULTIPLY,
                    Node(2),
                    Node(3)
                )
            ),
            Tokens(
                listOf(
                    Token(1),
                    Token(PLUS),
                    Token(2),
                    Token(MULTIPLY),
                    Token(3),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )

        assertEquals(
            Node(
                PLUS,
                Node(
                    MULTIPLY,
                    Node(1),
                    Node(2)
                ),
                Node(3)
            ),
            Tokens(
                listOf(
                    Token(1),
                    Token(MULTIPLY),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )

        assertEquals(
            Node(
                MULTIPLY,
                Node(5),
                Node(
                    PLUS,
                    Node(2),
                    Node(3)
                )
            ),
            Tokens(
                listOf(
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )

        assertEquals(
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
            )
            ,
            Tokens(
                listOf(
                    Token(MINUS),
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )

        assertEquals(
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
            )
            ,
            Tokens(
                listOf(
                    Token(10),
                    Token(EQUAL),
                    Token(MINUS),
                    Token(5),
                    Token(DIVIDE),
                    Token(ROUND_BRACKET_OPEN),
                    Token(3),
                    Token(PLUS),
                    Token(1),
                    Token(MULTIPLY),
                    Token(2),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(PLUS),
                    Token(11),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )


        //"a=4;b=3;a+b;"
        assertEquals(
            listOf(
                Node(
                    ASSIGN,
                    Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                    Node(5)
                ),
                Node(
                    ASSIGN,
                    Node(Token(NOT_ASSIGNED_VAL, val_ = Val("b"))),
                    Node(2)
                ),
                Node(
                    PLUS,
                    Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                    Node(Token(ASSIGNED_VAL, val_ = Val("b")))
                )
            )

            ,
            Tokens(
                listOf(
                    Token(NOT_ASSIGNED_VAL, val_ = Val("a")),
                    Token(ASSIGN),
                    Token(5),
                    Token(SEMI_COLON),

                    Token(NOT_ASSIGNED_VAL, val_ = Val("b")),
                    Token(ASSIGN),
                    Token(2),
                    Token(SEMI_COLON),

                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(PLUS),
                    Token(ASSIGNED_VAL, val_ = Val("b")),
                    Token(SEMI_COLON)
                )
            ).parse()
        )


        //"a*b+(a-b);"
        assertEquals(
            Node(
                PLUS,
                Node(
                    MULTIPLY,
                    Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                    Node(Token(ASSIGNED_VAL, val_ = Val("b")))
                ),
                Node(
                    MINUS,
                    Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                    Node(Token(ASSIGNED_VAL, val_ = Val("b")))
                )
            ),
            Tokens(
                listOf(
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(MULTIPLY),
                    Token(ASSIGNED_VAL, val_ = Val("b")),
                    Token(PLUS),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(MINUS),
                    Token(ASSIGNED_VAL, val_ = Val("b")),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON)
                )
            ).parse()[0]
        )


    }


}