import ACTIONS from '../actions.js'

const token = ''

const tokenReducer = (state = token, action) => {
    switch(action.type){
        case ACTIONS.GET_TOKEN:
            return action.payload
        default:
            return state
    }
}

export default tokenReducer