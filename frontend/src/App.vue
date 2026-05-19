<template>
  <div id="app">
    <el-container>
      <el-header class="header">
        <h2>售后工单处理系统</h2>
        <div class="role-switch">
          <span>当前角色：</span>
          <el-select v-model="currentRole" @change="handleRoleChange" style="width: 120px; margin-right: 10px;">
            <el-option label="客户" value="CUSTOMER"></el-option>
            <el-option label="客服" value="SERVICE"></el-option>
            <el-option label="处理人" value="HANDLER"></el-option>
          </el-select>
          <span>用户：</span>
          <el-input v-model="currentUser" style="width: 120px;"></el-input>
          <el-button type="primary" size="small" @click="applyRole">应用</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      currentRole: 'CUSTOMER',
      currentUser: '张三'
    }
  },
  mounted() {
    const savedRole = localStorage.getItem('currentRole')
    const savedUser = localStorage.getItem('currentUser')
    if (savedRole) this.currentRole = savedRole
    if (savedUser) this.currentUser = savedUser
    if (!savedRole) {
      localStorage.setItem('currentRole', this.currentRole)
      localStorage.setItem('currentUser', this.currentUser)
    }
  },
  methods: {
    handleRoleChange(role) {
      const userMap = {
        CUSTOMER: '张三',
        SERVICE: '客服小王',
        HANDLER: '李工'
      }
      this.currentUser = userMap[role]
    },
    applyRole() {
      localStorage.setItem('currentRole', this.currentRole)
      localStorage.setItem('currentUser', this.currentUser)
      window.location.reload()
    }
  }
}
</script>

<style>
#app {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  min-height: 100vh;
}
.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header h2 {
  margin: 0;
  color: white;
}
.role-switch {
  display: flex;
  align-items: center;
  color: white;
}
.role-switch .el-select, .role-switch .el-input {
  margin: 0 5px;
}
</style>
