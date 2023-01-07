import request from '@/utils/request'

const baseUrl = "/edu/exam/question/"

// 查询考试试卷题目列表
export function listQuestion(query) {
  return request({
    url: baseUrl + 'list',
    method: 'get',
    params: query
  })
}

// 查询考试试卷题目详细
export function getQuestion(id) {
  return request({
    url: baseUrl + id,
    method: 'get'
  })
}

// 删除考试试卷题目
export function delQuestion(id) {
  return request({
    url: baseUrl + id,
    method: 'delete'
  })
}

// 导出考试试卷题目
export function exportQuestion(query) {
  return request({
    url: baseUrl + 'export',
    method: 'get',
    params: query
  })
}

// 获取所有引用了该试题的试卷信息
export function getExamQuestionReferredExams(id) {
  return request({
    url: baseUrl + 'referredPapers/' + id,
    method: 'get'
  })
}


// 导出考核试题[从题库批量导出试题到考核]
export function importByQuestionIds(query) {
  return request({
    url: baseUrl + 'importByQuestionIds',
    method: 'post',
    params: query
  })
}

// 导出考核试题[从试卷导出所有试题到考核]
export function importByPaperQuestion(examId, paperId) {
  return request({
    url: baseUrl + 'importByPaperQuestion/' + examId + '/' + paperId,
    method: 'post',
  })
}

/* 排序 */
export function changeExamQuestionSort(data) {
  return request({
    url: baseUrl + 'changeExamQuestionSort',
    method: 'post',
    params: data
  })
}
