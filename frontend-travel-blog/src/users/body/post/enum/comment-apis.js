const baseUrl = '/api/comment'

export default {
    getComments(id) {
        return `${baseUrl}/post/${id}`
    },
    postComment(id) {
        return `${baseUrl}/post/${id}`
    },
    deleteComment(commentId) {
        return `${baseUrl}/${commentId}`
    },
    likeComment(commentId) {
        return `${baseUrl}/${commentId}/like`
    },
    updateComment(commentId) {
        return `${baseUrl}/${commentId}`
    }
}