
<div style="display: flex; flex-direction: column; align-items: center; justify-content: center;">
<img src="https://play-lh.googleusercontent.com/TtUj94noX7g5B6Vs84A2PpVSCreYWVye5mHz32mSMHXCojT0xxDRtXBwXbc1q42AaA=w240-h480-rw" height="100" alt="Toolz-logo"/>
<h1 style="margin-top: 10px;">Toolz: ChatBot and Unit Converter</h1>
</div>


[![GitHub license](https://img.shields.io/badge/License-Apache2.0-blue.svg)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/prime-zs/toolz2?style=social)](https://github.com/prime-zs/toolz2/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/prime-zs/toolz2?style=social)](https://github.com/prime-zs/toolz2/network/members)
[![GitHub watchers](https://img.shields.io/github/watchers/prime-zs/toolz2?style=social)](https://github.com/prime-zs/toolz2/watchers)
[![GitHub follow](https://img.shields.io/github/followers/prime-zs?label=Follow&style=social)](https://github.com/prime-zs)

## 🌞 Preview 

|   Shot-1    | Shot-2 | Shot-3 | Shot-4 | Shot-5 | Shot-6 | Shot-7
|---	|--- |---	|--- |---	|--- |---
| ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/4a356e6f-07b6-4b0d-a710-5316366485fe) | ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/9a7fede8-3667-4b0a-b2e7-8b85e5d266ef) | ![Apple iPhone 11 Pro Max Screenshot 2](https://github.com/iZakirSheikh/toolkit/assets/46754437/0ae1acef-dc2b-4534-8b34-a6dd93aee4e8) | ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/adc6931f-ea51-406d-848b-42777b529a0d) | ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/aeb47b2e-98c6-4756-a8fb-b9bbdd0d8fc2) | ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/fa263ffb-c561-4c70-85d4-a235023138f8) | ![](https://github.com/iZakirSheikh/toolkit/assets/46754437/09d38631-1d41-403b-b697-f6de36ab459a)
<br />

## Features

### Unit Converter
Toolz provides a comprehensive Unit Converter that simplifies complex unit conversions. You can effortlessly convert between various units within each category, such as:

| Category | Units |
| --- | --- |
| Volume | Liter, Milliliter, Gallon (US), Gallon (UK), Quart (US), Quart (UK), Pint (US), Pint (UK), Cup (US), Cup (UK), Fluid Ounce (US), Fluid Ounce (UK), Tablespoon (US), Tablespoon (UK), Teaspoon (US), Teaspoon (UK) |
| Temperature | Celsius, Fahrenheit, Kelvin |
| Weight | Kilogram, Gram, Milligram, Pound, Ounce, Stone |
| Length | Kilometer, Meter, Centimeter, Millimeter, Mile, Yard, Foot, Inch |
| Time | Year, Month, Week, Day, Hour, Minute, Second |
| Speed | Kilometer per hour, Meter per second, Mile per hour, Knot |

## Chatbot
You can talk to Chat AI, a smart and friendly chatbot that can answer your questions, help you with writing, and inspire your creativity. Chat AI is powered by ChatGPT and GPT-3.5 API, which are advanced natural language processing (NLP) algorithms. You can ask Chat AI anything you want, such as:

  - General knowledge questions, such as "Who is the prime minister of India?" or "What is the population of China?"
  - Writing assistance, such as "Write a paragraph about dogs" or "Generate a catchy slogan for my product"
  - Creative prompts, such as "Tell me a story" or "Write a poem about love"
  - And more!

Here is the enhanced text:

## Installation
Toolz is an amazing app that helps you with various tasks and makes your life easier. It has two main features: a chatbot and a unit converter. To install Toolz on your Android device, follow these simple steps:

### Prerequisites
- Make sure your device is running Android OS version Lolypop or above. The minimum API level required is 21.
- Ensure you have enough storage space on your device to download and install the app.

### Download
- Go to the Google Play Store and search for Toolz or click on the link below:

   <a href='https://play.google.com/store/apps/details?id=com.prime.toolz2&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="256"/></a>
   
- Tap on the Install button and wait for the app to download and install on your device.
- Once the installation is complete, you can launch the app from your home screen or app drawer.

## Build and run the project
If you are a developer and want to build and run the Toolz app from the source code, you need to follow these additional steps:

### Prerequisites
- Install Android Studio Alpha (Hedgehog) or above on your machine. You can download it from here: https://developer.android.com/studio/preview
- Install Git on your machine. You can download it from here: https://git-scm.com/downloads

### Steps
To build the Toolz app, follow these steps:

1. Clone the repository: Open a terminal window and type the following command: `git clone https://github.com/prime-zs/toolz2.git`
2. Add the private file:

Some features and functionalities of the app require a private file that contains some keys and ids. To add this file to the project, do the following:

- Create a file named `Private.kt` in the package `com.prime.toolz.core.billing`.
- Paste the following code into the file:

```kotlin
package com.prime.toolz.core.billing

object Private {
    /**
     * Base64-encoded RSA public key to include in your app binary
     */
    const val PLAY_CONSOLE_PUBLIC_KEY = "replace_this_with_id"
    const val UNITY_APP_ID = "replace_this_with_id"
}

object Product {
    const val DISABLE_ADS = "replace_this_with_id"
}

object Placement {
    const val INTERSTITIAL = "replace_this_with_id"
    const val BANNER_SETTINGS = "replace_this_with_id"
    const val BANNER_UNIT_CONVERTER = "replace_this_with_id"
}
```
Note: Replace the placeholders `replace_this_with_id` with the appropriate values for your app. You can get these values from the Play Console, Unity Dashboard, or other sources.

3. Open Android Studio and select Open an existing project.
4. Navigate to the folder where you cloned or downloaded this repository and select it.
5. Wait for Android Studio to sync the project and resolve the dependencies.
6. Run the app on an emulator or a connected device by clicking the Run button.

You have successfully built and run the Toolz app from the source code. Enjoy!

## Support and Contribution

I love it when people use my tool and we’d love to make it even better. If you like this tool and
want to support me in developing more free tools for you, I’d really appreciate a donation. Feel
free to `buy me a cup of coffee` 😄. Thanks!


<a href="https://www.buymeacoffee.com/sheikhzaki3" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="160">
</a>

<br>


### Bug Reports and Feature Requests
If you encounter any issues or have ideas for new features, please [create an issue](https://github.com/prime-zs/toolz2/issues) on the GitHub repository.

## Architecture
This app uses [***MVVM (Model View
View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contact
* If you have any questions, feedback, or suggestions, please feel free to contact me at helpline.prime.zs@gmail.com. You can also follow me on GitHub [@prime-zs](https://github.com/prime-zs). Thank you for using Toolz! 🙏
* Twitter: <a href="https://twitter.com/ZakirSheikhReal" target="_blank">@ZakirSheikhReal</a>
