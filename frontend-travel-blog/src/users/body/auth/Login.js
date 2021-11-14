import React, { useState } from 'react'
import { Link, useHistory, useLocation } from 'react-router-dom'
import axios from 'axios'
import {dispatchLogin} from '../../../redux/actions/authAction.js'
import {useDispatch} from 'react-redux'
import {showErrMsg, showSuccessMsg} from '../../utils/notification/Notification.js'
import { isEmail, isEmpty, isLength } from '../../utils/validation/Validation.js'
import {GoogleLogin} from 'react-google-login'
import CookiesService from '../../../services/CookiesService.js'
import useSocketDataObject from '../../../real-time/useSocketDataObject.js'
import authApis from './enum/authentication-apis.js'


const initialState = {
    email: '',
    password: '',
    err: '',
    success: ''
}


function Login() {
    // const auth = useSelector(state => state.auth)
    const location = useLocation()
    const search = location.search;
    const params = new URLSearchParams(search);
    const redirectTo = params.get('redirectTo');

    const [user, setUser] = useState(initialState)
    const dispatch = useDispatch()
    const history = useHistory()
    const {email, password, err, success} = user
    const cookiesService = CookiesService.getService()
    const {Subscribe_notification} = useSocketDataObject()

    const handleChangeInput = e => {
        const {name, value} = e.target
        setUser({...user, [name]: value, err: '', success: ''})
    }

    const handleSubmit = async e => {
        e.preventDefault()
        if(isEmpty(email) || isEmpty(password)){
            return setUser({...user, err: 'Hãy điền đầy đủ thông tin', success: ''})
        }

        if(!isEmail(email)){
            return setUser({...user, err: 'Email không đúng định dạng', success: ''})
        }

        if(isLength(password)){
            return setUser({...user, err: "Mật khẩu không đủ 6 ký tự", success: ''})
        }

        var loginForm = new FormData()
            loginForm.append('email', email)
            loginForm.append('password', password)

        try {

            const res = await axios.post(authApis.login, loginForm)
            if(res){
                setUser({...user, err:'', success: res.data.msg})
                //Da Dang Nhap
                dispatch(dispatchLogin())
                cookiesService.setToken(res.data.token)
                Subscribe_notification(email)
                if(redirectTo) {
                    history.push(redirectTo)
                }
                else {
                    history.push("/")
                }
            }
        }catch(err){
            if(err.response.status === 401){
                setUser({...user, err: 'Sai email hoặc password', success: ''})
            }
            else if(err.response.status === 400){
                setUser({...user, err: 'Email hoặc password không hợp lệ', success: ''})
            }
            else if(err.response.status === 423) {
                setUser({...user, err: 'Email đã đăng ký nhưng chưa kích hoạt', success: ''})
            }
            else {
                setUser({...user, err: 'Đã có lỗi xảy ra', success: ''})
            }
        }
    }

    const responseGoogle = async (response) => {
        try{
            const google_token = response.tokenId
            var loginForm = new FormData()
            loginForm.append('google_token', google_token)
    
            const res = await axios.post(authApis.loginByGoogle, loginForm)
            if(res){
                setUser({...user, err: '', success: 'Đăng nhập thành công'})
                dispatch(dispatchLogin())
                cookiesService.setToken(res.data.token)
                Subscribe_notification(response.profileObj.email)
                
                if(redirectTo) history.push(redirectTo)
                else history.push("/")
                
            }
        }catch(err){
            setUser({...user, err: 'Đã có lỗi xảy ra', success: ''})
        }
        
    }

    return (
        <main className="main__auth">
        <div className="register">
            <h3>Đăng nhập</h3>
            {err && showErrMsg(err)}
            {success && showSuccessMsg(success)}

            <form onSubmit={handleSubmit}>
                <input type="email" placeholder="Email của bạn" name="email" value={email} onChange={handleChangeInput}/>
                <input type="password" placeholder="Mật khẩu" name="password" value={password} onChange={handleChangeInput}/>
                <button type="submit">
                    ĐĂNG NHẬP
                </button>
                <Link to="/forgot_password">
                    Quên mật khẩu?
                </Link>
                
            <div className="register__divider">
                <span>OR</span>
            </div>
            </form>
            <div className="register__social">
                <GoogleLogin
                clientId="545452035521-c4eljpuu1281eml2ci6kaud39s5kc9ct.apps.googleusercontent.com"
                buttonText="Đăng nhập bằng Google"
                onSuccess={responseGoogle}
                cookiePolicy={'single_host_origin'}
                />
            </div>
            <Link to="/register">
                <p className="register__link">
                    Không có tài khoản? Đăng ký ở đây
                </p>
            </Link>
        </div>
    </main>
    )
}

export default Login
