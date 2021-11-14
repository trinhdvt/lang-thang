import axios from 'axios'
import React, { forwardRef, useEffect, useState } from 'react'
import withClickOutsideNotification from './withClickOutsideNotification'
import ReactHtmlParser from 'react-html-parser'
import { Link } from 'react-router-dom'
import { dispatchCountNoti, fetchUnseenNoti, dispatchDecreaseCount, dispatchRemoveCountNoti } from '../../../redux/actions/notificationAction'
import { useDispatch } from 'react-redux'
import { useSelector } from 'react-redux'
import InfiniteScroll from 'react-infinite-scroll-component'
import notificationApis from './enum/notification-apis'

const Notification = forwardRef(({ openNotification, setOpenNotification }, ref) => {
    const [notifications, setNotifications] = useState([])
    const dispatch = useDispatch()
    const notification = useSelector(state => state.notification)
    const { count } = notification
    const [pageNoti, setPageNoti] = useState(1)

    //TODO: Show amount of notifications when load page
    useEffect(() => {
        fetchUnseenNoti().then(count => {
            dispatch(dispatchCountNoti(count))
        })
    }, [])

    //Seen one notification
    const seenNotification = async (notificationId) => {
        const res = await axios.put(notificationApis.seenNotification(notificationId), null)
        if (res) {
            setOpenNotification(!openNotification)
            dispatch(dispatchDecreaseCount())
        }
    }

    const showNotification = () => {
        getFirstNotifications()
        setOpenNotification(!openNotification)
    }


    const getFirstNotifications = async () => {  
        const res = await axios.get(notificationApis.getNotifications, {
            params: {
                page: 0,
                size: 6
            }
        })
        if (res) {
            dispatch(dispatchRemoveCountNoti())
            setNotifications(res.data)
            setPageNoti(1)
        }
    }

    const getNotifications = async () => {
        setPageNoti(pageNoti + 1)
        const res = await axios.get(notificationApis.getNotifications, {
            params: {
                page: pageNoti,
                size: 6
            }
        })
        if (res) {
            dispatch(dispatchRemoveCountNoti())
            setNotifications([...notifications, ...res.data])
        }
    }

    const seenAllNotification = async () => {
        const res = await axios.put(notificationApis.seenAllNotification)
        if(res) {
            var updatedNotifications = notifications.map((notification) => {
                return {...notification, seen: true}
            })
            setNotifications(updatedNotifications)
        }
    }

    return (
        <>
            <li className="menu__right--notify" style={{ position: 'relative' }} ref={ref}>
                {count > 0 ?
                    <div className="menu__notify--unseen">{count}</div>
                : null}

                <i className="far fa-bell" onClick={showNotification}></i>

                {openNotification && (
                    <ul className="menu__notify--dropdown">
                        <div className="menu__notify--header">
                            <h5>Thông báo</h5>
                            <div 
                                className="menu__notify--header-clear-all-notif"
                                onClick={seenAllNotification}
                            >
                                <i className="fal fa-check"></i>
                                Đánh dấu xem tất cả
                            </div>
                        </div>
                        <InfiniteScroll
                            dataLength={notifications.length}
                            next={getNotifications}
                            height={500}
                            hasMore={true}
                            scrollableTarget="menu__notify--dropdown"
                        >
                            <div className="menu__notify--content">
                                {notifications.map((item) => {
                                    return (
                                        <li key={item.notificationId} onClick={() => seenNotification(item.notificationId)}>
                                            <Link to={`/posts/${item.destPost.slug}`}
                                                style={{ display: 'flex' }}
                                            >
                                                <div className="avatar-comment"
                                                    style={{
                                                        flexBasis: '20%',
                                                        backgroundImage: `url(${ReactHtmlParser(item.sourceAccount.avatarLink)})`
                                                    }}></div>

                                                <div style={{ flexBasis: '70%', }}>
                                                    <div >
                                                        {ReactHtmlParser(ReactHtmlParser(item.content))}
                                                    </div>

                                                    <p style={{ fontSize: '12px' }}>{item.notifyDate}</p>
                                                </div>

                                                {!item.seen ? <i style={{ flexBasis: '10%', }} className="fas fa-circle"></i> : null}
                                            </Link>
                                        </li>
                                    )
                                })}
                            </div>
                        </InfiniteScroll>
                    </ul>
                )}
            </li>
        </>
    )
})

export default withClickOutsideNotification(Notification)
