[![](https://jitpack.io/v/kim-joohyoung/annotations-core.svg)](https://jitpack.io/#kim-joohyoung/annotations-core)
# annotations-core

# Installation

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
```gradle
dependencies {
  ksp 'com.github.kim-joohyoung.annotations-core:annotations-compiler:<version>'
  implementation 'com.github.kim-joohyoung.annotations-core:annotations-api:<version>'
}
```

# Usage

## EActivity
```kotlin
@EActivity
class MainActivity : AppCompatActivity() {
    @Extra
    var arg1 : String = ""
    @Extra
    var arg2 : String? = null
	...
	override fun onCreate(savedInstanceState: Bundle?) {
		...
		injectExtra()
	}
}
```

```kotlin

val intent = MainActivityBuilder("aaa", null).intent(this)
val bundle = MainActivityBuilder("aaa", null).bundle()

```

## EFragment
```kotlin
@EFragment
class FirstFragment : Fragment() {
    @Arg
    var arg1 : String = ""
    @Arg
    var arg2 : String? = null
	...
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectArgs()
    }
}
```

```kotlin
val fragment = FirstFragmentBuilder("aaa", null).build()
val bundle = FirstFragmentBuilder("aaa", null).bundle()
```

## Fragment with Companion
```kotlin
@EFragment
class FirstFragment : Fragment() {
    @Arg
    var arg1 : String = ""
    @Arg
    var arg2 : String? = null
	...
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectArgs()
    }
	...
    companion object {

    }
}
```

```kotlin
val fragment = FirstFragment.newInstance("aaa", null)	
```

## Launcher

```kotlin
@Launcher
class SecondActivity : AppCompatActivity() {

    @Extra
    var arg1 : String = ""
    @Extra
    var arg2 : String? = null

    @ResultExtra
    var result : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        button_close.setOnClickListener {
            result = "Successes"
            SecondActivityLauncher.setResult(this, RESULT_OK)
            finish()
        }
    }
}

```

```kotlin
class MainActivity : AppCompatActivity() {
    private val launcher = SecondActivityLauncher()
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        launcher.register(this)
        ...
        binding.fab.setOnClickListener {
            launcher.launcher("test", "tes2"){ result ->
                Toast.makeText(this, "$result", Toast.LENGTH_SHORT).show()
            }
        }
    }
```