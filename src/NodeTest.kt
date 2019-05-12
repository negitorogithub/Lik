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
            ).eval()
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
            ).eval()
        )


    }
}