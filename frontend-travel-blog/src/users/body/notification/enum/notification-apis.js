const baseUrl = '/api/notifications'

export default {
    seenNotification(notificationId){
        return `${baseUrl}/${notificationId}/seen`
    },
    getNotifications: `${baseUrl}`,
    seenAllNotification: `${baseUrl}/seenAll`
}