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

try 10 2*5\;
try 13 2*5+3\;
try 6 2*5-4\;
try 40 2*5*4\;
try 136 2*5+3*6*7\;

try 4 8/2\;
try 9 4+10/2\;
try 5 2*5/2\;
try 2 5/2\;
try 100 2*5*3/3*2*5\;

try 4 -2+6\;
try 2 -2-6+10\;
try 5 -2*6+17\;

try 1 3==3\;
try 0 3==4\;
try 1 5==3+2\;
try 1 100==2*5*3/3*2*5\;

try 3 ab=3\;ab\;
try 4 ab=3\;ab+1\;
try 12 ab=3\;ab*4\;
try 15 ab=3\;cd=5\;ab*cd\;
try 2 ab=3\;cd=5\;cd-ab\;

try 4 "return 4;"
try 1 "return 1; return 2;3;4;"
try 1 "3;4;return 1;return 2;"
try 2 "ab=3;cd=5;return cd-ab; return 6; return 7;"

echo OK