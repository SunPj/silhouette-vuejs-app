const state = {
  user: {}
};

const getters = {
  getToken: state => state.user.token,
  getUser: state => state.user,
  getRole: state => state.user ? state.user.role : null,
  isAuthorized: state => state.user.hasOwnProperty('token'),
  isAdmin: state => state.user ? (state.user.role === 'Admin'):false
};

const actions = {
  setUser: (context, user) => {
    context.commit('setUserData', user);
  }
};

const mutations = {
  setUserData: (state, user) => {
    state.user = user
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
