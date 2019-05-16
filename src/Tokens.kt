import TokenType.*

class Tokens(private val innerList: List<Token>) {

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

    fun parse(): Node {
        val result = expression()
        return result
    }

    private fun expression(): Node {
        return equality()
    }

    private fun equality(): Node {
        var result = relational()

        loop@ while (innerList.size - 1 > cursor)
            when {
                consume(EQUAL) -> {
                    result = Node(EQUAL, result, relational())
                }
                consume(NOT_EQUAL) -> innerList[cursor].value?.let {
                    result = Node(NOT_EQUAL, result, relational())
                }
                else -> break@loop
            }
        return result
    }

    private fun relational(): Node {
        var result = add()

        loop@ while (innerList.size - 1 > cursor)
            when {
                consume(LESS_THAN) -> {
                    result = Node(LESS_THAN, result, add())
                }
                consume(LESS_THAN_OR_EQUAL) -> {
                    result = Node(LESS_THAN_OR_EQUAL, result, add())
                }
                consume(GREATER_THAN) -> {
                    result = Node(GREATER_THAN, result, add())
                }
                consume(GREATER_THAN_OR_EQUAL) -> {
                    result = Node(GREATER_THAN_OR_EQUAL, result, add())
                }
                else -> break@loop
            }
        return result
    }


    private fun add(): Node {
        var result = multiply()

        loop@ while (innerList.size - 1 > cursor)
            when {
                consume(PLUS) -> {
                    result = Node(PLUS, result, multiply())
                }
                consume(MINUS) -> {
                    result = Node(MINUS, result, multiply())
                }
                else -> break@loop
            }
        return result
    }

    private fun multiply(): Node {
        var result = unary()
        loop@ while (innerList.size - 1 > cursor) {
            when {
                consume(MULTIPLY) -> {
                    result = Node(MULTIPLY, result, unary())
                }
                consume(DIVIDE) -> {
                    result = Node(DIVIDE, result, unary())
                }
                else -> break@loop

            }
        }
        return result
    }

    private fun unary(): Node {
        if (consume(PLUS)) {
            return term()
        }
        if (consume(MINUS)) {
            return Node(MINUS, Node(0), term())
        }
        return term()
    }

    private fun term(): Node {
        if (consume(ROUND_BRACKET_OPEN)) {
            val result = expression()
            if (!consume(ROUND_BRACKET_CLOSE))
                throw java.lang.Exception("開きカッコに対応する閉じカッコがありません: $cursor")
            return result
        }

        if (innerList[cursor].value != null) {
            return Node(innerList[cursor++])
        }
        throw java.lang.Exception("数字でも()でもないトークンです: $cursor")
    }


}