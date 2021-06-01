# detectMe
Android App Build Link : https://i.diawi.com/rHFbCq

detectMe is a face detection Android application which can be used to detect the face using Front Camera and also allow us to record the video.
detectMMe is built on top of MLKit's face detection API and CameraX API's.

#Functionality inside Application:
1. Detect the face using MLKit's API and add track the face using Yellow Color Rectangular Overlay Box
2. Use the Camera API's to preview the front camera and added green color Camera Overlay to position the user face inside the rectanular box to make the movement efficiently
3. It allow us to capture the video snapshot of 3 sec as soon as user position there face with yellow color box inside the Green Color box
4. It allow us to preview the video snapshot and also able to share the video to others using all possible android sharing component

#Technology Stack Used for the Application:
1. Kotlin
2. MLKit's API 16.1.0
3. Camera Preview Third Party Library(for quick integration)
4. Custom View API Camera Overlay
5. LiveData,MVVM Architecture
6. Singleton and Observer Design Pattern

#Steps to use the Application
1. Install the application
2. Camera app preview screen will open
3. The yellow color overlay box visible which track your face
4.  In the center , one fix green color rectangular box is visible
5. Position your face inside the green rectangle box , make sure yellow box should place comletely inside green box
6. As soon as position is correct as per step 5 , the automatic video recording start for 3 sec
7 . After successful recording of video , one button place at bottom is visible . Onclick of this button user can see the preview of recorded video
8. On the VideoRecording preview screen , at the task bar there is icon of share which allow to share the recorded video to other user through avaialble sharing app

#Future Improvement or Enhancement :
1. Optimization of face detection
2. Make the app functionality as an reusable component like SDK
3. Use Own CameraX functionality and make the UI more approchable
4. Add the user movements to authorize for video snapshot recording
5. Security Enahncement for data protection

#Known issue :
1.Unable to use Webhook.site as facing some issue while creating URL. However all required files and classes are created for calling any Server API.
