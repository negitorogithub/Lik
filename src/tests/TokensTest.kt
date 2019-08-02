package tests

import Node
import Nodes
import Token
import TokenType.*
import Tokens
import Val
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TokensTest {

    @Test
    fun parseTest() {

        //class A(){fun get42(){return 42;}} fun main(){return A().get42();}
        assertEquals(
            listOf(
                Node(
                    Token(CLASS, className = "A"),
                    Node(ARGUMENTS),
                    Node(
                        NODES,
                        nodes =
                        Nodes(
                            listOf(
                                Node(
                                    Token(FUN, funName = "get42"),
                                    Node(ARGUMENTS),
                                    Node(
                                        NODES,
                                        nodes =
                                        Nodes(
                                            listOf(
                                                Node(
                                                    RETURN,
                                                    null,
                                                    Node(42)
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                Node(
                    Token(FUN, funName = "main"),
                    Node(ARGUMENTS),
                    Node(
                        NODES,
                        nodes =
                        Nodes(
                            listOf(
                                Node(
                                    RETURN,
                                    null,
                                    Node(
                                        DOT,
                                        Node(
                                            Token(CLASS_CALL, className = "A"),
                                            Node(ARGUMENTS)
                                        ),
                                        Node(
                                            Token(FUN_CALL, funName = "get42"),
                                            Node(ARGUMENTS)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )

            )
            ,
            Tokens(
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
                    Token(SEMI_COLON),
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()
        )//class A(){fun get42(){return 42;}} fun main(){return A().get42();}


        //class a(){} fun b(){} fun main(){b();a();}

        assertEquals(
            listOf(
                Node(
                    Token(CLASS, className = "a"),
                    Node(ARGUMENTS),
                    Node(
                        NODES,
                        nodes = Nodes()
                    )
                ),
                Node(
                    Token(FUN, funName = "b"),
                    Node(ARGUMENTS),
                    Node(NODES)
                ),
                Node(
                    Token(FUN, funName = "main"),
                    Node(ARGUMENTS),
                    Node(
                        NODES,
                        nodes = Nodes(
                            listOf(
                                Node(
                                    Token(FUN_CALL, funName = "b"),
                                    Node(ARGUMENTS)
                                ),
                                Node(
                                    Token(CLASS_CALL, className = "a"),
                                    Node(ARGUMENTS)
                                )
                            )
                        )
                    )
                )
            )
            ,
            Tokens(
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
                    Token(SEMI_COLON),
                    Token(CLASS_OR_FUN_CALL, classOrFunName = "a"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()
        )


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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(1),
                    Token(PLUS),
                    Token(2),
                    Token(MULTIPLY),
                    Token(3),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(1),
                    Token(MULTIPLY),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(MINUS),
                    Token(5),
                    Token(MULTIPLY),
                    Token(ROUND_BRACKET_OPEN),
                    Token(2),
                    Token(PLUS),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(MULTIPLY),
                    Token(ASSIGNED_VAL, val_ = Val("b")),
                    Token(PLUS),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(MINUS),
                    Token(ASSIGNED_VAL, val_ = Val("b")),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList?.get(0)
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(2),
                    Token(SEMI_COLON),
                    Token(RETURN),
                    Token(3),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
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
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
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
            //fun main(){
            // while(1==2){
            //     a = 3;
            // }
            //}
            Tokens(
                listOf(
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
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
                    Token(CURLY_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
        )

        assertEquals(
            listOf(
                Node(
                    INCREASE,
                    Node(Token(ASSIGNED_VAL, val_ = Val("a"))),
                    null
                )
            )
            ,
            // a++;
            Tokens(
                listOf(
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(INCREASE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
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
                    null
                )
            )
            ,
            // isA123(a,1,3);
            Tokens(
                listOf(
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(FUN_CALL, funName = "isA123"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("a")),
                    Token(COMMA),
                    Token(1),
                    Token(COMMA),
                    Token(3),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0].rightNode?.nodes?.innerList
        )

        //fun fac(n){if(n == 1){return 1;} return n*fac(n-1);} fun main(){fac(5);}
        assertEquals(
            listOf(
                Node(
                    Token(FUN, funName = "fac"),
                    Node(
                        Token(ARGUMENTS),
                        argumentsOnDeclare = mutableListOf(
                            Val("n")
                        )
                    ),
                    Node(
                        NODES,
                        nodes = Nodes(
                            listOf(
                                Node(
                                    IF,
                                    Node(
                                        EQUAL,
                                        Node(Token(ASSIGNED_VAL, val_ = Val("n"))),
                                        Node(1)
                                    ),
                                    Node(
                                        NODES,
                                        nodes =
                                        Nodes(
                                            listOf(
                                                Node(
                                                    RETURN,
                                                    null,
                                                    Node(1)
                                                )
                                            )
                                        )
                                    )
                                ),
                                Node(
                                    RETURN,
                                    null,
                                    Node(
                                        MULTIPLY,
                                        Node(Token(ASSIGNED_VAL, val_ = Val("n"))),
                                        Node(
                                            Token(FUN_CALL, funName = "fac"),
                                            Node(
                                                Token(ARGUMENTS),
                                                nodes =
                                                Nodes(
                                                    mutableListOf(
                                                        Node(
                                                            MINUS,
                                                            Node(Token(ASSIGNED_VAL, val_ = Val("n"))),
                                                            Node(1)
                                                        )
                                                    )
                                                )
                                            ),
                                            null
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                Node(
                    Token(FUN, funName = "main"),
                    Node(
                        Token(ARGUMENTS),
                        argumentsOnDeclare = mutableListOf(
                        )
                    )
                    ,
                    Node(
                        NODES,
                        nodes = Nodes(
                            listOf(
                                Node(
                                    Token(FUN_CALL, funName = "fac"),
                                    Node(
                                        Token(ARGUMENTS),
                                        nodes =
                                        Nodes(
                                            mutableListOf(
                                                Node(5)
                                            )
                                        )
                                    ),
                                    null
                                )
                            )
                        )
                    )
                )
            )
            ,
            //fun fac(n){if(n == 1){return 1;} return n*fac(n-1);} fun main(){fac(5);}
            Tokens(
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE),
                    Token(RETURN),
                    Token(ASSIGNED_VAL, val_ = Val("n")),
                    Token(MULTIPLY),
                    Token(FUN_CALL, funName = "fac"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ASSIGNED_VAL, val_ = Val("n")),
                    Token(MINUS),
                    Token(1),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE),
                    Token(FUN, funName = "main"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(FUN_CALL, funName = "fac"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(5),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()
        )

        //class a(){}
        assertEquals(
            Node(
                Token(CLASS, className = "a"),
                Node(ARGUMENTS),
                Node(
                    NODES,
                    nodes = Nodes()
                )
            )
            ,
            Tokens(
                listOf(
                    Token(CLASS, className = "a"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0]
        )

        //class a(){b=42;}
        assertEquals(
            Node(
                Token(CLASS, className = "a"),
                Node(ARGUMENTS),
                Node(
                    NODES,
                    nodes = Nodes(
                        listOf(
                            Node(
                                ASSIGN,
                                Node(Token(NOT_ASSIGNED_VAL, val_ = Val("b"))),
                                Node(42)
                            )
                        )
                    )
                )
            )
            ,
            Tokens(
                listOf(
                    Token(CLASS, className = "a"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(NOT_ASSIGNED_VAL, val_ = Val("b")),
                    Token(ASSIGN),
                    Token(42),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0]
        )

        //class a(){b=42;}
        assertEquals(
            Node(
                Token(CLASS, className = "a"),
                Node(ARGUMENTS),
                Node(
                    NODES,
                    nodes = Nodes(
                        listOf(
                            Node(
                                ASSIGN,
                                Node(Token(NOT_ASSIGNED_VAL, val_ = Val("b"))),
                                Node(42)
                            )
                        )
                    )
                )
            )
            ,
            Tokens(
                listOf(
                    Token(CLASS, className = "a"),
                    Token(ROUND_BRACKET_OPEN),
                    Token(ROUND_BRACKET_CLOSE),
                    Token(CURLY_BRACKET_OPEN),
                    Token(NOT_ASSIGNED_VAL, val_ = Val("b")),
                    Token(ASSIGN),
                    Token(42),
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[0]
        )

        assertEquals(
            Node(
                RETURN,
                null,
                Node(
                    DOT,
                    Node(
                        Token(CLASS_CALL, className = "A"),
                        Node(ARGUMENTS)
                    ),
                    Node(Token(ASSIGNED_VAL, val_ = Val("member")))
                )
            ),
            Tokens(
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
                    Token(SEMI_COLON),
                    Token(CURLY_BRACKET_CLOSE)
                )
            ).parse()[1].rightNode!!.nodes.innerList[0]
        )

    }

}