import java.io.File

fun main() {

    val input = mutableListOf<String>()
    var next = readLine()
    while (next != "***") {
        input.add(next!!)
        next = readLine()
    }
    val out = File("asm/out.s").absoluteFile

    val text = ".intel_syntax noprefix\n" +
            ".global main\n" +
            "main:\n" +
            "  mov rax, ${input[0]}\n" +
            "  ret\n"
    print(text)
    out.writeText(text)
}