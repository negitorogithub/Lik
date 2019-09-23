data class Nodes(val innerList: List<Node> = mutableListOf()) {

    val valList: MutableList<Val> = mutableListOf()
    val classSizeMap: LinkedHashMap<String, Int> = linkedMapOf()

    fun printAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.printAssembly()
        }
    }

    fun printAssembliesIf() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.printAssembly()
        }
    }

    fun printFunDeclareAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                node.printAssembly()
            }
        }
    }

    fun printInClassFunDeclareAssemblies(className: String) {
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                node.printInClassFunAssembly(className)
            }
        }
    }

    fun printClassDeclareAssemblies() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                node.printAssembly()
            }
        }
    }

    fun genClassNodesTable() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                ClassNodesTable.mapOfClassNode[node.token.className!!] = node
            }
        }
    }

    fun genFunNodesTable(prefix: String? = null) {
        for (node in innerList) {
            if (node.token.type == TokenType.FUN) {
                FunNodesTable.mapOfFunNode[FunNodesTableName(prefix, node.token.funName!!)] = node
            }
            if (node.token.type == TokenType.CLASS) {
                node.rightNode?.nodes?.genFunNodesTable(node.token.className)
            }
        }
    }

    fun setType2FunCall(prefix: String? = null) {
        innerList.forEach { it.setType2FunCall(prefix) }
    }

    fun setValType2NotAssignedVal() {
        for (node in innerList) {
            node.setValType2NotAssignedVal()
        }
    }

    fun genValList() {
        for (node in innerList) {
            node.genValList()
            valList.addAll(node.valList)
        }
    }


    fun genValListEach() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.genValList()
        }
    }


    fun genClassSizeMap() {
        if (innerList.isEmpty()) throw Exception("NodeListが空です")
        for (node in innerList) {
            node.genClassSizeMap()
            classSizeMap.putAll(node.classSizeMap)
        }
    }

    fun propagateClassSizeMap() {
        for (node in innerList) {
            node.classSizeMap.clear()
            node.classSizeMap.putAll(classSizeMap)
            node.propagateClassSizeMap()
        }
    }

    fun printInClassAssembliesWithoutFun() {
        for (node in innerList) {
            if (node.token.type != TokenType.FUN) {
                node.printAssembly()
                if (node.token.type != TokenType.IF) {
                    println("  pop rax #ノードブロックの結果")
                }
            }
        }
    }

    fun genClassConstructorsMap() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                ClassConstructorsMap.mapOfConstructors[node.token.className!!] =
                    node.leftNode!!.argumentsOnDeclare
            }
        }
    }

    fun genClassMemberValsMap() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                ClassMemberValsMap.mapOfVals[node.token.className!!] =
                    node.rightNode!!.valList
            }
        }
    }

    fun genFunArgumentsMap() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                node.rightNode!!.nodes.genFunArgumentsMap()
            } else if (node.token.type == TokenType.FUN) {
                FunArgumentsMap.mapOfArguments[node.token.funName!!] =
                    node.leftNode!!.argumentsOnDeclare
            }
        }
    }

    fun genLocalValsMap() {
        for (node in innerList) {
            if (node.token.type == TokenType.CLASS) {
                node.rightNode!!.nodes.genLocalValsMap()
            } else if (node.token.type == TokenType.FUN) {
                FunLocalValsMap.mapOfVals[node.token.funName!!] =
                    node.rightNode!!.valList
            }
        }
    }

    fun genScopes() {
        innerList.forEach { it.genScopes() }
    }

    fun propagateScopes() {
        innerList.forEach { it.propagateScopes() }
    }

    fun propagateScopes(funName: String?, className: String?) {
        innerList.forEach { it.propagateScopes(funName, className) }
    }

    fun addValTypeAndClassName2AssignedVal() {
        innerList.forEach { it.addValTypeAndClassName2AssignedVal() }
    }

    fun genCurrentFunOffsetMap() {
        FunNodesTable.mapOfFunNode.values.forEach { node: Node ->
            CurrentFunOffsetMap.offsetMap[node.token.funName!!] = node.getFunOffsetWithoutInstance() + 8
        }
    }

}