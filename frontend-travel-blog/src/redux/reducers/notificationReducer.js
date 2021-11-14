import ACTIONS from '../actions'

const initialState = {
    count: 0,
    notifications : []
}

const notificationReducer = (state = initialState, action) => {
    switch(action.type){
        case ACTIONS.DECREASE_NOTI: 
            return {
                count : state.count - 1,
            }
            //TANG
        case ACTIONS.INCREASE_NOTI: 
            console.log(state.notifications)
            return {
                count: state.count + 1,
                notifications: [...state.notifications, action.payload]
            }
        case ACTIONS.GET_COUNT_NOTI: 
            return {
                ...state,
                count: action.payload
            }

        case ACTIONS.REMOVE_COUNT_NOTI: 
            return {
                ...state,
                count: 0
            }
        default:
            return state
    }
}

export default notificationReducer