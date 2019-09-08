class Assembly {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val buffer = StringBuilder()
            args.forEach {
                buffer.append(it)
            }
            println(".intel_syntax noprefix")
            println(".global main #mainのプロローグ")
            Nodes(
                Tokens(
                    tokenize(
                        buffer.toString()
                    )
                ).parse()
            ).apply {
                genScopes()
                propagateScopes()
                genValListEach()
                genClassSizeMap()
                genClassNodesTable()
                genFunNodesTable()
                genClassConstructorsMap()
                genClassMemberValsMap()
                genFunArgumentsMap()
                genLocalValsMap()
                genValType()
                genCurrentFunOffsetMap()
                addValTypeAndClassName2AssignedVal()
                setType2FunCall()
                printClassDeclareAssemblies()
                printFunDeclareAssemblies()
            }
        }
    }
}

