// import '../AdminPage/AdminPage.css';
import React, { useEffect, useState } from "react";
import axios from "axios";
import ReactHtmlParse from "react-html-parser";
import { Link } from 'react-router-dom'

function AdminPosts() {

    const [postList, setPostList] = useState([]);
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10
    })

    useEffect(() => {
        const getPostList = async () => {

            const res = await axios.get(`/api/post`, {
                params: {
                    page: pagination.page
                }
            })
            if (res) {
                setPostList(res.data);
            }

        }

        getPostList();
    }, [pagination])

    const onClickNext = () => {
        setPagination({
            ...pagination,
            page: pagination.page + 1
        })
    }

    const onClickPrev = () => {
        setPagination({
            ...pagination,
            page: pagination.page - 1
        })
    }

    const elmPost = postList.map((post, index) => {
        return (
            <tr key={post.postId}>
                <th scope="row">{index + 1 + (pagination.page * pagination.size)}</th>

                <td>
                    <Link to={`/profile/${post.author.accountId}`} >
                        {post.author ? post.author.name : "Chưa có"}</Link>
                </td>

                <td>
                    <Link to={`/posts/${post.slug}`}>
                        {post.title.length > 40 ? ReactHtmlParse(post.title.slice(0, 40) + "...") : ReactHtmlParse(post.title)}
                    </Link>
                </td>

                <td>
                    {new Date(post.publishedDate).toLocaleString()}
                </td>

                <td className="text-center">{post.commentCount}</td>
                <td className="text-center">{post.bookmarkedCount}</td>
            </tr>
        )
    })

    return (
        <div className="right-panel">
            <div className="post-list">
                <div className="admin__favoriteposts--title">
                    <h2 className="list-name">Danh sách bài viết</h2>
                    <h5 className="web-name">LangThang.com</h5>
                </div>
                <div className="post-table table-responsive">
                    <table className="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Tác giả</th>
                                <th scope="col">Tiêu đề</th>
                                <th scope="col">Ngày viết</th>
                                <th scope="col" className="text-center">Số comment</th>
                                <th scope="col" className="text-center">Số bookmark</th>
                            </tr>
                        </thead>

                        <tbody>
                            {elmPost}
                        </tbody>
                    </table>
                </div>

            </div>

            <div className="admin__pagination">
                <button className="btn btn-secondary mr-10" disabled={pagination.page === 0} onClick={onClickPrev}>
                    <i className="far fa-chevron-double-left mr-5"></i>
                    Prev
                </button>

                <button className="btn btn-secondary" disabled={true}>{pagination.page + 1}</button>

                <button className="btn btn-secondary ml-10" disabled={postList.length < pagination.size} onClick={onClickNext}>
                    Next
                    <i className="far fa-chevron-double-right ml-5"></i>
                </button>
            </div>
        </div>

    );
}

export default AdminPosts;