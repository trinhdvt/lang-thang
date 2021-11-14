const baseUrl = '/api'

export default {
    getPost: `${baseUrl}/post`,
    getPostsOfAuthor(accountId){
        return `${baseUrl}/user/posts/${accountId}`
    },
    bookmarkPost: `${baseUrl}/bookmark`,
    unBookmarkPost(postId) {
        return `${baseUrl}/bookmark/${postId}`
    },
    deletePost(postId) {
        return `${baseUrl}/post/${postId}`
    },
    reportPost: `${baseUrl}/report`
}