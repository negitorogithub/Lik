#!/usr/bin/env bash
gdb test
break main
display $rdi
display {int}$rsp