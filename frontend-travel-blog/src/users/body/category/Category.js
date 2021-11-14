import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import axios from 'axios'
import Empty from '../../utils/Empty/Empty'
import CurrentPost from '../home/components/CurrentPost'
import Loading from '../../utils/Loading/Loading'
import categoryApis from './enum/category-apis'

const Category = () => {
    const params = useParams()
    const [currentPage, setCurrentPage] = useState(0)
    const [postsResult, setPostsResult] = useState([])
    //Kiem tra trang tiep theo co rong k?
    const [isEmpty, setIsEmpty] = useState(false)
    const [isLoading, setIsLoading] = useState(false)

    useEffect(() => {
        const findPost = async () => {
            const res = await axios.get(categoryApis.getPostsByCategory(params.id), {
                params: {
                    page: currentPage
                }
            })
            if (res) {
                setPostsResult([...postsResult, ...res.data])
                setIsLoading(true)
                if (res.data.length === 0 || res.data.length < 10) {
                    setIsEmpty(true)
                }
            }
        }
        findPost()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentPage])

    return (
        <main className="main__home">
            <div className="container pb-50 pt-30">
                {isLoading ?
                    <>
                        <div className="font-small text-uppercase pb-15">
                            <h5 style={{ fontSize: '14px' }}>Kết quả tìm kiếm:</h5>
                        </div>
                        {postsResult.length === 0 ? <Empty /> :
                            postsResult.map((post, index) => {
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
                    : <Loading />
                }
            </div>
        </main>
    )
}

export default Category
