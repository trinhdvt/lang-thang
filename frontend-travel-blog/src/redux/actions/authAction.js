import ACTIONS from './index'
import axios from 'axios'

//TODO: INSTALL LOGIN DISPATCH
export const dispatchLogin = () => {
    return {
        type: ACTIONS.LOGIN
    }
}

//TODO: LOGOUT
export const dispatchLogout = () => {
    return {
        type: ACTIONS.LOGOUT
    }
}

//TODO: GET USER INFOR
export const fetchUser = async () => {
    const res = await axios.get('/api/whoami')
    return res
}

export const dispatchGetUser = (res) => {
    if(res.data.role === "ROLE_ADMIN"){
        return {
            type: ACTIONS.GET_USER,
            payload: {
                user: res.data,
                isAdmin: true
            }
        }
    }
    else {
        return {
            type: ACTIONS.GET_USER, 
            payload: {
                user: res.data,
                isAdmin: false
            }
        }
    }
    
}