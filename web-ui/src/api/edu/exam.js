import request from '@/utils/request'

const baseUrl = '/edu/exam/'

// 查询考核列表
export function listExam(query) {
  return request({
    url: baseUrl + 'list',
    method: 'get',
    params: query
  })
}

// 查询考核试题列表
export function examQuestionList(query) {
  return request({
    url: baseUrl + 'examQuestionList',
    method: 'get',
    params: query
  })
}

// 查询考核试题列表
export function examineDetail(recordCollectId) {
  return request({
    url:  `${baseUrl}examineDetail/${recordCollectId}`,
    method: 'get',
  })
}

// 查询考核结果
export function examineResult(recordCollectId) {
  return request({
    url:  `${baseUrl}examineResult/${recordCollectId}`,
    method: 'get',
  })
}


// 查询考核详细
export function getExam(id) {
  return request({
    url: baseUrl + id,
    method: 'get'
  })
}

// 新增考核
export function addExam(data) {
  return request({
    url: baseUrl,
    method: 'post',
    data: data
  })
}

// 修改考核
export function updateExam(data) {
  return request({
    url: baseUrl,
    method: 'put',
    data: data
  })
}

// 修改状态
export function updateState(examId, newState) {
  return request({
    url: baseUrl + 'updateState/' + examId + "/" + newState,
    method: 'post',
    data: {
      newState: newState
    }
  })
}

// 删除考核
export function delExam(id) {
  return request({
    url: baseUrl + id,
    method: 'delete'
  })
}

// 导出考核
export function exportExam(query) {
  return request({
    url: baseUrl + 'export',
    method: 'get',
    params: query
  })
}

// 查询考核试卷基础信息列表
export function storeQuestionListForExam(query) {
  return request({
    url: baseUrl + 'storeQuestionListForExam',
    method: 'get',
    params: query
  })
}
