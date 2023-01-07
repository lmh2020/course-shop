import request from '@/utils/request'

// 查询考试试卷题目列表
export function listStoreQuestion(query) {
  return request({
    url: '/edu/question/store/list',
    method: 'get',
    params: query
  })
}

// 查询考试试卷题目详细
export function getStoreQuestion(id) {
  return request({
    url: '/edu/question/store/' + id,
    method: 'get'
  })
}

// 新增考试试卷题目
export function addStoreQuestion(data) {
  return request({
    url: '/edu/question/store',
    method: 'post',
    data: data
  })
}

// 修改考试试卷题目
export function updateStoreQuestion(data) {
  return request({
    url: '/edu/question/store',
    method: 'put',
    data: data
  })
}

// 删除考试试卷题目
export function delStoreQuestion(id) {
  return request({
    url: '/edu/question/store/' + id,
    method: 'delete'
  })
}

// 导出考试试卷题目
export function exportStoreQuestion(query) {
  return request({
    url: '/edu/question/store/export',
    method: 'get',
    params: query
  })
}

// 获取所有引用了该试题的试卷信息
export function getExamQuestionReferredExams(id) {
  return request({
    url: '/edu/question/store/referredExams/' + id,
    method: 'get'
  })
}
