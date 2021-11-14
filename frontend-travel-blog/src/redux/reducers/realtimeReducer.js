import ACTIONS from '../actions.js'


const data = {
    ws: null,
    notificationSubscription: null,
    postSubcription: null,
    isSuccess: false
}

const realtimeReducer = (state = data, action) => {
    switch(action.type){
        case ACTIONS.SET_REALTIME_WS: 
            return {
                ...state,
                ws: action.payload,
                isSuccess: true
            }
        case ACTIONS.SET_REALTIME_NOTI: 
            return {
                ...state,
                notificationSubscription: action.payload
            }

        case ACTIONS.REMOVE_REALTIME_NOTI: {
            return {
                ...state,
                notificationSubscription: null
            }
        }
        
        case ACTIONS.SET_REALTIME_POST: {
            return {
                ...state, 
                postSubcription: action.payload
            }
        }

        case ACTIONS.REMOVE_REALTIME_POST: {
            return {
                ...state,
                postSubcription: null
            }
        }
        // case ACTIONS.CLEAR_REALTIME: {
        //     return {data}
        // }

        default:
            return state
    }
}

export default realtimeReducer