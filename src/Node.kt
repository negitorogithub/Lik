import TokenType.*

data class Node(
    val token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valMap: LinkedHashMap<String, Int> = linkedMapOf(),
    val valSet: LinkedHashSet<Val> = linkedSetOf(),
    val classSizeMap: LinkedHashMap<String, Int> = linkedMapOf(),
    val funMap: MutableMap<String, Node> = mutableMapOf(),
    val nodes: Nodes = Nodes(),
    val argumentsOnDeclare: MutableList<Val> = mutableListOf()
) {

    constructor(
        type: TokenType,
        leftNode: Node? = null,
        rightNode: Node? = null,
        nodes: Nodes = Nodes(),
        valMap: LinkedHashMap<String, Int> = linkedMapOf(),
        valSet: LinkedHashSet<Val> = linkedSetOf(),
        classSizeMap: LinkedHashMap<String, Int> = linkedMapOf(),
        funMap: MutableMap<String, Node> = mutableMapOf(),
        arguments: MutableList<Val> = mutableListOf()
    ) :
            this(
                Token(type),
                leftNode,
                rightNode,
                nodes = nodes,
                valMap = valMap,
                valSet = valSet,
                classSizeMap = classSizeMap,
                funMap = funMap,
                argumentsOnDeclare = arguments
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


    fun genValType() {

    }

    //TODO: 変数にクラスが代入されている場合の処理
    fun genClassSizeMap() {
        if (token.type == CLASS) {
            val classSize = rightNode!!.valSet.size
            classSizeMap[token.className!!] = classSize
        }
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushValAddress() {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${(valSet.map { it.name }.indexOf(token.val_!!.name) + 1) * 8}")
        println("  push rax")
        println("")
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushValAddress(valName: String) {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL && token.type != ARGUMENTS) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${(valSet.map { it.name }.indexOf(valName) + 1) * 8}")
        println("  push rax")
        println("")
    }

    fun printAssembly() {
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
                    println("  mov ${registerListOfArguments[index]},rax")
                }
                println("  call ${token.funName}")
                println("  push rax")
            }
            FUN -> {
                leftNode!!.printFunPrologue(token.funName!!)
                leftNode.printAssemblyArgumentsOnDeclare()
                rightNode!!.printAssembly()
                printFunEpilogue()
            }
            CLASS -> {
                printClassPrologue(token.className!!)
                rightNode!!.nodes.printInClassAssembliesWithoutFun()
                printClassEpilogue()
                rightNode.nodes.printInClassFunDeclareAssemblies(token.className)
            }
            CLASS_CALL -> {
                println("  mov rax,rsp")
                println("  call ${token.className}_$init")
            }
            DOT -> {
                leftNode!!.printAssembly()//インスタンス生成またはインスタンスにアクセスし、this(rbp)をraxに返却
                if (rightNode!!.token.type == ASSIGNED_VAL) {
                    println("  sub rax, ${(valSet.map { it.name }.indexOf(rightNode.token.val_!!.name) + 1) * 8}")
                    println("  push rax")
                    println("")
                    println("  pop rax #代入済み変数をpush!")
                    println("  mov rax, [rax]")
                    println("  push rax")
                }



                if (rightNode.token.type == FUN_CALL) {
                    val className = if (leftNode.token.type == CLASS_CALL) {
                        leftNode.token.className
                    } else {
                        leftNode.token.val_!!.valType
                    }
                    println("  pop rax #thisのアドレスを取得")
                    println("  call ${className}_${rightNode.token.funName}")
                    println("  push rax")
                }
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

    fun printInClassFunAssembly(classname: String) {
        leftNode!!.printFunPrologue("${classname}_${token.funName!!}")
        leftNode.printAssemblyArgumentsOnDeclare()
        rightNode!!.printAssembly()
        printFunEpilogue()
    }

    private fun printAssemblyArgumentsOnDeclare() {
        //まだ途中
        //引数を変数とみなして引数レジスタリストを参照しながら代入アセンブリを生成
        argumentsOnDeclare.forEachIndexed { index, val_ ->
            printAssemblyPushValAddress(val_.name)
            println("  mov rdi,${registerListOfArguments[index]} #引数に代入し右辺をpush")
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

    private fun printFunEpilogue() {
        println("  mov rsp, rbp")
        println("  pop rbp")
    }

    private fun printFunPrologue(funName: String) {
        println("$funName:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub rsp, ${valSet.size * 8}")
        println("")
    }

    private fun printClassPrologue(className: String) {
        println("${className}_$init:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub rsp, ${valSet.size * 8}")
        println("")
    }

    private fun printClassEpilogue() {
        println("  mov rax, rbp #thisにあたるポインタをraxで返している")
        println("  mov rsp, rbp")
        println("  pop rbp")
        println("  ret")
    }

    fun genValSet() {
        if (token.type == ASSIGN) {
            val valName = leftNode?.token?.val_?.name
            if (valSet.map { it.name }.contains(valName)) {
                //代入済み時
            } else {
                // 未代入時
                val type = if (leftNode!!.token.type == CLASS_CALL) {
                    leftNode.token.className!!
                } else {
                    "int"
                }
                valSet.add(Val("$valName", valType = type))
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
            valSet.addAll(argumentsOnDeclare)
        }

        if (token.type == CLASS) {
            leftNode!!.genValSet()
            rightNode!!.genValSet()
            valSet.addAll(leftNode.valSet)
            valSet.addAll(rightNode.valSet)
            return
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

    fun propagateClassSizeMap() {
        leftNode?.classSizeMap?.clear()
        leftNode?.classSizeMap?.putAll(classSizeMap)
        leftNode?.propagateClassSizeMap()
        rightNode?.classSizeMap?.clear()
        rightNode?.classSizeMap?.putAll(classSizeMap)
        rightNode?.propagateClassSizeMap()
        nodes.classSizeMap.clear()
        nodes.classSizeMap.putAll(classSizeMap)
        nodes.propagateClassSizeMap()
    }

    companion object {
        private val registerListOfArguments = listOf("rdi", "rsi", "rdx", "rcx", "r8", "r9")
    }


}