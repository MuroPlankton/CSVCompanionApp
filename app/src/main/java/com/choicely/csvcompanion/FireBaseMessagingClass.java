//package com.choicely.csvcompanion;
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class FireBaseMessagingClass extends FirebaseMessagingService {
//
//    private static final String TAG = "FirebaseMessagingClass";
//
//    public void getFCMToken() {
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                return;
//            }
//            String token = task.getResult();
//            Log.d(TAG, "token: " + token);
//        });
//    }
//
//    public void subscribeToTopic() {
//        FirebaseMessaging.getInstance().subscribeToTopic("Library")
//                .addOnCompleteListener(task -> {
//                    String message = "subscribe was successful";
//                    if (!task.isSuccessful()) {
//                        message = "subscribe failed";
//                    }
//                    Log.d(TAG, "Message: " + message);
//                });
//    }
//
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        Log.d(TAG, "New token: " + s);
//    }
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        Log.d(TAG, "Message received from: " + remoteMessage.getFrom());
//
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data: " + remoteMessage.getData());
////            if (true) {
////                scheduleJob();
////            } else {
////                handleNow();
////            }
//        }
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Msg notification: " + remoteMessage.getNotification().getBody());
//        }
//    }
//
////    private void handleNow() {
////    }
////
////    private void scheduleJob() {
////        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
////                .build();
////        WorkManager.getInstance().beginWith(work).enqueue();
////    }
//
//
//    public void sendToTopic() {
//        String topic = "library";
//
//        Message message = Message.builder()
//                .putData("Testi", "testidata")
//                .putData("Testi2", "toinen testidata")
//                .setTopic(topic)
//                .build();
//
//        String response = FirebaseMessaging.getInstance().send(message);
//        System.out.println("Successfully sent message: " + response);
//    }
//}
