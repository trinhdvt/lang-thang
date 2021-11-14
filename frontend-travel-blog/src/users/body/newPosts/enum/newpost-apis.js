const baseUrl = '/api'

export default {
    getCategories: `${baseUrl}/category`,
    loadPost(slug) {
        return `${baseUrl}/post/${slug}/edit`
    },
    uploadImg: `${baseUrl}/upload`,
    savePost: `${baseUrl}/post`,
    saveDraft: `${baseUrl}/draft`,
    updateDraft(postId) {
        return `${baseUrl}/draft/${postId}`
    },
    updatePost(postId){
        return `${baseUrl}/post/${postId}`

    }
    
}