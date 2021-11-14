import {combineReducers} from 'redux'
import auth from './authReducer'
import token from './tokenReducer'
import notification from './notificationReducer'
import realtime from './realtimeReducer'
import commentsReducer from './commentReducer'

export default combineReducers({
    auth,
    token,
    notification,
    realtime,
    commentsReducer
})