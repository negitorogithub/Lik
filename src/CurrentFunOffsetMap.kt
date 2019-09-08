class CurrentFunOffsetMap {
    companion object {
        val offsetMap: MutableMap<String, Int> = mutableMapOf()
        fun addOffset(funName: String, offset: Int) {
            offsetMap[funName] = offset + offsetMap[funName]!!
        }
    }
}