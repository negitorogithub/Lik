import TokenType.*

fun main() {
    while (true) {

        val inputs = mutableListOf<String>()
        while (true) {
            val input = readLine()
            if (input == "***") break
            input?.let { inputs.add(it) }
        }
        val buffer = StringBuilder()
        inputs.forEach {
            buffer.append(it)
        }
        println(parse(buffer.toString()))
    }
}

fun parse(likScript: String): String {
    val tokens = Tokens(tokenize(likScript))
    return Nodes(tokens.parse()).exec().evaledInt.toString()
}

fun tokenize(str: String): List<Token> {
    val resultList = mutableListOf<Token>()
    val rest = str.toConsumableString()

    while (rest.isNotEmpty()) {
        when {
            rest.consume(roundBracketOpen) -> resultList.add(Token(ROUND_BRACKET_OPEN))
            rest.consume(roundBracketClose) -> resultList.add(Token(ROUND_BRACKET_CLOSE))
            rest.consume(curlyBracketOpen) -> resultList.add(Token(CURLY_BRACKET_OPEN))
            rest.consume(curlyBracketClose) -> resultList.add(Token(CURLY_BRACKET_CLOSE))
            rest.consume(increase) -> resultList.add(Token(INCREASE))
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
            rest.consume(semiColon) -> resultList.add(Token(SEMI_COLON))
            rest.isFunExpression() -> {
                rest.consume(fun_)
                rest.consume(space)
                resultList.apply {
                    add(
                        Token(
                            FUN,
                            funName = rest.popIdentification()
                        )
                    )
                    rest.consume(roundBracketOpen)
                    add(
                        Token(
                            ROUND_BRACKET_OPEN
                        )
                    )
                    while (rest.hasNextArgument()) {
                        while (rest.consume(space)) {
                        }
                        add(
                            Token(
                                ARGUMENT,
                                val_ = Val(rest.popIdentification())
                            )
                        )
                        while (rest.consume(space)) {
                        }
                        if (!rest.consume(comma)) {
                            break
                        }
                    }
                    rest.consume(roundBracketClose)
                    add(
                        Token(
                            ROUND_BRACKET_CLOSE
                        )
                    )
                }
            }

            rest.isAssignExpression() -> {
                resultList.apply {
                    add(
                        Token(
                            NOT_ASSIGNED_VAL,
                            val_ = Val(rest.popIdentification())
                        )
                    )
                    add(Token(ASSIGN))
                }

                while (rest.consume(space)) {

                }
                rest.consume(assign)
            }
            rest.consume(return_ + space) -> resultList.add(Token(RETURN))
            rest.consume(if_) -> resultList.add(Token(IF))
            rest.consume(while_) -> resultList.add(Token(WHILE))
            rest.startWithNumber() -> resultList.add(Token(Integer.parseInt(rest.popNumber())))//consumeだと数字が特定できないため
            rest.isFunCallExpression() -> {
                resultList.apply {
                    add(Token(FUN_CALL, funName = rest.popIdentification()))
                }
            }
            rest.consume(comma) -> resultList.add(Token(COMMA))
            rest.startWithAlphabet() -> resultList.add(
                Token(
                    ASSIGNED_VAL,
                    val_ = Val(rest.popIdentification())
                )
            )//代入の文脈ではない変数
            rest.consume(space) -> {
                //飛ばす
            }
            rest.consume(newLine) -> {
                //飛ばす
            }
            else -> {
                throw Exception("予期せぬ文字です")
            }
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



