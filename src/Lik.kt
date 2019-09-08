import TokenType.*

//TODO:while
//TODO:for
//TODO:配列
//TODO:ポインタ
//TODO:else
//TODO:when
//TODO:宣言時の型
//TODO:&&,||

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
                        while (rest.consume(space)) {//TODO: skipSpaces()の追加
                        }
                        add(
                            Token(
                                ARGUMENTS,
                                val_ = Val(rest.popIdentification())
                            )
                        )
                        while (rest.consume(space)) {
                        }
                        rest.consume(comma)
                        while (rest.consume(space)) {
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
            rest.isClassExpression() -> {
                rest.consume(class_)
                rest.consume(space)
                resultList.apply {
                    add(
                        Token(
                            CLASS,
                            className = rest.popIdentification()
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
                                ARGUMENTS,
                                val_ = Val(rest.popIdentification())
                            )
                        )
                        while (rest.consume(space)) {
                        }
                        rest.consume(comma)
                        while (rest.consume(space)) {
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
            rest.isValDeclareAssignExpression() -> {
                rest.consume(val_)
                rest.consume(space)
                resultList.apply {
                    add(
                        Token(
                            NOT_ASSIGNED_VAL,
                            val_ = Val(rest.popIdentification())
                        )
                    )
                    add(Token(DECLARE_AND_ASSIGN_VAL))
                }
                while (rest.consume(space)) {

                }
                rest.consume(assign)
            }
            rest.consume(return_ + space) -> resultList.add(Token(RETURN))
            rest.consume(if_) -> resultList.add(Token(IF))
            rest.consume(while_) -> resultList.add(Token(WHILE))
            rest.startWithNumber() -> resultList.add(Token(Integer.parseInt(rest.popNumber())))//consumeだと数字が特定できないため
            rest.isClassOrFunCallExpression() -> {
                resultList.add(Token(CLASS_OR_FUN_CALL, classOrFunName = rest.popIdentification()))
            }
            rest.consume(comma) -> resultList.add(Token(COMMA))
            rest.consume(dot) -> resultList.add(Token(DOT))
            rest.startWithAlphabet() -> resultList.add(
                Token(
                    ASSIGNED_VAL,
                    val_ = Val(rest.popIdentification())
                )
            )//代入の文脈ではない変数
            rest.consume(colon) -> {
                while (rest.consume(space)) {
                }
                val type = rest.popIdentification()
                resultList.add(
                    Token(TYPE_OF_FUN, typeOfFun = type)
                )
            }
            rest.consume(space) -> {
                //飛ばす
            }
            rest.consume(newLine) -> {
                //飛ばす
            }
            else -> {
                throw Exception("${rest.innerString[0]}は予期せぬ文字です")
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

fun getValInfoByName(valName: String, funScope: String?, classScope: String?): ValInfo? {//TODO:メンバ変数がうまくいってない
    //TODO:変数に抽出
    if (FunLocalValsMap.mapOfVals[funScope]?.find { it.name == valName } != null) {
        return ValInfo(FunLocalValsMap.mapOfVals[funScope]!!.find { it.name == valName }!!, Scope.LOCAL)
    }
    if (FunArgumentsMap.mapOfArguments[funScope]?.find { it.name == valName } != null) {
        return ValInfo(FunArgumentsMap.mapOfArguments[funScope]!!.find { it.name == valName }!!, Scope.ARGUMENT)
    }
    if (ClassMemberValsMap.mapOfVals[classScope]?.find { it.name == valName } != null) {
        return ValInfo(ClassMemberValsMap.mapOfVals[classScope]!!.find { it.name == valName }!!, Scope.MEMBER)
    }
    if (ClassConstructorsMap.mapOfConstructors[classScope]?.find { it.name == valName } != null) {
        return ValInfo(
            ClassConstructorsMap.mapOfConstructors[classScope]!!.find { it.name == valName }!!,
            Scope.CONSTRUCTOR
        )
    }
    return null
}

fun getValOffsetFunByName(valName: String, funScope: String?): Int {
//TODO 関数分離

    val localVals = FunLocalValsMap.mapOfVals[funScope]
    val arguments = FunArgumentsMap.mapOfArguments[funScope]

    val valInLocal = localVals?.find { it.name == valName }
    val isValLocal = valInLocal != null
    if (isValLocal) {
        val offSetOfArguments = arguments!!.size * 8
        val offSetOfValLocal = localVals!!.indexOfFirst { val_: Val -> val_.name == valName } * 8 + 8
        return offSetOfArguments + offSetOfValLocal
    }

    val valInArguments = arguments?.find { it.name == valName }
    val isValArgument = valInArguments != null
    if (isValArgument) {
        return arguments!!.indexOfFirst { val_: Val -> val_.name == valName } * 8 + 8
    }
    throw Exception("変数${valName}は関数${funScope}で解決できませんでした")
}

fun getValOffsetClassByName(valName: String, classScope: String?): Int {

    val classMembers = ClassMemberValsMap.mapOfVals[classScope]
    val constructors = ClassConstructorsMap.mapOfConstructors[classScope]
    val valInClassMembers = classMembers?.find { it.name == valName }
    val isValClassMembers = valInClassMembers != null
    if (isValClassMembers) {
        val offSetOfConstructors = constructors!!.size * 8 - 8
        val offSetOfValMember = classMembers!!.indexOfFirst { val_: Val -> val_.name == valName } * 8 + 8
        return offSetOfConstructors + offSetOfValMember
    }

    val valInConstructors = constructors?.find { it.name == valName }
    val isValConstructor = valInConstructors != null
    if (isValConstructor) {
        return constructors!!.indexOfFirst { val_: Val -> val_.name == valName } * 8
    }
    throw Exception("変数${valName}はクラス${classScope}で解決できませんでした")

}




