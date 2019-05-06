import TokenType.NUMBER

class Token(val type: TokenType, val value: Int? = null) {
    init {
        if (type == NUMBER) {
            value ?: throw IllegalArgumentException("number expected")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Token) {
            return false
        }
        if (other.type != type) {
            return false
        }
        if (other.value != value) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (value ?: 0)
        return result
    }
}
