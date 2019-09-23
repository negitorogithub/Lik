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
                genClassNodesTable()
                genFunNodesTable()
                setValType2NotAssignedVal()
                genValListEach()
                genClassConstructorsMap()
                genClassMemberValsMap()
                genFunArgumentsMap()
                genLocalValsMap()
                genClassSizeMap()
                addValTypeAndClassName2AssignedVal()
                setType2FunCall()
                genCurrentFunOffsetMap()
                printClassDeclareAssemblies()
                printFunDeclareAssemblies()
            }
        }
    }
}

