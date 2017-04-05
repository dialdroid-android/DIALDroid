# DIALDroid

DIALDroid is a highly scalable tool to identify inter-app collusions and privilege escalations among Android apps. 

## Instructions
1. Please download or clone this repository.
2. You can directly use the standalone Jar file (dialdroid.jar) inside the build directory.
Or you can build using ant (ant ).
3. To run DIALDroid you will need android platform files. You can get a collection here: https://github.com/dialdroid-android/android-platforms
4. DIALDroid stores results in a MySQL database. The database schema is here: https://github.com/dialdroid-android/dialdroid-db/blob/master/DIALDroid.sql
5. Please modify the cc.properties file inside the build directory to provide database username and password.
Please note the the cc.properties file, ic3-android.jar, AndroidCallbacks.txt, and EasyTaintWrappersSource.txt must be in the same directory as the dialdroid.jar.
6. The easy way would be to use dialdroid.sh script. Edit the dialdroid.sh file and modify the database_name and classpath. At least 32GB memory is recommended to run dialdroid. You can customize the memory in the dialdroid.sh (-Xms16G -Xmx64G).
7. dialdroid.sh takes two arguments. The first argument is the name of the apk file and second argument is the category of the app (e.g., Games, Social, Business).
: ./dialdroid.sh directory_containing_apks SOCIAL
8. dialdroid will analyze the apps one by one and finally compute the sensitive channels (ICC links that can potentially carry sensitive information) and store those in the table named SensitiveChannels.
9. Use following MySQL query to compute ICC-based leaks: https://github.com/dialdroid-android/dialdroid-db/blob/master/ICCBasedLeaks.sql
10. Use following MySQL query to compute privilege escalations: https://github.com/dialdroid-android/dialdroid-db/blob/master/PrivEscalations.sql

## Issues
If you encounter any issues in running DIALDroid, please post it in the issues tab. Alternatively, you can also contact Dr. Amiangshu Bosu [abosu at cs dot siu dot edu].
