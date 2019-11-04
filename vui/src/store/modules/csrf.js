const state = {
  token: {}
};

const getters = {
  getToken: state => state.token
};

const actions = {
  setToken: (context, token) => {
    context.commit('setToken', token);
  }
};

const mutations = {
  setToken: (state, token) => {
    state.token = token
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
