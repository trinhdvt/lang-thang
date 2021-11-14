import axios from 'axios'
import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router'
import { showErrMsg, showSuccessMsg } from '../../utils/notification/Notification'
import { isLength, isMatch } from '../../utils/validation/Validation'
import { useHistory } from 'react-router-dom'
import authApis from './enum/authentication-apis'
import { successNotification } from '../../utils/notification/ToastNotification'


function ResetPassword() {
    const {token} = useParams()
    const [canChange, setCanChange] = useState(false)

    const [data, setData] = useState({
        password: '',
        matchedPassword: '',
        err:'',
        success: ''
    })
    const history = useHistory()

    useEffect(() => {
        const confirmToken = async () => {
            try {
                const res = await axios.get(authApis.resetPassword, {
                    params: {
                        token: token
                    }
                })
                if(res) {
                    setCanChange(true)
                }
            } catch (error) {
                if(error.response.status === 401){
                    setData({...data, err: 'Mã không hợp lệ hoặc không tồn tại', success: ''})
                }
                else {
                    setData({...data, err: 'Mã đã hết hạn, hãy thực hiện lại yêu cầu', success: ''})
                }
            }
            
        }
        if(token) {
            confirmToken()
        }
    }, [token])

    const handleChangeInput = (e) => {
        const { name, value } = e.target
        setData({ ...data, [name]: value, err: '', success: '' })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        if(isLength(password)){
            return setData({...data, err: "Mật khẩu không đủ 6 ký tự", success: ''})
        }
        if(!isMatch(matchedPassword, password)){
            return setData({...data, err:"Mật khẩu không giống nhau", success: ''})
        }

        var resetForm = new FormData()
        resetForm.append('token', token)
        resetForm.append('password', password)
        resetForm.append('matchedPassword', matchedPassword)
        
        try{
            const res = await axios.put(authApis.resetPassword, resetForm)
            if(res) {
                successNotification('Đã đổi mật khẩu thành công ✔')
                history.push("/login")
            }
        }catch(err){
            if(err.response.status === 410){
                setData({...data, err: 'Quá thời gian lấy lại mật khẩu', success: ''})
            }
            else if(err.response.status === 401){
                setData({...data, err: 'Mã không hợp lệ', success: ''})
            }
            else if(err.response.status === 400){
                setData({...data, err: 'Mật khẩu không đủ 6 kí tự', success: ''})
            }
            else {
                setData({...data, err: 'Đã có lỗi xảy ra', success: ''})
            }
        }
    }

    const {password, matchedPassword, err, success} = data 
    return (
        <main className="main__auth">
            <div className="register">
                <h3>Lấy lại mật khẩu</h3>
                {err && showErrMsg(err)}
                {success && showSuccessMsg(success)}
                {canChange && 
                <form onSubmit={handleSubmit}>
                    <input type="password" placeholder="Nhập mật khẩu mới" name="password"
                        value={password} onChange={handleChangeInput} />
                    <input type="password" placeholder="Nhập lại mật khẩu mới" name="matchedPassword"
                        value={matchedPassword} onChange={handleChangeInput} />
                    <button type="submit">
                        Xác nhận
                </button>
                </form>  }
                
            </div>
        </main>
    )
}

export default ResetPassword
