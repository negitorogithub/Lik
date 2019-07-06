class Assembly {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val buffer = StringBuilder()
            args.forEach {
                buffer.append(it)
            }
            printPrologue()
            Nodes(
                Tokens(
                    tokenize(
                        buffer.toString()
                    )
                ).parse()
            ).apply { refreshValSet() }.printAssemblies()
            printEpilogue()
        }

        private fun printEpilogue() {
            println("  mov rsp, rbp")
            println("  pop rbp")
            println("  ret")
        }

        private fun printPrologue() {
            println(".intel_syntax noprefix")
            println(".global main")
            println("main:")
            println("")

            println("  push rbp")
            println("  mov rbp, rsp")
            println("  sub rsp, 208")
            println("")
        }
    }
}

