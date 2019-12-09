<template>
  <div>
    <section>
      <b-input placeholder="Search..."
               type="search"
               icon-pack="fas"
               v-model="q"
               @input="loadAsyncData()"
               icon="search">
      </b-input>
    </section>
    <section>
      <b-table
              :data="data"
              :loading="loading"
              :striped="true"
              :hoverable="true"
              paginated
              backend-pagination
              :total="total"
              :per-page="pageSize"
              @page-change="onPageChange"
              aria-next-label="Next page"
              aria-previous-label="Previous page"
              aria-page-label="Page"
              aria-current-label="Current page"
              backend-sorting
              :default-sort-direction="defaultSortOrder"
              :default-sort="[sortField, sortOrder]"
              @sort="onSort">

        <template slot-scope="props">
          <b-table-column field="firstName" label="FirstName" sortable>
            {{props.row.firstName}}
          </b-table-column>

          <b-table-column field="lastName" label="LastName" centered sortable>
            {{props.row.lastName}}
          </b-table-column>

          <b-table-column field="email" label="Email" centered sortable>
            <span v-if="props.row.email" class="tag" :class="{'is-success': props.row.confirmed, 'is-warning': !props.row.confirmed}" :title="props.row.confirmed ? 'Confirmed':'Non confirmed'">
              {{props.row.email}}
            </span>
          </b-table-column>

          <b-table-column field="role" label="Role" centered sortable>
            <user-role-edit
                    :user-id="props.row.id"
                    :roles="roles"
                    :current-role-id="props.row.roleId"
                    :key="props.row.id"
                    @role-change="(roleId) => props.row.roleId = roleId">
            </user-role-edit>
          </b-table-column>

          <b-table-column field="source" label="Source" centered>
            <font-awesome-icon title="Credentials" style="margin-left: 5px" icon="at" v-if="props.row.credentialsProvider"/>
            <font-awesome-icon title="Facebook" style="margin-left: 5px" :icon="['fab', 'facebook-square']" v-if="props.row.facebookProvider"/>
            <font-awesome-icon title="Google" style="margin-left: 5px" :icon="['fab', 'google-plus-square']" v-if="props.row.googleProvider"/>
            <font-awesome-icon title="Twitter" style="margin-left: 5px" :icon="['fab', 'twitter-square']" v-if="props.row.twitterProvider"/>
          </b-table-column>

          <b-table-column field="signedUpAt" label="Signed Up" sortable numeric>
            {{ props.row.signedUpAt ? new Date(props.row.signedUpAt).toLocaleString() : '' }}
          </b-table-column>
        </template>
      </b-table>
    </section>
  </div>
</template>

<script>
    import UserRoleEdit from './UserRoleEdit.vue'
    export default {
        name: 'UserManager',
        components: {UserRoleEdit},
        data() {
            return {
                data: [],
                roles: [],
                total: 0,
                q: '',
                loading: false,
                sortField: 'signedUpAt',
                sortOrder: 'desc',
                defaultSortOrder: 'desc',
                page: 1,
                pageSize: 20,
            }
        },
        methods: {
            /*
            * Load async data
            */
            loadAsyncData() {
                this.loading = true;
                this.$http.get(`/api/user`, {
                    params: {
                        search: this.q,
                        page: this.page,
                        pageSize: this.pageSize,
                        draw: 0,
                        order: [this.sortField + ',' + this.sortOrder]
                    }
                })
                    .then(({data}) => {
                        this.data = data.data;
                        this.total = data.recordsFiltered;
                        this.loading = false
                    })
                    .catch((error) => {
                        this.data = [];
                        this.total = 0;
                        this.loading = false;
                        throw error
                    })
            },
            /*
             * Handle page-change event
             */
            onPageChange(page) {
                this.page = page;
                this.loadAsyncData()
            },
            /*
             * Handle sort event
             */
            onSort(field, order) {
                this.sortField = field;
                this.sortOrder = order;
                this.loadAsyncData()
            },
        },
        mounted() {
            this.$http.get(`/api/user/role`).then(({data}) => {
                this.roles = data;
                this.loadAsyncData();
            });
        }
    }
</script>

<style scoped>
  .table-wrapper td {
    vertical-align: middle;
  }
</style>
