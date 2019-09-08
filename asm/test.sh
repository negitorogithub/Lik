#!/bin/bash
try() {
  cd ../asm || exit
  expected="$1"
  input="$2"
  java -jar Assembly.jar "$input" >test.s
  gcc -o test test.s
  ./test
  actual="$?"

  if [[ "$actual" == "$expected" ]]; then
    echo "$input => $actual"
  else
    echo "$expected expected, but got $actual"
    echo "at $input"
    exit 1
  fi
}

cd ../src || exit
kotlinc *.kt -include-runtime -d ../temp

cd ../temp || exit
jar cfm ../asm/Assembly.jar ../asm/MANIFEST.MF *.class

try 0 "fun main():Int{return 0;}"
try 42 "fun main():Int{return 42;}"
try 5 "fun main():Int {return 2+3;}"
try 15 "fun main():Int{return 2+3+4+6;}"

try 2 "fun main():Int{return 5-3;}"
try 8 "fun main():Int{return 5-3+6;}"
try 4 "fun main():Int{return 9-3-2;}"

try 10 "fun main():Int{return 2*5;}"
try 13 "fun main():Int{return 2*5+3;}"
try 6 "fun main():Int{return 2*5-4;}"
try 40 "fun main():Int{return 2*5*4;}"
try 136 "fun main():Int{return 2*5+3*6*7;}"

try 4 "fun main():Int{return 8/2;}"
try 9 "fun main():Int{return 4+10/2;}"
try 5 "fun main():Int{return 2*5/2;}"
try 2 "fun main():Int{return 5/2;}"
try 100 "fun main():Int{return 2*5*3/3*2*5;}"

try 4 "fun main():Int{return -2+6;}"
try 2 "fun main():Int{return -2-6+10;}"
try 5 "fun main():Int{return -2*6+17;}"

try 1 "fun main():Int{return 3==3;}"
try 0 "fun main():Int{return 3==4;}"
try 1 "fun main():Int{return 5==3+2;}"
try 1 "fun main():Int{return 100==2*5*3/3*2*5;}"

try 3 "fun main():Int{val ab=3;return ab;}"
try 4 "fun main():Int{val ab=3;return ab+1;}"
try 12 "fun main():Int{val ab=3;return ab*4;}"
try 15 "fun main():Int{val ab=3;val cd=5;return ab*cd;}"
try 2 "fun main():Int{val ab=3;val cd=5;return cd-ab;}"

try 4 "fun main():Int{return 4;}"
try 1 "fun main():Int{return 1; return 2;3;4;}"
try 1 "fun main():Int{3;4;return 1;return 2;}"
try 2 "fun main():Int{val ab=3;val cd=5;return cd-ab; return 6; return 7;}"

try 3 "fun main():Int{val ab=3;val cd=5;if(ab==cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=3;val cd=5;if(ab!=cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=3;val cd=5;if(ab<cd)return 2;return 3;}"
try 3 "fun main():Int{val ab=3;val cd=5;if(ab>cd)return 2;return 3;}"
try 3 "fun main():Int{val ab=3;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=6;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=5;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=4;val cd=5;if(ab<=cd)return 2;return 3;}"
try 3 "fun main():Int{val ab=6;val cd=5;if(ab<=cd)return 2;return 3;}"
try 2 "fun main():Int{val ab=5;val cd=5;if(ab<=cd)return 2;return 3;}"

try 3 "fun main():Int{{return 3; return 4}return 5;}"
try 2 "fun main():Int{val ab=5;val cd=5;if(ab==cd){return 2;return 3;}return 5;}"

try 3 "fun get3():Int{return 3;} fun main():Int{return get3();}"
try 3 "fun get3():Int{return 3; return 4;} fun main():Int{return get3();}"
try 3 "fun get3():Int{val ab=6;val cd=5;if(ab<=cd)return 2;return 3;} fun main():Int{return get3();}"
try 3 "fun get3():Int{val ab=2;val cd=5;return cd-ab; return 6; return 7;} fun main():Int{return get3();}"

try 3 "fun id(a):Int{return a;} fun main():Int{return id(3);}"
try 7 "fun add(a,b):Int{return a+b;} fun main():Int{return add(3, 4);}"
try 12 "fun add5(a,b):Int{val c=5; return a+b+c;} fun main():Int{return add5(3, 4);}"
try 21 "fun addAll(a,b,c,d,e,f):Int{return a+b+c+d+e+f;} fun main():Int{return addAll(1,2,3,4,5,6);}"
try 120 "fun mulAll(a,b,c,d,e):Int{return a*b*c*d*e;} fun main():Int{return mulAll(1,2,3,4,5);}"
try 1 "fun is5(n){if(n == 5){return 1;} return 2; return 3;} fun main():Int{return is5(5);}"
try 2 "fun is5(n){if(n == 5){return 1;} return 2; return 3;} fun main():Int{return is5(14);}"
try 100 "fun id5(n):Int{if((n == 5)){return 100;} return n; return 2;} fun main():Int{return id5(5);}"
try 20 "fun id5(n):Int{if((n == 5)){return 100;} return n; return 2;} fun main():Int{return id5(20);}"
try 5 "fun get4():Int{return 4;} fun get5(){return get4() + 1;} fun main():Int{return get5();}"
try 7 "fun get3():Int{return 3;} fun getPlus3(n){return get3() + n;} fun main():Int{return getPlus3(4);}"
try 7 "fun id(n):Int{return n;} fun add(a,b){return id(a) + id(b);} fun main():Int{return add(3,4);}"
try 7 "fun id(a):Int{return a;} fun add(a,b){return id(a) + id(b);} fun main():Int{return add(3,4);}"
try 1 "fun sum(n):Int{if(n == 1){return 1;} return n + sum(n-1);} fun main():Int{return sum(1);}"
try 3 "fun sum(n):Int{if(n == 1){return 1;} return n + sum(n-1);} fun main():Int{return sum(2);}"
try 6 "fun sum(n):Int{if(n == 1){return 1;} return n + sum(n-1);} fun main():Int{return sum(3);}"
try 10 "fun sum(n):Int{if(n == 1){return 1;} return n + sum(n-1);} fun main():Int{return sum(4);}"
try 15 "fun sum(n):Int{if(n == 1){return 1;} return n + sum(n-1);} fun main():Int{return sum(5);}"
try 120 "fun fac(n):Int{if(n == 1){return 1;} return n*fac(n-1);} fun main():Int{return fac(5);}"
try 1 "fun fib(n):Int{if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main():Int{return fib(1);}"
try 1 "fun fib(n):Int{if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main():Int{return fib(2);}"
try 2 "fun fib(n):Int{if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main():Int{return fib(3);}"
try 3 "fun fib(n):Int{if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main():Int{return fib(4);}"
try 5 "fun fib(n):Int{if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main():Int{return fib(5);}"

try 5 "fun assign():Int{val n=2;n=3;return n;} fun main():Int{val n=5; assign(); return n;}"
try 5 "fun reassign():Int{val n=5;n=6;return n;} fun assign():Int{val n=2;n=3;return n;}fun main():Int{val n=5; assign(); reassign(); return n;}"
try 5 "fun main():Int{val n=5; assign(); reassign(); return n;}fun reassign():Int{val n=5;n=6;return n;} fun assign():Int{val n=2;n=3;return n;}"
try 5 "fun main():Int{val n=5; assign(); reassign(); return n;}fun assign():Int{val n=2;n=3;return n;}fun reassign():Int{val n=5;n=6;return n;}"
try 5 "fun reassign():Int{val n=5;n=6;return n;} fun main():Int{val n=5; assign(); reassign(); return n;}fun assign():Int{val n=2;n=3;return n;}"
try 5 "fun assign():Int{val n=2;n=3;return n;}fun main():Int{val n=5; assign(); reassign(); return n;}fun reassign():Int{val n=5;n=6;return n;} "
try 5 "fun assign():Int{val n=2;n=3;return n;}fun reassign():Int{val n=5;n=6;return n;}fun main():Int{val n=5; assign(); reassign(); return n;} "

try 42 "class A(){fun get42():Int{return 42;}} fun main():Int{val a = A(); return a.get42();}"

try 42 "class A(){val b = 42;} fun main():Int{val a = A(); return a.b}"

try 42 "class A(){val a = 42;} fun main():Int{val a = A(); return a.a}"

try 42 "class A(){val a = 42; fun get42():Int{return a;}} fun main():Int{val a = A(); return a.get42()}"

try 42 "class A(){val a = 42; fun get42():Int{return a;}} fun main():Int{return A().get42();}"

try 42 "class A(){val a=42;} class B(){val b=A().a;} fun main():Int{return B().b;}"

try 42 "class A(){fun get42():Int{return 42;}} fun main():Int{return A().get42();}"

try 42 "fun main():Int{val a = 41; a = a + 1; return a}"

try 42 "class A(){val b = 41; fun inc(){b = b + 1;} fun get42():Int{return b;}} fun main():Int{val a = A(); a.inc(); return a.get42()}"

try 42 "class A(){val a = 41; fun inc(){a = a + 1;} fun get42():Int{return a;}} fun main():Int{val a = A(); a.inc(); return a.get42()}"

try 42 "class A(n){fun get42():Int{return n;}} fun main():Int{return A(42).get42();}"

try 42 "class A(n){fun get42():Int{return n;}} fun main():Int{a = A(42); return a.get42();}"

try 42 "class A(n){a = 4; b = 36; fun get42():Int{return n;}} fun main():Int{a = A(42); return a.get42();}"

try 42 "class A(n){a = 4; b = 36; fun get42():Int{return a + b + n;}} fun main():Int{a = A(2); return a.get42();}"

try 42 "class A(n){a = 4; b = 36;fun get42():Int{c = 1; return a + b + c + n;}} fun main():Int{a = A(1); return a.get42();}"

try 42 "class A(a,b,c,d,e,f){fun get42():Int{return a + b + c + d + e + f;}} fun main():Int{a = A(1,2,3,4,5,27); return a.get42();}"

try 42 "class A(n){fun add(b):Int{return n + b;}} fun main():Int{a = A(27); return a.add(15);}"

try 42 "class A(a){fun add(b):Int{return a + b;}} fun main():Int{a = A(27); return a.add(15);}"

try 42 "class A(a){fun add(b):Int{c = 5; return a + b + c;}} fun main():Int{a = A(27); return a.add(10);}"

try 42 "class A(){val b=42;} fun main():Int{return A().b;}"
try 6 "class A(){val b=42;val c=6;val d=4;} fun main():Int{return A().c;}"
try 4 "class A(){val b=42;val c=6;val d=4;} fun main():Int{return A().d;}"
try 6 "class A(){val b=42;val c=6;val d=4;} fun main():Int{val c=100; return A().c;}"
try 4 "class A(){val b=42;val c=6;val d=4;} fun main():Int{val d=111; return A().d;}"

try 42 "class A(){val b=42;} fun main():Int{val a = A(); b = 1 + 2 + 3 + 4 + 5 + 6 + 7;return A().b;}"

echo OK
