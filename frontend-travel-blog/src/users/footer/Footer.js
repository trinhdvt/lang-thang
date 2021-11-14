import React from 'react'

function Footer() {
    return (
        <footer>
        <div className="container">
            <div style={{display: 'flex', flexWrap:'wrap'}} >
                <div className="footer--col">
                    <h5>Contact me</h5>
                    <div>
                        <i className="fal fa-map-marker-alt"></i>
                        <p>
                            54 Nguyen Luong Bang 
                            <br />
                            <span>
                                Lien Chieu, Da Nang
                            </span>
                            
                        </p>
                    </div>

                    <div>
                        <i className="fal fa-phone-alt"></i>
                        <p>0368220872</p>
                    </div>
                    <div>
                        <i className="fal fa-envelope"></i>
                        <p>102180266@sv.dut.udn.vn</p>
                    </div>
                    <div>
                        <a href="#"><i className="fab fa-facebook-square"></i></a>
                        <a href="#"><i className="fab fa-twitter-square"></i></a>
                        <a href="#"><i className="fab fa-google-plus-square"></i></a>
                        <a href="#"><i className="fab fa-youtube"></i></a>
                        <a href="#"><i className="fab fa-linkedin"></i></a>
                    </div>
                </div>
                <div className="footer--col">
                    <h5>About me</h5>
                    <p>Đây là một bài tập đồ án môn Công Nghệ Phần Mềm.
                        <br/>
                        Chúng tôi đã phát triển một trang web du lịch. 
                    </p>
                    
                    
                </div>

            </div>

            <div>
            </div>
        </div>
    </footer>
    )
}

export default Footer
