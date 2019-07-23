data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valMap = mutableMapOf<String, Int>()
    val funMap: MutableMap<String, Node> = mutableMapOf()
    val valSet: LinkedHashSet<String> = linkedSetOf()
    fun exec(): Evaled {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        var lastEvaled = Evaled(0)//仕方なく
        for (node in innerList) {
            node.valMap.putAll(valMap)
            node.funMap.putAll(funMap)
            lastEvaled = node.eval()
            if (lastEvaled.type == EvaledType.RETURN) {
                break
            }
            valMap.putAll(node.valMap)
            funMap.putAll(node.funMap)
        }
        return lastEvaled
    }

    fun printAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.valMap.putAll(valMap)
            node.printAssembly()
            println("  pop rax #ノードブロックの結果")
            valMap.putAll(node.valMap)
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