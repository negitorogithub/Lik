import TokenType.MULTIPLY
import TokenType.PLUS
import org.junit.jupiter.api.Test

internal class NodeTest {

    @Test
    fun evalTest() {
        kotlin.test.assertEquals(
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

        kotlin.test.assertEquals(
            5,
            Node(
                PLUS,
                Node(
                    MULTIPLY,
                    Node(1),
                    Node(2)
                ),
                Node(3)
            ).eval()
        )
    }
}