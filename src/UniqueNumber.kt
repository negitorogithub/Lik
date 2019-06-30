class UniqueNumber {
    companion object {
        private var serialNumber = 0
        fun next(): Int {
            return serialNumber++
        }
    }
}