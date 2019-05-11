import TokenType.*

fun main() {
    val input = readLine()
    println(input?.let { parse(it) })
    println(numberList2number(listOf("1", "2")))
}

fun parse(likScript: String): String {
    val tokens = Tokens(tokenize(likScript))
    tokens.parse()
    return "default"
}

fun tokenize(str: String): List<Token> {
    val spaceRemoved = str.toCharArray().map { it.toString() }.filterNot { it == space }
    var temporaryNumberList = mutableListOf<String>()
    val resultList = mutableListOf<Token>()

    spaceRemoved.forEach { char: String ->
        if (!(numbers.contains(char))) {
            if (temporaryNumberList.isNotEmpty()) {
                resultList.add(Token(NUMBER, numberList2number(temporaryNumberList)))
                temporaryNumberList = mutableListOf()
            }
        }
        if (operators.contains(char)) {
            resultList.add(
                when (char) {
                    plus -> Token(PLUS)
                    minus -> Token(MINUS)
                    multiply -> Token(MULTIPLY)
                    divide -> Token(DIVIDE)
                    roundBracketOpen -> Token(ROUND_BRACKET_OPEN)
                    roundBracketClose -> Token(ROUND_BRACKET_CLOSE)
                    else -> Token(NULL)//絶対来ない
                }
            )
            return@forEach
        }
        if (numbers.contains(char)) {
            temporaryNumberList.add(char)
            return@forEach
        }
    }
    if (temporaryNumberList.isNotEmpty()) {
        resultList.add(Token(NUMBER, numberList2number(temporaryNumberList)))
    }
    return resultList
}

fun numberList2number(list: List<String>): Int {
    val buffer = StringBuilder()
    list.forEach {
        buffer.append(it)
    }
    return Integer.parseInt(buffer.toString())
}

fun parseAdd(innerList: List<Token>): Int? {
    var cursor = 0
    innerList[cursor].value ?: run {
        throw IllegalArgumentException("first element must be number")
    }

    var result = innerList[cursor].value!!

    cursor++
    while (innerList.size - 1 > cursor)
        if (innerList[cursor].type == PLUS) {
            cursor++
            innerList[cursor].value?.let {
                result += it
                cursor++
            }
        } else if (innerList[cursor].type == MINUS) {
            cursor++
            innerList[cursor].value?.let {
                result -= it
                cursor++
            }
        }
    return result
}









