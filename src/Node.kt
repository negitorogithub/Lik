import TokenType.*

class Node(val token: Token, val leftNode: Node? = null, val rightNode: Node? = null) {
    constructor(
        type: TokenType,
        leftNode: Node? = null,
        rightNode: Node? = null
    ) :
            this(
                Token(type),
                leftNode,
                rightNode
            )

    constructor(
        value: Int,
        leftNode: Node? = null,
        rightNode: Node? = null
    ) :
            this(
                Token(NUMBER, value),
                leftNode,
                rightNode
            )

    fun eval(): Int {
        val leftValue: Int = when {
            leftNode?.token?.value != null -> leftNode.token.value
            leftNode != null -> leftNode.eval()
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }
        val rightValue: Int = when {
            rightNode?.token?.value != null -> rightNode.token.value
            rightNode != null -> rightNode.eval()
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }
        return when (token.type) {
            PLUS -> leftValue + rightValue
            MINUS -> leftValue - rightValue
            MULTIPLY -> leftValue * rightValue
            DIVIDE -> leftValue / rightValue
            else -> throw Exception("予期せぬトークンです")
        }


    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (token != other.token) return false
        if (leftNode != other.leftNode) return false
        if (rightNode != other.rightNode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + (leftNode?.hashCode() ?: 0)
        result = 31 * result + (rightNode?.hashCode() ?: 0)
        return result
    }
}