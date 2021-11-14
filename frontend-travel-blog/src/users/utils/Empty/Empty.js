import React from 'react'
import emptyImg from './Empty.png'

const Empty = () => {
    return (
        <div  className="background__404" >
           <div className="background__404--image" style={{backgroundImage: `url(${emptyImg})`}}></div>
        </div>
    )
}

export default Empty
