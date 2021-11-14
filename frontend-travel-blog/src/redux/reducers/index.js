import {combineReducers} from 'redux'
import auth from './authReducer.js'
import token from './tokenReducer.js'
import notification from './notificationReducer.js'
import realtime from './realtimeReducer.js'
import commentsReducer from './commentReducer.js'

export default combineReducers({
    auth,
    token,
    notification,
    realtime,
    commentsReducer
})