const state = {
  user: {}
};

const getters = {
  getToken: state => state.user.token,
  isAuthorized: state => state.user.hasOwnProperty('token')
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
