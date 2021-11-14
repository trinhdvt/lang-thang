const baseUrl = '/api'

export default {
    getUserList: `${baseUrl}/system/users`,
    updateRole(id) {
        return `${baseUrl}/user/${id}/admin`
    }
}