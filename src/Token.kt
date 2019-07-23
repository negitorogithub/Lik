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

    override fun toString(): String {
        val result = mutableListOf<String>()
        result.apply {
            add("[")
            add("tyoe=")
            add(type.toString())
            add(",")
            value?.toString()?.let {
                add("value=")
                add(it)
                add(",")
            }
            val_?.toString()?.let {
                add("val_=")
                add(it)
                add(",")
            }
            funName?.let {
                add("funName=")
                add(it)
            }
            add("}")
        }
        return result.joinToString(separator = "")
    }

}
