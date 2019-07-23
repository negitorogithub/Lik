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

        assertEquals(
            listOf(
                Node(
                    2
                ),
                Node(
                    RETURN,
                    null,
                    Node(3)
                )
            ),
            Tokens(
                listOf(
                    Token(2),
                    Token(SEMI_COLON),
                    Token(RETURN),
                    Token(3),
                    Token(SEMI_COLON)
                )
            ).parse()
        )
        assertEquals(
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
            ),
            //if(2==2)return 3;return 4;
            Tokens(
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
                )
            ).parse()
        )

        assertEquals(
            listOf(
                Node(
                    WHILE,
                    Node(
                        EQUAL,
                        Node(1),
                        Node(2)
                    ),
                    Node(
                        NODES,
                        nodes = Nodes(
                            listOf(
                                Node(
                                    ASSIGN,
                                    Node(Token(NOT_ASSIGNED_VAL, val_ = Val("a"))),
                                    Node(3)

                                )
                            )
                        )
                    )
                )
            ),
            // while(1==2){
            //     a = 3;
            // }
            Tokens(
                listOf(
                    Token(WHILE),
                    Token(ROUND_BRACKET_OPEN),
                    Token(1),
                    Token(EQUAL),
                    Token(2),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(NOT_ASSIGNED_VAL, val_ = Val("a")),
                    Token(ASSIGN),
                    Token(3),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()
        )

        assertEquals(
            listOf(
                Node(
                    INCREASE,
                    Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                    Node(Token(NULL))
                )
            )
            ,
            // a++;
            Tokens(
                listOf(
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(INCREASE),
                    Token(SEMI_COLON)
                )
            ).parse()
        )

        assertEquals(
            listOf(
                Node(
                    Token(FUN, funName = "a"),
                    Node(
                        Token(ARGUMENTS),
                        argumentsOnDeclare = mutableListOf(
                            Val("b"),
                            Val("c")
                        )
                    ),
                    Node(
                        NODES,
                        nodes = Nodes(
                            listOf(
                                Node(
                                    RETURN,
                                    null,
                                    Node(2)
                                )
                            )
                        )
                    )
                )
            )
            ,
            // fun a(b,c){return 2;}
            Tokens(
                listOf(
                    Token(FUN, funName = "a"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ARGUMENTS, val_ = Val("b")),
                    Token(ARGUMENTS, val_ = Val("c")),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(RETURN),
                    Token(2),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()
        )

        assertEquals(
            listOf(
                Node(
                    Token(FUN_CALL, funName = "isA123"),
                    Node(
                        Token(ARGUMENTS),
                        nodes =
                        Nodes(
                            mutableListOf(
                                Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                                Node(1),
                                Node(3)
                            )
                        )
                    ),
                    Node(NULL)
                )
            )
            ,
            // isA123(a,1,3)
            Tokens(
                listOf(
                    Token(FUN_CALL, funName = "isA123"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(COMMA),
                    Token(1),
                    Token(COMMA),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE)
                )
            ).parse()
        )


    }

}