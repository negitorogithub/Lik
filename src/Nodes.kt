data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valSet: LinkedHashSet<Val> = linkedSetOf()
    val classSizeMap: LinkedHashMap<String, Int> = linkedMapOf()

    fun printAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.printAssembly()
        }
    }

    fun printAssembliesIf() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.printAssembly()
        }
    }

    fun printFunDeclareAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                node.printAssembly()
            }
        }
    }

    fun printInClassFunDeclareAssemblies(className: String) {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                node.printInClassFunAssembly(className)
            }
        }
    }

    fun printClassDeclareAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                node.printAssembly()
            }
        }
    }

    fun genClassNodesTable() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                ClassNodesTable.mapOfClassNode[node.token.className!!] = node
            }
        }
    }

    fun genFunNodesTable(prefix: String? = null) {
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                FunNodesTable.mapOfFunNode[FunNodesTableName(prefix, node.token.funName!!)] = node
            }
            if (node.token.type == TokenType.CLASS) {
                node.rightNode?.nodes?.genFunNodesTable(node.token.className)
            }
        }
    }

    fun setType2FunCall(prefix: String? = null) {
        innerList.forEach { it.setType2FunCall(prefix) }
    }

    fun genValType() {
        for (node in innerList) {
            node.genValType()
        }
    }

    fun genValSet() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.genValSet()
            valSet.addAll(node.valSet)
        }
    }

    fun propagateValSet() {
        for (node in innerList) {
            node.valSet.clear()
            node.valSet.addAll(valSet)
            node.propagateValSet()
        }
    }

    fun genValSetEach() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.genValSet()
        }
    }

    fun propagateValSetEach() {
        for (node in innerList) {
            node.propagateValSet()
        }
    }

    fun genClassSizeMap() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.genClassSizeMap()
            classSizeMap.putAll(node.classSizeMap)
        }
    }

    fun propagateClassSizeMap() {
        for (node in innerList) {
            node.classSizeMap.clear()
            node.classSizeMap.putAll(classSizeMap)
            node.propagateClassSizeMap()
        }
    }

    fun printInClassAssembliesWithoutFun() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type != TokenType.FUN) {
                node.printAssembly()
                if (node.token.type != TokenType.IF) {
                    println("  pop rax #ノードブロックの結果")
                }
            }
        }
    }
}