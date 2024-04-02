https://github.com/kennyRamadhan/FlipTestMobile



A. For running this project make sure your local machine already installed :
1. Java
2. Eclipse for Java Developers
3. Node JS
4. Appium Server
5. Android Studio (For create virtual device if you choose running using emulators)

B. After you cloning this repo and open it with eclipse there must be an error in pom.xml (the system cannot find the path specified) its happen because different path from my local machine to yours, but you can updated the project and solved the error by doing : Right-click on the project -> Go to Maven -> Update project.

C. Change configurations settings on appiumBase.java 
1. Line number 27 change with your local path main.js usually placed in AppData/Roaming/npm/node_modules/appium/build/lib/main.js
2. Line number 31 change your own device name
3. Line number 32 change to your path where you stored the apk
      
D. Before run the test make sure your mobile device already enable developers options 

D. After the errors gone you can open e2eMobile.java file and do : Right-click -> Run As -> 1. TestNG Test

E. The reports will be stored in test-output folders and you can choose index.html or emailable-report.html

F. Sometimes you might be face an errors but you can try with another mobile device

G. Library used in this project :
1. TestNG
2. TestNG Reports
3. Hamcrest
4. Appium Inspector (For Get Elements)
5. Appium Server
6. Appium Client

H. Folder Explanations :
1. /FlipTest/src/test/java/KennyRamadhan/FlipTest/e2eMobile.java = Contains Test Case

2. /FlipTest/src/main/java : Contains Appium Configurations &  Page Object Design Pattern

3. /FlipTest/test-output : Contains All Reports


IMPORTANT!! : I already fixing about Before Test & After Test , I realized there is problem when running test the app doesn quit for each test case but now its already fixed . now you can running and the app will quit and relaunch for each test case. 
