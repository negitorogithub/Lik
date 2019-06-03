fun main(args: Array<String>) {
    println(".intel_syntax noprefix")
    println(".global main")
    println("main:")
    println("  mov rax, ${args[0]}")
    println("  ret")
}

