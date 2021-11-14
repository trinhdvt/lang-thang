const baseUrl = '/api/auth'

export default {
    login:  `${baseUrl}/login`,
    loginByGoogle: `${baseUrl}/google`,
    register:  `${baseUrl}/registration`,
    registerByGoogle: `${baseUrl}/google`,
    // changePassword: `${baseUrl}/changePassword`,
    // savePassword: `${baseUrl}/savePassword`,
    resetPassword: `${baseUrl}/resetPassword`,
    confirmRegistration: `${baseUrl}/registrationConfirm`,
}