const baseUrl = '/api'

export default {
    getReportList: `${baseUrl}/report`,
    getDetailReport(id) {
        return `${baseUrl}/report/${id}`
    },
    deletePost(postId) {
        return `${baseUrl}/post/${postId}`
    },
    solveReport(id) {
        return `${baseUrl}/report/${id}`
    },
    getPostOfReport(id) {
        return `${baseUrl}/post/${id}`
    }
}