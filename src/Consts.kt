const val space = " "
const val plus = "+"
const val minus = "-"
const val multiply = "*"
const val divide = "/"
const val roundBracketOpen = "("
const val roundBracketClose = ")"
const val assign = "="
const val equal = "=="
const val notEqual = "!="
const val lessThan = "<"
const val greaterThan = ">"
const val lessThanOrEqual = "<="
const val greaterThanOrEqual = ">="
const val semiColon = ";"
val operators = setOf(plus, minus, multiply, divide)
val numbers = (0..9).map { it.toString() }
val alphabets = listOf('a'..'z', 'A'..'Z').flatten()
