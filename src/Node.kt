import TokenType.*

class Node(
    val token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valMap: MutableMap<String, Int> = mutableMapOf()
) {
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
                Token(value),
                leftNode,
                rightNode
            )


    fun eval(): Evaled {

        token.value?.let { return it.toEvaled() }//数字単体
        token.val_?.name?.let { valName ->
            //変数単体
            valMap[valName]?.let {
                return it.toEvaled()
            }
        }


        val leftValue: Evaled = when {
            leftNode?.token?.value != null -> leftNode.token.value.toEvaled()//数字
            valMap[leftNode?.token?.val_?.name] != null -> valMap[leftNode?.token?.val_?.name]!!.toEvaled()//代入済み変数　!!は自明だよね
            leftNode?.token?.val_?.name != null -> Evaled(Val(leftNode.token.val_.name))//非代入済み変数
            leftNode != null -> {
                leftNode.valMap.putAll(valMap)
                leftNode.eval()//leftNodeではvalMapの更新は行われないためvalMapを反映させていない
            }
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }

        val rightValue: Evaled = when {
            rightNode?.token?.value != null -> rightNode.token.value.toEvaled()
            valMap[rightNode?.token?.val_?.name] != null -> valMap[rightNode?.token?.val_?.name]!!.toEvaled()//!!は自明だよね
            rightNode != null -> {
                rightNode.valMap.putAll(valMap)
                rightNode.eval()//rightNodeではvalMapの更新は行われないためvalMapを反映させていない
            }
            else -> throw Exception("二項演算子は数字に挟まれなければなりません")
        }

        if ((leftValue.type == EvaledType.INT) && (rightValue.type == EvaledType.INT)) {
            return when (token.type) {
                PLUS -> leftValue + rightValue
                MINUS -> leftValue - rightValue
                MULTIPLY -> leftValue * rightValue
                DIVIDE -> leftValue / rightValue
                EQUAL -> (leftValue.evaledInt == rightValue.evaledInt).toEvaled()
                NOT_EQUAL -> (leftValue.evaledInt != rightValue.evaledInt).toEvaled()
                LESS_THAN -> (leftValue.evaledInt!! < rightValue.evaledInt!!).toEvaled()
                GREATER_THAN -> (leftValue.evaledInt!! > rightValue.evaledInt!!).toEvaled()
                LESS_THAN_OR_EQUAL -> (leftValue.evaledInt!! <= rightValue.evaledInt!!).toEvaled()
                GREATER_THAN_OR_EQUAL -> (leftValue.evaledInt!! >= rightValue.evaledInt!!).toEvaled()
                else -> throw Exception("予期せぬトークンです")
            }
        } else if (token.type == ASSIGN) {
            //leftValue.val2assignは非null確定
            valMap[leftValue.val2assign!!.name] = rightValue.evaledInt ?: throw Exception("変数に数字以外が代入されました")
            return Evaled(EvaledType.ASSIGN)
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