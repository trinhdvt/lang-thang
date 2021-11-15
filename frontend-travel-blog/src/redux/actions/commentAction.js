import ACTIONS from './index.js'
import axios from 'axios'
import commentApis from '../../users/body/post/enum/comment-apis.js'
import { errorNotification } from '../../users/utils/notification/ToastNotification.js'


export const dispatchNextCommentsPage = (id, page) => async dispatch => {
    try {
        const res = await axios.get(commentApis.getComments(id), {
            params: {
                page: page
            }
        })
        if (res.data.length === 0 || res.data.length < 10) {
            dispatch({
                type: ACTIONS.GET_NEXT_COMMENTS_PAGE,
                payload: {
                    commentsArr: res.data,
                    isEmpty: true
                }

            })
        }
        else {
            dispatch({
                type: ACTIONS.GET_NEXT_COMMENTS_PAGE,
                payload: {
                    commentsArr: res.data,
                    isEmpty: false
                }
            })

        }
    } catch (err) {
        dispatch({
            type: ACTIONS.COMMENT_ERROR,
            payload: "Không thể tải bình luận"
        })
    }
}

//GET FIRST COMMENTS PAGE
export const dispatchGetComments = (id) => async dispatch => {
    try {
        const res = await axios.get(commentApis.getComments(id))

        if (res.data.length === 0 || res.data.length < 10) {
            dispatch({
                type: ACTIONS.GET_COMMENTS,
                payload: {
                    commentsArr: res.data,
                    isEmpty: true
                }
            })
        }
        else {
            dispatch({
                type: ACTIONS.GET_COMMENTS,
                payload: {
                    commentsArr: res.data,
                    isEmpty: false
                }
            })

        }
    } catch (err) {
        dispatch({
            type: ACTIONS.COMMENT_ERROR,
            payload: "Không thể tải bình luận"
        })
    }
}

export const dispatchSubmitComments = (id, commentForm) => async dispatch => {
    try {
        const res = await axios.post(commentApis.postComment(id), commentForm)

        dispatch({
            type: ACTIONS.SUBMIT_COMMENT,
            payload: res.data
        })
    } catch (error) {
        dispatch({
            type: ACTIONS.COMMENT_ERROR,
            payload: "Không thể bình luận"
        })
    }
}

export const dispatchDeleteCmt = (commentId) => async dispatch => {
    try {
        await axios.delete(commentApis.deleteComment(commentId))

        dispatch({
            type: ACTIONS.DELETE_COMMENT,
            payload: commentId
        })
    } catch (error) {
        dispatch({
            type: ACTIONS.COMMENT_ERROR,
            payload: "Không thể xóa bình luận"
        })
    }
}

export const dispatchLikeCmt = (commentId) => async dispatch => {
    try {
        const res = await axios.put(commentApis.likeComment(commentId), null)

        dispatch({
            type: ACTIONS.LIKE_COMMENT,
            payload: {
                commentId: commentId,
                likeCount: res.data
            }
        })
    } catch (error) {
        if(error.response.status === 404) {
            errorNotification("Bình luận này không tồn tại")
            dispatch({
                type: ACTIONS.DELETE_COMMENT,
                payload: commentId
            })
        }
        else {
            dispatch({
                type: ACTIONS.COMMENT_ERROR,
                payload: "Không thể thích bình luận"
            })
        }
        
    }
}


export const dispatchEditCmt = (commentId, formData) => async dispatch => {
    dispatch({ type: ACTIONS.LOADING_COMMENT, payload: commentId })
    try {
        const res = await axios.put(commentApis.updateComment(commentId), formData)

        dispatch({
            type: ACTIONS.EDIT_COMMENT,
            payload: {
                commentId: commentId,
                comment: res.data
            }
        })
    } catch (error) {
        dispatch({
            type: ACTIONS.COMMENT_ERROR,
            payload: "Không thể sửa bình luận"
        })
    }
}

export const dispatchAddCmt = (comment) => {
    return {
        type: ACTIONS.ADD_COMMENT,
        payload: comment
    }
}

export const dispatchClearCmts = () => {
    return {
        type: ACTIONS.CLEAR_COMMENT,
    }
}

