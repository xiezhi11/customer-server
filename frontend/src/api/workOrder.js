import request from './request'

export function createWorkOrder(data) {
  return request({
    url: '/api/workorders',
    method: 'post',
    data
  })
}

export function assignWorkOrder(id, data) {
  return request({
    url: `/api/workorders/${id}/assign`,
    method: 'put',
    data
  })
}

export function acceptWorkOrder(id) {
  return request({
    url: `/api/workorders/${id}/accept`,
    method: 'put'
  })
}

export function submitResult(id, data) {
  return request({
    url: `/api/workorders/${id}/submit`,
    method: 'put',
    data
  })
}

export function confirmComplete(id) {
  return request({
    url: `/api/workorders/${id}/confirm`,
    method: 'put'
  })
}

export function returnWorkOrder(id, data) {
  return request({
    url: `/api/workorders/${id}/return`,
    method: 'put',
    data
  })
}

export function closeWorkOrder(id, data) {
  return request({
    url: `/api/workorders/${id}/close`,
    method: 'put',
    data
  })
}

export function queryWorkOrders(params) {
  return request({
    url: '/api/workorders',
    method: 'get',
    params
  })
}

export function getWorkOrderDetail(id) {
  return request({
    url: `/api/workorders/${id}`,
    method: 'get'
  })
}

export function getOperationLogs(id) {
  return request({
    url: `/api/workorders/${id}/logs`,
    method: 'get'
  })
}

export function getStatistics() {
  return request({
    url: '/api/workorders/statistics',
    method: 'get'
  })
}
