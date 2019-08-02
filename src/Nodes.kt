data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valSet: LinkedHashSet<Val> = linkedSetOf()
    val classSizeMap: LinkedHashMap<String, Int> = linkedMapOf()

    fun printAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.printAssembly()
            if (node.token.type != TokenType.IF) {
                println("  pop rax #ノードブロックの結果")
            }
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

    fun printMainAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type != TokenType.FUN) {
                if (node.token.type != TokenType.FUN_CALL) {
                    node.printAssembly()
                    println("  pop rax #結果を戻り値に")
                } else {
                    node.printAssembly()//戻り値がraxにある
                }
            }
        }
    }

    fun genValType() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
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