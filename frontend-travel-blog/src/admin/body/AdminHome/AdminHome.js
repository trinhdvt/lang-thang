import './AdminHome.css';
import React, { useState, useEffect } from "react";
import axios from "axios";
import ReactHtmlParse from "react-html-parser";
import { Link } from 'react-router-dom'
import homeApis from './enum/home-apis.js';

function AdminHome() {
    const initialState = {
        userCount: 0,
        postCount: 0,
        reportedPostCount: 0
    }

    const [info, setInfo] = useState(initialState);
    const [topUsers, setTopUsers] = useState([]);
    const [topPosts, setTopPosts] = useState([]);

    useEffect(() => {
        const getInfo = async () => {
            const res = await axios.get(homeApis.getInforSystem)
            if (res) {
                setInfo(res.data)
            }
        }

        const getTopUsers = async () => {
            const res = await axios.get(homeApis.getTopUsers, {
                params: {
                    size: 10
                }
            })
            if (res) {
                setTopUsers(res.data)
            }
        }

        const getTopPosts = async () => {
            const res = await axios.get(homeApis.getTopPosts, {
                params: {
                    prop: "bookmark",
                    size: 10
                }
            })

            if (res) {
                setTopPosts(res.data)
            }
        }
        getInfo()
        getTopUsers()
        getTopPosts()
    }, [])

    const elmPost = topPosts.map((post, index) => {
        return (
            <tr key={post.postId}>
                <th className="text-center" scope="row">{index + 1}</th>
                <td>{post.title.length > 20 ? ReactHtmlParse(post.title.slice(0, 20)) + "..." : ReactHtmlParse(post.title)}</td>
                <td>{post.author.name}</td>
                <td className="text-center">{post.bookmarkedCount}</td>
            </tr>
        )
    })

    const elmUser = topUsers.map((user, index) => {
        return (
            <tr key={user.accountId}>
                <th scope="row" className="text-center">{index + 1}</th>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td className="text-center">{user.followCount}</td>
            </tr>
        )
    })

    return (
        <>
            <main className="admin__home">
                <div className="right-panel">
                    <div className="row">
                        <div className="col-lg-4 pd-15">
                            <Link to="/admin/users">
                                <div className="admin__home--card admin__home--hieuung hieu-ung">
                                    <div className="row">
                                        <div className="col-lg-5">
                                            <div className="icon">
                                                <i className="fad fa-users fa-5x"></i>
                                            </div>
                                        </div>

                                        <div className="col-lg-7">
                                            <div className="title">
                                                <h2>Người dùng</h2>
                                            </div>

                                            <div className="number">
                                                <h1>{info.userCount}</h1>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>

                        <div className="col-lg-4 pd-15">
                            <Link to="/admin/posts">
                                <div className="admin__home--card admin__home--hieuung hieu-ung">
                                    <div className="row">
                                        <div className="col-lg-5">
                                            <div className="icon">
                                                <i className="fal fa-newspaper fa-5x"></i>
                                            </div>
                                        </div>

                                        <div className="col-lg-7">
                                            <div className="title">
                                                <h2>Bài viết</h2>
                                            </div>

                                            <div className="number">
                                                <h1>{info.postCount}</h1>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>

                        <div className="col-lg-4 pd-15">
                            <Link to="/admin/reports">
                                <div className="admin__home--card admin__home--hieuung hieu-ung">
                                    <div className="row">
                                        <div className="col-lg-5">
                                            <div className="icon">
                                                <i className="far fa-exclamation-triangle fa-5x"></i>
                                            </div>
                                        </div>

                                        <div className="col-lg-7">
                                            <div className="title">
                                                <h2>Báo cáo</h2>
                                            </div>

                                            <div className="number">
                                                <h1>{info.reportedPostCount}</h1>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-lg-6">
                            <div className="admin__favoriteposts hieu-ung">
                                <div className="admin__favoriteposts--title">
                                    <h3 className="list-name">Top 10</h3>
                                    <h5 className="web-name">Bài viết được yêu thích nhất</h5>
                                </div>

                                <div className="most-favorite-post-table table-responsive">
                                    <table className="table table-hover">
                                        <thead>
                                            <tr>
                                                <th scope="col" className="text-center">Rank</th>
                                                <th scope="col">Tiêu đề</th>
                                                <th scope="col">Tác giả</th>
                                                <th scope="col" className="text-center">Bookmark</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {elmPost}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div className="col-lg-6">
                            <div className="admin__topusers hieu-ung">
                                <div className="admin__favoriteposts--title">
                                    <h3 className="list-name">Top 10</h3>
                                    <h5 className="web-name">Người có nhiều lượt theo dõi nhất</h5>
                                </div>

                                <div className="most-followed-user-table table-responsive">
                                    <table className="table table-hover">
                                        <thead>
                                            <tr>
                                                <th scope="col" className="text-center">Rank</th>
                                                <th scope="col">Tên</th>
                                                <th scope="col">Email</th>
                                                <th scope="col" className="text-center">Số follow</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {elmUser}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </>
    );
}

export default AdminHome;
