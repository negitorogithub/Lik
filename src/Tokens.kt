import TokenType.*

class Tokens(private val innerList: List<Token>) {

    init {
        if (innerList.isEmpty()) {
            throw IllegalArgumentException("The list is empty")
        }
    }

    var cursor = 0


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
        innerList[cursor].value ?: run {
            throw IllegalArgumentException("The first element must be number")
        }

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
        var result = Node(innerList[cursor].value!!)
        cursor++
        loop@ while (innerList.size - 1 > cursor) {
            when {
                consume(MULTIPLY) -> innerList[cursor].value.let {
                    result = Node(MULTIPLY, result, Node(innerList[cursor++].value!!))
                }
                consume(DIVIDE) -> innerList[cursor].value.let {
                    result = Node(DIVIDE, result, Node(innerList[cursor++].value!!))
                }
                else -> break@loop

            }
        }
        return result
    }
}