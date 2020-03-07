# Android Toolbox by Midnightys

[![](https://jitpack.io/v/Midnightys/Toolbox.svg)](https://jitpack.io/#Midnightys/Toolbox)

### How to implement it

```groovy
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```

```groovy
dependencies {
  implementation "com.github.Midnightys.Toolbox:common:$toolbox_version"
  implementation "com.github.Midnightys.Toolbox:status:$toolbox_version"
  implementation "com.github.Midnightys.Toolbox:firestore-flow:$toolbox_version"
  implementation "com.github.Midnightys.Toolbox:use-case:$toolbox_version"
}
```

