import axios from 'axios'
import React, { forwardRef, useEffect, useState } from 'react'
import { useHistory, useLocation, useParams } from 'react-router-dom'
import Empty from '../../utils/Empty/Empty'
import Loading from '../../utils/Loading/Loading'
import CurrentPost from '../home/components/CurrentPost'
import ReactHtmlParser from 'react-html-parser'
import "./Profile.css"
import profileApis from './enum/profile-apis'
import Cookies from 'js-cookie'
import withClickOutsideFollowerDialog from './withClickOutsideFollower'
import { useSelector } from 'react-redux'
import InfiniteScroll from 'react-infinite-scroll-component'


const Profile = forwardRef(({ openFollowerDialog, setOpenFollowerDialog }, ref) => {
  const location = useLocation()
  const auth = useSelector(state => state.auth)
  const history = useHistory()
  const id = useParams().id
  const [userInfor, setUserInfor] = useState({})
  const [posts, setPosts] = useState([])
  const [isLoading, setLoading] = useState(false)
  const [followerList, setFollowerList] = useState([])
  const [followerPage, setFollowerPage] = useState(0)
  const [isLoadingFollower, setIsLoadingFollower] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)


  useEffect(() => {
    const getUserInfor = async () => {
      const res = await axios.get(profileApis.getUserInfor(id))
      if (res) {
        setUserInfor(res.data)
      }
    }
    const getFirstPosts = async () => {
      const res = await axios.get(profileApis.getPostsOfUser(id), {
        params: {
          page: 0,
          size: 10
        }
      })
      setPosts(res.data)
      setLoading(true)
    }

    getUserInfor()
    getFirstPosts()

    return () => {
      setLoading(false)
      setCurrentPage(0)
      setIsLoadingFollower(false)
    }

  }, [id])

  const getPosts = async () => {
    const res = await axios.get(profileApis.getPostsOfUser(id), {
      params: {
        page: currentPage + 1,
        size: 10
      }
    })
    setPosts([...posts, ...res.data])
    setCurrentPage(currentPage + 1)
  }



  const handleClickFollow = async () => {
    if (!Cookies.get("token")) return history.push(`/login?redirectTo=${location.pathname}`)

    const res = await axios.put(profileApis.followUser(id), null)
    if (res) {
      setUserInfor({ ...userInfor, followCount: res.data, followed: !userInfor.followed })
    }
  }

  const followUser = async (id) => {
    const token = Cookies.get("token")
    if (!token) return history.push('/login')
    
    await axios.put(profileApis.followUser(id), null)
    const editedFollowerList = followerList.map((follower) =>
      follower.accountId === id ? { ...follower, followed: !follower.followed } : follower
    )
    setFollowerList(editedFollowerList)
  }


  useEffect(() => {
    if (openFollowerDialog) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
  }, [openFollowerDialog]);

  const redirectToAnotherProfile = (id) => {
    setOpenFollowerDialog(false)
    history.push(`/profile/${id}`)
  }

  const followerDialog = () => {
    return (
      <div className="dialog-container profile__follower-dialog">
        <div className="profile__follower-dialog--header">
          <span>Người theo dõi</span>
          <i
            className="fal fa-times"
            onClick={() => setOpenFollowerDialog(false)}
          ></i>
        </div>
        <div>
          <InfiniteScroll
            dataLength={followerList.length}
            next={getFollowers}
            hasMore={true}
            height={350}
            scrollableTarget="profile__follower-dialog--list"
          >
            {
              isLoadingFollower ?
                followerList.map((follower) =>
                  <div key={follower.accountId}
                    className="profile__follower-dialog--list-item"
                  >
                    <div className="profile__follower-dialog--left">
                      <div
                        className="profile__follower-dialog--avatar"
                        style={{ backgroundImage: `url(${ReactHtmlParser(follower.avatarLink)})` }}
                        onClick={() => redirectToAnotherProfile(follower.accountId)}
                      >
                      </div>

                      <span onClick={() => redirectToAnotherProfile(follower.accountId)}>
                        {follower.name}
                      </span>
                    </div>

                    <div>
                      {auth.user.accountId === follower.accountId ? null
                        :
                        <button
                          className={`${follower.followed ? 'button-light' : 'button-primary-no-hover'} button`}
                          style={{ padding: '3px 8px' }}
                          onClick={() => { followUser(follower.accountId) }}
                        >
                          {follower.followed ? "Đang theo dõi" : "Theo dõi"}
                        </button>
                      }
                    </div>
                  </div>
                )
                : <span
                  style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}
                >
                  Loading...
                </span>
            }
          </InfiniteScroll>
        </div>
      </div>
    )
  }
  const getFollowers = async () => {
    const response = await axios.get(profileApis.loadFollowerList(userInfor.accountId), {
      params: {
        size: 10,
        page: followerPage
      }
    })
    setFollowerList([...followerList, ...response.data])
    setFollowerPage(followerPage + 1)
  }

  const getFirstFollowers = async () => {
    const response = await axios.get(profileApis.loadFollowerList(userInfor.accountId), {
      params: {
        size: 10,
        page: 0
      }
    })
    setFollowerList(response.data)
    setIsLoadingFollower(true)
    setFollowerPage(1)
  }


  const showFollower = () => {
    getFirstFollowers()
    setOpenFollowerDialog(!openFollowerDialog)
  }

  return (
    <>
      {isLoading ?
        <main className="main__home">
          <div className="container">
            <div className="row">
              {/* TODO: TAB INFOR */}
              <div ref={ref}>
                {openFollowerDialog && followerDialog()}
              </div>

              <div className="mt-30 col-lg-4" >
                <div className="information mb-30">
                  <div className="author-info">
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <div className="avatar" style={{ backgroundImage: `url(${ReactHtmlParser(userInfor.avatarLink)})` }} ></div>

                      <div className="post-count">
                        <h4>{userInfor.postCount}</h4>
                        <p>Bài viết</p>
                      </div>

                      <div className="follower-count inline-item">
                        <h4 onClick={showFollower}>{userInfor.followCount}</h4>
                        <p>Người theo dõi</p>
                      </div>
                    </div>
                    <h5 className="author-name mt-10">{userInfor.name}</h5>

                    <div className="author__social">
                      <i className="fab fa-instagram" onClick={() => window.open(ReactHtmlParser(userInfor.instagramLink), '_blank')} ></i>
                      <i className="fab fa-facebook-square" onClick={() => window.open(ReactHtmlParser(userInfor.fbLink), '_blank')}></i>
                    </div>

                    <p className="mt-10" style={{ fontSize: "14px" }}>
                      {userInfor.about}
                    </p>

                    <div className="author__infor--count mt-15 d-flex">
                      <div className="count__div">
                        <h5>Số Bookmark: </h5>
                        <h4 >{userInfor.bookmarkOnOwnPostCount}</h4>
                      </div>

                      <div className="count__div">
                        <h5>Số bình luận: </h5>
                        <h4>{userInfor.commentOnOwnPostCount}</h4>
                      </div>
                    </div>

                    <div className="post-info-button" style={{ marginTop: '10px' }}>
                      {!userInfor.followed ?
                        <button className="button button-primary bookmark-btn"
                          style={{ backgroundColor: '#5869DA', color: 'white' }}
                          onClick={handleClickFollow}>Theo dõi</button>
                        :
                        <button className="button button-light bookmark-btn"
                          onClick={handleClickFollow}
                        >
                          <i className="fal fa-user-check" style={{ marginRight: '5px' }}></i>
                          Đã theo dõi</button>
                      }
                    </div>
                  </div>
                </div>
              </div>
              {/* TODO: TAB POSTS */}
              <div className="mt-30 col-lg-8" >
                {posts.length === 0 ? <Empty /> :
                  <>
                    {
                      posts.map((post, index) => {
                        return (
                          <CurrentPost post={post} key={index} />
                        )
                      })
                    }
                    <div className="pagination-area mb-30">
                      <nav aria-label="Page navigation example">
                        <ul className="pagination justify-content-start">
                          <li className={`page-item" ${((posts.length - 10) < currentPage * 10) ? 'disabled' : null}`}
                            onClick={getPosts}>
                            <div className="page-link">
                              <i className="fal fa-long-arrow-right"></i>
                            </div>
                          </li>
                        </ul>
                      </nav>
                    </div>
                  </>
                }
              </div>
            </div>
          </div>
        </main>

        :
        <div ref={ref}>
          <Loading />
        </div>
      }
    </>
  )
})

export default withClickOutsideFollowerDialog(Profile)
