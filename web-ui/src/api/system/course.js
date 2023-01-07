import request from '@/utils/request'

// 查询课程列表
export function listCourse(query) {
  return request({
    url: '/system/course/page',
    method: 'get',
    params: query
  })
}

// 查询所有课程列表
export function getAll() {
  return request({
    url: '/system/course/all',
    method: 'get'
  })
}

// 查询课程详细
export function getCourse(id) {
  return request({
    url: '/system/course/' + id,
    method: 'get'
  })
}

// 新增课程
export function addCourse(data) {
  return request({
    url: '/system/course',
    method: 'post',
    data: data
  })
}

// 修改课程
export function updateCourse(data) {
  return request({
    url: '/system/course',
    method: 'put',
    data: data
  })
}

// 删除课程
export function delCourse(id) {
  return request({
    url: '/system/course/' + id,
    method: 'delete'
  })
}

// 导出课程
export function exportCourse(query) {
  return request({
    url: '/system/course/export',
    method: 'get',
    params: query
  })
}
