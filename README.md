<div align="center">
<img width="1200" height="475" alt="CandyNodes Banner" src="https://img.sanishtech.com/u/c79c9979e90d2951d83b6551fd86475a.jpeg" alt="8da3058f015ab6cc51123326005db101" />
</div>

# A app by CandyNodes team

This app contains panel for users of CandyNodes.

Visit our discord for new updates and offer: https://discord.gg/YCRvewR3dn

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
