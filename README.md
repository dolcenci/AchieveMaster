ACHIEVEMASTER

AchieveMaster is an Android fitness tracking application developed using Kotlin. It allows users to explore exercises, create personal fitness goals, record completed exercises and monitor their progress. The application uses Firebase to store user data and supports offline access, with automatic synchronisation when an internet connection becomes available.

1. PURPOSE OF THE APPLICATION

AchieveMaster was developed to help users manage their fitness activities and keep track of their personal goals in one place. The application combines exercise discovery with goal tracking, allowing users to find suitable exercises and work towards specific fitness targets.

Users can browse exercises from the API Ninjas library and filter the results according to muscle groups. They can also create measurable goals based on their fitness needs. When an exercise is marked as completed, the application updates the progress of any goal associated with the same muscle group. This makes it easier for users to see how their activities contribute towards their targets.

The application also supports four languages, namely English, isiZulu, isiXhosa and Afrikaans. Other features include dark mode, Google Sign-In and a settings screen where users can manage their fitness profiles.

2. DESIGN CONSIDERATIONS

Application structure

The application follows the Model View ViewModel architecture, commonly known as MVVM. Activities and fragments manage the user interface, while ViewModels handle the state and data displayed on different screens. A FirebaseService class is responsible for authentication and database operations, while Retrofit is used to communicate with the API Ninjas exercise service.

Separating these responsibilities makes the application easier to maintain. For example, changes to the exercise API or Firebase operations can be handled without requiring major changes to the user interface.

Authentication

Firebase Authentication is used to manage account access through email and password as well as Google Sign-In. Password handling is managed by Firebase rather than by a custom password storage system within the application. This avoids the need to develop and maintain a separate authentication mechanism.

Offline functionality

Firebase disk persistence is enabled to allow supported data to remain available when the device loses its internet connection. Goal information and exercise logs can be written to the local cache, with pending changes synchronised when connectivity returns.

The application also uses the OkHttp disk cache to retain previously retrieved exercise information. This allows users to access exercises that have already been loaded while offline. However, searching for exercises that have not been retrieved before still requires an internet connection.

Data management

Fitness goals are associated with exercises through the relatedMuscle field. When a user completes an exercise, the application converts the muscle name into the format used by the API before checking which goals are linked to it.

For example, the muscle name "Middle Back" is converted to "middle_back". The application then increases the completed exercise count for matching goals. This ensures that progress is based on the type of exercise completed rather than increasing every goal regardless of its purpose.

User interface

The application uses a purple and gold colour scheme, gradient headers, card layouts and Material Design components to create a consistent interface. On the goals screen, a floating action button provides access to the goal creation feature.

The login screen presents email and password authentication separately from Google Sign-In, making both options easy to identify. Users can also change the language and switch between light and dark themes through the settings without needing to restart the application.

3. USE OF GITHUB AND GITHUB ACTIONS

GitHub is used to manage the project's source code and track changes during development. The repository also includes a GitHub Actions workflow that automatically runs when changes are pushed or a pull request is created.

The workflow is configured in the file .github/workflows/android-ci.yml. It performs the following tasks.

The repository is checked out onto a clean Ubuntu virtual machine so that the project can be tested in a separate environment.

JDK 17 is configured because it is required by the Android Gradle Plugin used by the project.

The workflow runs the unit tests using the command ./gradlew testDebugUnitTest.

The application is then compiled into a debug APK using ./gradlew assembleDebug. The resulting APK and test reports are uploaded as downloadable workflow artefacts.

The GitHub-hosted Ubuntu environment provides Android development tooling, including the Android SDK, which allows the workflow to build the application without relying on the developer's personal computer.

Using this workflow helps identify compilation problems and failing unit tests before the project is submitted or reviewed. The uploaded test reports also provide evidence of the test results and can help identify the cause of a failure.

The unit tests cover the FitnessGoal.progressPercent calculation, which determines the percentage displayed on the goal progress bar, and the Exercise.equipment mapping, which processes the equipment information returned by the API. These functions can be tested independently of Firebase and the Android interface, making them suitable for automated testing.

4. RELEASE NOTES

Version 1.0.0

Final POE submission

Features added

Google Sign-In was integrated with Credential Manager and Firebase Authentication.

Offline support was added for goal progress and exercise logs, allowing pending changes to synchronise when an internet connection becomes available.

Exercise information is cached using the OkHttp disk cache so that previously retrieved exercises can still be viewed offline.

The application supports English, isiZulu, isiXhosa and Afrikaans.

A dark mode option was added, with theme changes applied immediately.

A Privacy Policy screen was added to the settings section.

A sign-up confirmation screen was included in the registration process.

A custom adaptive application icon was created.

Issues corrected

The registration process was adjusted so that creating an account does not automatically sign the user in. Users must complete registration before logging in with their new credentials.

The equipment mapping was corrected to handle the API's equipments array properly. Exercise details and goals can now display the available equipment information instead of incorrectly defaulting to Bodyweight.

Goal progress updates were corrected so that completing an exercise increases the completed exercise count for goals associated with the relevant muscle group.

Goal creation was updated to use valid muscle identifiers supported by the API, including abdominals, lats, middle_back and quadriceps. This replaced previously incorrect values such as back, legs, abs and cardio.

Offline progress updates were adjusted to avoid the "Job was cancelled" error. The updated increment method uses a standard write operation that Firebase can queue while the device is offline.

A Firebase persistence issue that could cause a crash when launching the application for a second time was addressed by using a process-wide flag to prevent persistence from being enabled repeatedly.

Known limitations

Exercise searches for information that has not already been retrieved require an internet connection. Previously loaded exercise information remains available through the local cache.

Google Sign-In requires a physical device or emulator with Google Play services enabled.

5. HOW TO BUILD AND RUN THE APPLICATION

Requirements

Android Studio

JDK 17

A physical Android device or an emulator with Google Play services enabled if Google Sign-In is to be tested.

Installation steps

Clone the project repository from GitHub.

Obtain the google-services.json configuration file for the Firebase project and place it inside the app folder. This file contains project-specific configuration and is not included in the repository.

Open the project in Android Studio and allow Gradle to synchronise the project dependencies.

Select the app run configuration, connect an Android device or start a suitable emulator, and run the application.

Running tests locally

Open a terminal in the project directory and execute the following command to run the unit tests.

./gradlew testDebugUnitTest

To build the debug APK, execute the following command.

./gradlew assembleDebug

The generated debug APK can then be found in the app/build/outputs/apk/debug directory, provided that the build completes successfully.