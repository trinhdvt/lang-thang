import Cookies from 'js-cookie'
import { useDispatch, useSelector } from 'react-redux'
import SockJS from 'sockjs-client'
import * as Stomp from 'stompjs'
import { dispatchSetWs, dispatchSetNoti, dispatchRemoveNoti, dispatchRemovePost, dispatchSetPost } from '../redux/actions/realtimeAction'
import { dispatchIncreaseCount } from '../redux/actions/notificationAction'
import { dispatchAddCmt } from '../redux/actions/commentAction'

const useSocketDataObject = () => {
    let ws = null
    let notificationSubscription = null
    let commentSubcription = null
    const dispatch = useDispatch()
    const realtime = useSelector(state => state.realtime)

    const ConnectSocket = () => {
        const socket = new SockJS("/socket-server")
        ws = Stomp.over(socket)
        ws.debug = null
        ws.connect({}, () => {
            console.log("Kết nối thành công")
            dispatch(dispatchSetWs(ws))
        }, (err) => {
            console.log(err)
        })
    }

    const Subscribe_notification = (email) => {
        if (realtime.ws !== null && realtime.isSuccess === true) {
            const token = Cookies.get("token")
            notificationSubscription = realtime.ws.subscribe(`/topic/notify/${email}`, (notify) => {
                dispatch(dispatchIncreaseCount(JSON.parse(notify.body)))
            }, { 'Authorization': `Bearer ${token}` })
            dispatch(dispatchSetNoti(notificationSubscription))
        }
    }

    const Unsubscribe_notification = () => {
        if (realtime.notificationSubscription !== null) {
            realtime.notificationSubscription.unsubscribe();
            dispatch(dispatchRemoveNoti())
        }
    }

    const Subscribe_post = (id) => {
        if (realtime.ws !== null && realtime.isSuccess) {
            commentSubcription = realtime.ws.subscribe(`/topic/post/${id}`, (comment) => {
                if (comment.body) {
                    dispatch(dispatchAddCmt(JSON.parse(comment.body)))
                }
            })
            dispatch(dispatchSetPost(commentSubcription))
        }
    }

    const Unsubscribe_post = (postSubcription) => {
        if (postSubcription !== null && realtime.isSuccess) {

            postSubcription.unsubscribe();
            dispatch(dispatchRemovePost())
        }
    }

    // const Disconnect = () => {
    //     if (realtime.ws !== null) {
    //         realtime.ws.disconnect()
    //         console.log("disconnect")
    //     }
    // }

    return { Subscribe_notification, ConnectSocket, Unsubscribe_notification, Subscribe_post, Unsubscribe_post, }

}

export default useSocketDataObject