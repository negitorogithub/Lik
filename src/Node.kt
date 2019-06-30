import TokenType.*

data class Node(
    val token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valMap: LinkedHashMap<String, Int> = linkedMapOf(),
    val funMap: MutableMap<String, Node> = mutableMapOf(),
    val nodes: Nodes = Nodes(),
    val argumentsOnDeclare: MutableList<Val> = mutableListOf(),
    val offset: Int = 0
) {

    constructor(
        type: TokenType,
        leftNode: Node? = null,
        rightNode: Node? = null,
        nodes: Nodes = Nodes(),
        valMap: LinkedHashMap<String, Int> = linkedMapOf(),
        funMap: MutableMap<String, Node> = mutableMapOf(),
        arguments: MutableList<Val> = mutableListOf(),
        offset: Int = 0
    ) :
            this(
                Token(type),
                leftNode,
                rightNode,
                nodes = nodes,
                valMap = valMap,
                funMap = funMap,
                argumentsOnDeclare = arguments,
                offset = offset
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

        if (token.type == FUN) {
            if (funMap[token.funName!!] == null) {
                //定義
                token.funName.let {
                    funMap[it] = Node(token, leftNode, rightNode, valMap, mutableMapOf(), nodes, argumentsOnDeclare)
                    return Evaled(EvaledType.FUN_DECLARATION)
                }
            } else {

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
            nodes.valMap.putAll(valMap)
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

    //変数のアドレスを計算しpush
    private fun printAssemblyPushValAddress() {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp")
        println("  sub rax, ${(valMap.keys.indexOf(token.val_!!.name) + 1) * 8}")
        println("  push rax")
        println("")
    }

    fun printAssembly() {
        //とりあえずやっておく
        leftNode?.valMap?.putAll(valMap)
        rightNode?.valMap?.putAll(valMap)

        when (token.type) {
            NUMBER -> {
                println("  push ${token.value}")
            }
            ASSIGNED_VAL -> {
                printAssemblyPushValAddress()
                println("  pop rax")
                println("  mov rax, [rax]")
                println("  push rax")
            }
            ASSIGN -> {
                leftNode!!.printAssemblyPushValAddress()
                rightNode!!.printAssembly()
                println("  pop rdi")
                println("  pop rax")
                println("  mov [rax], rdi")
                println("  push rdi")
            }
            RETURN -> {
                rightNode!!.printAssembly()
                println("  pop rax")
                println("  mov rsp, rbp")
                println("  pop rbp")
                println("  ret")
            }
            IF -> {
                val labelNumber = UniqueNumber.next()
                leftNode!!.printAssembly()
                println("  pop rax")
                println("  cmp rax, 0")
                println("  je .Lend$labelNumber")
                rightNode!!.printAssembly()
                println(".Lend$labelNumber:")
            }
            else -> {
                //二項取るタイプ
                printAssemblyBinaryOperator()
            }
        }
        println("")
    }

    private fun printAssemblyBinaryOperator() {
        leftNode!!.printAssembly()
        rightNode!!.printAssembly()
        println("  pop rdi")
        println("  pop rax")

        when (token.type) {
            PLUS -> {
                println("  add rax, rdi")
            }
            MINUS -> {
                println("  sub rax, rdi")
            }
            MULTIPLY -> {
                println("  imul rdi")
            }
            DIVIDE -> {
                println("  cqo")
                println("  idiv rdi")
            }
            EQUAL -> {
                println("  cmp rax, rdi")
                println("  sete al")
                println("  movzb rax, al")
            }
            NOT_EQUAL -> {
                println("  cmp rax, rdi")
                println("  setne al")
                println("  movzb rax, al")
            }
            GREATER_THAN -> {
                println("  cmp rax, rdi")
                println("  setg al")
                println("  movzb rax, al")
            }
            GREATER_THAN_OR_EQUAL -> {
                println("  cmp rax, rdi")
                println("  setge al")
                println("  movzb rax, al")
            }
            LESS_THAN -> {
                println("  cmp rax, rdi")
                println("  setl al")
                println("  movzb rax, al")
            }
            LESS_THAN_OR_EQUAL -> {
                println("  cmp rax, rdi")
                println("  setle al")
                println("  movzb rax, al")
            }

            else -> {
                throw Exception("演算子${token.type}は未対応ナリ")
            }
        }
        println("")
        println("  push rax")
    }

}