#!/bin/bash
try() {
  cd ../asm
  expected="$1"
  input="$2"
  java -jar Assembly.jar "$input" > test.s
  gcc -o test test.s
  ./test
  actual="$?"

  if [[ "$actual" = "$expected" ]]; then
    echo "$input => $actual"
  else
    echo "$expected expected, but got $actual"
    exit 1
  fi
}

kotlinc Assembly.kt -include-runtime -d ../asm/Assembly.jar
try 0 0
try 42 42
try 2+3 5
echo OK