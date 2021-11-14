import './AdminReports.css';

import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import reportApis from './enum/report-apis.js';

function AdminReports() {
    const [reportList, setReportList] = useState([]);

    const [pagination, setPagination] = useState({
        page: 0,
        size: 10
    })

    useEffect(() => {
        const getReportList = async () => {
            const res = await axios.get(reportApis.getReportList, {
                params: {
                    page: pagination.page,
                    size: pagination.size
                }
            })
            if (res) {
                setReportList(res.data);
            }
        }
        getReportList();
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

    const solveStatus = (status) => {
        if (status) {
            return (
                <button className="btn btn-primary" disabled={true}>Đã xử lý</button>
            )
        }
        else {
            return (
                <button className="btn btn-warning" disabled={true}>Chưa xử lý</button>
            )
        }
    }

    const elmReport = reportList.map((report, index) => {
        var to = "/admin/reportDetail/" + report.reportId
        return (
            <tr key={report.reportId}>
                <th scope="row">{index + 1 + (pagination.page * pagination.size)}</th>
                <td>{report.reportId}</td>
                <td>{new Date(report.reportDate).toLocaleString()}</td>
                <td>{report.solved ? solveStatus(true) : "Chưa xử lý"}</td>
                <td className="text-center">
                    <Link to={to} className="btn btn-primary">
                        <i className="fal fa-eye mr-5"></i>
                        Xem
                    </Link>
                </td>
            </tr>
        )
    })

    return (
        <div className="">
            <div className="right-panel">
                <div className="post-list">
                    <div className="admin__favoriteposts--title">
                        <h2 className="list-name">Danh sách báo cáo</h2>
                        <h5 className="web-name">LangThang.com</h5>
                    </div>
                    <div className="post-table table-responsive">
                        <table className="table table-hover">
                            <thead>
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">ID</th>
                                    <th scope="col">Ngày báo cáo</th>
                                    <th scope="col">Trạng thái</th>
                                    <th scope="col" className="text-center">Chi tiết</th>
                                </tr>
                            </thead>
                            <tbody>
                                {elmReport}
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
                    <button className="btn btn-secondary ml-10" disabled={reportList.length < pagination.size} onClick={onClickNext}>
                        Next
                        <i className="far fa-chevron-double-right ml-5"></i>
                    </button>
                </div>
            </div>
        </div>
    );
}

export default AdminReports;