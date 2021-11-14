import { Link, useHistory, useParams } from "react-router-dom";
import React, { useState } from "react";
import axios from "axios";
import categoryApis from "./enum/category-apis";
import { errorNotification, successNotification, warnNotification } from "../../../users/utils/notification/ToastNotification";


function AdminCategoryForm(props) {
    const { isAdd } = props;
    const { id, name } = useParams();

    var initialState = ""
    if (!isAdd) {
        initialState = name
    }
    const history = useHistory()
    const [category, setCategory] = useState(initialState);

    const postNewCate = async (data) => {
        try {
            const res = await axios.post(categoryApis.postNewCategory, data)
            if (res) {
                successNotification('Thêm mới thành công ✔')
                setCategory("")
                history.push("/admin/categories")
            }
        } catch (error) {
            if (error.response.status === 409) {
                errorNotification('Đã tồn tại thể loại này 🙁')
            }
            else {
                errorNotification('Đã xảy ra lỗi khi thêm mới 🙁')
            }
        }
    }

    const updateCate = async (data) => {
        try {
            const res = await axios.put(categoryApis.updateCategory(id), data)
            if (res) {
                successNotification('Cập nhật thành công ✔')
                setCategory("")
                history.push("/admin/categories")
            }
        } catch (error) {
            errorNotification('Đã xảy ra lỗi khi cập nhật 🙁')
        }
    }

    const onChange = (event) => {
        setCategory(event.target.value)
    }

    const onSubmit = (event) => {
        event.preventDefault();
        const data = new FormData();
        data.append("name", category)
        if (isAdd) {
            if (category === "") {
                warnNotification('Bạn chưa nhập tên thể loại')
            } else {
                postNewCate(data)
            }

        }
        else {
            if (category === "") {
                warnNotification('Bạn chưa nhập tên thể loại')
            } else {
                updateCate(data)
            }
        }
    }

    return (
        <div className="right-panel-100vh">
            <div className="cate-form">

                <h1 className="form-title">{isAdd ? "Thêm mới" : "Sửa"}</h1>
                <form onSubmit={onSubmit} >
                    <div className="mb-3">
                        <label className="form-label">Tên thể loại</label>
                        <input type="text" className="form-control" name="category" value={category} onChange={onChange} />
                    </div>

                    <div className="cate-group-button">
                        <button className="btn btn-primary mr-5" type="submit">Lưu</button>
                        <Link to="/admin/categories" className="btn btn-danger">Hủy</Link>
                    </div>
                </form>
            </div>
        </div>
    )


}

export default AdminCategoryForm;