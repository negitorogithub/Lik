fun main() {
    val input = readLine()
    println(input?.let { parse(it) })
    println(numberList2number(listOf("1", "2")))
}

fun parse(likScript: String): String {
    val tokens = tokenize(likScript)
    return "default"

}

fun tokenize(str: String): List<Token> {
    val spaceRemoved = str.split(space)
    var temporaryNumberList = mutableListOf<String>()
    val resultList = mutableListOf<Token>()

    spaceRemoved.forEach { char: String ->
        if (!(numbers.contains(char))) {
            resultList.add(Token(TokenType.NUMBER, numberList2number(temporaryNumberList)))
            temporaryNumberList = mutableListOf()
        }
        if (operators.contains(char)) {
            resultList.add(
                when (char) {
                    plus -> Token(TokenType.PLUS)
                    minus -> Token(TokenType.MINUS)
                    multiply -> Token(TokenType.MULTIPLY)
                    divide -> Token(TokenType.DIVIDE)
                    else -> Token(TokenType.NULL)//絶対来ない
                }
            )
            return@forEach
        }
        if (numbers.contains(char)) {
            temporaryNumberList.add(char)
            return@forEach
        }
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



