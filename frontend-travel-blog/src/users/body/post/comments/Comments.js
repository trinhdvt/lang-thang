import React, { useEffect, useRef, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import Comment from './components/Comment.js'
import Cookies from 'js-cookie'
import ReactHtmlParser from 'react-html-parser'
import { useHistory } from 'react-router'
import {
  dispatchGetComments, dispatchNextCommentsPage,
  dispatchSubmitComments, dispatchClearCmts
} from '../../../../redux/actions/commentAction.js'
import useSocketDataObject from '../../../../real-time/useSocketDataObject.js'
import { showErrMsg } from '../../../utils/notification/Notification.js'
import TextareaAutosize from 'react-textarea-autosize';
import { Redirect, useLocation } from 'react-router-dom'


const Comments = ({ id }) => {
  const history = useHistory()
  const location = useLocation()
  const auth = useSelector(state => state.auth)
  const userInfor = auth.user
  const [commentInput, setCommentInput] = useState('')
  const [currentPage, setCurrentPage] = useState(0)
  const { Subscribe_post, Unsubscribe_post } = useSocketDataObject()
  const dispatch = useDispatch()
  const commentsReducer = useSelector(state => state.commentsReducer)
  const realtime = useSelector(state => state.realtime)
  const ref = useRef(realtime.postSubcription)


  useEffect(() => {
    Subscribe_post(id)
  }, [realtime.isSuccess])

  useEffect(() => {
    ref.current = realtime.postSubcription
  }, [realtime.postSubcription])

  useEffect(() => {
    dispatch(dispatchGetComments(id))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id])

  useEffect(() => {
    return () => {
      Unsubscribe_post(ref.current)
      dispatch(dispatchClearCmts())
    }
  }, [])

  const handleChangeInput = (e) => {
    const { value } = e.target
    setCommentInput(value)

  }

  const handleSubmitComment = (e) => {
    if ((e.key == "Enter" || e.which === 13) && e.shiftKey == false) {
      e.preventDefault()
      if (!Cookies.get("token")) return history.push(`/login?redirectTo=${location.pathname}`)

      if(commentInput.trim()){
        var commentForm = new FormData()
        commentForm.append('content', commentInput)
        dispatch(dispatchSubmitComments(id, commentForm))
        setCommentInput("")
      } 
    }
  }

  const handleNextPageCmt = () => {
    setCurrentPage(currentPage + 1)
    dispatch(dispatchNextCommentsPage(id, currentPage + 1))
  }


  return (
    <div className="mt-50">
      <div className="comment-area-title" >
        <h5>Bình luận:</h5>
      </div>
      <form className="d-flex">
        <div className="avatar-comment"
          style={{ backgroundImage: `url(${ReactHtmlParser(userInfor.avatarLink)})` }}>
        </div>

        <TextareaAutosize
          className="comment__input-submit"
          placeholder="Viết bình luận..."
          name="commentInput"
          value={commentInput}
          onKeyUp={handleSubmitComment}
          onChange={handleChangeInput}
        />
      </form>

      {commentsReducer.errData && showErrMsg(commentsReducer.errData)}

      {commentsReducer.commentsArr ?
        commentsReducer.commentsArr.map((comment, index) => {
          return (
            <Comment key={index} comment={comment} />
          )
        })
        : null
      }

      <div className="post__cmt--morecmt" style={commentsReducer.isEmpty ? { display: 'none' } : null}
        onClick={handleNextPageCmt}>
        Xem thêm
        <i className="fal fa-chevron-down" />
      </div>
    </div>
  )
}

export default Comments
