import React, { useState } from 'react'
import { Link, useHistory } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { dispatchLogout } from '../../redux/actions/authAction.js'
import ReactHtmlParser from 'react-html-parser'
import Notification from '../body/notification/Notification.js'
import CookiesService from '../../services/CookiesService.js'
import useSocketDataObject from '../../real-time/useSocketDataObject.js'

function Header() {
    const dispatch = useDispatch()
    const auth = useSelector(state => state.auth)
    const userInfor = auth.user
    const history = useHistory()
    const cookiesService = CookiesService.getService()
    const {Unsubscribe_notification} = useSocketDataObject()
    const [keyword, setKeyword] = useState('')
    const {isAdmin} = auth

    const handleChangeKeyword = (e) => {
        const { value } = e.target
        setKeyword(value)
    }

    const handleFindSubmit = (e) => {
        e.preventDefault();
        history.push(`/search?keyword=${keyword}`)
        setKeyword("")
    }


    const handleLogout = () => {
        try {
            cookiesService.clearToken()
            dispatch(dispatchLogout())

            Unsubscribe_notification()
            // Disconnect()
            history.push("/")
        } catch (err) {
            history.push("/")
        }
    }

    const loginMenu = () => {
        return (
            <div className="menu__right">
                <ul>
                    <li>
                        <Link to="/posts/new">
                            <i className="far fa-edit"></i>
                            <span> Viết bài</span>
                        </Link>
                    </li>
                    <li >
                        <Link to="/bookmarks">
                            <i className="far fa-bookmark"></i>
                            <span>BookMark</span>
                        </Link>
                    </li>
                    <Notification/>
                </ul>

                <div className="menu__right--avatar">
                    <img src={ReactHtmlParser(userInfor.avatarLink)} />
                    <i className="fas fa-caret-down"></i>
                    <div className="menu__right--dropdown" id="dropDown" >
                        {isAdmin ?
                        <Link to="/admin/dashboard">
                            <i className="fal fa-users-cog"></i>
                            <p>Trang admin</p>
                        </Link> : null}
                        <Link to={`/profile/${userInfor.accountId}`}>
                            <i className="far fa-user"></i>
                            <p>Trang cá nhân</p>
                        </Link>
                        <Link to="/myprofile/edit">
                            <i className="far fa-user-edit"></i>
                            <p className="p-edit">Chỉnh sửa</p>
                        </Link>
                        <Link to="/" onClick={handleLogout}>
                            <i className="far fa-sign-out-alt"></i>
                            <p>Đăng xuất</p>
                        </Link>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <>
            <header>
                <div className="menu container">
                    <div className="menu__left">
                        <Link to="/">
                            <p>
                                <i className="fal fa-map-marker-edit"></i>
                                <span>Lang Thang</span>
                            </p>
                        </Link>
                        <form onSubmit={handleFindSubmit}>
                            <i className="fal fa-search"></i>
                            <input
                                onChange={handleChangeKeyword}
                                value={keyword}
                                type="text"
                                placeholder="Tìm kiếm theo tiêu đề bài viết hoặc nội dung" />
                        </form>

                    </div>
                    {auth.isLogged ? loginMenu() :
                    <div className="menu__right menu__right__login">
                        <Link to="/login">
                                <i className="far fa-user"></i>
                            Đăng nhập
                           
                        </Link>
                         </div>
                    }
                </div>
            </header>
        </>

    )
}

export default Header
