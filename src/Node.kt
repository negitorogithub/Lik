import Scope.*
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
            //TODO:テンプレート構文を使った方が良いのでは
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

        println("  mov rax, rbp #変数(${valName})のアドレスを計算しpush")
        println("  sub rax, ${getValOffsetFunByName(valName, funScope)}")
        println("  push rax")
        println("")
    }

    fun printAssembly() {
        //TODO:initをする前にインスタンスのサイズを知る必要がある?
        //TODO:initをする前にr12とrspを下げる必要がある?
        when (token.type) {
            NUMBER -> {
                println("  push ${token.value} #数字をpush")
            }
            ASSIGNED_VAL -> {
                val valInfo = getValInfoByName(token.val_!!.name, funScope, classScope)!!
                when (valInfo.scope) {
                    LOCAL, ARGUMENT -> printAssemblyPushFunValAddress()
                    CONSTRUCTOR, MEMBER -> printAssemblyPushClassValAddress(valInfo.val_.name)
                }
                println("  pop rax #代入済み変数をpush!")
                println("  mov rax, [rax]")
                println("  push rax")
            }
            ASSIGN, DECLARE_AND_ASSIGN_VAL -> {
                when (rightNode!!.token.type) {
                    CLASS_CALL -> {
                        leftNode!!.printAssemblyPushFunValAddress()
                        leftNode.printAssemblyPushInstanceValAddress()
                        println("  pop rdi #変数にインスタンス(${rightNode.token.className})を代入し右辺をpush")
                        println("  pop rax")
                        println("  mov [rax], rdi")
                        println("  push rdi")
                        println("")
                        println("  pop rbx #thisのアドレスをクラスコールに渡す")
                        rightNode.printAssembly()
                        //r12はrightNodeで下げる
                    }
                    FUN_CALL -> {
                        if (classScope == null) {
                            leftNode!!.printAssemblyPushFunValAddress()
                        } else {
                            leftNode!!.printAssemblyPushClassValAddress(leftNode.token.val_!!.name)
                        }
                        leftNode.printAssemblyPushInstanceValAddress()
                        println("  pop rdi #変数ヘッダ（rbpからアクセスできる方）にインスタンスの最初のメンバのアドレスを代入")
                        println("  pop rax")
                        println("  mov [rax], rdi")
                        println("")
                        val typeOfFun = rightNode.token.typeOfFun
                        if (classScope == null) {
                            leftNode.printAssemblyPushFunValAddress()
                        } else {
                            leftNode.printAssemblyPushClassValAddress(leftNode.token.val_!!.name)
                        }
                        rightNode.printAssembly()
                        if (typeOfFun == "Int") {
                            println("  pop rdi #変数に代入し右辺をpush")
                            println("  pop rax")
                            println("  mov [rax], rdi")
                            println("  push rdi")
                        } else {
                            println("  pop rdi #代入元インスタンスのヘッドアドレスを保存")
                            println("  pop rax")
                            println("  mov r10,[rax] #代入先インスタンスのヘッドアドレスを保存")
                            println("  mov r11,rdi #代入元インスタンスのヘッドアドレスを保存")
                            val classSize = ClassSizeMap.mapOfClassSize[typeOfFun]
                            for (i in 1 until classSize!!) {
                                println("  mov rax, r10 #代入先インスタンスのメンバのアドレスを計算しpush")
                                println("  sub rax, ${i * 8 - 8}")
                                println("  push rax")
                                println("")
                                println("  mov rax, r11 #代入元インスタンスのメンバのアドレスを計算しpush")
                                println("  sub rax, ${i * 8 - 8}")
                                println("  push [rax]")
                                println("")
                                println("  pop rdi #変数に代入")
                                println("  pop rax")
                                println("  mov [rax], rdi")
                                println("")
                            }
                            val instanceSizeOffset = ClassSizeMap.mapOfClassSize[typeOfFun]!! * 8
                            println("  sub r12, $instanceSizeOffset #${typeOfFun}のインスタンス生成の為変数領域引き下げ")
                            println("  mov rax, r12")
                            println("  sub rax, 8")
                            println("  mov rsp, rax #rspも伴って引き下げる")
                        }
                        println("")
                    }
                    else -> {
                        if (classScope == null) {
                            leftNode!!.printAssemblyPushFunValAddress()
                        } else {
                            leftNode!!.printAssemblyPushClassValAddress(leftNode.token.val_!!.name)
                        }
                        rightNode.printAssembly()
                        println("  pop rdi #変数に代入し右辺をpush")
                        println("  pop rax")
                        println("  mov [rax], rdi")
                        println("  push rdi")
                    }
                }
            }
            RETURN -> {
                rightNode!!.printAssembly()
                println("  pop rax #リターン")
                println("  mov rsp, rbp")
                println("  pop rbp")
                println("  pop r12")
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
                val className = token.className!!
                if (className != "Array") {
                    val instanceSizeOffset = ClassSizeMap.mapOfClassSize[className]!! * 8
                    println("  sub r12, $instanceSizeOffset #${className}のインスタンス生成の為変数領域引き下げ")
                    println("  mov rax, r12")
                    println("  sub rax, 8")
                    println("  mov rsp, rax #rspも伴って引き下げる")
                    println("  call ${className}_$init")
                    println("  push rax #thisのアドレスをpush")
                } else {
                    //TODO:数字の配列しか対応していない
                    println("  mov rax, ${registerListOfArguments[0]}")
                    println("  add rax, 8 #Arrayのindex変数分下げる")
                    println("  sub r12, rax #${className}のインスタンス生成の為変数領域引き下げ")
                    println("  mov rax, r12")
                    println("  sub rax, 8")
                    println("  mov rsp, rax #rspも伴って引き下げる")
                    println("  call ${className}_$init")
                    println("  push rax #thisのアドレスをpush")
                }
            }
            DOT -> {
                when (leftNode!!.token.type) {
                    ASSIGNED_VAL -> {
                        leftNode.printAssembly()//インスタンスにアクセスし、this(rbx)をraxに返却
                    }
                    CLASS_CALL -> throw Exception("クラスコールの直呼びは未対応")//TODO:対応
                    else -> throw Exception("ドットの左辺が解決できませんでした")
                }


                println("  pop rbx #thisのアドレスをpop ")
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
                        if (rightNode.token.typeOfFun != null) {
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

    private fun printAssemblyPushClassValAddress(valName: String) {
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

        println("  mov rax, r12 #インスタンスの本体の最初のアドレスを計算しpush #r12は変数領域最後のアドレス")
        println("  sub rax, 8")
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
        //引数を変数とみなして引数レジスタリストを参照しながら代入アセンブリを生成
        argumentsOnDeclare.forEachIndexed { index, val_ ->
            when (getValInfoByName(val_.name, funScope, classScope)!!.scope) {
                ARGUMENT, LOCAL -> printAssemblyPushFunValAddress(val_.name)
                CONSTRUCTOR, MEMBER -> printAssemblyPushClassValAddress(val_.name)
            }
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


    private fun getFunValHeaderOffset(): Int {
        return getArgumentsValHeaderOffSet() + FunLocalValsMap.mapOfVals[funScope]!!.size * 8
    }

    private fun getArgumentsValHeaderOffSet(): Int {
        return FunArgumentsMap.mapOfArguments[funScope]!!.size * 8
    }

    private fun getClassSizeOffset(): Int {
        return getConstructorsOffSet() + ClassMemberValsMap.mapOfVals[classScope]!!.size * 8
    }

    private fun getConstructorsOffSet(): Int {
        return ClassConstructorsMap.mapOfConstructors[classScope]!!.size * 8
    }

    private fun printInClassFunPrologue(funName: String) {
        println("$funName:")
        println("  push r12")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("  sub r12, ${getClassSizeOffset()}")
        println("  sub rsp, ${getClassSizeOffset()}")
        println("")
    }

    private fun printFunPrologue(funName: String) {
        println("$funName:")
        println("  push r12")
        println("  push rbp")
        println("  mov r12, rsp")
        println("  mov rbp, rsp")
        println("  sub r12, ${getFunValHeaderOffset()}")
        println("  sub rsp, ${getFunValHeaderOffset()}")
        println("")
    }

    private fun printFunEpilogue() {
        println("  mov rsp, rbp")
        println("  pop rbp")
        println("  pop r12")
        println("  ret")
    }

    private fun printClassPrologue(className: String) {
        println("${className}_$init:")
        println("  push r12")
        println("  push rbp")
        println("  mov rbp, rsp")
        println("")
    }

    private fun printClassEpilogue() {
        println("  mov rax, rbx #thisにあたるポインタをraxで返している")
        println("  mov rsp, rbp")
        println("  pop rbp")
        println("  pop r12")
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
            DECLARE_AND_ASSIGN_VAL -> {
                valList.add(leftNode!!.token.val_!!)
            }
            NODES -> {
                nodes.valList.addAll(valList)
                nodes.genValList()
                valList.addAll(nodes.valList)
            }
            FUN, CLASS -> {
                leftNode!!.genValList()
                rightNode!!.genValList()
            }
            ARGUMENTS -> {
                valList.addAll(argumentsOnDeclare)
            }
            else -> {
                //特になし
            }
        }
    }

    fun setValType2NotAssignedVal() {
        when (token.type) {
            DECLARE_AND_ASSIGN_VAL -> {
                val valType = when (rightNode!!.token.type) {
                    CLASS_CALL -> rightNode.token.className
                    FUN_CALL -> FunNodesTable.mapOfFunNode[FunNodesTableName(
                        null,
                        rightNode.token.funName
                    )]!!.token.typeOfFun
                    else -> "Int"
                }
                leftNode!!.addValTypeAndClassName(valType!!)
            }
            DOT -> {
                if (leftNode!!.token.type == CLASS_CALL) {
                    val valType = leftNode.token.className
                    if (rightNode!!.token.type == ASSIGNED_VAL) {
                        rightNode.addValTypeAndClassName(valType!!)
                    }
                }
            }
            else -> {
                nodes.setValType2NotAssignedVal()
                leftNode?.setValType2NotAssignedVal()
                rightNode?.setValType2NotAssignedVal()
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

    fun setType2FunCall(className: String? = null) {
        if (token.type == FUN_CALL) {
            token.typeOfFun = FunNodesTable.mapOfFunNode[FunNodesTableName(className, token.funName)]!!.token.typeOfFun
            return
        }
        if (token.type == DOT) {
            if (rightNode!!.token.type == FUN_CALL) {
                rightNode.setType2FunCall(getLeftClassType())
            }
            return
        }
        leftNode?.setType2FunCall(className)
        rightNode?.setType2FunCall(className)
        nodes.setType2FunCall(className)
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
                getValInfoByName(leftNode.token.val_!!.name, funScope, classScope)!!.val_.valType
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

//TODO:leftNodeとrightNodeとNodesに同じメソッドを実行させる関数
}