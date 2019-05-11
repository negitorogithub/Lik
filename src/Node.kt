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
                Token(TokenType.NUMBER, value),
                leftNode,
                rightNode
            )

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