data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valSet: LinkedHashSet<String> = linkedSetOf()

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
}