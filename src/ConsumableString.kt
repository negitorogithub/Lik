data class ConsumableString(var innerString: String) {
    fun consume(str: String): Boolean {
        if (innerString.startsWith(str)) {
            innerString = innerString.removePrefix(str)
            return true
        }
        return false
    }

    fun popNumber(): String {
        if (innerString.isEmpty()) throw Exception("Number not found in head")
        if (numbers.contains(innerString.toCharArray()[0].toString())) {
            val temp = innerString
            innerString = innerString.dropWhile { char: Char -> numbers.contains(char.toString()) }
            return temp.takeWhile { char: Char -> numbers.contains(char.toString()) }
        }
        throw Exception("Number not found in head")
    }

    fun popAlphabets(): String {
        if (innerString.isEmpty()) throw Exception("Alphabet not found in head")
        if (alphabets.contains(innerString.toCharArray()[0])) {
            val temp = innerString
            innerString = innerString.dropWhile { char: Char -> alphabets.contains(char) }
            return temp.takeWhile { char: Char -> alphabets.contains(char) }
        }
        throw Exception("Alphabet not found in head")
    }


    private fun isEmpty(): Boolean {
        return innerString.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return innerString.isNotEmpty()
    }


    fun startWithNumber(): Boolean {
        if (isEmpty()) return false
        return numbers.contains(innerString.toCharArray()[0].toString())
    }

    fun startWithAlphabet(): Boolean {
        if (isEmpty()) return false
        return alphabets.contains(innerString.toCharArray()[0])
    }

    fun isAssignExpression(): Boolean {
        if (isEmpty()) return false
        if (!startWithAlphabet()) return false
        val clone = this.copy(innerString = innerString)
        clone.innerString = clone.innerString.filterNot { it.toString() == space }
        clone.popAlphabets()
        return clone.innerString.startsWith(assign)
    }


    fun consumeReturn(): Boolean {
        if (isEmpty()) return false
        if (!startWithAlphabet()) return false
        val clone = this.copy(innerString = innerString)

        if (clone.consume(return_)) {
            if (clone.consume(space)) {
                consume(return_)
                consume(space)
                return true
            }
        }
        return false
    }

}