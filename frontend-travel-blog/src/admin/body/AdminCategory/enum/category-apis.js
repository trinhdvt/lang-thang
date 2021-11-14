const baseUrl = '/api'

export default {
    getCategoryList: `${baseUrl}/category`,
    deleteCategory(id) {
        return `${baseUrl}/category/${id}`
    },
    updateCategory(id) {
        return `${baseUrl}/category/${id}`
    },
    postNewCategory: `${baseUrl}/category`,
}