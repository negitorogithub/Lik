fun printBuiltIns() {
    printArray()
}

private fun printArray() {
    printArrayInit()
    printArrayAt()
}

fun printArrayAt() {
    println(
        "Array_at:\n" +
                "  push rbp\n" +
                "  mov rbp, rsp\n" +
                "  sub rsp, 8\n" +
                "\n" +
                "  mov rbp, rax #rbpに配列要素0のアドレスを代入\n" +
                "\n" +
                "  mov rax, rbp #変数のアドレスを計算しpush\n" +
                "  sub rax, 8\n" +
                "  push rax\n" +
                "\n" +
                "  mov rdi,rdi #引数に代入し右辺をpush\n" +
                "  pop rax\n" +
                "  mov [rax], rdi\n" +
                "  push rdi\n" +
                "\n" +
                "  mov rax, rbp #変数のアドレスを計算しpush\n" +
                "  sub rax, 8\n" +
                "  push rax\n" +
                "\n" +
                "  pop rax #代入済み変数をpush!\n" +
                "  mov rax, [rax]\n" +
                "  push rax\n" +
                "\n" +
                "  pop rax #リターン\n" +
                "  mov rsp, rbp\n" +
                "  pop rbp\n" +
                "  ret"
    )
}

private fun printArrayInit() {
    println(
        "Array_init:\n" +
                "  #空間サイズをraxに入れる\n" +
                "  push rdi #size引数\n" +
                "  push 8 #要素（整数）一つ当たりのサイズ\n" +
                "  pop rdi\n" +
                "  pop rax\n" +
                "  imul rdi\n" +
                "\n" +
                "  push rbp\n" +
                "  mov rbp, rsp\n" +
                "  sub rsp, rax\n" +
                "\n" +
                "  mov rax, rbp #thisにあたるポインタをraxで返している\n" +
                "  mov rsp, rbp\n" +
                "  pop rbp\n" +
                "  ret"
    )
}