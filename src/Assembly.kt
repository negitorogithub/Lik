fun main() {

    val input = mutableListOf<String>()
    var next = readLine()
    while (next != "***") {
        input.add(next!!)
        next = readLine()
    }
    println(".intel_syntax noprefix")
    println(".global main")
    println("main:")
    println("  mov rax, ${input[0]}")
    println("  ret")


}