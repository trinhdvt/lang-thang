import axios from 'axios'
import React, { useEffect, useState } from 'react'
import Empty from '../../utils/Empty/Empty'
import Loading from '../../utils/Loading/Loading'
import CurrentPost from '../home/components/CurrentPost'
import bookmarkApis from './enum/bookmark-apis'

const Bookmarks = () => {
    const [listBookmarks, setListBookmarks] = useState([])
    const [currentPage, setCurrentPage] = useState(0)
    const [isEmpty, setIsEmpty] = useState(false)
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        const getListBookmarks = async () => {
            try {
                const res = await axios.get(bookmarkApis.getBookmarkedPosts, {
                    params: { page: currentPage }
                })
                setListBookmarks([...listBookmarks, ...res.data])
                if (res.data.length === 0 || res.data.length < 10) {
                    setIsEmpty(true)
                }
                setIsLoading(false)

            } catch (err) {
                console.log(err)
            }
        }
        getListBookmarks()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentPage])
    return (
        <main className="main__home">
            <div className="container pb-50 pt-30">
                {!isLoading ?
                    <>
                        <div className="font-small text-uppercase pb-15">
                            <h5 style={{ fontSize: '14px' }}>Các bài viết bạn đã bookmark:</h5>
                        </div>
                        {listBookmarks.length === 0 ? <Empty /> :
                            listBookmarks.map((post, index) => {
                                return (
                                    <CurrentPost post={post} key={index} />
                                )
                            })}

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
                    </>
                    :
                    <Loading />
                }
            </div>
        </main>
    )
}

export default Bookmarks
