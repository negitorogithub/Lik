data class Nodes(private val innerList: List<Node>) {

    private val valMap = mutableMapOf<String, Int>()

    fun exec(): Evaled {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        var lastEvaled = Evaled(0)//仕方なく
        for (node in innerList) {
            node.valMap.putAll(valMap)
            lastEvaled = node.eval()
            if (lastEvaled.type == EvaledType.RETURN) {
                break
            }
            valMap.putAll(node.valMap)
        }
        return lastEvaled
    }

}