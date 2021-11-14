import React from 'react'
import Footer from '../users/footer/Footer.js'
import Header from '../users/header/Header.js'

function UserLayout({children}) {
    return (
        <>
        <Header />
        {children}
        <Footer />
        </>
    )
}

export default UserLayout
