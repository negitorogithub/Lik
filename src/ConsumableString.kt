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
        if (numbers.contains(innerString.toCharArray()[0])) {
            val temp = innerString
            innerString = innerString.dropWhile { char: Char -> numbers.contains(char) }
            return temp.takeWhile { char: Char -> numbers.contains(char) }
        }
        throw Exception("Number not found in head")
    }

    fun popIdentification(): String {//先頭はアルファベット
        if (innerString.isEmpty()) throw Exception("string is empty")
        if (alphabets.contains(innerString.toCharArray()[0])) {
            val temp = innerString
            innerString = innerString.dropWhile { char: Char ->
                (alphabets.contains(char) || numbers.contains(char))
            }
            return temp.takeWhile { char: Char ->
                (alphabets.contains(char) || numbers.contains(char))
            }
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
        return numbers.contains(innerString.toCharArray()[0])
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
        clone.popIdentification()
        if (clone.innerString.startsWith(equal)) {
            return false
        }
        return clone.innerString.startsWith(assign)
    }

    //class name
    //ならtrue
    fun isClassExpression(): Boolean {
        if (isEmpty()) return false
        if (!startWithAlphabet()) return false
        val clone = this.copy(innerString = innerString)
        if (!clone.consume(class_)) return false
        if (!clone.consume(space)) return false
        if (clone.popIdentification().isEmpty()) return false
        return true
    }

    fun isArgumentExpression(): Boolean {
        //関数宣言から(を取った後の形かどうか
        if (isEmpty()) return false
        val clone = this.copy(innerString = innerString)
        clone.innerString = clone.innerString.filterNot { it.toString() == space }
        return if (clone.startWithAlphabet()) {
            clone.popIdentification()
            (clone.innerString.startsWith(roundBracketClose) || clone.innerString.startsWith(comma))
        } else {
            clone.innerString.startsWith(roundBracketClose)
        }
    }

    fun isFunExpression(): Boolean {
        if (isEmpty()) return false
        if (!startWithAlphabet()) return false
        val clone = this.copy(innerString = innerString)
        if (!clone.consume(fun_)) return false
        if (!clone.consume(space)) return false
        clone.popIdentification()
        return clone.innerString.startsWith(roundBracketOpen)
    }

    fun hasNextArgument(): Boolean {
        if (isEmpty()) return false
        val clone = this.copy(innerString = innerString)
        clone.innerString = clone.innerString.filterNot { it.toString() == space }
        return clone.startWithAlphabet()
    }

    fun isClassOrFunCallExpression(): Boolean {
        if (isEmpty()) return false
        if (!startWithAlphabet()) return false
        val clone = this.copy(innerString = innerString)
        clone.popIdentification()
        return clone.innerString.startsWith(roundBracketOpen)
    }
}