import React from 'react'
import { Link, } from 'react-router-dom'
import ReactHtmlParser from 'react-html-parser'
import "./currentpost.css"

function CurrentPost({ post }) {
    return (
        <div className="transition-normal hover-up-2 " key={post.postId} >
            <div className="row-1 mb-40 list-style-1 current-post">
                <div className="col-md-4">
                    <div className="post-thumb position-relative border-radius-5">
                        <div className="img-hover-slide border-radius-5 position-relative"
                            style={{ backgroundImage: `url(${ReactHtmlParser(post.postThumbnail)})` }}
                        >
                            <Link to={{ pathname: `/posts/${post.slug}`, state: { id: post.postId } }}
                                className="img-link"></Link>
                        </div>
                    </div>
                </div>

                <div className="col-md-8 align-self-center" style={{ margin: '10px 0' }}>
                    <div className="post-content">
                        <div className="current-post__category-area mb-10 ">
                            {post.categories.map((item) => {
                                return (
                                    <Link to={{ pathname: `/category/${item.categoryId}` }} key={item.categoryId}>
                                        <div className="current-post__category" key={item.categoryId}>{item.categoryName}</div>
                                    </Link>
                                )
                            })}
                        </div>

                        <h5 className="current-post__title mb-10">
                            <Link to={{ pathname: `/posts/${post.slug}`, state: { id: post.postId } }}
                                href="">{ReactHtmlParser(post.title)}</Link>
                        </h5>

                        
                        
                        <div className="entry-meta meta-1 float-left font-x-small text-uppercase">
                            <span className="post-on">{post.bookmarkedCount} bookmark</span>
                            <span className="time-reading has-dos">
                                {post.commentCount} bình luận
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default CurrentPost
