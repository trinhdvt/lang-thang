import React from 'react'

import Menu from '../admin/Menu/Menu'


const AdminLayout = ({children}) => {
    return (
        <>
        
            <div className="row">
                <div className="col-lg-2">
                    <Menu />
                </div>
                <div className="col-lg-10">
                    {children}
                </div>
                
            </div>
            
            {/* <Footer /> */}
        </>
    )
}

export default AdminLayout
