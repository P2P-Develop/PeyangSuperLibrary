<h1 align="center">PeyangSuperLibrary</h1>

<p align="center">
    <a href="https://search.maven.org/search?q=g:%22tokyo.peya.lib%22%20AND%20a:%22PeyangSuperLibrary">
        <img alt="Maven Central" src="https://img.shields.io/maven-central/v/tokyo.peya.lib/PeyangSuperLibrary.svg?label=Maven%20Central&style=flat-square">
    </a>
    <img alt="GitHub Workflow Status" src="https://img.shields.io/github/workflow/status/P2P-Develop/PeyangSuperLibrary/Java%20CI%20with%20Maven?style=flat-square">
    <a href="https://www.codacy.com/gh/P2P-Develop/PeyangSuperLibrary/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=P2P-Develop/PeyangSuperLibrary&amp;utm_campaign=Badge_Grade">
        <img alt="Codacy grade" src="https://img.shields.io/codacy/grade/2e4e46dd3db54b23843fba42e471aa72?logo=codacy&style=flat-square">
    </a>
    <img alt="GitHub" src="https://img.shields.io/github/license/P2P-Develop/PeyangSuperLibrary?style=flat-square">
    <img alt="Java version" src="https://img.shields.io/static/v1?label=Java%20version&message=1.8&color=success&style=flat-square">
</p>

<p align="center">よく使うものまとめたやつ(願望</p>

---
# 導入方法
+ Maven
```xml
<dependency>
  <groupId>tokyo.peya.lib</groupId>
  <artifactId>PeyangSuperLibrary</artifactId>
  <version>114.191.9.810</version>
</dependency>
```
+ Gradle
```js
implementation 'tokyo.peya.lib:PeyangSuperLibrary:114.191.9.810'
implementation("tokyo.peya.lib:PeyangSuperLibrary:114.191.9")
```

# ドキュメント
+ [JavaDoc](https://lib.peya.tokyo/)

# はいってるもの

+ EntitySelector  
  Bukkit 1.12.2くらいで`@e`とか`@a[name=SaikyouPeyangsan]` を使える。
+ Say2Functional  
  プレイヤーに「続行しますか？y/N>」見たいのをつけられる。コンソールにも対応。
+ ItemUtils  
  引っ語りするやつを簡単につけられる。
+ ExceptionUtils  
  ExceptionのスタックトレースをStringにできる
+ LearnMath  
  機械学習用の高度な計算を提供する。
+ LeetConverter  
  入れたやつを何でもかんでも133Tにしてくれる。
+ Intellij  
  Intellijでデバッグしているかどうかを判定
+ TimeParser  
  `1y 1mo 4d 5h 1m 4s` を Date@\(1year,2months,4days,5hours,1minute,5seconds\) に変換する。  
  相互変換可能。
+ WaveCreator  
  波を生成する。
+ SQLModifier  
  SQL文を書く必要なく、簡単にinsertとかできるようになる。
+ FileConfiguration  
  ymlファイルをコンフィグとして使えるようになる。
