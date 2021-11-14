import ACTIONS from './index'
import axios from 'axios'


//TODO: INCREASE COUNT
export const dispatchIncreaseCount = (noti) => {
    return {
        type: ACTIONS.INCREASE_NOTI,
        payload: noti
    }
}

//TODO: DECREASE COUNT
export const dispatchDecreaseCount = () => {
    return {
        type: ACTIONS.DECREASE_NOTI
    }
}

export const fetchUnseenNoti = async () => {
    const res = await axios('/api/notifications/unseen')
    return res.data.length
}

export const dispatchCountNoti = (count) => {
    return {
        type: ACTIONS.GET_COUNT_NOTI,
        payload: count
    }
}

//TODO: REMOVE ALL COUNT WHEN CLICK NOTIFICATION
export const dispatchRemoveCountNoti = () => {
    return {
        type: ACTIONS.REMOVE_COUNT_NOTI,
    }
}