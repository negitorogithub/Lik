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
    val spaceRemoved = str.filterNot { it.toString() == space }
    val resultList = mutableListOf<Token>()
    val rest = spaceRemoved.toConsumableString()

    while (rest.isNotEmpty()) {
        when {
            rest.consume(roundBracketOpen) -> resultList.add(Token(ROUND_BRACKET_OPEN))
            rest.consume(roundBracketClose) -> resultList.add(Token(ROUND_BRACKET_CLOSE))
            rest.consume(plus) -> resultList.add(Token(PLUS))
            rest.consume(minus) -> resultList.add(Token(MINUS))
            rest.consume(multiply) -> resultList.add(Token(MULTIPLY))
            rest.consume(divide) -> resultList.add(Token(DIVIDE))
            rest.consume(equal) -> resultList.add(Token(EQUAL))
            rest.consume(notEqual) -> resultList.add(Token(NOT_EQUAL))
            rest.consume(lessThanOrEqual) -> resultList.add(Token(LESS_THAN_OR_EQUAL))
            rest.consume(greaterThanOrEqual) -> resultList.add(Token(GREATER_THAN_OR_EQUAL))
            rest.consume(lessThan) -> resultList.add(Token(LESS_THAN))
            rest.consume(greaterThan) -> resultList.add(Token(GREATER_THAN))
            rest.startWithNumber() -> resultList.add(Token(Integer.parseInt(rest.popNumber())))//consumeだと数字が特定できないため
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

fun String.toConsumableString(): ConsumableString {
    return ConsumableString(this)
}



