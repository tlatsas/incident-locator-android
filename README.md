Incident Locator - android client
=================================

About
-----

Android client for an incident locator service. Part of my thesis.


| Project          | IncidentLocator-android
|------------------|-----------------------------------------------------
| Homepage         | https://github.com/tlatsas/incident-locator-android
| Version          | v0.8
| Min SDK Version  | 8 (Froyo 2.2.x)
| Max SDK Version  | 17 (Jelly Bean 4.2.x)
| License          | BSD 3-Clause


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

* INTERNET

    Communicate with the service using the network.


SDK Limitations
---------------

* There is a [known][3] issue with stock android-sdk api v10 image which
crashes the emulator when using GPS. Please use the image provided with
android-google-apis v10.

* There is an issue with android-google-apis v10 image. It fails to
initialize the GPS when turning GPS functionality on/off from the
android settings. It works fine with a real device though.


Compatibility
-------------

There is a known issue, reported by a number of users using various CM7 based roms, that
leads to wrong compass readings. So far I have limited the problem to CM7 based roms
using 2.6.35.7-perf+ kernel version.

Table of tested devices and relevant version information:

| Device Model     | Android rom version | Kernel version                     | Status
|------------------|---------------------|------------------------------------|----------------------
| Samsung GT-I5800 | 2.2                 | 2.6.32.9                           | working
| ZTE Skate        | 2.3.7               | 2.6.35.7-perf+                     | wrong compass reading
| ZTE SKate        | 4.0.4               | 2.6.35.7-perf+                     | wrong compass reading
| ZTE Skate        | 2.3.5               | 2.6.35.7-perf+zte-kernel@Zdroid-SMT| working


Precompiled APKs
----------------

Stable and development builds can be found [here][4].


License
-------
(c) 2012 Tasos Latsas, under the BSD 3-Clause License. See `COPYING`.


[1]: https://github.com/tlatsas/incident-locator-android/tags
[2]: http://developer.android.com/tools/building/building-eclipse.html
[3]: http://code.google.com/p/android/issues/detail?id=13015
[4]: http://dl.kodama.gr/incident-locator-apks/
