import Vue from 'vue'
import VueRouter from 'vue-router'
import WorkOrderList from '../components/WorkOrderList.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'WorkOrderList',
    component: WorkOrderList
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
