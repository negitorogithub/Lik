import TokenType.*

data class Node(
    val token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valMap: LinkedHashMap<String, Int> = linkedMapOf(),
    val valSet: LinkedHashSet<String> = linkedSetOf(),
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
        valSet: LinkedHashSet<String> = linkedSetOf(),
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
                valSet = valSet,
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

    override fun toString(): String {
        val result = mutableListOf<String>()
        result.apply {
            add("[")
            add("token=")
            add(token.toString())
            add(",")
            leftNode?.toString()?.let {
                add("leftNode=")
                add(it)
                add(",")
            }
            rightNode?.toString()?.let {
                add("rightNode=")
                add(it)
                add(",")
            }
            if (nodes.innerList.isNotEmpty()) {
                add("Nodes=")
                add(nodes.toString())
                add(",")
            }
            if (valSet.isNotEmpty()) {
                add("valSet=")
                add(valSet.toString())
                add(",")
            }
            if (funMap.isNotEmpty()) {
                add("funMap=")
                add(funMap.toString())
                add(",")
            }
            if (argumentsOnDeclare.isNotEmpty()) {
                add("argumentsOnDeclare=")
                add(argumentsOnDeclare.toString())
            }
            add("}")
        }
        return result.joinToString(separator = "")
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushValAddress() {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${(valSet.indexOf(token.val_!!.name) + 1) * 8}")
        println("  push rax")
        println("")
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushValAddress(valName: String) {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL && token.type != ARGUMENTS) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${(valSet.indexOf(valName) + 1) * 8}")
        println("  push rax")
        println("")
    }

    fun printAssembly() {
        //とりあえずやっておく
        when (token.type) {
            NUMBER -> {
                println("  push ${token.value} #数字をpush")
            }
            ASSIGNED_VAL -> {
                printAssemblyPushValAddress()
                println("  pop rax #代入済み変数をpush!")
                println("  mov rax, [rax]")
                println("  push rax")
            }
            ASSIGN -> {
                leftNode!!.printAssemblyPushValAddress()
                rightNode!!.printAssembly()
                println("  pop rdi #変数に代入し右辺をpush")
                println("  pop rax")
                println("  mov [rax], rdi")
                println("  push rdi")
            }
            RETURN -> {
                rightNode!!.printAssembly()
                println("  pop rax #リターン")
                println("  mov rsp, rbp")
                println("  pop rbp")
                println("  ret")
            }
            FUN_CALL -> {
                leftNode?.nodes?.innerList?.forEachIndexed { index, node ->
                    node.printAssembly()
                    println("  pop rax")
                    println("  mov ${Companion.registerListOfArguments[index]},rax")
                }
                println("  call ${token.funName}")
                println("  push rax")
            }
            FUN -> {
                leftNode!!.printPrologue(token.funName!!)
                leftNode.printAssemblyArgumentsOnDeclare()
                rightNode!!.printAssembly()
                printEpilogue()
            }
            IF -> {
                val labelNumber = UniqueNumber.next()
                leftNode!!.printAssembly()
                println("  pop rax #if文")
                println("  cmp rax, 0")
                println("  je .Lend$labelNumber")
                if (rightNode!!.token.type == NODES) {
                    rightNode.nodes.printAssembliesIf()
                } else {
                    rightNode.printAssembly()
                }


                println(".Lend$labelNumber:")
            }
            NODES -> {
                nodes.printAssemblies()
            }
            else -> {
                //二項取るタイプ
                printAssemblyBinaryOperator()
            }
        }
        println("")
    }

    private fun printAssemblyArgumentsOnDeclare() {
        //まだ途中
        //引数を変数とみなして引数レジスタリストを参照しながら代入アセンブリを生成
        argumentsOnDeclare.forEachIndexed { index, val_ ->
            printAssemblyPushValAddress(val_.name)
            println("  mov rdi,${Companion.registerListOfArguments[index]} #引数に代入し右辺をpush")
            println("  pop rax")
            println("  mov [rax], rdi")
            println("  push rdi")
        }

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

    private fun printEpilogue() {
        println("  mov rsp, rbp")
        println("  pop rbp")
    }

    private fun printPrologue(funName: String) {
        println("$funName:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub rsp, ${valSet.size * 8}")
        println("")
    }

    fun genValSet() {
        if (token.type == ASSIGN) {
            val valName = leftNode?.token?.val_?.name
            if (valSet.contains(valName)) {
                //代入済み時
            } else {
                // 未代入時
                valSet.add(valName!!)
            }
            return
        }
        if (token.type == NODES) {
            nodes.valSet.addAll(valSet)
            nodes.genValSet()
            valSet.addAll(nodes.valSet)
            return
        }
        if (token.type == FUN) {
            leftNode!!.genValSet()
            valSet.addAll(leftNode.valSet)
            rightNode!!.genValSet()
            valSet.addAll(rightNode.valSet)
            return
        }
        if (token.type == ARGUMENTS) {
            valSet.addAll(argumentsOnDeclare.map { it.name })
        }
    }

    fun propagateValSet() {
        leftNode?.valSet?.clear()
        leftNode?.valSet?.addAll(valSet)
        leftNode?.propagateValSet()
        rightNode?.valSet?.clear()
        rightNode?.valSet?.addAll(valSet)
        rightNode?.propagateValSet()
        nodes.valSet.clear()
        nodes.valSet.addAll(valSet)
        nodes.propagateValSet()
    }

    fun refreshFunMap() {
        if (token.type == FUN) {
            if (funMap[token.funName!!] == null) {
                //定義
                token.funName.let {
                    funMap[it] =
                        Node(token, leftNode, rightNode, valMap, valSet, mutableMapOf(), nodes, argumentsOnDeclare)
                }
                rightNode!!.funMap.putAll(funMap)
                rightNode.refreshFunMap()
            }
        }
    }

    companion object {
        private val registerListOfArguments = listOf("rdi", "rsi", "rdx", "rcx", "r8", "r9")
    }


}