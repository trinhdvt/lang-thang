import ACTIONS from '../actions'

const initialState = {
    commentsArr: [],
    //Check if page is empty
    isEmpty: false,
    errData: null,
    isLoadSubmit: false,
}


const commentReducer = (state = initialState, action) => {
    switch (action.type) {
        //get first page
        case ACTIONS.GET_COMMENTS:
            return {
                ...state,
                commentsArr: action.payload.commentsArr,
                isEmpty: action.payload.isEmpty,
                errData: null,
                isLoadSubmit: false
            }
        case ACTIONS.GET_NEXT_COMMENTS_PAGE:
            return {
                ...state,
                commentsArr: [...state.commentsArr, ...action.payload.commentsArr],
                isEmpty: action.payload.isEmpty,
                errData: null,
                isLoadSubmit: false
            }

        case ACTIONS.EMPTY_COMMENT:
            return {
                ...state,
                isEmpty: action.payload,
                errData: null,
                isLoadSubmit: false
            }

        case ACTIONS.SUBMIT_COMMENT: {
            return {
                ...state,
                errData: null,
                isLoadSubmit: false
            }
        }

        case ACTIONS.DELETE_COMMENT:
            return {
                ...state,
                commentsArr: state.commentsArr.filter(
                    item => item.commentId !== action.payload
                ),
                errData: null,
                isLoadSubmit: false
            }
        case ACTIONS.ADD_COMMENT: {
            if(state.commentsArr.includes(action.payload)) {
                return {
                    ...state,
                    errData: null,
                    isLoadSubmit: false
                }
            }
            else {
                return {
                    ...state,
                    commentsArr: [action.payload, ...state.commentsArr],
                    errData: null,
                    isLoadSubmit: false,
                }
            }
            
        }
        case ACTIONS.LIKE_COMMENT:
            return {
                ...state,
                commentsArr: state.commentsArr.map(item =>
                    item.commentId === action.payload.commentId
                        ? { ...item, likeCount: action.payload.likeCount, liked: !item.liked }
                        : item
                ),
                errData: null,
                isLoadSubmit: false
            }
        case ACTIONS.EDIT_COMMENT:
            return {
                ...state,
                commentsArr: state.commentsArr.map(item =>
                    item.commentId === action.payload.commentId
                        ? action.payload.comment
                        : item
                ),
                errData: null,
                isLoadSubmit: false
            }
        
        case ACTIONS.CLEAR_COMMENT:
            return {
                initialState
            }

        case ACTIONS.COMMENT_ERROR:
            return {
                ...state,
                errData: action.payload
            }

        case ACTIONS.LOADING_COMMENT:
            return {
                ...state,
                errData: null,
                isLoadSubmit: action.payload
            }
        default:
            return state
    }
}

export default commentReducer