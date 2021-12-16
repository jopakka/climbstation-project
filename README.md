# ClimbStation
Android application to control ClimbStation device.

## Requirements
- Android 8.0 (sdk 26)

![Connect fragment](https://i.ibb.co/YZW3Gqk/Connect.png)
![CLimb fragment](https://i.ibb.co/YZdNnkM/Climb.png)
![CLimbOn fragment](https://i.ibb.co/LC9xDt9/ClimbOn.png)
![CreateProfile fragment](https://i.ibb.co/d6147pt/Create-Profile.png)
![Stats fragment](https://i.ibb.co/pdmVxQ3/Stats.png)

## Usage
### How to connect the App to the ClimbStation 
Menu can be found by clicking on the dotted menu button on the top right corner of the app.

First, search and connect to the same WIFI network as the ClimbStation. 
Then either input the device serial number in the input field and click next, 
or scan the QR code located on the ClimbStation machine.

### Start climbing 
First, navigate to the "Climb" view from the bottom navigation menu. 
On the top left, a wall profile previews the ClimbStation terrain profile. 
On the right side, you can see the difficulty levels. The "start" button prepares 
the ClimbStation machine ready to start climbing. Select various difficulty terrain 
profiles and difficulty levels. If you click the "manual" -button, another screen appears, 
here you may adjust a static angle, distance, and timer for the climbing session. Enjoy climbing!

### Create your custom climbing profile 
Navigate to the "Create" and add a new profile by clicking the "ADD PROFILE" button. 
Name your challenge and start creating the profile by clicking the "EDIT" -button. 
From here you can add climbing phases also known as steps. 
Add a new step by clicking the "ADD NEW STEP" button.
Add as many steps as you want to make your ideal challenge. 
Once created, go to the "Climb" view and choose your created profile in the list of difficulty terrain profiles. 
Start and enjoy your created climbing profile!

### Delete profiles or steps
Swipe left on profile or step to delete it.

### How to view previous climbing sessions
Navigate to the "Profile" and select the "History" tab on the top right (or swipe left).
Then click on of the sessions in the list. If you want, you can also climb the particular session again by clicking "CLIMB AGAIN" button.

### Body weight
Used in calorie calculation.

Menu can be found by clicking on the dotted menu button on the top right corner of the app. 
First, click on "SETTINGS" item, then click on the "BODYWEIGHT" item.
Enter you weight in kilograms to input field and press "SAVE".

### ClimbStation engine speed
Average speed of the ClimbStation. 

Menu can be found by clicking on the dotted menu button on the top right corner of the app.
First, click on "SETTINGS" item, then click on the "MACHINE SPEED" item.
Select your desired speed from the dropdown menu and click "SAVE".

### Text-to-speech
Text to speech informs user of climbed time and distance during climbing session.

Menu can be found by clicking on the dotted menu button on the top right corner of the app.
First, click on "SETTINGS" item, then click on the "TEXT TO SPEECH" to toggle it ON or OFF.

## Help
### WallProfile
Xml:
```xml
<fi.climbstationsolutions.climbstation.utils.WallProfile
    android:id="@+id/wall"
    app:profile="<ADD YOUR ClimbProfileWithSteps HERE>"
    app:sessionWithData="<ADD YOUR SessionWithData HERE>"
    app:wallColor="#FFFFFF"
    app:wallFillColor="#FF00FF"
    app:wallOutlineColor="#00FFFF"
    app:wallOutlineThickness="@{2}" />
```

Programmatically:
```kotlin
val wallProfile = findViewById<WaLLProfile>(R.id.wall)
wallProfile.profile = ProfileWithSteps()
wallProfile.sessionWithData = SessionWithData()
```

## Developers
Oskar Wiiala
Patrik Pölkki
Joonas Niemi
