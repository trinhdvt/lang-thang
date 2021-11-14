import axios from 'axios';
import React, { useState } from 'react'
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom'
import Empty from '../../utils/Empty/Empty';
import Loading from '../../utils/Loading/Loading';
import CurrentPost from '../home/components/CurrentPost';
import searchApis from './enum/search-apis';

const SearchPage = () => {
    const location = useLocation()
    const search = location.search;
    const params = new URLSearchParams(search);
    const keyword = params.get('keyword');
    const [currentPage, setCurrentPage] = useState(0)
    const [postsResult, setPostsResult] = useState([])
    //Kiem tra trang tiep theo co rong k?
    const [isEmpty, setIsEmpty] = useState(false)
    const [isLoading, setIsLoading] = useState(true)


    useEffect(() => {
        const findPost = async () => {
            setCurrentPage(0)
            try {
                const res = await axios.get(searchApis.getPosts, {
                    params: {
                        keyword: keyword
                    }
                })
                setPostsResult(res.data)
            } catch (error) {
                console.log(error)
            }
        }
        if (keyword) {
            findPost()
        }
    }, [keyword])

    useEffect(() => {
        try {
            const nextPost = async () => {
                const res = await axios.get(searchApis.getPosts, {
                    params: {
                        keyword: keyword,
                        page: currentPage
                    }
                })

                if (res) {
                    setPostsResult([...postsResult, ...res.data])
                    if (res.data.length === 0 || res.data.length < 10) {
                        setIsEmpty(true)
                    }
                    setIsLoading(false)
                }
            }
            nextPost()
        } catch (error) {
            console.log(error)
        }
    }, [currentPage])

    return (
        <main className="main__home">
            <div className="container pb-50 pt-30">
                {!isLoading ?
                    <>
                        <div className="font-small text-uppercase pb-15">
                            <h5 style={{ fontSize: '14px' }}>Kết quả tìm kiếm của: {keyword}</h5>
                        </div>
                        {postsResult.length === 0 ? <Empty /> :
                            postsResult.map((post) =>

                                <CurrentPost post={post} key={post.postId} />

                            )}
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

export default SearchPage
