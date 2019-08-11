import TokenType.*

class Tokens(private var innerList: List<Token>) {

    init {
        if (innerList.isEmpty()) {
            throw IllegalArgumentException("The list is empty")
        }
    }

    private var cursor = 0


    private fun consume(tokenType: TokenType): Boolean {

        return if (innerList[cursor].type == tokenType) {
            cursor++
            true
        } else {
            false
        }
    }

    private fun resolveClassOrFunCall() {
        val funNameSet = mutableSetOf<String>()
        val classNameSet = mutableSetOf<String>()
        val resultList = mutableListOf<Token>()
        for (token in innerList) {
            if (token.type == FUN) {
                funNameSet.add(token.funName!!)
            }
            if (token.type == CLASS) {
                classNameSet.add(token.className!!)
            }
        }
        for (token in innerList) {
            resultList.add(
                if (token.type == CLASS_OR_FUN_CALL) {
                    when {
                        funNameSet.contains(token.classOrFunName) -> token.interpretAsFunCall()
                        classNameSet.contains(token.classOrFunName) -> token.interpretAsClassCall()
                        else -> throw  Exception("CLASS_OR_FUN_CALLで呼び出された${token.classOrFunName}は関数呼び出しかインスタンス化か分かりませんでした")
                    }
                } else {
                    token
                }
            )
        }
        innerList = resultList
    }

    fun parse(): List<Node> {
        resolveClassOrFunCall()
        val result = program()
        return result
    }

    private fun program(): List<Node> {
        val result = mutableListOf<Node>()
        while (!hasFinishedReading()) {
            result.add(classes())
        }
        return result
    }

    private fun hasFinishedReading(): Boolean {
        return (cursor >= innerList.lastIndex)
    }

    private fun classes(): Node {
        if (consume(CLASS)) {
            val classToken = innerList[cursor - 1]
            val constructorNode = Node(ARGUMENTS)
            consume(ROUND_BRACKET_OPEN)
            while (innerList[cursor].type == ARGUMENTS) {
                constructorNode.argumentsOnDeclare.add(innerList[cursor].val_!!)
                cursor++
            }
            if (!consume(ROUND_BRACKET_CLOSE)) {
                throw Exception("開きカッコに対応する閉じカッコがありません@cursor=$cursor")
            } else {
                val innerNodes = function()
                return Node(classToken, constructorNode, innerNodes)
            }
        } else {
            return function()
        }
    }

    private fun function(): Node {
        if (consume(FUN)) {
            val funToken = innerList[cursor - 1]
            val argumentsNode = Node(ARGUMENTS)
            consume(ROUND_BRACKET_OPEN)
            while (innerList[cursor].type == ARGUMENTS) {
                argumentsNode.argumentsOnDeclare.add(innerList[cursor].val_!!)
                cursor++
            }
            if (!consume(ROUND_BRACKET_CLOSE)) {
                throw Exception("開きカッコに対応する閉じカッコがありません@cursor=$cursor")
            } else {
                val innerNodes = statement()
                return Node(funToken, argumentsNode, innerNodes)
            }
        } else {
            return statement()
        }

    }

    private fun statement(): Node {

        val result = when {
            consume(RETURN) -> {
                val result = Node(RETURN, null, expression())
                consume(SEMI_COLON)
                return result
            }

            consume(IF) -> {
                consume(ROUND_BRACKET_OPEN)
                val condition = expression()
                if (!consume(ROUND_BRACKET_CLOSE)) {
                    throw java.lang.Exception("開きカッコに対応する閉じカッコがありません: $cursor")
                } else {
                    Node(IF, condition, statement())
                }
            }

            consume(WHILE) -> {
                consume(ROUND_BRACKET_OPEN)
                val condition = expression()
                if (!consume(ROUND_BRACKET_CLOSE)) {
                    throw java.lang.Exception("開きカッコに対応する閉じカッコがありません: $cursor")
                } else {
                    Node(WHILE, condition, statement())
                }
            }

            consume(CURLY_BRACKET_OPEN) -> {
                val resultList = mutableListOf<Node>()
                while (!consume(CURLY_BRACKET_CLOSE)) {
                    resultList.add(function())
                }
                return Node(NODES, nodes = Nodes(resultList))
            }


            else -> {
                val result = expression()
                if (!consume(SEMI_COLON)) {
                    throw Exception("文末にセミコロンがありません")
                } else {
                    result
                }
            }
        }

        return result
    }

    private fun expression(): Node {
        return assign()
    }

    private fun assign(): Node {
        var result = equality()
        if (consume(ASSIGN)) {
            result = Node(ASSIGN, result, assign())
        }
        if (consume(DECLARE_AND_ASSIGN_VAL)) {
            result = Node(DECLARE_AND_ASSIGN_VAL, result, assign())
        }
        return result
    }

    private fun equality(): Node {
        var result = relational()

        loop@ while (innerList.size - 1 > cursor)
            when {
                consume(EQUAL) -> {
                    result = Node(EQUAL, result, relational())
                }
                consume(NOT_EQUAL) -> {
                    result = Node(NOT_EQUAL, result, relational())
                }
                else -> break@loop
            }
        return result
    }

    private fun relational(): Node {
        var result = add()

        loop@ while (innerList.size - 1 > cursor)
            result = when {
                consume(LESS_THAN) -> {
                    Node(LESS_THAN, result, add())
                }
                consume(LESS_THAN_OR_EQUAL) -> {
                    Node(LESS_THAN_OR_EQUAL, result, add())
                }
                consume(GREATER_THAN) -> {
                    Node(GREATER_THAN, result, add())
                }
                consume(GREATER_THAN_OR_EQUAL) -> {
                    Node(GREATER_THAN_OR_EQUAL, result, add())
                }
                else -> break@loop
            }
        return result
    }


    private fun add(): Node {
        var result = multiply()

        loop@ while (innerList.size - 1 > cursor)
            result = when {
                consume(PLUS) -> {
                    Node(PLUS, result, multiply())
                }
                consume(MINUS) -> {
                    Node(MINUS, result, multiply())
                }
                else -> break@loop
            }
        return result
    }

    private fun multiply(): Node {
        var result = unary()
        loop@ while (innerList.size - 1 > cursor) {
            result = when {
                consume(MULTIPLY) -> {
                    Node(MULTIPLY, result, unary())
                }
                consume(DIVIDE) -> {
                    Node(DIVIDE, result, unary())
                }
                else -> break@loop

            }
        }
        return result
    }

    private fun unary(): Node {
        if (consume(PLUS)) {
            return memberAccess()
        }
        if (consume(MINUS)) {
            return Node(MINUS, Node(0), memberAccess())
        }
        return memberAccess()
    }

    private fun memberAccess(): Node {
        var result = term()
        if (consume(DOT)) {
            result = Node(DOT, result, term())
        }
        return result
    }

    private fun term(): Node {
        if (consume(ROUND_BRACKET_OPEN)) {
            val result = expression()
            if (!consume(ROUND_BRACKET_CLOSE))
                throw java.lang.Exception("開きカッコに対応する閉じカッコがありません: $cursor")
            return result
        }

        if (innerList[cursor].value != null) {//数字
            return Node(innerList[cursor++])
        }

        if (innerList[cursor].val_ != null) {//変数
            val result = Node(innerList[cursor++])
            return if (consume(INCREASE)) {
                Node(INCREASE, result, null)
            } else {
                result
            }

        }

        if (consume(FUN_CALL)) {
            val funToken = innerList[cursor - 1].copy(type = FUN_CALL)
            consume(ROUND_BRACKET_OPEN)
            val nodes2add = mutableListOf<Node>()
            while (!consume(ROUND_BRACKET_CLOSE)) {
                nodes2add.add(expression())
                consume(COMMA)
            }
            val argumentsNode = Node(ARGUMENTS, nodes = Nodes(nodes2add))
            return Node(funToken, argumentsNode, null)
        }

        if (consume(CLASS_CALL)) {
            val classToken = innerList[cursor - 1].copy(type = CLASS_CALL)
            consume(ROUND_BRACKET_OPEN)
            val nodes2add = mutableListOf<Node>()
            while (!consume(ROUND_BRACKET_CLOSE)) {
                nodes2add.add(expression())
                consume(COMMA)
            }
            val argumentsNode = Node(ARGUMENTS, nodes = Nodes(nodes2add))
            return Node(classToken, argumentsNode, null)
        }



        throw java.lang.Exception("数字でも()でもないトークンです: $cursor")
    }


}