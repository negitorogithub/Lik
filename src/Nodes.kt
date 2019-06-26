data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valMap = mutableMapOf<String, Int>()
    val funMap: MutableMap<String, Node> = mutableMapOf()

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
            println("  pop rax")
            valMap.putAll(node.valMap)
        }
    }
}