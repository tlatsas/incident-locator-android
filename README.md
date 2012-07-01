Incident Locator - android client
=================================

About
-----

Android client for an incident locator service.

**Note** this is still work in progress.

| Project          | IncidentLocator-android                             |
|------------------|-----------------------------------------------------|
| Homepage         | https://github.com/tlatsas/incident-locator-android |
| Version          | v0.1                                                |
| Min SDK Version  | v10                                                 |
| License          | BSD 3-Clause                                        |


Build
-----

1. Clone the git repository (or download from a specific [tag][1])

    `$ git clone https://github.com/tlatsas/incident-locator-android.git`

2. Create the project environment

    `$ android update project --path incident-locator-android/`

3. Navigate into the project folder and compile in debug mode

    `$ cd incident-locator-android && ant debug`


Install
-------

* Install in default running emulator using `ant`

    `$ ant installd`

* Install in default running emulator using `adb`

    `$ adb -e install -s bin/incident_locator-debug.apk`

* You can combine compile and installation using `ant`

    `$ ant debug install`

* Install in default device

    `$ adb -d install -s bin/incident_locator-debug.apk`


If your are using eclipse you can find relevant instructions [here][2].


Permissions
-----------

* ACCESS_FINE_LOCATION

    Use GPS to obtain user location.

* WRITE_EXTERNAL_STORAGE

    Log coordinates in external storage.


SDK Limitations
---------------

* There is a [known][3] issue with stock android-sdk api v10 image which crashes the emulator when using GPS.
Please use the image provided with and android-google-apis v10.

* There is an issue with android-google-apis v10 image. It fails to initialize the GPS when turning GPS functionality on/off from the android settings.
It works fine with a real device.


License
-------
(c) 2012 Tasos Latsas, under the BSD 3-Clause License. See `COPYING`.


[1]: https://github.com/tlatsas/incident-locator-android/tags
[2]: http://developer.android.com/tools/building/building-eclipse.html
[3]: http://code.google.com/p/android/issues/detail?id=13015
