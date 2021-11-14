
export const isMatch = (cf_password, password) => {
    if(cf_password === password) return true
    return false
}

export const isEmail = email => {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

export const isLength = password => {
    if(password.length < 6) return true
    return false
}

export const isEmpty = value => {
    if(!value) return true
    return false
}

export const isImgFormat = file => {
    if (file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/jpg') return false
    return true
    
}

export const isImgSize = file => {
    if(file.size >= 1024*1024*2) return false // 2mb
    return true
}


