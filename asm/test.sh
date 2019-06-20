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

cd ../src
kotlinc *.kt -include-runtime -d ../temp

cd ../temp
jar cfm ../asm/Assembly.jar ../asm/MANIFEST.MF *.class

try 0 0\;
try 42 42\;
try 5 2+3\;
try 15 2+3+4+6\;

try 2 5-3\;
try 8 5-3+6\;
try 4 9-3-2\;
echo OK