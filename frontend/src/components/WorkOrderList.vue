<template>
  <div class="work-order-list">
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">工单总数</div>
            <div class="stat-value">{{ statistics.total }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card stat-pending" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">待分派</div>
            <div class="stat-value">{{ statistics.pendingAssign }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card stat-processing" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">处理中</div>
            <div class="stat-value">{{ statistics.processing }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card stat-confirm" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">待确认</div>
            <div class="stat-value">{{ statistics.pendingConfirm }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card stat-completed" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">已完成</div>
            <div class="stat-value">{{ statistics.completed }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card stat-closed" shadow="hover">
          <div class="stat-content">
            <div class="stat-label">已关闭</div>
            <div class="stat-value">{{ statistics.closed }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" size="small">
        <el-form-item label="工单编号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入工单编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="queryForm.customerName" placeholder="请输入客户名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="问题类型">
          <el-select v-model="queryForm.problemType" placeholder="全部" clearable>
            <el-option label="设备故障" value="EQUIPMENT_FAULT"></el-option>
            <el-option label="系统异常" value="SYSTEM_EXCEPTION"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="queryForm.priority" placeholder="全部" clearable>
            <el-option label="高" value="HIGH"></el-option>
            <el-option label="中" value="MEDIUM"></el-option>
            <el-option label="低" value="LOW"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="待分派" value="PENDING_ASSIGN"></el-option>
            <el-option label="已分派" value="ASSIGNED"></el-option>
            <el-option label="处理中" value="PROCESSING"></el-option>
            <el-option label="待确认" value="PENDING_CONFIRM"></el-option>
            <el-option label="已完成" value="COMPLETED"></el-option>
            <el-option label="已退回" value="RETURNED"></el-option>
            <el-option label="已关闭" value="CLOSED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadWorkOrders">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3>工单列表</h3>
        <el-button v-if="currentRole === 'CUSTOMER'" type="primary" @click="openCreateDialog">创建工单</el-button>
      </div>
      <el-table :data="workOrders" border stripe>
        <el-table-column prop="orderNo" label="工单编号" width="160"></el-table-column>
        <el-table-column prop="customerName" label="客户名称" width="100"></el-table-column>
        <el-table-column prop="contact" label="联系方式" width="120"></el-table-column>
        <el-table-column prop="problemType" label="问题类型" width="100">
          <template slot-scope="scope">{{ getProblemTypeDesc(scope.row.problemType) }}</template>
        </el-table-column>
        <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template slot-scope="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)">{{ getPriorityDesc(scope.row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handler" label="处理人" width="100"></el-table-column>
        <el-table-column prop="status" label="处理状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusDesc(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="viewDetail(scope.row)">详情</el-button>
            <template v-if="currentRole === 'SERVICE' && scope.row.status === 'PENDING_ASSIGN'">
              <el-button size="mini" type="primary" @click="openAssignDialog(scope.row)">分派</el-button>
            </template>
            <template v-if="currentRole === 'SERVICE' && canClose(scope.row.status)">
              <el-button size="mini" type="danger" @click="openCloseDialog(scope.row)">关闭</el-button>
            </template>
            <template v-if="currentRole === 'HANDLER' && scope.row.status === 'ASSIGNED'">
              <el-button size="mini" type="success" @click="handleAccept(scope.row)">接单</el-button>
            </template>
            <template v-if="currentRole === 'HANDLER' && (scope.row.status === 'PROCESSING' || scope.row.status === 'RETURNED')">
              <el-button size="mini" type="primary" @click="openSubmitDialog(scope.row)">提交结果</el-button>
            </template>
            <template v-if="currentRole === 'CUSTOMER' && scope.row.status === 'PENDING_CONFIRM'">
              <el-button size="mini" type="success" @click="handleConfirm(scope.row)">确认完成</el-button>
              <el-button size="mini" type="warning" @click="openReturnDialog(scope.row)">退回</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="创建工单" :visible.sync="createDialogVisible" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createForm" label-width="100px">
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="createForm.customerName"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="createForm.contact"></el-input>
        </el-form-item>
        <el-form-item label="问题类型" prop="problemType">
          <el-select v-model="createForm.problemType" placeholder="请选择">
            <el-option label="设备故障" value="EQUIPMENT_FAULT"></el-option>
            <el-option label="系统异常" value="SYSTEM_EXCEPTION"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="problemDesc">
          <el-input type="textarea" v-model="createForm.problemDesc" rows="4"></el-input>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="createForm.priority">
            <el-radio label="HIGH">高</el-radio>
            <el-radio label="MEDIUM">中</el-radio>
            <el-radio label="LOW">低</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分派工单" :visible.sync="assignDialogVisible" width="400px">
      <el-form :model="assignForm" :rules="assignRules" ref="assignForm" label-width="80px">
        <el-form-item label="工单编号">
          <span>{{ selectedOrder.orderNo }}</span>
        </el-form-item>
        <el-form-item label="处理人" prop="handler">
          <el-select v-model="assignForm.handler" placeholder="请选择处理人">
            <el-option label="李工" value="李工"></el-option>
            <el-option label="王工" value="王工"></el-option>
            <el-option label="赵工" value="赵工"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="提交处理结果" :visible.sync="submitDialogVisible" width="500px">
      <el-form :model="submitForm" :rules="submitRules" ref="submitForm" label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ selectedOrder.orderNo }}</span>
        </el-form-item>
        <el-form-item label="处理结果" prop="processResult">
          <el-input type="textarea" v-model="submitForm.processResult" rows="6"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog title="退回工单" :visible.sync="returnDialogVisible" width="500px">
      <el-form :model="returnForm" :rules="returnRules" ref="returnForm" label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ selectedOrder.orderNo }}</span>
        </el-form-item>
        <el-form-item label="退回原因" prop="returnReason">
          <el-input type="textarea" v-model="returnForm.returnReason" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReturn">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="关闭工单" :visible.sync="closeDialogVisible" width="500px">
      <el-form :model="closeForm" :rules="closeRules" ref="closeForm" label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ selectedOrder.orderNo }}</span>
        </el-form-item>
        <el-form-item label="关闭原因" prop="closeReason">
          <el-input type="textarea" v-model="closeForm.closeReason" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="closeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleClose">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="工单详情" :visible.sync="detailDialogVisible" width="800px">
      <el-descriptions :column="2" border v-if="selectedOrder">
        <el-descriptions-item label="工单编号">{{ selectedOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getStatusTagType(selectedOrder.status)">{{ getStatusDesc(selectedOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户名称">{{ selectedOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ selectedOrder.contact }}</el-descriptions-item>
        <el-descriptions-item label="问题类型">{{ getProblemTypeDesc(selectedOrder.problemType) }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityTagType(selectedOrder.priority)">{{ getPriorityDesc(selectedOrder.priority) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ selectedOrder.handler || '未分派' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ selectedOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ selectedOrder.problemDesc }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="selectedOrder.processResult">{{ selectedOrder.processResult }}</el-descriptions-item>
        <el-descriptions-item label="退回原因" :span="2" v-if="selectedOrder.returnReason">{{ selectedOrder.returnReason }}</el-descriptions-item>
        <el-descriptions-item label="关闭原因" :span="2" v-if="selectedOrder.closeReason">{{ selectedOrder.closeReason }}</el-descriptions-item>
        <el-descriptions-item label="分派时间" v-if="selectedOrder.assignTime">{{ selectedOrder.assignTime }}</el-descriptions-item>
        <el-descriptions-item label="接单时间" v-if="selectedOrder.acceptTime">{{ selectedOrder.acceptTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" v-if="selectedOrder.completeTime">{{ selectedOrder.completeTime }}</el-descriptions-item>
      </el-descriptions>

      <div class="logs-section" v-if="operationLogs.length > 0">
        <h4>操作记录</h4>
        <el-timeline>
          <el-timeline-item
            v-for="log in operationLogs"
            :key="log.id"
            :timestamp="log.operateTime"
            placement="top">
            <el-card>
              <h4>{{ log.operation }}</h4>
              <p>操作人：{{ log.operator }}</p>
              <p>操作后状态：{{ getStatusDesc(log.statusAfter) }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  createWorkOrder,
  assignWorkOrder,
  acceptWorkOrder,
  submitResult,
  confirmComplete,
  returnWorkOrder,
  closeWorkOrder,
  queryWorkOrders,
  getWorkOrderDetail,
  getOperationLogs,
  getStatistics
} from '../api/workOrder'

export default {
  name: 'WorkOrderList',
  data() {
    return {
      currentRole: '',
      workOrders: [],
      statistics: {
        total: 0,
        pendingAssign: 0,
        processing: 0,
        pendingConfirm: 0,
        completed: 0,
        closed: 0
      },
      queryForm: {
        orderNo: '',
        customerName: '',
        problemType: '',
        priority: '',
        status: ''
      },
      createDialogVisible: false,
      assignDialogVisible: false,
      submitDialogVisible: false,
      returnDialogVisible: false,
      closeDialogVisible: false,
      detailDialogVisible: false,
      selectedOrder: {},
      operationLogs: [],
      createForm: {
        customerName: '',
        contact: '',
        problemType: '',
        problemDesc: '',
        priority: ''
      },
      assignForm: {
        handler: ''
      },
      submitForm: {
        processResult: ''
      },
      returnForm: {
        returnReason: ''
      },
      closeForm: {
        closeReason: ''
      },
      createRules: {
        customerName: [{ required: true, message: '客户名称不能为空', trigger: 'blur' }],
        contact: [{ required: true, message: '联系方式不能为空', trigger: 'blur' }],
        problemType: [{ required: true, message: '问题类型不能为空', trigger: 'change' }],
        problemDesc: [{ required: true, message: '问题描述不能为空', trigger: 'blur' }],
        priority: [{ required: true, message: '优先级不能为空', trigger: 'change' }]
      },
      assignRules: {
        handler: [{ required: true, message: '处理人不能为空', trigger: 'change' }]
      },
      submitRules: {
        processResult: [{ required: true, message: '处理结果不能为空', trigger: 'blur' }]
      },
      returnRules: {
        returnReason: [{ required: true, message: '退回原因不能为空', trigger: 'blur' }]
      },
      closeRules: {
        closeReason: [{ required: true, message: '关闭原因不能为空', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.currentRole = localStorage.getItem('currentRole') || 'CUSTOMER'
    this.loadStatistics()
    this.loadWorkOrders()
  },
  methods: {
    async loadStatistics() {
      try {
        this.statistics = await getStatistics()
      } catch (e) {
        this.$message.error(e.message)
      }
    },
    async loadWorkOrders() {
      try {
        this.workOrders = await queryWorkOrders(this.queryForm)
      } catch (e) {
        this.$message.error(e.message)
      }
    },
    resetQuery() {
      this.queryForm = {
        orderNo: '',
        customerName: '',
        problemType: '',
        priority: '',
        status: ''
      }
      this.loadWorkOrders()
    },
    openCreateDialog() {
      this.createForm = {
        customerName: localStorage.getItem('currentUser') || '',
        contact: '',
        problemType: '',
        problemDesc: '',
        priority: ''
      }
      this.createDialogVisible = true
    },
    async handleCreate() {
      this.$refs.createForm.validate(async valid => {
        if (valid) {
          try {
            await createWorkOrder(this.createForm)
            this.$message.success('创建成功')
            this.createDialogVisible = false
            this.loadWorkOrders()
            this.loadStatistics()
          } catch (e) {
            this.$message.error(e.message)
          }
        }
      })
    },
    openAssignDialog(order) {
      this.selectedOrder = order
      this.assignForm = { handler: '' }
      this.assignDialogVisible = true
    },
    async handleAssign() {
      this.$refs.assignForm.validate(async valid => {
        if (valid) {
          try {
            await assignWorkOrder(this.selectedOrder.id, this.assignForm)
            this.$message.success('分派成功')
            this.assignDialogVisible = false
            this.loadWorkOrders()
            this.loadStatistics()
          } catch (e) {
            this.$message.error(e.message)
          }
        }
      })
    },
    async handleAccept(order) {
      try {
        await acceptWorkOrder(order.id)
        this.$message.success('接单成功')
        this.loadWorkOrders()
        this.loadStatistics()
      } catch (e) {
        this.$message.error(e.message)
      }
    },
    openSubmitDialog(order) {
      this.selectedOrder = order
      this.submitForm = { processResult: '' }
      this.submitDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.submitForm.validate(async valid => {
        if (valid) {
          try {
            await submitResult(this.selectedOrder.id, this.submitForm)
            this.$message.success('提交成功')
            this.submitDialogVisible = false
            this.loadWorkOrders()
            this.loadStatistics()
          } catch (e) {
            this.$message.error(e.message)
          }
        }
      })
    },
    async handleConfirm(order) {
      this.$confirm('确认工单已完成吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await confirmComplete(order.id)
          this.$message.success('确认成功')
          this.loadWorkOrders()
          this.loadStatistics()
        } catch (e) {
          this.$message.error(e.message)
        }
      })
    },
    openReturnDialog(order) {
      this.selectedOrder = order
      this.returnForm = { returnReason: '' }
      this.returnDialogVisible = true
    },
    async handleReturn() {
      this.$refs.returnForm.validate(async valid => {
        if (valid) {
          try {
            await returnWorkOrder(this.selectedOrder.id, this.returnForm)
            this.$message.success('退回成功')
            this.returnDialogVisible = false
            this.loadWorkOrders()
            this.loadStatistics()
          } catch (e) {
            this.$message.error(e.message)
          }
        }
      })
    },
    openCloseDialog(order) {
      this.selectedOrder = order
      this.closeForm = { closeReason: '' }
      this.closeDialogVisible = true
    },
    async handleClose() {
      this.$refs.closeForm.validate(async valid => {
        if (valid) {
          try {
            await closeWorkOrder(this.selectedOrder.id, this.closeForm)
            this.$message.success('关闭成功')
            this.closeDialogVisible = false
            this.loadWorkOrders()
            this.loadStatistics()
          } catch (e) {
            this.$message.error(e.message)
          }
        }
      })
    },
    async viewDetail(order) {
      try {
        this.selectedOrder = await getWorkOrderDetail(order.id)
        this.operationLogs = await getOperationLogs(order.id)
        this.detailDialogVisible = true
      } catch (e) {
        this.$message.error(e.message)
      }
    },
    canClose(status) {
      return ['PENDING_ASSIGN', 'ASSIGNED', 'PROCESSING', 'RETURNED'].includes(status)
    },
    getProblemTypeDesc(type) {
      const map = {
        EQUIPMENT_FAULT: '设备故障',
        SYSTEM_EXCEPTION: '系统异常',
        OTHER: '其他'
      }
      return map[type] || type
    },
    getPriorityDesc(priority) {
      const map = { HIGH: '高', MEDIUM: '中', LOW: '低' }
      return map[priority] || priority
    },
    getPriorityTagType(priority) {
      const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'success' }
      return map[priority] || 'info'
    },
    getStatusDesc(status) {
      const map = {
        PENDING_ASSIGN: '待分派',
        ASSIGNED: '已分派',
        PROCESSING: '处理中',
        PENDING_CONFIRM: '待确认',
        COMPLETED: '已完成',
        RETURNED: '已退回',
        CLOSED: '已关闭'
      }
      return map[status] || status
    },
    getStatusTagType(status) {
      const map = {
        PENDING_ASSIGN: 'info',
        ASSIGNED: 'primary',
        PROCESSING: 'warning',
        PENDING_CONFIRM: 'warning',
        COMPLETED: 'success',
        RETURNED: 'danger',
        CLOSED: 'info'
      }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped>
.work-order-list {
  padding: 20px;
}
.statistics-row {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
  cursor: pointer;
}
.stat-content .stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-content .stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-pending .stat-value {
  color: #e6a23c;
}
.stat-processing .stat-value {
  color: #f56c6c;
}
.stat-confirm .stat-value {
  color: #409eff;
}
.stat-completed .stat-value {
  color: #67c23a;
}
.stat-closed .stat-value {
  color: #909399;
}
.search-card {
  margin-bottom: 20px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.table-header h3 {
  margin: 0;
}
.logs-section {
  margin-top: 20px;
}
.logs-section h4 {
  margin-bottom: 15px;
}
</style>
