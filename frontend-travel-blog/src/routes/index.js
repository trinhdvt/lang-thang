import React from 'react'
import { useSelector } from 'react-redux'
import { Route, Switch } from 'react-router-dom'
import AdminCategory from '../admin/body/AdminCategory/AdminCategory'
import AdminCategoryForm from '../admin/body/AdminCategory/AdminCategoryForm'
import AdminHome from '../admin/body/AdminHome/AdminHome'
import AdminPosts from '../admin/body/AdminPosts/AdminPosts'
import AdminReportDetails from '../admin/body/AdminReports/AdminReportDetail'
import AdminReports from '../admin/body/AdminReports/AdminReports'
import AdminUsers from '../admin/body/AdminUsers/AdminUsers'
import AdminLayout from '../layouts/AdminLayout'
import UserLayout from '../layouts/UserLayout'
import ActiveGmail from '../users/body/auth/ActiveGmail'
import ForgotPassword from '../users/body/auth/ForgotPassword'
import Login from '../users/body/auth/Login'
import Register from '../users/body/auth/Register'
import ResetPassword from '../users/body/auth/ResetPassword'
import Bookmarks from '../users/body/bookmark/Bookmarks'
import Category from '../users/body/category/Category'
import Home from '../users/body/home/Home'
import NewPosts from '../users/body/newPosts/NewPosts'
import Post from '../users/body/post/Post'
import EditProfile from '../users/body/profile/EditProfile'
import MyProfile from '../users/body/profile/MyProfile'
import Profile from '../users/body/profile/Profile'
import ProfileWrapper from '../users/body/profile/ProfileWrapper'
import SearchPage from '../users/body/search/SearchPage'
import NotFound from '../users/utils/NotFound/NotFound'
import NotPermission from '../users/utils/NotFound/NotPermission'

const AppRoute = ({component: Component, layout: Layout, ...rest}) => {
    return (
        <Route {...rest} render={
            props => (
                <Layout>
                    <Component {...props} />
                </Layout>
            )
        } />
    )
}   

export default () => {
    const auth = useSelector(state => state.auth)
    const { isLogged, isAdmin } = auth
    return(
        
            <Switch>
                <AppRoute exact path="/" layout={UserLayout} component={Home} />
                <AppRoute path="/" component={Home} exact layout={UserLayout}/>
                <AppRoute path="/register" component={Register} exact layout={UserLayout}/>
                {/* <AppRoute path="/login" component={Login} /> */}
                <AppRoute path="/login" component={!isLogged ? Login : Home} exact layout={UserLayout}/>
                <AppRoute path="/forgot_password" component={isLogged ? Home : ForgotPassword} exact layout={UserLayout}/>
                <AppRoute path="/auth/resetPassword/:token" component={isLogged ? Home : ResetPassword} exact layout={UserLayout} />
                <AppRoute path="/auth/active/:token" component={isLogged ? Home : ActiveGmail} exact layout={UserLayout} />


                <AppRoute path="/posts/new" component={isLogged ? NewPosts : Home} exact layout={UserLayout}/>
                <AppRoute path='/posts/:slug/edit' component={isLogged ? NewPosts : Home} exact layout={UserLayout} />
                <AppRoute path='/posts/:slug' component={Post} exact layout={UserLayout}/>


                <AppRoute path='/search' component={SearchPage} exact layout={UserLayout}/>
                <AppRoute path='/bookmarks' component={isLogged ? Bookmarks : Login} exact layout={UserLayout}/>
                <AppRoute path='/category/:id' component={Category} exact layout={UserLayout}/>

                {/* <AppRoute path="/profile/:id" component={Profile} exact layout={UserLayout}/> */}
                <AppRoute path="/profile/:id" component={ProfileWrapper} exact layout={UserLayout}/>

                {/* <AppRoute path="/myprofile" component={isLogged ? MyProfile : Login} exact layout={UserLayout}/> */}
                <AppRoute path="/myprofile/edit" component={isLogged ? EditProfile : Login} exact layout={UserLayout} />
                
                <AppRoute path="/admin/dashboard" component={isAdmin ? AdminHome : NotPermission} exact  layout={isAdmin ? AdminLayout : UserLayout}/>
                <AppRoute path="/admin/posts"  component={isAdmin ? AdminPosts : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                <AppRoute path="/admin/categories"  component={isAdmin ? AdminCategory : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                <AppRoute path="/admin/categoriesAdd"  component={isAdmin ? () => <AdminCategoryForm isAdd={true} /> : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                <AppRoute path="/admin/categoriesUpdate/:id/:name"  component={isAdmin ? AdminCategoryForm : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                
                
                <AppRoute path="/admin/users"  component={isAdmin ? AdminUsers : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                <AppRoute path="/admin/reports"  component={isAdmin ? AdminReports : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                
                <AppRoute path="/admin/reportDetail/:id"  component={isAdmin ? AdminReportDetails : NotPermission} exact layout={isAdmin ? AdminLayout : UserLayout} />
                
                <AppRoute component={NotFound} exact layout={UserLayout}/>
            </Switch>
        
    )
}
