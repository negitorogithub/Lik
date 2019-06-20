import TokenType.NUMBER

data class Token(val type: TokenType, val value: Int? = null, val val_: Val? = null, val funName: String? = null) {

    constructor(value: Int) : this(NUMBER, value)

    init {
        if (type == NUMBER) {
            value ?: throw Exception("number expected")
        }
        if (type != NUMBER) {
            if (value != null) {
                throw IllegalArgumentException("number is not expected")
            }
        }

    }

}
