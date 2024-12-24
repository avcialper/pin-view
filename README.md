# pin-view

[![](https://jitpack.io/v/avcialper/pin-view.svg)](https://jitpack.io/#avcialper/pin-view)

![pin_view_header](/images/pin-view-header.png)

Pin-View is a versatile and customizable UI component designed for seamless PIN entry. This library allows you to effortlessly integrate a modern PIN input view into your application. By default, it supports 6-character passwords, but the length can be adjusted to suit your needs.

### Key features include:

-   Easy integration and customization options.
-   Support for variable PIN lengths.
-   A sleek and intuitive user experience for secure input.

# Installation

First, add the following dependency to settings.gradle.kts file

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Later, add this implementation build.gradle.kts (Module:app)

```gradle
dependencies {
    ..
    implementation("com.github.avcialper:pin-view:1.1.0")
}
```

TOML usage

```gradle
dependencies {
    ..
    implementation(libs.pin.view)
}
```

```toml
pinView = "1.1.0"

[libraries]
pin-view = { module = "com.github.avcialper:pin-view", version.ref = "pinView" }
```

# Basic Usage

Add these codes to the used layout

```xml
<com.avcialper.pinview.PinView
    android:id="@+id/pinView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Then, add these codes to your activity or fragment

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pinView.setOnPinCompletedListener { pin: String ->
            if(pin == "123456") {
                // do something
                true
            } else {
                // do something
                false
            }
        }
        ...
    }
}
```

# Attributes
| Attribute | Format | Default Value | Description | Usage |
| --- | --- | --- | --- | --- |
| textSize | Dimension | `14sp` | Determines the font size. | `android:textSize="14sp"` |
| textColor | Color | `#000000` | Determines the text color. | `android:textColor="#000000"` |
| width | Dimension | `50dp` | Determines the pin box width. | `app:width="50dp"` |
| height | Dimension | `50dp` | Determines the pin box height. | `app:height="50dp"` |
| box_count | Integer | `6` | Determines the pin box count. | `app:box_count="6"` | 
| pin_border_width | Dimension | `4dp` | Determines the pin box border width. | `app:pin_border_width="4dp"` |
| margin_horizontal | Dimension | `6dp` | Determines the pin box horizontal margin. | `app:margin_horizontal="4dp"` |
| selected_background_color | Color | `#34BAB7B7` | Determines the selected pin box bakground color. | `app:selected_background_color="#34BAB7B7"` |
| selected_border_color | Color | `#7752FE` | Determines the selected pin box border color. | `app:selected_border_color="#7752FE"` |
| unselected_background_color | Color | `#34BAB7B7` | Determines the unselected pin box bakground color. | `app:unselected_background_color="#34BAB7B7"` |
| unselected_border_color | Color | `#7A000000` | Determines the unselected pin box border color. | `app:unselected_border_color="#7A000000"` |
| error_background_color | Color | `#34BAB7B7` | Determines the incorrect pin box bakground color. | `app:error_background_color="#34BAB7B7"` |
| error_border_color | Color | `#FF0000` | Determines the incorrect pin box border color. | `app:error_border_color="#FF0000"` |
| correct_background_color | Color | `#34BAB7B7` | Determines the correct pin box bakground color. | `app:correct_background_color="#34BAB7B7"` |
| correct_border_color | Color | `#2E9731` | Determines the correct pin box border color. | `app:correct_border_color="#2E9731"` |