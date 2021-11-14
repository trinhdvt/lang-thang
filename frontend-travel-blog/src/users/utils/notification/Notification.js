import React from 'react'
import './notification.css'

export const showErrMsg = (msg) => {
    return <div className="errMsg">{msg}</div>
}

export const showSuccessMsg = (msg) => {
    return <div className="successMsg">{msg}</div>
}

export const showErrMsg80 = (msg) => {
    return <div className="errMsg-80 errMsg">{msg}</div>
}

export const showSuccessMsg80 = (msg) => {
    return <div className="successMsg successMsg-80">{msg}</div>
}