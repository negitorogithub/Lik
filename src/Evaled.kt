class Evaled(
    val type: EvaledType,
    val evaledInt: Int? = null,
    val evaledBool: Boolean? = null,
    val val2assign: Val? = null
//TODO:値が有るときの禁則処理
) {
    constructor(evaledInt: Int) : this(EvaledType.INT, evaledInt)
    constructor(evaledBool: Boolean) : this(EvaledType.BOOL, evaledBool = evaledBool)
    constructor(val2assign: Val) : this(EvaledType.NOT_ASSIGNED_VAL, val2assign = val2assign)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Evaled

        if (type != other.type) return false
        if (evaledInt != other.evaledInt) return false
        if (evaledBool != other.evaledBool) return false
        if (val2assign != other.val2assign) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (evaledInt ?: 0)
        result = 31 * result + (evaledBool?.hashCode() ?: 0)
        result = 31 * result + (val2assign?.hashCode() ?: 0)
        return result
    }
}

fun Int.toEvaled(): Evaled {
    return Evaled(this)
}

fun Boolean.toEvaled(): Evaled {
    return Evaled(this)
}


operator fun Evaled.plus(other: Evaled): Evaled {
    return (evaledInt!! + other.evaledInt!!).toEvaled()
}

operator fun Evaled.minus(other: Evaled): Evaled {
    return (evaledInt!! - other.evaledInt!!).toEvaled()
}

operator fun Evaled.times(other: Evaled): Evaled {
    return (evaledInt!! * other.evaledInt!!).toEvaled()
}

operator fun Evaled.div(other: Evaled): Evaled {
    return (evaledInt!! / other.evaledInt!!).toEvaled()
}




