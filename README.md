## DJ Music Application

This Android application has been developed in the scope of the EPFL Software Engineer course.
The goal of the app is to allow people to share their favorite musics while discovering new music that might interest them.

### Set-up to make Google+ sign-in work while debugging :

Simply copy the key to your machine:
###### On Linux:
    In the folder "~/.android/debug.keystore"
###### On Windows;
    Int the folder "%USERPROFILE%\.android\debug.keystore"

The key can be found at the root of our repo, and is named "debug.keystore".

Under Linux :
```bash
$ cd <app root folder>
$ cp debug.keystore ~/.android/debug.keystore
```
