import React from 'react'
import { useState } from 'react'
import { showErrMsg, showSuccessMsg } from '../../utils/notification/Notification'
import axios from 'axios'
import { isEmail, isEmpty } from '../../utils/validation/Validation'
import authApis from './enum/authentication-apis'


function ForgotPassword() {
    const [data, setData] = useState({
        email: '',
        err: '',
        success: ''
    })
    const handleChangeInput = (e) => {
        const { name, value } = e.target
        setData({ ...data, [name]: value, err: '', success: '' })
    }
    const handleSubmit = async (e) => {
        e.preventDefault()

        if (isEmpty(email)) {
            return setData({ ...data, err: 'Hãy điền đầy đủ thông tin', success: '' })
        }

        if (!isEmail(email)) {
            return setData({ ...data, err: 'Email không đúng định dạng', success: '' })
        }

        var resetForm = new FormData()
        resetForm.append('email', email)
        try {
            const res = await axios.post(authApis.resetPassword, resetForm)
            if (res) {
                setData({ ...data, success: 'Hãy kiểm tra email của bạn', err: '' })
            }
        } catch (err) {
            console.log(err)
            if (err.response.status === 404) {
                setData({ ...data, err: 'Email không hợp lệ', success: '' })
            }

            else if (err.response.status === 400) {
                setData({ ...data, err: 'Email không đúng định dạng', success: '' })
            }
            else if (err.response.status === 403) {
                setData({ ...data, err: 'Tài khoản chưa kích hoạt', success: '' })
            }
            else {
                setData({ ...data, err: 'Đã có lỗi xảy ra', success: '' })
            }
        }
    }

    const { email, err, success } = data

    return (
        <main className="main__auth">
            <div className="register">
                <h3>Lấy lại mật khẩu</h3>
                {err && showErrMsg(err)}
                {success && showSuccessMsg(success)}
                <form onSubmit={handleSubmit}>

                    <input type="email" placeholder="Nhập tài khoản email của bạn" name="email"
                        value={email} onChange={handleChangeInput} />

                    <button type="submit">
                        Gửi 
                </button>
                </form>
            </div>
        </main>
    )
}

export default ForgotPassword
