const baseUrl = '/api/category'

export default {
    getPostsByCategory(categoryId){
        return `${baseUrl}/${categoryId}/post`
    }
}