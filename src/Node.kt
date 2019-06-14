import TokenType.*

data class Node(
    val token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valMap: MutableMap<String, Int> = mutableMapOf(),
    val nodes: Nodes? = null
) {
    constructor(
        type: TokenType,
        leftNode: Node? = null,
        rightNode: Node? = null,
        nodes: Nodes? = null
    ) :
            this(
                Token(type),
                leftNode,
                rightNode,
                nodes = nodes
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

        if (token.type == WHILE) {
            leftNode!!.valMap.putAll(valMap)
            while (leftNode.eval().evaledBool!!)//これは例外で落としてよい
            {
                rightNode!!.valMap.putAll(valMap)
                rightNode.eval()//これは例外で落としてよい
                leftNode.valMap.putAll(rightNode.valMap)
                valMap.putAll(rightNode.valMap)
            }
            return Evaled(EvaledType.WHILE)
        }

        if (token.type == NODES) {
            nodes!!.valMap.putAll(valMap)
            val result = nodes.exec() //これは確定できる
            valMap.putAll(nodes.valMap)
            return result
        }


        val rightValue: Evaled = when {
            rightNode?.token?.value != null -> rightNode.token.value.toEvaled()
            valMap[rightNode?.token?.val_?.name] != null -> valMap[rightNode?.token?.val_?.name]!!.toEvaled()//!!は自明だよね
            rightNode != null -> {
                rightNode.valMap.putAll(valMap)
                val result = rightNode.eval()
                valMap.putAll(rightNode.valMap)
                result
            }
            else -> {
                print("EvaledType.NULLが検出されました")
                Evaled(EvaledType.NULL)
            }
        }

        //leftを評価するとnullになる
        if (token.type == RETURN) {
            return Evaled(EvaledType.RETURN, evaledInt = rightValue.evaledInt)
        }

        val leftValue: Evaled = when {
            leftNode?.token?.value != null -> leftNode.token.value.toEvaled()//数字
            valMap[leftNode?.token?.val_?.name] != null -> valMap[leftNode?.token?.val_?.name]!!.toEvaled()//代入済み変数　!!は自明だよね
            leftNode?.token?.val_?.name != null -> Evaled(Val(leftNode.token.val_.name))//非代入済み変数
            leftNode != null -> {
                leftNode.valMap.putAll(valMap)
                leftNode.eval()//leftNodeではvalMapの更新は行われないためvalMapを反映させていない
            }
            else -> {
                Evaled(EvaledType.NULL)
            }
        }



        if (token.type == ASSIGN) {
            if (leftValue.val2assign?.name != null) {
                // 未代入時
                // leftValue.val2assignは非null確定
                valMap[leftValue.val2assign.name] = rightValue.evaledInt ?: throw Exception("変数に数字以外が代入されました")
            } else {
                //代入済み時
                valMap[leftNode!!.token.val_!!.name] = rightValue.evaledInt ?: throw Exception("変数に数字以外が代入されました")
            }
            return Evaled(EvaledType.ASSIGN)
        }


        return evalBothSides(leftValue, rightValue)
    }

    private fun evalBothSides(leftValue: Evaled, rightValue: Evaled): Evaled {
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
        } else if (token.type == IF) {
            return if (leftValue.evaledBool!!) {
                rightValue
            } else {
                Evaled(EvaledType.IF_FALSE)
            }
        } else if (token.type == INCREASE) {
            valMap[leftNode?.token?.val_?.name!!] = valMap[leftNode.token.val_.name]!! + 1 //代入済み変数　!!は自明だよね
            return valMap[leftNode.token.val_.name]!!.toEvaled()
        } else if (token.type == NULL) {
            return Evaled(EvaledType.NULL)
        } else {
            throw Exception("予期せぬトークンです")
        }
    }

    fun printAssembly() {
        when (token.type) {
            PLUS -> {
                println("push ${leftNode!!.token.value}")
                println("push ${rightNode!!.token.value}")

                println("pop rdi")
                println("pop rax")
                println("add rax, rdi")
            }
            NUMBER -> {
                println("mov rax, ${token.value}")
            }
        }
    }

}