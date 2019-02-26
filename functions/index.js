const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

/* Listens for new messages added to /messages/:pushId and sends a notification to subscribed users */
exports.pushNotification = functions.database.ref('/messages/{pushId}').onWrite( ( change, context) => {

    console.log('Push notification event triggered');
    
    /* Grab the current value of what was written to the Realtime Database */
        var valueObject = change.after.val();
    
    /* Create a notification and data payload. They contain the notification information, and message to be sent respectively */ 
        const payload = {
            notification: {
                title: 'Trimetrack',
                body: "{0} shared location".format(valueObject.senderName),
                sound: "default"
            },
            data: {
                event: valueObject.event,
                receiverId: valueObject.receiverId,
                senderId : valueObject.senderId,
                senderName : valueObject.senderName,
                plateNumber : valueObject.plateNumber,
                sessionId : valueObject.sessionId
            }
        };
    
    /* Create an options object that contains the time to live for the notification and the priority. */
        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24 //24 hours
        };
    
    return admin.messaging().sendToTopic("notifications-{0}".format(valueObject.receiverId), payload, options);
    });

String.prototype.format = function () {
    var a = this;
    for (var k in arguments) {
        a = a.replace(new RegExp("\\{" + k + "\\}", 'g'), arguments[k]);
    }
    return a
}