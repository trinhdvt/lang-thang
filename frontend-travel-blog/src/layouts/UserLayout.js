import React from 'react'
import Footer from '../users/footer/Footer'
import Header from '../users/header/Header'

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
