# Android Toolbox by Midnightys

##### How to implement it

```css
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

```css
dependencies {
  implementation "com.github.Midnightys.Toolbox:common:$toolbox_version"
  implementation "com.github.Midnightys.Toolbox:status:$toolbox_version"
  implementation "com.github.Midnightys.Toolbox:firestore-flow:$toolbox_version"
 	implementation "com.github.Midnightys.Toolbox:use-case:$toolbox_version"
	}
```

