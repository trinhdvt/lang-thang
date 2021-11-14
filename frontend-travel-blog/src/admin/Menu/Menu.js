import React from "react";
import './Menu.css';
import {
    Route,
    NavLink
} from "react-router-dom";
import ReactHtmlParser from "react-html-parser";
import { useSelector } from "react-redux";

const menu = [
    {
        name: "Thống kê",
        to: "/admin/dashboard",
        icon: <i className="fas fa-chart-bar mr-5"></i>,
        exact: true
    },
    {
        name: "Bài viết",
        to: "/admin/posts",
        icon: <i className="fas fa-newspaper mr-5"></i>,

        exact: true
    },
    {
        name: "Thể loại",
        to: "/admin/categories",
        icon: <i className="fa fa-list-alt mr-5"></i>,

        exact: true
    },
    {
        name: "Người dùng",
        to: "/admin/users",
        icon: <i className="fad fa-users mr-5"></i>,

        exact: true
    },
    {
        name: "Báo cáo",
        to: "/admin/reports",
        icon: <i className="fas fa-exclamation-triangle mr-5"></i>,
        exact: true
    },
    {
        name: "Về trang chủ",
        to: "/",
        icon: <i className="far fa-home-alt mr-5"></i>,
        exact: true
    }
]

const MenuLink = ({ icon, label, to, activeOnlyWhenExact }) => {
    return (
        <Route path={to} exact={activeOnlyWhenExact} children={({ match }) => {
            var active = match ? "nav-item active" : "nav-item";
            return (
                <li className={active}>
                    <NavLink to={to} className="nav-link">
                        {icon}
                        {label}
                    </NavLink>
                </li>
            )
        }} />
    );
}

const showMenu = (menu) => {
    var result = null;
    if (menu.length > 0) {
        result = menu.map((item, index) => {
            return (
                <MenuLink icon={item.icon} key={index} label={item.name} to={item.to} activeOnlyWhenExact={item.exact} />
            )
        })
    }
    return result;
}

function Menu() {
    const auth = useSelector(state => state.auth)
    const { user } = auth
    return (
        <div className="admin__menu">
            <div className="admin__menu--infor">
                <div className="admin__menu--avatar" style={{ backgroundImage: `url(${ReactHtmlParser(user.avatarLink)})` }} ></div>
                <h5 className="admin__menu--name "> {user.name}</h5>
            </div>
            <ul className="nav flex-column">
                {showMenu(menu)}
            </ul>
        </div>

    );
}

export default Menu;