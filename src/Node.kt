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

    fun eval(): Evaled {

        val leftValue: Evaled = when {
            leftNode?.token?.value != null -> leftNode.token.value.toEvaled()
            leftNode != null -> leftNode.eval()
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }
        val rightValue: Evaled = when {
            rightNode?.token?.value != null -> rightNode.token.value.toEvaled()
            rightNode != null -> rightNode.eval()
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }

        if ((leftValue.type == EvaledType.INT) && (rightValue.type == EvaledType.INT)) {
            return when (token.type) {
                PLUS -> leftValue + rightValue
                MINUS -> leftValue - rightValue
                MULTIPLY -> leftValue * rightValue
                DIVIDE -> leftValue / rightValue
                EQUAL -> (leftValue.evaledBool == rightValue.evaledBool).toEvaled()
                NOT_EQUAL -> (leftValue.evaledBool != rightValue.evaledBool).toEvaled()
                LESS_THAN -> (leftValue.evaledBool!! < rightValue.evaledBool!!).toEvaled()
                GREATER_THAN -> (leftValue.evaledBool!! > rightValue.evaledBool!!).toEvaled()
                LESS_THAN_OR_EQUAL -> (leftValue.evaledBool!! <= rightValue.evaledBool!!).toEvaled()
                GREATER_THAN_OR_EQUAL -> (leftValue.evaledBool!! >= rightValue.evaledBool!!).toEvaled()
                else -> throw Exception("予期せぬトークンです")
            }
        } else {
            throw Exception("予期せぬトークンです")
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