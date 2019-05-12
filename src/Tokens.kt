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
        return add()
    }

    private fun add(): Node {
        var result = multiply()

        loop@ while (innerList.size - 1 > cursor)
            when {
                consume(PLUS) -> innerList[cursor].value?.let {
                    result = Node(PLUS, result, multiply())
                }
                consume(MINUS) -> innerList[cursor].value?.let {
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
                consume(MULTIPLY) -> innerList[cursor].value.let {
                    result = Node(MULTIPLY, result, unary())
                }
                consume(DIVIDE) -> innerList[cursor].value.let {
                    result = Node(DIVIDE, result, unary())
                }
                else -> break@loop

            }
        }
        return result
    }


    private fun term(): Node {
        if (consume(ROUND_BRACKET_OPEN)) {
            val result = add()
            if (!consume(ROUND_BRACKET_CLOSE))
                throw java.lang.Exception("開きカッコに対応する閉じカッコがありません: $cursor")
            return result
        }

        if (innerList[cursor].value != null) {
            return Node(innerList[cursor++])
        }
        throw java.lang.Exception("数字でも()でもないトークンです: $cursor")
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
}