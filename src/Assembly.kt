class Assembly {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val buffer = StringBuilder()
            args.forEach {
                buffer.append(it)
            }
            println(".intel_syntax noprefix")
            Nodes(
                Tokens(
                    tokenize(
                        buffer.toString()
                    )
                ).parse()
            ).apply {
                refreshFunMap()
                refreshValSet()
                printFunDeclareAssemblies()
                printPrologue()
                printMainAssemblies()
                printEpilogue()
            }
        }

        private fun printEpilogue() {
            println("  mov rsp, rbp #mainのエピローグ")
            println("  pop rbp")
            println("  ret")
        }

        private fun printPrologue() {
            println(".global main #mainのプロローグ")
            println("main:")
            println("  push rbp")
            println("  mov rbp, rsp")
            println("  sub rsp, 208")
            println("")
        }
    }
}

