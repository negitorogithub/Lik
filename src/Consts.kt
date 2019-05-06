const val space = " "
const val plus = "+"
const val minus = "-"
const val multiply = "*"
const val divide = "/"
val operators = setOf(plus, minus, multiply, divide)
val numbers = setOf(1..9).map { intRange: IntRange -> intRange.toString() }.toSet()
