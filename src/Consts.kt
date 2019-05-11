const val space = " "
const val plus = "+"
const val minus = "-"
const val multiply = "*"
const val divide = "/"
const val roundBracketOpen = "("
const val roundBracketClose = ")"
val operators = setOf(plus, minus, multiply, divide)
val numbers = (0..9).toSet().map { it.toString() }
