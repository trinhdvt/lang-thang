import React from 'react'
import { useParams } from 'react-router-dom'
import { useSelector } from 'react-redux'
import Profile from './Profile'
import MyProfile from './MyProfile'

function ProfileWrapper() {
    const auth = useSelector(state => state.auth)
    const id = useParams().id

    const isMyProfile = (id == auth.user.accountId)

    return (
        <>
            {isMyProfile ? <MyProfile /> : <Profile />}
        </>
    )
}

export default ProfileWrapper
