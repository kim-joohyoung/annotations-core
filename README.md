[![](https://jitpack.io/v/kim-joohyoung/annotations-core.svg)](https://jitpack.io/#kim-joohyoung/annotations-core)

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

## @ActivityLauncher
```kotlin
@ActivityLauncher
@Result("result1", String::class)
@Result("result2", String::class)
class SecondActivity : AppCompatActivity() {
    @Extra
    lateinit var arg1 : String
    @Extra
    lateinit var arg2 : String
	...
	override fun onCreate(savedInstanceState: Bundle?) {
		...
		SecondActivityLauncher.inject(this)
		...
        binding.close.setOnClickListener {
            SecondActivityLauncher.setResult(this, RESULT_OK, "Successes", "Successes")
            finish()
        }
	}
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    ...
    private val launcher = SecondActivityLauncher()
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
	launcher.register(this)
        ...
        binding.fab.setOnClickListener {
            launcher.launch("test", "tes2"){result1, result2 ->
                Toast.makeText(this, "$result1, $result2", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

## @ActivityBuilder
```kotlin
@ActivityBuilder
class SecondActivity : AppCompatActivity() {
    @Extra
    lateinit var arg1 : String
    @Extra
    lateinit var arg2 : String
	...
	override fun onCreate(savedInstanceState: Bundle?) {
		...
		SecondActivityBuilder.inject(this)
		...
	}
}
```

```kotlin
val intent = SecondActivityBuilder.intent(this, "aaa", "bbb")
val bundle = SecondActivityBuilder.bundle("aaa", "bbb")
SecondActivityBuilder.startActivity(this, "aaa", "bbb")
````


## @FragmentBuilder
```kotlin
@FragmentBuilder
class FirstFragment : Fragment() {
    @Arg
    lateinit var arg1 : String
    @Arg
    lateinit var arg2 : String
	...
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirstFragmentBuilder.inject(this)
    }
}
```

```kotlin
val fragment = FirstFragmentBuilder.build("aaa", "bbb")
val fragment = FirstFragmentBuilder.newInstance("aaa", "bbb")
val bundle = FirstFragmentBuilder.bundle("aaa", null)
```

## @FragmentBuilder with listener
```kotlin
@FragmentBuilder(listener = true)
@Result("result1", String::class)
@Result("result2", String::class)
class SecondFragment : Fragment() {
    @Arg
    var arg1 : String = ""
    @Arg
    var arg2 : String? = null
	...
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SecondFragmentBuilder.inject(this)
    }
	...
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ...
        binding.buttonSecond.setOnClickListener {
            SecondFragmentBuilder.setResult(parentFragmentManager, "SecondFragment","Result")
            parentFragmentManager.popBackStack()
        }
    }
}
```

```kotlin
    SecondFragmentBuilder.register(this){result1, result2 ->
        Toast.makeText(context, "==>$result1, $result2", Toast.LENGTH_SHORT).show()
    }
```
