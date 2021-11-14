import React, { useState, useEffect, forwardRef } from 'react'
import { useSelector } from 'react-redux'
import axios from 'axios'
import CurrentPost from '../home/components/CurrentPost'
import Empty from '../../utils/Empty/Empty'
import ReactHtmlParser from 'react-html-parser'
import { Link, useHistory } from 'react-router-dom'
import profileApis from './enum/profile-apis'
import withClickOutsideFollowerDialog from './withClickOutsideFollower'
import InfiniteScroll from 'react-infinite-scroll-component'



const MyProfile = forwardRef(({ openFollowerDialog, setOpenFollowerDialog }, ref) => {
  const auth = useSelector(state => state.auth)
  const userInfor = auth.user
  const history = useHistory()
  const [posts, setPosts] = useState([])
  const [drafts, setDrafts] = useState([])
  //false load bai viet, true load ban nhap
  const [isLoadPost, setIsLoadPost] = useState(false)
  const [callback, setCallback] = useState(false)
  const [currentPageDrafts, setCurrentPage] = useState(0)
  const [currentPagePosts, setCurrentPagePosts] = useState(0)

  const [isEmpty, setIsEmpty] = useState(false)
  const [isEmptyPosts, setIsEmptyPosts] = useState(false)

  const [followerList, setFollowerList] = useState([])
  const [followerPage, setFollowerPage] = useState(0)
  const [isLoadingFollower, setIsLoadingFollower] = useState(false)

  //get posts
  useEffect(() => {
    const getPosts = async () => {
      const res = await axios.get(profileApis.getPostsOfUser(userInfor.accountId), {
        params: {
          page: currentPagePosts
        }
      })
      if (res) {
        setPosts([...posts, ...res.data])
        if (res.data.length === 0 || res.data.length < 10) {
          setIsEmptyPosts(true)
        }
      }
    }

    if (userInfor.accountId) {
      getPosts()
    }
  }, [userInfor.accountId, currentPagePosts])

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

  //GET DRAFTS
  useEffect(() => {
    getDrafts()
  }, [userInfor.accountId, callback, currentPageDrafts])

  const getDrafts = async () => {
    const res = await axios.get(profileApis.getDraftsOfUser, {
      params: {
        page: currentPageDrafts
      }
    })
    if (res) {
      setDrafts([...drafts, ...res.data])
      if (res.data.length === 0 || res.data.length < 10) {
        setIsEmpty(true)
      }
    }
  }

  const handleClickLoad = (value) => {
    setIsLoadPost(value)
  }

  const handleDelDraft = async (postId) => {
    const res = await axios.delete(profileApis.deleteDraft(postId))

    if (res) {
      setDrafts([])
      setCallback(!callback)
    }
  }

  const redirectToAnotherProfile = (id) => {
    setOpenFollowerDialog(false)
    history.push(`/profile/${id}`)
  }

  const followUser = async (id) => {
    await axios.put(profileApis.followUser(id), null)
    const editedFollowerList = followerList.map((follower) =>
      follower.accountId === id ? { ...follower, followed: !follower.followed } : follower
    )
    setFollowerList(editedFollowerList)
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
                    <button
                      className={`${follower.followed ? 'button-light' : 'button-primary-no-hover'} button`}
                      style={{ padding: '3px 8px' }}
                      onClick={() => { followUser(follower.accountId) }}
                    >
                      {follower.followed ? "Đang theo dõi" : "Theo dõi lại"}
                    </button>
                  </div>
                </div>
              )
              : <span
                  style={{display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%'}}
                >
                  Loading...
                </span>
            }
          </InfiniteScroll>
        </div>

      </div>
    )
  }

  useEffect(() => {
    if (openFollowerDialog) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
  }, [openFollowerDialog]);

  const showFollower = () => {
    getFirstFollowers()
    setOpenFollowerDialog(!openFollowerDialog)
  }

  return (
    <main className="main__home">
      <div className="container">
        <div className="row">
          <div ref={ref}>
            {openFollowerDialog && followerDialog()}
          </div>

          <div className="mt-30 mb-30 col-lg-4" >
            <div className="information mb-30">
              <div className="author-info">
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div className="avatar" style={{ backgroundImage: `url(${ReactHtmlParser(userInfor.avatarLink)})` }} ></div>
                  <div className="post-count">
                    <h4>{userInfor.postCount}</h4>
                    <p>Bài viết</p>
                  </div>
                  <div className="follower-count inline-item" onClick={showFollower}>
                    <h4 >{userInfor.followCount}</h4>
                    <p>Người theo dõi</p>
                  </div>
                </div>
                <h5 className="author-name" style={{ marginTop: '5px' }}>{userInfor.name}</h5>
                <div className="author__social">
                  <i className="fab fa-instagram" onClick={() => window.open(ReactHtmlParser(userInfor.instagramLink), '_blank')} ></i>
                  <i className="fab fa-facebook-square" onClick={() => window.open(ReactHtmlParser(userInfor.fbLink), '_blank')}></i>
                </div>
                <p style={{ fontSize: "14px" }}>
                  {ReactHtmlParser(userInfor.about)}
                </p>
                <div className="author__infor--count mt-15 d-flex">
                  <div className="count__div">
                    <h5>Số Bookmark: </h5>
                    <h4 >{userInfor.bookmarkOnOwnPostCount}</h4>
                  </div>
                  <div className="count__div">
                    <h5>Số Comment: </h5>
                    <h4>{userInfor.commentOnOwnPostCount}</h4>
                  </div>
                </div>

                <div className="myprofile__choice">
                  <button className={`child-1 ${!isLoadPost ? 'active' : null}`} onClick={() => handleClickLoad(false)}>Xem các bài viết</button>
                  <button className={`child-2 ${isLoadPost ? 'active' : null}`} onClick={() => handleClickLoad(true)} >Xem các bài nháp</button>
                </div>
              </div>
            </div>
          </div>
          {/* TODO: TAB POSTS */}
          <div className="mt-30 col-lg-8" >
            {/* LOAD NHAP HAY BAI VIET */}
            {!isLoadPost ?
              posts.length === 0 ? <Empty /> :
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
                        <li className={`page-item" ${isEmptyPosts ? 'disabled' : null}`}
                          onClick={() => setCurrentPagePosts(currentPagePosts + 1)}>
                          <div className="page-link">
                            <i className="fal fa-long-arrow-right"></i>
                          </div>
                        </li>
                      </ul>
                    </nav>
                  </div>
                </>
              :
              // TODO: PHAN BAI NHAP
              drafts.length === 0 ? <Empty /> :
                <>
                  {
                    drafts.map((draft, index) => {
                      return (
                        <div key={index} className="myprofile__draft ">
                          <h5>{ReactHtmlParser(draft.title)}</h5>
                          <div style={{ textAlign: 'end' }}>
                            <Link to={`/posts/${draft.slug}/edit`}>
                              <button className="button button-primary mr-5">
                                <i className="fal fa-pencil"></i>
                                Tiếp tục viết</button>
                            </Link>

                            <button className="button button-red" onClick={() => handleDelDraft(draft.postId)}>
                              <i className="fal fa-trash-alt"></i>
                              Xóa
                            </button>
                          </div>

                        </div>
                      )
                    })
                  }
                  <div className="pagination-area mb-30">
                    <nav aria-label="Page navigation example">
                      <ul className="pagination justify-content-start">
                        <li className={`page-item" ${isEmpty ? 'disabled' : null}`}
                          onClick={() => setCurrentPage(currentPageDrafts + 1)}>
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
  )
})

export default withClickOutsideFollowerDialog(MyProfile)
