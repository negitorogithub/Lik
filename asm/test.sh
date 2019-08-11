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

try 42 "class A(){val b=42;} fun main(){return A().b;}"
try 6 "class A(){val b=42;val c=6;val d=4;} fun main(){return A().c;}"
try 4 "class A(){val b=42;val c=6;val d=4;} fun main(){return A().d;}"
try 6 "class A(){val b=42;val c=6;val d=4;} fun main(){val c=100; return A().c;}"
try 4 "class A(){val b=42;val c=6;val d=4;} fun main(){val d=111; return A().d;}"

try 42 "class A(){val a=42;} class B(){val b=A().a;} fun main(){return B().b;}"

try 42 "class A(){fun get42(){return 42;}} fun main(){return A().get42()}"

try 42 "class A(){val a = 42; fun get42(){return a;}} fun main(){return A().get42()}"

try 42 "class A(){val a = 42; fun get42(){return a;}} fun main(){return A().get42()}"

try 42 "class A(){val a = 42; fun get42(){return a;}} fun main(){return A().get42()}"

try 0 "fun main(){return 0;}"
try 42 "fun main(){return 42;}"
try 5 "fun main() {return 2+3;}"
try 15 "fun main(){return 2+3+4+6;}"

try 2 "fun main(){return 5-3;}"
try 8 "fun main(){return 5-3+6;}"
try 4 "fun main(){return 9-3-2;}"

try 10 "fun main(){return 2*5;}"
try 13 "fun main(){return 2*5+3;}"
try 6 "fun main(){return 2*5-4;}"
try 40 "fun main(){return 2*5*4;}"
try 136 "fun main(){return 2*5+3*6*7;}"

try 4 "fun main(){return 8/2;}"
try 9 "fun main(){return 4+10/2;}"
try 5 "fun main(){return 2*5/2;}"
try 2 "fun main(){return 5/2;}"
try 100 "fun main(){return 2*5*3/3*2*5;}"

try 4 "fun main(){return -2+6;}"
try 2 "fun main(){return -2-6+10;}"
try 5 "fun main(){return -2*6+17;}"

try 1 "fun main(){return 3==3;}"
try 0 "fun main(){return 3==4;}"
try 1 "fun main(){return 5==3+2;}"
try 1 "fun main(){return 100==2*5*3/3*2*5;}"

try 3 "fun main(){val ab=3;return ab;}"
try 4 "fun main(){val ab=3;return ab+1;}"
try 12 "fun main(){val ab=3;return ab*4;}"
try 15 "fun main(){val ab=3;val cd=5;return ab*cd;}"
try 2 "fun main(){val ab=3;val cd=5;return cd-ab;}"

try 4 "fun main(){return 4;}"
try 1 "fun main(){return 1; return 2;3;4;}"
try 1 "fun main(){3;4;return 1;return 2;}"
try 2 "fun main(){val ab=3;val cd=5;return cd-ab; return 6; return 7;}"

try 3 "fun main(){val ab=3;val cd=5;if(ab==cd)return 2;return 3;}"
try 2 "fun main(){val ab=3;val cd=5;if(ab!=cd)return 2;return 3;}"
try 2 "fun main(){val ab=3;val cd=5;if(ab<cd)return 2;return 3;}"
try 3 "fun main(){val ab=3;val cd=5;if(ab>cd)return 2;return 3;}"
try 3 "fun main(){val ab=3;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main(){val ab=6;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main(){val ab=5;val cd=5;if(ab>=cd)return 2;return 3;}"
try 2 "fun main(){val ab=4;val cd=5;if(ab<=cd)return 2;return 3;}"
try 3 "fun main(){val ab=6;val cd=5;if(ab<=cd)return 2;return 3;}"
try 2 "fun main(){val ab=5;val cd=5;if(ab<=cd)return 2;return 3;}"

try 3 "fun main(){{return 3; return 4}return 5;}"
try 2 "fun main(){val ab=5;val cd=5;if(ab==cd){return 2;return 3;}return 5;}"

try 3 "fun get3(){return 3;} fun main(){return get3();}"
try 3 "fun get3(){return 3; return 4;} fun main(){return get3();}"
try 3 "fun get3(){val ab=6;val cd=5;if(ab<=cd)return 2;return 3;} fun main(){return get3();}"
try 3 "fun get3(){val ab=2;val cd=5;return cd-ab; return 6; return 7;} fun main(){return get3();}"

try 3 "fun id(a){return a;} fun main(){return id(3);}"
try 7 "fun add(a,b){return a+b;} fun main(){return add(3, 4);}"
try 12 "fun add5(a,b){val c=5; return a+b+c;} fun main(){return add5(3, 4);}"
try 21 "fun addAll(a,b,c,d,e,f){return a+b+c+d+e+f;} fun main(){return addAll(1,2,3,4,5,6);}"
try 120 "fun mulAll(a,b,c,d,e){return a*b*c*d*e;} fun main(){return mulAll(1,2,3,4,5);}"
try 1 "fun is5(n){if(n == 5){return 1;} return 2; return 3;} fun main(){return is5(5);}"
try 2 "fun is5(n){if(n == 5){return 1;} return 2; return 3;} fun main(){return is5(14);}"
try 100 "fun id5(n){if((n == 5)){return 100;} return n; return 2;} fun main(){return id5(5);}"
try 20 "fun id5(n){if((n == 5)){return 100;} return n; return 2;} fun main(){return id5(20);}"
try 5 "fun get4(){return 4;} fun get5(){return get4() + 1;} fun main(){return get5();}"
try 7 "fun get3(){return 3;} fun getPlus3(n){return get3() + n;} fun main(){return getPlus3(4);}"
try 7 "fun id(n){return n;} fun add(a,b){return id(a) + id(b);} fun main(){return add(3,4);}"
try 7 "fun id(a){return a;} fun add(a,b){return id(a) + id(b);} fun main(){return add(3,4);}"
try 1 "fun sum(n){if(n == 1){return 1;} return n + sum(n-1);} fun main(){return sum(1);}"
try 3 "fun sum(n){if(n == 1){return 1;} return n + sum(n-1);} fun main(){return sum(2);}"
try 6 "fun sum(n){if(n == 1){return 1;} return n + sum(n-1);} fun main(){return sum(3);}"
try 10 "fun sum(n){if(n == 1){return 1;} return n + sum(n-1);} fun main(){return sum(4);}"
try 15 "fun sum(n){if(n == 1){return 1;} return n + sum(n-1);} fun main(){return sum(5);}"
try 120 "fun fac(n){if(n == 1){return 1;} return n*fac(n-1);} fun main(){return fac(5);}"
try 1 "fun fib(n){if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main(){return fib(1);}"
try 1 "fun fib(n){if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main(){return fib(2);}"
try 2 "fun fib(n){if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main(){return fib(3);}"
try 3 "fun fib(n){if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main(){return fib(4);}"
try 5 "fun fib(n){if(n == 1){return 1;}if(n == 2){return 1;} return fib(n-1) + fib(n-2);} fun main(){return fib(5);}"

try 5 "fun assign(){val n=2;n=3;return n;} fun main(){val n=5; assign(); return n;}"
try 5 "fun reassign(){val n=5;n=6;return n;} fun assign(){val n=2;n=3;return n;}fun main(){val n=5; assign(); reassign(); return n;}"
try 5 "fun main(){val n=5; assign(); reassign(); return n;}fun reassign(){val n=5;n=6;return n;} fun assign(){val n=2;n=3;return n;}"
try 5 "fun main(){val n=5; assign(); reassign(); return n;}fun assign(){val n=2;n=3;return n;}fun reassign(){val n=5;n=6;return n;}"
try 5 "fun reassign(){val n=5;n=6;return n;} fun main(){val n=5; assign(); reassign(); return n;}fun assign(){val n=2;n=3;return n;}"
try 5 "fun assign(){val n=2;n=3;return n;}fun main(){val n=5; assign(); reassign(); return n;}fun reassign(){val n=5;n=6;return n;} "
try 5 "fun assign(){val n=2;n=3;return n;}fun reassign(){val n=5;n=6;return n;}fun main(){val n=5; assign(); reassign(); return n;} "

echo OK
