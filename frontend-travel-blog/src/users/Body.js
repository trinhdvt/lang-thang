import React from 'react'
import { Route, Switch } from 'react-router-dom'
import { useSelector } from 'react-redux'


import Register from './body/auth/Register'
import Login from './body/auth/Login'
import Home from './body/home/Home'
import NotFound from './utils/NotFound/NotFound'
import NewPosts from './body/newPosts/NewPosts'
import Post from './body/post/Post'
import ForgotPassword from './body/auth/ForgotPassword'
import ResetPassword from './body/auth/ResetPassword'
import Profile from './body/profile/Profile'
import MyProfile from './body/profile/MyProfile'
import EditProfile from './body/profile/EditProfile'
import Bookmarks from './body/bookmark/Bookmarks'
import SearchPage from './body/search/SearchPage'
import Category from './body/category/Category'
import AdminHome from '../admin/body/AdminHome/AdminHome'
import UserLayout from '../layouts/UserLayout'
import AdminLayout from '../layouts/AdminLayout'
import NotPermission from './utils/NotFound/NotPermission'
// import EditPost from './body/newPosts/EditPost'

function Body() {
    const auth = useSelector(state => state.auth)
    const { isLogged, isAdmin } = auth
    return (
        <section>
            <Switch>
                <Route path="/" component={Home} exact />
                <Route path="/register" component={Register} exact />
                {/* <Route path="/login" component={Login} /> */}
                <Route path="/login" component={!isLogged ? Login : Home} exact />
                <Route path="/forgot_password" component={isLogged ? Home : ForgotPassword} exact />
                <Route path="/auth/resetPassword/:token" component={isLogged ? Home : ResetPassword} exact />

                <Route path="/posts/new" component={isLogged ? NewPosts : Home} exact />
                <Route path='/posts/:slug' component={Post} exact />
                <Route path='/posts/:slug/edit' component={isLogged ? NewPosts : Home} exact />


                <Route path='/search' component={SearchPage} exact />
                <Route path='/bookmarks' component={isLogged ? Bookmarks : Login} exact />
                <Route path='/category/:id' component={Category} exact />

                <Route path="/profile/:id" component={Profile} exact />
                <Route path="/myprofile" component={isLogged ? MyProfile : Login} exact />
                <Route path="/myprofile/edit" component={isLogged ? EditProfile : Login} exact />
                
                {/* <Route path="/admin" component={isAdmin ? AdminLayout : NotPermission} exact /> */}

                <Route component={NotFound} exact />
                
            </Switch>

        </section>
    )
}

export default Body
