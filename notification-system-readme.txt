Notification System
by Radu Cotofrei

/-------------------------/
/********** V.1 **********/
/-------------------------/

/****Files you need from the project****/
-NotificationSystem.java
-DJMNotificationSystem.java
-dj_music_icon.png (drawable-mdpi, drawable-hdpi and drawable-xhdpi)
-----------------------------------------

/****How to use****/
in an activity :
  DJMNotificationSystem notifications = new DJMNotificationSystem();
  
  //this refers to the activity context
  notifications.show(this, "Title", "message", TargetActivity.class); 
-----------------------------------------

/****More details****/
see http://developer.android.com/guide/topics/ui/notifiers/notifications.html
-----------------------------------------
