class Assembly {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val buffer = StringBuilder()
            args.forEach {
                buffer.append(it)
            }
            println(".intel_syntax noprefix")
            println(".global main")
            println("main:")
            println("")

            println("  push rbp")
            println("  mov rbp, rsp")
            println("  sub rsp, 208")
            println("")

            Nodes(
                Tokens(
                    tokenize(
                        buffer.toString()
                    )
                ).parse()
            ).apply { exec() }.printAssemblies()
            println("  mov rsp, rbp")
            println("  pop rbp")
            println("  ret")
        }
    }
}

