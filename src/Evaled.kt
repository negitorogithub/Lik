class Evaled(val type: EvaledType, val evaledInt: Int? = null, val evaledBool: Boolean? = null) {
    constructor(evaledInt: Int) : this(EvaledType.INT, evaledInt)
    constructor(evaledBool: Boolean) : this(EvaledType.BOOL, evaledBool = evaledBool)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Evaled

        if (type != other.type) return false
        if (evaledInt != other.evaledInt) return false
        if (evaledBool != other.evaledBool) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (evaledInt ?: 0)
        result = 31 * result + (evaledBool?.hashCode() ?: 0)
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




