import request from '@/utils/request'

const baseUrl = '/edu/paper/'

// 查询考核试卷基础信息列表
export function listPaper(query) {
  return request({
    url: baseUrl + 'list',
    method: 'get',
    params: query
  })
}

// 查询考核试卷基础信息详细
export function getPaper(id) {
  return request({
    url: baseUrl + id,
    method: 'get'
  })
}
// 查询考核试卷基础信息详细
export function getPaperQuestionList(query) {
  return request({
    url: baseUrl + 'paperQuestionList',
    method: 'get',
    params: query
  })
}

// 新增考核试卷基础信息
export function addPaper(data) {
  return request({
    url: baseUrl,
    method: 'post',
    data: data
  })
}

// 修改考核试卷基础信息
export function updatePaper(data) {
  return request({
    url: baseUrl,
    method: 'put',
    data: data
  })
}

// 删除考核试卷基础信息
export function delPaper(id) {
  return request({
    url: baseUrl + id,
    method: 'delete'
  })
}

// 导出考核试卷基础信息
export function exportPaper(query) {
  return request({
    url: baseUrl + 'export',
    method: 'get',
    params: query
  })
}

/* -------------------------- 下面是试题API -------------------------- */
// 查询考核试卷基础信息列表
export function storeQuestionListForPaper(query) {
  return request({
    url: baseUrl + 'storeQuestionListForPaper',
    method: 'get',
    params: query
  })
}

// 从题库导入试题到试卷里
export function importQuestion(query) {
  return request({
    url: baseUrl + 'importQuestion',
    method: 'post',
    params: query
  })
}

export function removeStoreQuestion(data) {
  return request({
    url: baseUrl + 'paperQuestion',
    method: 'delete',
    params: data
  })
}

export function changePaperQuestionSort(data) {
  return request({
    url: baseUrl + 'changePaperQuestionSort',
    method: 'post',
    params: data
  })
}

/* 试卷数据统计 */
export function statisticsPaperInfo(paperId, searchMode) {
  return request({
    url: baseUrl + 'statisticsPaperInfo/' + paperId + '/' + searchMode,
    method: 'get'
  })
}
