import React from 'react'
import ReactHtmlParser from 'react-html-parser'
import { Link } from 'react-router-dom'
function CommentPost({ item }) {
    item = {...item, title: ReactHtmlParser(item.title)[0]}
    return (
        <div className="post__popular d-flex hover-up-1" key={item.postId}>
            <div className="post__popular--content media-body" >
                <h6 className="post-title mb-15" title={item.title}>
                    <Link to={{ pathname: `/posts/${item.slug}`, state: { id: item.postId } }}>
                        {item.title.length > 36 ? ReactHtmlParser(item.title.slice(0, 36)) + "..." : ReactHtmlParser(item.title)}
                    </Link>
                </h6>
                
                <div className="entry-meta meta-1 font-x-small text-uppercase">
                    <span>{item.bookmarkedCount} bookmark</span>
                    <span className="post-by has-dos">{item.commentCount} Bình luận</span>
                </div>
            </div>

            <div className="d-flex ml-15 post__popular--image">
                <a className="color-white">
                    <img alt="image" className="border-radius-5 " src={ReactHtmlParser(item.postThumbnail)}></img>
                </a>
            </div>
        </div>
    )
}

export default CommentPost
