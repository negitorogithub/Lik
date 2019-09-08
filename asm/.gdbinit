break main
display $rax
display {int}$rax
display $rsp
display {int}$rsp
display $rbp
display {int}$rbp
display $rdi
display {int}$rdi

gdb -q -ex 'set disassembly-flavor intel' -ex 'disp/i $pc' test