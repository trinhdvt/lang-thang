import axios from 'axios'
import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import authApis from './enum/authentication-apis.js'


const ActiveGmail = () => {
    const {token} = useParams()
    const [success, setSuccess] = useState(false)
    useEffect(() => {
        const  registerConfirm = async () => {
            var postForm = new FormData()
            postForm.append("token", token)

            try {
                const res = await axios.post(authApis.confirmRegistration, postForm)
                if(res) {
                    setSuccess(true)
                }
            } catch (error) {
                setSuccess(false)
            }
        }
        if(token){
            registerConfirm()
        }
    }, [])

    return (
        <main className="main__auth">
            <div className="register">
            <h3 style={{color: "var(--color-primary)"}}>
                {success ? "Chúc mừng bạn đã đăng ký tài khoản thành công 🎉" : 
                "Mã đăng ký không hợp lệ, không thể kích hoạt tài khoản 🙁"
                }
            </h3>
            </div>
        </main>
    )
}

export default ActiveGmail
