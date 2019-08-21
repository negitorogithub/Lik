import TokenType.*

data class Node(
    var token: Token,
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
        argumentsOnDeclare: MutableList<Val> = mutableListOf()
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
                argumentsOnDeclare = argumentsOnDeclare
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
            DECLARE_AND_ASSIGN_VAL -> {
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
                leftNode!!.printAssemblyArgumentsOnDeclare()
                rightNode!!.nodes.printInClassAssembliesWithoutFun()
                printClassEpilogue()
                rightNode.nodes.printInClassFunDeclareAssemblies(token.className!!)
            }
            CLASS_CALL -> {
                leftNode?.nodes?.innerList?.forEachIndexed { index, node ->
                    node.printAssembly()
                    println("  pop rax")
                    println("  mov ${registerListOfArguments[index]},rax")
                }
                println("  call ${token.className}_$init")
                println("  push rax #thisのアドレスをpush")
            }
            DOT -> {
                leftNode!!.printAssembly()//インスタンス生成またはインスタンスにアクセスし、this(rbp)をraxに返却
                println("  pop rax #thisのアドレスをpop ")
                when (rightNode!!.token.type) {
                    ASSIGNED_VAL -> {
                        val className = when (leftNode.token.type) {
                            CLASS_CALL -> leftNode.token.className
                            ASSIGNED_VAL -> leftNode.token.val_!!.valType
                            else -> throw Exception("ドットの左辺が${leftNode.token.type}で解決できません")
                        }
                        val classValSet = ClassNodesTable.mapOfClassNode[className]!!.valSet
                        println("  sub rax, ${(classValSet.map { it.name }.indexOf(rightNode.token.val_!!.name) + 1) * 8}")//TODO:クラスコールの時クラスのvalSetを参照
                        println("  push rax")
                        println("")
                        println("  pop rax #代入済み変数をpush!")
                        println("  mov rax, [rax]")
                        println("  push rax")
                    }
                    FUN_CALL -> {
                        val className = when {
                            leftNode.token.type == CLASS_CALL -> leftNode.token.className
                            leftNode.token.type == ASSIGNED_VAL -> leftNode.token.val_!!.valType
                            else -> throw Exception("ドットの左辺が${leftNode.token.type}で解決できません")
                        }
                        println("  call ${className}_${rightNode.token.funName}")
                        if (rightNode.token.typeOfFun == null) {
                        } else {
                            println("  push rax")
                        }
                    }
                    else -> throw Exception("ドットの右辺が${rightNode.token.type}で解決できません")
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
        leftNode!!.printInClassFunPrologue("${classname}_${token.funName!!}")
        println("  mov rbp, rax #rbpにthisを代入")
        println("")
        leftNode.printAssemblyArgumentsOnDeclare()
        rightNode!!.printAssembly()
        println("")
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
        println("  ret")
    }

    private fun printFunPrologue(funName: String) {
        println("$funName:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub rsp, ${valSet.size * 8}")
        println("")
    }

    private fun printInClassFunPrologue(funName: String) {
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

    private fun addValTypeAndClassName(type: String) {
        token.val_!!.valType = type
        token.className = type
    }

    fun genValSet() {
        when (token.type) {
            DECLARE_AND_ASSIGN_VAL,
            ASSIGN -> {
                val valName = leftNode?.token?.val_?.name
                val hasAssigned = valSet.map { it.name }.contains(valName)
                if (hasAssigned) {
                } else {
                    val type = if (rightNode!!.token.type == CLASS_CALL) {
                        rightNode.token.className!!
                    } else {
                        "int"
                    }
                    valSet.add(Val("$valName", valType = type))
                }
            }
            NODES -> {
                nodes.valSet.addAll(valSet)
                nodes.genValSet()
                valSet.addAll(nodes.valSet)
            }
            FUN -> {
                leftNode!!.genValSet()
                valSet.addAll(leftNode.valSet)
                rightNode!!.genValSet()
                valSet.addAll(rightNode.valSet)
            }
            ARGUMENTS -> {
                valSet.addAll(argumentsOnDeclare)
            }
            CLASS -> {
                leftNode!!.genValSet()
                rightNode!!.genValSet()
                valSet.addAll(leftNode.valSet)
                valSet.addAll(rightNode.valSet)
            }
            ASSIGNED_VAL -> {
                val valType = valSet.find { it.name == token.val_!!.name }!!.valType
                addValTypeAndClassName(valType)
            }
            else -> {
                //特になし
            }
        }

    }

    fun genValType() {
        when (token.type) {
            DECLARE_AND_ASSIGN_VAL,
            ASSIGN -> {
                val valName = leftNode?.token?.val_?.name
                val hasAssigned = valSet.map { it.name }.contains(valName)
                if (hasAssigned) {
                    val valType = valSet.find { it.name == leftNode!!.token.val_!!.name }!!.valType
                    leftNode!!.addValTypeAndClassName(valType)
                } else {
                    if (rightNode!!.token.type == CLASS_CALL) {
                        leftNode!!.addValTypeAndClassName(rightNode.token.className!!)
                    }
                }
            }
            DOT -> {
                when (leftNode!!.token.type) {
                    CLASS_CALL -> {
                        val valType = leftNode.token.className
                        if (rightNode!!.token.type == ASSIGNED_VAL) {
                            rightNode.addValTypeAndClassName(valType!!)
                        }
                    }
                    ASSIGNED_VAL -> {
                        val valType = valSet.find { it.name == leftNode.token.val_!!.name }!!.valType
                        leftNode.addValTypeAndClassName(valType)
                        //TODO:ドットの右辺の型情報付与
                    }
                    else -> {
                        throw Exception("ドットの左辺が${leftNode.token.type}で解決できません")
                    }
                }
            }
            else -> {
                nodes.genValType()
                leftNode?.genValType()
                rightNode?.genValType()
            }
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

    fun setType2FunCall(prefix: String? = null) {
        if (token.type == FUN_CALL) {
            token.typeOfFun = FunNodesTable.mapOfFunNode[FunNodesTableName(prefix, token.funName)]!!.token.typeOfFun
            return
        }
        if (token.type == DOT) {
            if (rightNode!!.token.type == FUN_CALL) {
                rightNode.setType2FunCall(leftNode!!.token.className!!)
            }
            return
        }
        leftNode?.setType2FunCall(prefix)
        rightNode?.setType2FunCall(prefix)
        nodes.setType2FunCall(prefix)
    }

    companion object {
        private val registerListOfArguments = listOf("rdi", "rsi", "rdx", "rcx", "r8", "r9")
    }


}