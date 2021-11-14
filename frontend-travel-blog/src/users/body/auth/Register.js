import axios from 'axios'
import React from 'react'
import { useState } from 'react'
import { showErrMsg, showSuccessMsg } from '../../utils/notification/Notification'
import { isEmail, isEmpty, isLength, isMatch } from '../../utils/validation/Validation'
import {GoogleLogin} from 'react-google-login'
import authApis from './enum/authentication-apis'



function Register() {
    const [user, setUser] = useState({
        name: '',
        email: '',
        password: '',
        matchedPassword: '',
        err: '',
        success: ''
    })

    const {name, email, password, matchedPassword, err, success} = user

    const handleChangeInput = (e) => {
        const {name, value} = e.target
        setUser({...user, [name]: value, err: '', success: ''})
    }

    const handleSubmit = async e => {
        e.preventDefault()
        
        if(isEmpty(email) || isEmpty(password) || isEmpty(matchedPassword) || isEmpty(name)){
            return setUser({...user, err: 'Hãy điền đầy đủ thông tin', success: ''})
        }

        if(!isEmail(email)){
            return setUser({...user, err: 'Email không đúng định dạng', success: ''})
        }

        if(isLength(password)){
            return setUser({...user, err: "Mật khẩu phải lớn hơn 6 ký tự", success: ''})
        }

        if(!isMatch(matchedPassword, password)){
            return setUser({...user, err:"Mật khẩu không giống nhau", success: ''})
        }

        try{
            var registerForm = new FormData()
            registerForm.append('name', name)
            registerForm.append('email', email)
            registerForm.append('password', password)
            registerForm.append('matchedPassword', matchedPassword)

            const res = await axios.post(authApis.register, registerForm)
            if(res.status === 202) {
                setUser({...user, err: '', success: 'Kiểm tra email để kích hoạt tài khoản'})
            }
        }catch(err){
            if(err.response.status === 423){
                setUser({...user, err: ' Email đã đăng ký nhưng chưa kích hoạt', success: ''})
            }
            else if(err.response.status === 409){
                setUser({...user, err: 'Email đã tồn tại', success: ''})
            }
            else if(err.response.status === 400){
                setUser({...user, err: 'Thông tin không hợp lệ', success: ''})
            }
            else {
                setUser({...user, err: 'Đã có lỗi xảy ra', success: ''})
            }
        }
    }

    const responseGoogle = async (response) => {
        console.log(response)
        try{
            const google_token = response.tokenId
            var registerForm = new FormData()
            registerForm.append('google_token', google_token)
    
            const res = await axios.post(authApis.registerByGoogle, registerForm)
            setUser({...user, err: '', success: 'Mật khẩu đã được gửi tới gmail của bạn'})           
        }catch(err){
            if(err.response.status === 401){
                setUser({...user, err: 'Sai email hoặc password', success: ''})
            }
            else if(err.response.status === 400){
                setUser({...user, err: 'Email hoặc password không hợp lệ', success: ''})
            }
            else {
                setUser({...user, err: 'Đã có lỗi xảy ra', success: ''})
            }
        }
        
    }

    return (
        <main className="main__auth">
        <div className="register">
            <h3>Đăng ký</h3>
            {err && showErrMsg(err)}
            {success && showSuccessMsg(success)}
            <form onSubmit={handleSubmit}>
                <input type="text" placeholder="Tên tài khoản" name="name" 
                value={name} onChange={handleChangeInput}/>

                <input type="email" placeholder="Nhập email" name="email" 
                value={email} onChange={handleChangeInput} />

                <input type="password" placeholder="Mật khẩu" name="password"
                value={password} onChange={handleChangeInput}/>

                <input type="password" placeholder="Nhập lại mật khẩu" name="matchedPassword" 
                value={matchedPassword} onChange={handleChangeInput}/>

                <button type="submit">
                    ĐĂNG KÝ
                </button>
                
            <div className="register__divider">
                <span>OR</span>
            </div>
            </form>
            <div className="register__social">
                <GoogleLogin
                clientId="545452035521-c4eljpuu1281eml2ci6kaud39s5kc9ct.apps.googleusercontent.com"
                buttonText="Đăng ký bằng Google"
                onSuccess={responseGoogle}
                cookiePolicy={'single_host_origin'}
                />
            </div>

        </div>
    </main>
    )
}

export default Register
