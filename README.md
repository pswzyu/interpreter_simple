#interpreter_simple

##How to run
###APK download
The downloadable apk can be downloaded from the 'download' folder, please select the version according to your device

###Test
Right now, we don't have a proper register function now. We have several pre-registered users that have the properties shown below:

####Smartphone Client
After app started, you should first input the user's id into the app for the app to know who you are. You can choose one of the pre-registered userids and input it into the "selfid" input box.
The second step is let the app know who you are talking to. You can finish this step by taking a picture of the people you are talking to. You can pull up the picture of the pre-registered users on the computer screen and then take a picture of that.
After the app has recognized the people you are talking to, the name of the user will be displayed in the app. Then, you can start talking by press the "Hold to Speak" button. Release the button after you finish talking, then the speech will be uploaded to the server for process.
At the same time, the listening user's device should start to fetch the speech, after it finished, the translated speech will be played automaticaly.

####Google Glass Client


##API used
###Speech text conversion
Xunfei
Att
Google voice api

###Translation
Google translate

###Face recognition
Face++



##Reference
We used some of the information from other git repositories
https://github.com/gillesdemey/google-speech-v2
https://github.com/benoitfragit/google2ubuntu
