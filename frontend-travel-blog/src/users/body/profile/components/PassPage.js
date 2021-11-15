import axios from 'axios'
import React, { useState } from 'react'
import { showErrMsg80, showSuccessMsg80 } from '../../../utils/notification/Notification.js'
import { isEmpty, isLength, isMatch } from '../../../utils/validation/Validation.js'
import profileApis from '../enum/profile-apis.js'

const PassPage = () => {
    const [data, setData] = useState({
        oldPassword: '',
        password: '',
        matchedPassword: '',
        err: '',
        success: ''
    })

    const handleChangeInput = (e) => {
        const { name, value } = e.target
        setData({ ...data, [name]: value, success: '', err: ''})
    }

    const handleSubmitPassword = async (e) => {
        e.preventDefault()

        const {oldPassword, password, matchedPassword} = data

        if(isEmpty(oldPassword) || isEmpty(password) || isEmpty(matchedPassword)){
            return setData({...data, err: 'Hãy điền đầy đủ thông tin', success: ''})
        }
        if(isLength(oldPassword) || isLength(password) || isEmpty(matchedPassword)){
            return setData({...data, err: 'Mật khẩu không đủ 6 ký tự', success: ''})
        }
        
        if(!isMatch(matchedPassword, password)){
            return setData({...data, err:"Mật khẩu mới không giống nhau", success: ''})
        }

        try {
            var passForm = new FormData()
            passForm.append('oldPassword', oldPassword)
            passForm.append('password', password)
            passForm.append('matchedPassword', matchedPassword)
            
            const res = await axios.put(profileApis.updatePassword, passForm)

            if(res) {
                setData({...data, err: '', success: 'Đổi mật khẩu thành công'})
            }
        } catch (error) {
            if(error.response.status === 422){ 
                setData({...data, err: 'Sai mật khẩu cũ', success: ''})
            }
            else if(error.response.status === 400){
                setData({...data, err: 'Mật khẩu mới không đủ 6 ký tự', success: ''})
            }
            else {
                setData({...data, err: 'Đã có lỗi xảy ra', success: ''})
            } 
        }
    }

    return (
        <form style={{ display: 'flex', flexDirection: 'column' }} 
            onSubmit={handleSubmitPassword}
            >
            {data.err && showErrMsg80(data.err)}
            {data.success && showSuccessMsg80(data.success)}
            <div className="editProfile__field">
                <div className="editProfile__field-warning">
                    <p style={{textAlign: 'center'}}>Mật khẩu phải dài hơn 6 ký tự</p>
                </div>
            </div>

            <div className="editProfile__field">
                <p>Mật khẩu cũ</p>
                <div>
                <i className="fad fa-lock-alt"></i>
                    <input type="password"
                        onChange={handleChangeInput}
                        name="oldPassword"
                        
                    ></input>
                </div>
            </div>

            <div className="editProfile__field">
                <p>Mật khẩu mới</p>
                <div>
                <i className="fad fa-lock-alt"></i>
                    <input type="password"
                        onChange={handleChangeInput}
                        name="password"
                    ></input>
                </div>

            </div>

            <div className="editProfile__field">
                <p>Xác nhận mật khẩu mới</p>
                <div>
                <i className="fad fa-lock-alt"></i>
                    <input type="password"
                    onChange={handleChangeInput}
                    name="matchedPassword"
                    ></input>
                </div>
            </div>
            
            <div>
                <button type="submit" className="button button-primary-no-hover">
                    <i className="fal fa-save" style={{marginRight: '5px'}}></i>
                    Đổi mật khẩu</button>
            </div>
        </form>
    )
}

export default PassPage
