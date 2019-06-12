fun main() {
    while (true) {
        val input = mutableListOf<String>()
        var next = readLine()
        while (next != "***") {
            input.add(next!!)
            next = readLine()
        }
        Assembly.main(input.toTypedArray())
    }
}