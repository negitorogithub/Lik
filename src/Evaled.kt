data class Evaled(
    val type: EvaledType,
    val evaledInt: Int? = null,
    val evaledBool: Boolean? = null,
    val val2assign: Val? = null
//TODO:値が有るときの禁則処理
) {
    constructor(evaledInt: Int) : this(EvaledType.INT, evaledInt)
    constructor(evaledBool: Boolean) : this(EvaledType.BOOL, evaledBool = evaledBool)
    constructor(val2assign: Val) : this(EvaledType.NOT_ASSIGNED_VAL, val2assign = val2assign)

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




