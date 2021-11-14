import React from 'react'
import _403 from './403.png'
import './notFound.css'

function NotPermission() {

    return (
        <div className="background__404">
            <div className="background__404--image" style={{backgroundImage: `url(${_403})`}}></div>
            <h5>Bạn không được phép truy cập vào trang này</h5>
        </div>
    )
}

export default NotPermission
