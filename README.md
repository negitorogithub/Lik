# Lik
Fun programming language!
https://www.sigbus.info/compilerbook
を参考にkotlinコンパイラを作成中(2020/4/6追記:開発は止まっています)。

経過:https://qiita.com/unifar/items/beb544c60bdf1cfc73dd

# 試し方
Terminal.ktを起動して、kotlinプログラムを入力後、***(アスタリスク3つ)と入力すると、アセンブリコードが出力されます。
今のところ、変数の型はIntのみで、主に使える構文は四則演算,不等号,val(と言いつつも再代入できる),fun,return,class,インクリメントなどです。

# 実行できるスクリプトの例

```kotlin
fun main():Int{return 42}// -> 42
fun main():Int{val ab = 3 return ab} // -> 3
fun fac(n):Int{if(n == 1){return 1} return n * fac(n - 1)} fun main():Int{return fac(5)} // -> 120
fun fib(n):Int{if(n == 1){return 1} if(n == 2){return 1} return fib(n - 1) + fib(n - 2)} fun main():Int{return fib(5)} // -> 5

class A(){
  val a = 42
  fun get42():Int{return a}
}
fun func():A{val a = A()  return a} 
fun main():Int{val a = func() return a.get42()} // -> 42

```

