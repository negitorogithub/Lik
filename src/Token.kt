import TokenType.*

data class Token(
    val type: TokenType,
    val value: Int? = null,
    val val_: Val? = null,
    val funName: String? = null,
    var className: String? = null,
    val classOrFunName: String? = null,
    var typeOfFun: String? = null

) {

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
            add("type=")
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
            className?.let {
                add("className=")
                add(it)
            }
            classOrFunName?.let {
                add("classOrFunName=")
                add(it)
            }
            add("}")
        }
        return result.joinToString(separator = "")
    }

    fun interpretAsFunCall(): Token {
        if (type != CLASS_OR_FUN_CALL) throw Exception("CLASS_OR_FUN_CALL以外のタイプ${type}でinterpretAsFunCallが呼び出されました")
        return copy(type = FUN_CALL, funName = classOrFunName, classOrFunName = null)
    }

    fun interpretAsClassCall(): Token {
        if (type != CLASS_OR_FUN_CALL) throw Exception("CLASS_OR_FUN_CALL以外のタイプ${type}でinterpretAsFunCallが呼び出されました")
        return copy(type = CLASS_CALL, className = classOrFunName, classOrFunName = null)
    }

}
