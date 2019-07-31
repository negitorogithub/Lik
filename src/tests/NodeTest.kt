package tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import Token
import TokenType.*
import Val
import Tokens
import Node


internal class NodeTest {

    @Test
    fun resolveClassSize() {
        /*
        Node(
            Token(TokenType.CLASS, className = "a"),
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
        */

    }
}