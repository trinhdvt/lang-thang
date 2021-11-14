const baseUrl = '/api'

export default {
    uploadImg: `${baseUrl}/upload`,
    updateInfor: `${baseUrl}/user/update/info`,
    updatePassword: `${baseUrl}/user/update/password`,
    getPostsOfUser(accountId) {
        return `${baseUrl}/user/posts/${accountId}`
    },
    getDraftsOfUser: `${baseUrl}/user/drafts`,
    deleteDraft(postId){    
        return  `${baseUrl}/post/${postId}`
    },
    getUserInfor(id) {
        return `${baseUrl}/user/${id}`
    },
    followUser(id) {
        return  `${baseUrl}/user/follow/${id}`
    },
    getPostsOfAuthor(accountId){
        return `${baseUrl}/user/posts/${accountId}`
    },
    loadFollowerList(account_id) {
        return  `${baseUrl}/user/${account_id}/follow`
    }
}