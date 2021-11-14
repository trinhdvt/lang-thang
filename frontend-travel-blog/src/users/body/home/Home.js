import React, { useState, useEffect } from 'react'
import axios from 'axios';
import "./home.css"
import { Link } from 'react-router-dom';
import ReactHtmlParser from 'react-html-parser'
import CurrentPost from './components/CurrentPost';
import CommentPost from './components/CommentPost'
import Loading from '../../utils/Loading/Loading';
import homeApis from './enum/home-apis';
import AOS from 'aos';
import 'aos/dist/aos.css';

function Home() {
  const [currentSlide, setCurrentSlide] = useState(3);
  const [currentLargePage, setCurrentLargePage] = useState(0)
  // Bai viet ở trên cùng
  const [featuredPosts, setFeaturedPosts] = useState([])
  const [cmtPosts, setCmtPosts] = useState([])
  const [recentPosts, setRecentPosts] = useState([])
  const [loadingPage, setLoadingPage] = useState(false)
  const length = 6
  const lengthOfLargeSlide = 3

  const [currentPage, setCurrentPage] = useState(0)
  const [isEmpty, setIsEmpty] = useState(false)


  useEffect(() => {
    const getData = async () => {
      const res = await axios.get(homeApis.getPosts, {
        params: {
          prop: "bookmark",
          size: 10
        }
      })
      if (res) {
        setFeaturedPosts(res.data)
        setLoadingPage(true)
      }
    }
    getData()
  }, [])

  //GET COMMENTS POST
  useEffect(() => {
    const getCmtPosts = async () => {
      const res = await axios.get(homeApis.getPosts, {
        params: {
          prop: "comment",
          size: 11
        }
      })
      if (res) setCmtPosts(res.data)
    }
    getCmtPosts()
  }, [])

  useEffect(() => {
    const getRecentPost = async () => {
      const res = await axios.get(homeApis.getPosts, {
        params: {
          page: currentPage
        }
      })
      if (res) {
        setRecentPosts([...recentPosts, ...res.data])
        if (res.data.length === 0 || res.data.length < 10) {
          setIsEmpty(true)
        }
      }
    }
    getRecentPost()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage])

  useEffect(() => {
    AOS.init({
      duration: 1000
    })
    AOS.refresh()
  }, [])


  // Di chuyen slide
  const nextSlide = () => {
    setCurrentSlide(currentSlide === length - 1 ? 3 : currentSlide + 1)
  }

  const prevSlide = () => {
    setCurrentSlide(currentSlide === 3 ? length - 1 : currentSlide - 1)
  }

  const nextLargeSlide = () => {
    setCurrentLargePage(currentLargePage === lengthOfLargeSlide - 1 ? 0 : currentLargePage + 1)
  }
  const prevLargeSlide = () => {
    setCurrentLargePage(currentLargePage === 0 ? lengthOfLargeSlide - 1 : currentLargePage - 1)
  }

  return (
    <>
      {loadingPage ?
        <main className="main__home">
          <div className="container pt-30 mb-15">
            <div data-aos='fade-up'>
              {featuredPosts.map((post, index) => {
                if (index < 3) {
                  return (
                    <div key={post.postId}>
                      {index === currentLargePage &&

                        <div
                          className="home__thumbnail--large"
                          style={{ backgroundImage: `url(${ReactHtmlParser(post.postThumbnail)})` }}
                        >
                          <div className="home__thumbnail--content">
                            {post.categories.map((item) => {
                              return (
                                <Link to={{ pathname: `/category/${item.categoryId}` }} key={item.categoryId}>
                                  <div className="home__thumbnail--category" key={item.categoryId}>{item.categoryName}</div>
                                </Link>
                              )
                            })}
                            <Link to={{ pathname: `/posts/${post.slug}` }}>
                              <h1>{post.title}</h1>
                            </Link>
                            <div className="entry-meta meta-1 text-12-px text-white text-uppercase">
                              <span className="post-on">{post.bookmarkedCount} bookmark</span>

                              <span className="time-reading has-dos">
                                {post.commentCount} bình luận
                              </span>
                            </div>
                          </div>

                          <div className="home__thumbnail--arrow">
                            <i className="fal fa-long-arrow-left" onClick={prevLargeSlide}></i>
                            <i className="fal fa-long-arrow-right" onClick={nextLargeSlide}></i>
                          </div>
                        </div>
                      }
                    </div>
                  )
                }
              })}
            </div>
          </div>

          <div className="container">
            {/* FEATURED POST */}
            <div className="featured__post pt-15 font-small text-uppercase pb-15">
              <h5 style={{ fontSize: '14px' }}>Các bài đăng nổi bật</h5>
            </div>
            <div className="row-1">
              <div className="col-lg-8 mb-30">
                {/* TODO: NỔI BẬT POSTS */}
                <div data-aos='fade-up'>
                  {featuredPosts.map((post, index) => {
                    if (index < 6 && index >= 3) {
                      return (
                        <div key={post.postId}>
                          {index === currentSlide && (
                            <div className="slider thumb-overlay hieu-ung ">
                              <div className="arrow-cover">
                                <i className="fal fa-long-arrow-left" onClick={prevSlide}></i>
                                <i className="fal fa-long-arrow-right" onClick={nextSlide}></i>
                              </div>

                              <img src={ReactHtmlParser(post.postThumbnail)} alt='travel image' className='image' />

                              <div className="post-content-overlay text-white ml-30 mr-30 pb-30">
                                <h3 className="post-title mb-20" style={{ fontSize: 20 }}>
                                  <Link to={{ pathname: `/posts/${post.slug}` }}
                                    className="text-white">
                                    {ReactHtmlParser(post.title)}
                                  </Link>
                                </h3>
                                <div className="entry-meta meta-1 font-small text-white mt-10 " style={{ textAlign: 'left' }}>
                                  <span>{post.bookmarkedCount} lượt bookmark</span>
                                  <span className="has-dos">{post.commentCount} bình luận</span>
                                </div>
                              </div>
                            </div>
                          )}
                        </div>
                      )
                    }
                  })
                  }
                </div>
              </div>

              {featuredPosts.map((post, index) => {
                if (index > 5) {
                  return (
                    <div className="col-lg-4 col-md-6 mb-30" key={post.postId} data-aos='fade-up'>
                      <div className="post-card-1 border-radius-10 hover-up-1">
                        <Link to={{ pathname: `/posts/${post.slug}` }}>
                          <div className="thumb-overlay img-hover-slide position-relative" style={{ background: `url(${ReactHtmlParser(post.postThumbnail)})` }}>
                          </div>
                        </Link>

                        <div className="post-content p-30">
                          <div className="current-post__category-area mb-10">
                            {post.categories.map((item) => {
                              return (
                                <Link to={{ pathname: `/category/${item.categoryId}` }} key={item.categoryId}>
                                  <div className="current-post__category" key={item.categoryId}>{item.categoryName}</div>
                                </Link>
                              )
                            })}
                          </div>
                          <div className="d-flex post-card-content">
                            <h5 className="post-title">
                              <Link to={{ pathname: `/posts/${post.slug}`, state: { id: post.postId } }}>
                                {ReactHtmlParser(post.title)}
                              </Link>
                            </h5>

                            <div className="entry-meta meta-1 float-left font-x-small text-uppercase">
                              <span>{post.bookmarkedCount} lượt bookmark</span>
                              <span className="time-reading has-dos">{post.commentCount} bình luận</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  )
                }
              })
              }
            </div>

          </div>

          {/* TODO: NEW POSTS */}
          <div className="pt-15 pb-50">
            <div className="container">
              <div className="row-1">
                <div className="col-lg-8">
                  <div className="post-module-2">
                    <div className="mb-30 position-relative">
                      <h5 className="mb-30 text-uppercase" style={{ fontSize: '14px' }}>Bài đăng mới</h5>
                    </div>

                    <div className="loop-list">
                      {recentPosts.map((post) => {
                        return (
                          <div data-aos='fade-up' key={post.postId}>
                            <CurrentPost post={post} key={post.postId} />
                          </div>
                        )
                      })}
                    </div>

                    <div className="pagination-area mb-30">
                      <nav aria-label="Page navigation example">
                        <ul className="pagination justify-content-start">
                          <li className={`page-item" ${isEmpty ? 'disabled' : null}`}
                            onClick={() => setCurrentPage(currentPage + 1)}>
                            <div className="page-link">
                              <i className="fal fa-long-arrow-right"></i>
                            </div>
                          </li>
                        </ul>
                      </nav>
                    </div>
                  </div>
                </div>

                <div className="col-lg-4">
                  <div className="mb-15">
                    <h5 className="text-uppercase" style={{ fontSize: '14px' }}>Nhiều bình luận nhất</h5>
                  </div>

                  {cmtPosts.map((item) => {
                    return (
                      <div data-aos='fade-up' key={item.postId}>
                        <CommentPost item={item} key={item.postId} />
                      </div>
                    )
                  })}
                </div>
              </div>
            </div>
          </div>
        </main>
        :
        <Loading />
      }
    </>
  )
}

export default Home
