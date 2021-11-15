import CookiesService from "./CookiesService.js"
import axios from 'axios';
import { store } from '../redux/store.js'
import { dispatchLogout } from '../redux/actions/authAction.js'

const cookiesService = CookiesService.getService()
let isRefreshing = false;
let refreshSubscribers = [];

axios.interceptors.request.use(
  config => {
    const accessToken = cookiesService.getToken()
    if (accessToken) {
      config.headers['Authorization'] = 'Bearer ' + accessToken
    }
    return config
  },
  error => {
    Promise.reject(error)
  }
)

const refreshAccessToken = () => {
  return axios.post("/api/auth/refreshToken", null, {
    headers:  { Authorization: 'Bearer ' + cookiesService.getToken() }
  })
}

axios.interceptors.response.use(response => {
  return response;
}, error => {
  const { config, response: { status } } = error;
  const originalRequest = config;
  if((status === 400 || status === 403 || status === 401) && originalRequest.url.includes("/api/auth/refreshToken")) {
    cookiesService.clearToken()
    store.dispatch(dispatchLogout())
    window.location.href = "/login"
    return Promise.reject(error)
  }

  else if (status === 403) {
    if (!isRefreshing) {
      isRefreshing = true;
      refreshAccessToken()
        .then(res => {
          isRefreshing = false;
          cookiesService.setToken(res.data.token)
          onRrefreshed(res.data.token);
        })
    }
    
    const retryOrigReq = new Promise((resolve, reject) => {
      subscribeTokenRefresh(token => {
        // replace the expired token and retry
        originalRequest.headers['Authorization'] = 'Bearer ' + token;
        resolve(axios(originalRequest));
      });
    });
    return retryOrigReq;
  } else {
    return Promise.reject(error);
  }
});

function subscribeTokenRefresh(cb) {
  refreshSubscribers.push(cb);
}

function onRrefreshed(token) {
  refreshSubscribers.map(cb => cb(token));
}