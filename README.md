#Glass Translator

##How to run
###APK download
The downloadable apk can be downloaded from the 'test' folder, please select the version according to your device. Or you can download from the hockey platform.

###Install
For testers, please download the app from download folder, and then download the file named 'SpeechService.apk'(this is the speech related service our app depends on). Then please install 'SpeechService.apk' and our app using this command:
adb install SpeechService.apk
```bash
#use this line if you are testing mobile client
adb install Interpreter.apk
# use this line if you are testing glass client
adb install InterpreterGlass.apk
```
or other apk install methods you prefer. After that, you can install our app and start to test.

###Testing
After start the app, it will show the login screen. Right now, we are in alpha phase, so we dont have public registeration supported. We have several pre-registered users that have the properties shown below(use Username to login):

| ID        | RealName       |   Username  |  Password | Native Language |
|-----------|---------------:|------------:|-----------|-----------------|
| 1         | zhangyu        |    pswzyu   |    123qwe |           en_us |
| 2         | xinyuyuan      |    xinyuyua |    123qwe |           zh_CN |
| 3         | yongshengsong  | sunsetrider |    123qwe |           zh_CN |
| 4         | paul           |    paul     |    123qwe |           en_us |

The image of these users can be found in 'test' folder, you can open these images in github directly and recognize these images when you are testing.

####Google Glass client
On the google glass client, since it does not have input device, you can select a user by swipe and then tap to login.
Then, you can start to recognize the person you are talking to. On Glass, you can use two finger tap gesture to take picture.
After that, you can start talking. On glass client, touch the touchpad while you speak, then release the touchpad when you finish. The speech will be sent automaticly on glass.

####Mobile phone client
On the smartphone client, You can login as one of the users. 
Then, you can start to recognize the person you are talking to. On mobile client, you can press "add target" to take picture for recognition.
After that, you can start talking. On mobile phone client, press and hold the "Press to record" button to talk and then release the button to finish. Then press the send button to send the speech.

####Known Issues
1. on some mobile phones, the screen rotation effects the photo orientation, please make sure the photo you took has the people upright in the picture, not rotated or upside down
2. on google glass if you see "Processing image..." stayed there for a long time, it probably means the recognition is failed, please take another picture by two finger tap gesture

##API used
###Speech text conversion
Xunfei

###Translation
Google translate

###Face recognition
Face++
