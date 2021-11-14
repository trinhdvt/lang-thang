import React, {useState} from 'react'
import "./buttonup.css"

function HoverUpButon() {
  const [showBtnUp, setShowBtnUp] = useState(false)

  const toggleVisible = () => {
    const scrolled = document.documentElement.scrollTop;
    if (scrolled > 300) {
      setShowBtnUp(true)
    }
    else if (scrolled <= 300) {
      setShowBtnUp(false)
    }
  };

  window.addEventListener('scroll', toggleVisible);


  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  };

    return (
        <button className="button-up"
            style={{ display: showBtnUp ? 'block' : 'none' }}
            onClick={scrollToTop}
          >
            <i className="fal fa-arrow-up"></i>
        </button>
    )
}

export default HoverUpButon
