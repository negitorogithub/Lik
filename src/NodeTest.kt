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


    }
}