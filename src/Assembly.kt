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
            Nodes(
                Tokens(
                    tokenize(
                        buffer.toString()
                    )
                ).parse()
            ).printAssemblies()
            println("  ret")
        }
    }
}

