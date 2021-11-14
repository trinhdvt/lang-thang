import React from 'react'
//applyMiddleware,
//compose
import { createStore, compose, applyMiddleware} from 'redux'
import rootReducer from './reducers'
import {Provider} from 'react-redux'
import thunk from 'redux-thunk'

export const store = createStore(
    rootReducer,
    compose(
        applyMiddleware(
            thunk
        ),
        window.__REDUX_DEVTOOLS_EXTENSION__ ? window.__REDUX_DEVTOOLS_EXTENSION__() : f=>f
    )
)

function DataProvider({children}) {
    return (
        <Provider store={store} >
            {children}
        </Provider>
    )
}

export default DataProvider