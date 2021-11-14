import Cookies from 'js-cookie'
import React, { useEffect, useState } from 'react'
import ReactHtmlParser from 'react-html-parser'
import { Link, useHistory, useLocation } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { dispatchDeleteCmt, dispatchLikeCmt, dispatchEditCmt } from '../../../../../redux/actions/commentAction'
import TextareaAutosize from 'react-textarea-autosize';

const Comment = ({ comment }) => {
  const location = useLocation()
  const auth = useSelector(state => state.auth)
  const userInfor = auth.user
  const history = useHistory()
  const [showChoose, setShowChoose] = useState(false)
  const [showEditCmt, setShowEditCmt] = useState(false)
  const [commentInputChange, setCommentInputChange] = useState('')
  const dispatch = useDispatch()

  useEffect(() => {
    setCommentInputChange(comment.content)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [comment.content])

  const handleChangeComment = (e) => {
    const { value } = e.target
    setCommentInputChange(value)
  }

  const handleLikeCmt = (commentId) => {
    if (!Cookies.get("token")) return history.push(`/login?redirectTo=${location.pathname}`)
    dispatch(dispatchLikeCmt(commentId))
  }

  const handleSubmitChangeCmt = async (e) => {
    if ((e.key == "Enter" || e.which === 13) && e.shiftKey == false) {
      e.preventDefault()
      if (!Cookies.get("token")) return history.push(`/login?redirectTo=${location.pathname}`)
      
      if(!commentInputChange.trim()) return
      const formData = new FormData()
      formData.append("content", commentInputChange)
      dispatch(dispatchEditCmt(comment.commentId, formData))
      setShowEditCmt(false)
      setCommentInputChange(commentInputChange)
    }
  }

  const handleDeleteCmt = async () => {
    dispatch(dispatchDeleteCmt(comment.commentId))
  }

  //Bấm hủy sửa xóa đi ô sửa và sửa lại input change
  const handleClickCancelEdit = () => {
    setShowEditCmt(false)
    setCommentInputChange(comment.content)
  }

  return (
    <div className="mb-10 d-flex" key={comment.commentId}>
      <Link to={`/profile/${comment.commenter.accountId}`}>
        <div className="avatar-comment inline-item"
          style={{ backgroundImage: `url(${ReactHtmlParser(comment.commenter.avatarLink)})` }}>
        </div>
      </Link>

      <div style={showEditCmt ? { flexGrow: 1 } : null}>
        <div className="comment__content--container">

          <div className="comment__title">
            {!showEditCmt ?
              <span className="comment__title--name">
                <Link to={`/profile/${comment.commenter.accountId}`}>
                  {ReactHtmlParser(comment.commenter.name)}
                </Link>
              </span> : null
            }

            {!showEditCmt ?
              <>
                {comment.commenter.email === userInfor.email ?
                  <i className="fal fa-chevron-down  ml-10" style={{ fontSize: '12px', cursor: 'pointer' }} onClick={() => setShowChoose(!showChoose)}>
                    <div className="comment__choose" style={showChoose ? { display: 'block' } : { display: 'none' }}>
                      <div className="d-flex comment__choose-option" onClick={handleDeleteCmt}>
                        <i className="fal fa-eraser mr-10 "></i>
                        <p>Xóa</p>
                      </div>

                      <div className="d-flex comment__choose-option" onClick={() => setShowEditCmt(true)}>
                        <i className="fal fa-pen mr-10 "></i>
                        <p>Chỉnh sửa</p>
                      </div>
                    </div>
                  </i> : null
                }
              </>
              : null}
          </div>


          {showEditCmt ?
            <form className="d-flex comment-form--edit">
              <TextareaAutosize
                className="comment__input-edit"
                value={commentInputChange}
                onKeyUp={handleSubmitChangeCmt}
                onChange={handleChangeComment}
              />
            </form>
            :
            <div className="comment__content">
              {ReactHtmlParser(ReactHtmlParser(comment.content))}
            </div>
          }
        </div>

        <div className="comment__like">
          {showEditCmt ? <p className="cursor-pointer" onClick={handleClickCancelEdit}>Hủy</p> :
            <>
              <p className="mr-10 comment__like-btn" onClick={() => handleLikeCmt(comment.commentId)}>
                {comment.liked ? 'Bỏ thích' : 'Thích'}
              </p>

              {comment.likeCount}

              <i className="fal fa-thumbs-up ml-5 mr-5"></i>
              <p className="text-12-px">{comment.commentDate}</p>
            </>
          }
        </div>
      </div>
    </div>
  )
}

export default Comment
