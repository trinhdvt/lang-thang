import React, { useEffect, useState } from "react";
import axios from "axios";
import ReactHtmlParse from "react-html-parser";
import userApis from "./enum/user-apis.js";
import { errorNotification, successNotification } from "../../../users/utils/notification/ToastNotification.js";
import { Link } from "react-router-dom";


function AdminUsers() {
    const [userList, setUserList] = useState([]);
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10
    })

    //Check pagination
    const [userToAdm, setUserToAdm] = useState({})

    const [isShowAlertToAdm, setIsShowAlertToAdm] = useState(false)
    useEffect(() => {
        const getUserList = async () => {
            const res = await axios.get(userApis.getUserList, {
                params: {
                    page: pagination.page,
                    size: pagination.size
                }
            })
            if (res) {
                setUserList(res.data);
            }
        }
        getUserList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [pagination])

    const onClickNext = () => {
        setPagination({
            ...pagination,
            page: pagination.page + 1
        })
    }

    const onClickPrev = () => {
        setPagination({
            ...pagination,
            page: pagination.page - 1
        })
    }

    const onToAdmin = () => {
        const id = String(userToAdm.accountId)
        const toAdmin = async () => {
            try {
                const res = await axios.put(userApis.updateRole(id))
                if (res) {
                    var newUserList = userList.map((item) => (item.accountId === userToAdm.accountId ? { ...item, role: "ROLE_ADMIN" } : item))
                    setUserList(newUserList)
                    successNotification(`${userToAdm.name} đã thành admin 🎉`)
                    setUserToAdm({})
                    setIsShowAlertToAdm(false)
                }
            } catch (error) {
                if (error.response.status === 422) {
                    errorNotification('Tài khoản không tồn tại hoặc chưa kích hoạt 🙁')
                    setUserToAdm({})
                    setIsShowAlertToAdm(false)

                }
                else {
                    errorNotification('Thảo tác không thành công')
                    setUserToAdm({})
                    setIsShowAlertToAdm(false)
                }
            }
        }
        toAdmin();
    }
    const handleShowAlertToAdmin = (user) => {
        setUserToAdm(user)
        setIsShowAlertToAdm(true)
    }

    const showAlertToAdmin = () => {
        return (
            <div className="dialog-container post__delete-dialog">
                <h5>Lưu ý</h5>
                <p style={{ margin: '0px 5px 10px 5px' }}>Bạn có muốn nâng {ReactHtmlParse(userToAdm.name)} làm admin?</p>
                <div className="post__report-delete-dialog--btn-container">
                    <button className="button button-red-no-hover mr-5"
                        onClick={() => setIsShowAlertToAdm(false)}
                    >
                        Hủy
                    </button>
                    <button className="button button-red"
                        onClick={onToAdmin}
                    >
                        Đồng ý
                    </button>
                </div>
            </div>

        )
    }

    const elmUser = userList.map((user, index) => {
        return (
            <tr key={user.accountId}>
                <th scope="row">{index + 1 + (pagination.page * pagination.size)}</th>
                <td className="">
                    <div className="user-avatar" style={{ backgroundImage: `url(${ReactHtmlParse(user.avatarLink)})` }}></div>
                </td>
                <td>
                    <Link to={`/profile/${user.accountId}`}>
                        {ReactHtmlParse(user.name)}
                    </Link>
                </td>
                <td>{user.email}</td>
                <td className="text-center">{user.postCount}</td>
                <td className="text-center">{user.followCount}</td>
                <td className="text-center">{user.role ? "Admin" : "User"}</td>
                <td>
                    <button
                        style={{ whiteSpace: 'nowrap', margin: 'auto' }}
                        className={user.role ? "btn btn-secondary" : "btn btn-primary"}
                        disabled={user.role}
                        onClick={() => handleShowAlertToAdmin(user)} >
                        {user.role ? "Admin" : "Cấp quyền"}
                    </button>
                </td>
            </tr>
        )
    })

    return (
        <div className="AdminUser">
            {isShowAlertToAdm && showAlertToAdmin()}
            <div className="right-panel">
                <div className="post-list">
                    <div className="admin__favoriteposts--title">
                        <h2 className="list-name">Danh sách người dùng</h2>
                        <h5 className="web-name">LangThang.com</h5>
                    </div>
                    <div className="post-table table-responsive">
                        <table className="table table-hover">
                            <thead>
                                <tr>
                                    <th scope="col" className="text-center">#</th>
                                    <th scope="col" className="text-center">Người dùng</th>
                                    <th scope="col">Tên</th>
                                    <th scope="col">Email</th>
                                    <th scope="col" className="text-center">Số bài viêt</th>
                                    <th scope="col" className="text-center">Số follow</th>
                                    <th scope="col" className="text-center">Quyền hạn</th>
                                    <th scope="col" className="text-center">Admin</th>
                                </tr>
                            </thead>
                            <tbody>
                                {elmUser}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div className="admin__pagination">
                    <button className="btn btn-secondary mr-10" disabled={pagination.page === 0} onClick={onClickPrev}>
                        <i className="far fa-chevron-double-left mr-5"></i>
                        Prev
                    </button>
                    <button className="btn btn-secondary" disabled={true}>{pagination.page + 1}</button>
                    <button className="btn btn-secondary ml-10" disabled={userList.length < pagination.size} onClick={onClickNext}>
                        Next
                        <i className="far fa-chevron-double-right ml-5"></i>
                    </button>
                </div>
            </div>
        </div>
    );
}

export default AdminUsers;