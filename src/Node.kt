import TokenType.*

data class Node(
    var token: Token,
    val leftNode: Node? = null,
    val rightNode: Node? = null,
    val valList: MutableList<Val> = mutableListOf(),
    val classSizeMap: LinkedHashMap<String, Int> = linkedMapOf(),
    val nodes: Nodes = Nodes(),
    val argumentsOnDeclare: MutableList<Val> = mutableListOf()
) {

    private var funScope: String? = null
    private var classScope: String? = null

    constructor(
        type: TokenType,
        leftNode: Node? = null,
        rightNode: Node? = null,
        nodes: Nodes = Nodes(),
        valList: MutableList<Val> = mutableListOf(),
        classSizeMap: LinkedHashMap<String, Int> = linkedMapOf(),
        argumentsOnDeclare: MutableList<Val> = mutableListOf()
    ) :
            this(
                Token(type),
                leftNode,
                rightNode,
                nodes = nodes,
                valList = valList,
                classSizeMap = classSizeMap,
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
            if (valList.isNotEmpty()) {
                add("valSet=")
                add(valList.toString())
                add(",")
            }
            if (argumentsOnDeclare.isNotEmpty()) {
                add("argumentsOnDeclare=")
                add(argumentsOnDeclare.toString())
            }
            if (funScope != null) {
                add("funScope=")
                add(funScope!!)
            }
            if (classScope != null) {
                add("classScope=")
                add(classScope!!)
            }
            add("}")
        }
        return result.joinToString(separator = "")
    }


    //TODO: 変数にクラスが代入されている場合の処理
    fun genClassSizeMap() {
        if (token.type == CLASS) {
            val classSize = leftNode!!.valList.size + rightNode!!.valList.size + 1
            classSizeMap[token.className!!] = classSize
            ClassSizeMap.mapOfClassSize[token.className!!] = classSize
        }
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushFunValAddress() {
        printAssemblyPushFunValAddress(token.val_!!.name)
    }

    //変数のアドレスを計算しpush
    private fun printAssemblyPushFunValAddress(valName: String) {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL && token.type != ARGUMENTS) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${getValOffsetFunByName(valName, funScope)}")
        println("  push rax")
        println("")
    }

    fun printAssembly() {
        when (token.type) {
            NUMBER -> {
                println("  push ${token.value} #数字をpush")
            }
            ASSIGNED_VAL -> {
                printAssemblyPushFunValAddress()
                println("  pop rax #代入済み変数をpush!")
                println("  mov rax, [rax]")
                println("  push rax")
            }
            ASSIGN -> {//TODO:↓のコピペ
                leftNode!!.printAssemblyPushFunValAddress()
                rightNode!!.printAssembly()
                println("  pop rdi #変数に代入し右辺をpush")
                println("  pop rax")
                println("  mov [rax], rdi")
                println("  push rdi")
            }
            DECLARE_AND_ASSIGN_VAL -> {//TODO:thisのアドレスはクラスコールのrbpではなくインスタンスのアドレスにする（インスタンスの場合）
                if (rightNode?.token?.type != CLASS_CALL) {
                    if (classScope == null) {
                        leftNode!!.printAssemblyPushFunValAddress()
                    } else {
                        leftNode!!.printAssemblyPushMemberValAddress(leftNode.token.val_!!.name)
                    }
                    rightNode!!.printAssembly()
                    println("  pop rdi #変数に代入し右辺をpush")
                    println("  pop rax")
                    println("  mov [rax], rdi")
                    println("  push rdi")
                } else {
                    leftNode!!.printAssemblyPushFunValAddress()
                    leftNode.printAssemblyPushInstanceValAddress()//TODO:ここを改造する
                    println("  pop rdi #変数にインスタンスを代入し右辺をpush")
                    println("  pop rax")
                    println("  mov [rax], rdi")
                    println("  push rdi")
                    println("")
                    println("  pop rbx #thisのアドレスをクラスコールに渡す")
                    rightNode.printAssembly()
                    funScope?.let {
                        CurrentFunOffsetMap.addOffset(
                            it,
                            ClassSizeMap.mapOfClassSize[rightNode.token.className!!]!! * 8
                        )
                    }

                }
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
                //TODO:引数とローカル変数を区別しなければならない気がする
                printClassPrologue(token.className!!)
                leftNode!!.printAssemblyArgumentsOnDeclare()
                rightNode!!.nodes.printInClassAssembliesWithoutFun()
                printClassEpilogue()
                rightNode.nodes.printInClassFunDeclareAssemblies(token.className!!)
            }
            CLASS_CALL -> {
                //rbxにthisのアドレスが入っている
                leftNode?.nodes?.innerList?.forEachIndexed { index, node ->
                    node.printAssembly()
                    println("  pop rax")
                    println("  mov ${registerListOfArguments[index]},rax")
                }
                println("  call ${token.className}_$init")
                println("  push rax #thisのアドレスをpush")
            }
            DOT -> {
                when (leftNode!!.token.type) {
                    ASSIGNED_VAL -> {
                        leftNode.printAssembly()//インスタンスにアクセスし、this(rbx)をraxに返却
                    }
                    CLASS_CALL -> throw Exception("クラスコールの直呼びは未対応")//TODO:対応
                    else -> throw Exception("ドットの左辺が解決できませんでした")
                }


                println("  pop rax #thisのアドレスをpop ")
                when (rightNode!!.token.type) {
                    ASSIGNED_VAL -> {
                        println(
                            "  sub rax, ${getValOffsetClassByName(
                                rightNode.token.val_!!.name,
                                getLeftClassType()
                            )}"
                        )
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
                        rightNode.leftNode!!.nodes.innerList.forEachIndexed { index, node ->
                            node.printAssembly()
                            println("  pop rax")
                            println("  mov ${registerListOfArguments[index]},rax")
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

    private fun printAssemblyPushMemberValAddress(valName: String) {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL && token.type != ARGUMENTS) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbx #変数(メンバ)のアドレスを計算しpush")
        println("  sub rax, ${getValOffsetClassByName(valName, classScope)}")
        println("  push rax")
        println("")
    }

    private fun printAssemblyPushInstanceValAddress() {
        if (token.type != NOT_ASSIGNED_VAL && token.type != ASSIGNED_VAL && token.type != ARGUMENTS) {
            throw Exception("代入の左辺値が変数ではありません")
        }

        println("  mov rax, rbp #変数のアドレスを計算しpush")
        println("  sub rax, ${CurrentFunOffsetMap.offsetMap[funScope]}")
        println("  push rax")
        println("")
    }

    fun printInClassFunAssembly(classname: String) {
        leftNode!!.printInClassFunPrologue("${classname}_${token.funName!!}")
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
            printAssemblyPushFunValAddress(val_.name)
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
        println("  sub rsp, ${getFunOffset()}")
        println("")
    }

    fun getFunOffset(): Int {
        var localValsOffset = 0
        FunLocalValsMap.mapOfVals[funScope]!!.forEach { localValsOffset += ClassSizeMap.mapOfClassSize[it.valType]!! * 8 }
        return getArgumentsOffSet() + localValsOffset
    }

    private fun getArgumentsOffSet(): Int {
        var result = 0
        FunArgumentsMap.mapOfArguments[funScope]!!.forEach { result += ClassSizeMap.mapOfClassSize[it.valType]!! * 8 }
        return result
    }

    private fun getClassOffset(): Int {
        var classMembersOffset = 0
        ClassMemberValsMap.mapOfVals[classScope]!!.forEach { classMembersOffset += ClassSizeMap.mapOfClassSize[it.valType]!! * 8 }
        return getConstructorsOffSet() + classMembersOffset
    }

    private fun getConstructorsOffSet(): Int {
        var result = 0
        ClassConstructorsMap.mapOfConstructors[classScope]!!.forEach { result += ClassSizeMap.mapOfClassSize[it.valType]!! * 8 }
        return result
    }

    private fun printInClassFunPrologue(funName: String) {
        println("$funName:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub rsp, ${getClassOffset()}")
        println("")
    }

    private fun printClassPrologue(className: String) {
        println("${className}_$init:")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("")
    }

    private fun printClassEpilogue() {
        println("  mov rax, rbx #thisにあたるポインタをraxで返している")
        println("  mov rsp, rbp")
        println("  pop rbp")
        println("  ret")
    }

    private fun addValTypeAndClassName(type: String) {
        token.val_!!.valType = type
        token.className = type
    }

    fun addValTypeAndClassName2AssignedVal() {//TODO:メンバアクセスが失敗する(メンバのクラススコープがnullのため)
        if (token.type == ASSIGNED_VAL) {
            val type = getValInfoByName(token.val_!!.name, funScope, classScope)!!.val_.valType
            addValTypeAndClassName(type)
        }
        if (token.type == DOT) {
            if (leftNode!!.token.type == ASSIGNED_VAL) {
                leftNode.addValTypeAndClassName(getLeftClassType()!!)
            }
            if (rightNode!!.token.type == ASSIGNED_VAL) {
                val rightType = getValInfoByName(rightNode.token.val_!!.name, null, getLeftClassType()!!)!!.val_.valType
                rightNode.addValTypeAndClassName(rightType)
            }
            return
        }
        leftNode?.addValTypeAndClassName2AssignedVal()
        rightNode?.addValTypeAndClassName2AssignedVal()
        nodes.addValTypeAndClassName2AssignedVal()
    }


    fun genValList() {
        when (token.type) {
            DECLARE_AND_ASSIGN_VAL,
            ASSIGN -> {
                val valName = leftNode?.token?.val_?.name
                val hasAssigned = valList.map { it.name }.contains(valName)
                if (hasAssigned) {
                } else {
                    val type = if (rightNode!!.token.type == CLASS_CALL) {
                        rightNode.token.className!!
                    } else {
                        "Int"
                    }
                    valList.add(Val("$valName", valType = type))
                }
            }
            NODES -> {
                nodes.valList.addAll(valList)
                nodes.genValList()
                valList.addAll(nodes.valList)
            }
            FUN -> {
                leftNode!!.genValList()
                rightNode!!.genValList()
            }
            ARGUMENTS -> {
                valList.addAll(argumentsOnDeclare)
            }
            CLASS -> {
                leftNode!!.genValList()
                rightNode!!.genValList()
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
                val hasAssigned = valList.map { it.name }.contains(valName)
                if (hasAssigned) {
                    val valType = valList.find { it.name == leftNode!!.token.val_!!.name }!!.valType
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
                        val valType = getValInfoByName(leftNode.token.val_!!.name, funScope, classScope)!!.val_.valType
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
                val className = when {
                    leftNode!!.token.type == ASSIGNED_VAL -> leftNode.token.val_!!.valType
                    leftNode.token.type == CLASS_CALL -> leftNode.token.className
                    else -> throw Exception("ドットの右辺が${leftNode.token}で解決できませんでした")
                }
                rightNode.setType2FunCall(className)
            }
            return
        }
        leftNode?.setType2FunCall(prefix)
        rightNode?.setType2FunCall(prefix)
        nodes.setType2FunCall(prefix)
    }

    fun genScopes() {
        if (token.type == FUN) {
            funScope = token.funName
        }
        if (token.type == CLASS) {
            classScope = token.className
            rightNode!!.nodes.genScopes()
        }
    }

    fun propagateScopes(funName: String? = null, className: String? = null) {
        funScope = funName
        classScope = className
        if (token.type == FUN) {
            funScope = token.funName
        }
        if (token.type == CLASS) {
            classScope = token.className
        }
        leftNode?.propagateScopes(funScope, classScope)
        rightNode?.propagateScopes(funScope, classScope)
        nodes.propagateScopes(funScope, classScope)
    }


    private fun getLeftClassType(): String? {
        return when (leftNode?.token?.type) {
            ASSIGNED_VAL -> {
                leftNode.token.val_!!.valType
            }
            CLASS_CALL -> {
                leftNode.token.className
            }
            else -> {
                throw Exception("右辺の型は${leftNode?.token?.type}でクラスの情報が分かりません")
            }
        }
    }

    companion object {
        private val registerListOfArguments = listOf("rdi", "rsi", "rdx", "rcx", "r8", "r9")
    }


}