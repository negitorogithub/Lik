import TokenType.*

fun main() {
    val input = readLine()
    println(input?.let { parse(it) })
    println(numberList2number(listOf("1", "2")))
}

fun parse(likScript: String): String {
    val tokens = Tokens(tokenize(likScript))
    return Nodes(tokens.parse()).exec().evaledInt.toString()
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
            rest.consume(assign) -> resultList.add(Token(ASSIGN))
            rest.consume(semiColon) -> resultList.add(Token(SEMI_COLON))
            rest.isAssignExpression() -> {
                resultList.apply {
                    add(
                        Token(
                            NOT_ASSIGNED_VAL,
                            val_ = Val(rest.popAlphabets())
                        )
                    )
                    add(Token(ASSIGN))
                }
                rest.consume(assign)
            }
            rest.startWithNumber() -> resultList.add(Token(Integer.parseInt(rest.popNumber())))//consumeだと数字が特定できないため
            rest.startWithAlphabet() -> resultList.add(
                Token(
                    ASSIGNED_VAL,
                    val_ = Val(rest.popAlphabets())
                )
            )//代入の文脈ではない変数
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


fun String.toConsumableString(): ConsumableString {
    return ConsumableString(this)
}



