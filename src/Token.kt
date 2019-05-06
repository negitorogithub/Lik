class Token(type: TokenType, value: Int? = null) {
    init {
        if (type == TokenType.NUMBER) {
            value ?: throw IllegalArgumentException("number expected")
        }
    }
}
