import request from '@/utils/request'

const baseUrl = '/edu/exam/record/'

// 查询【我的】考核列表
export function listExamRecord(query) {
  return request({
    url: baseUrl + 'list',
    method: 'get',
    params: query
  })
}

// 考核报名
export function enrollExam(examId) {
  return request({
    url: baseUrl + 'enroll/' + examId,
    method: 'post',
  })
}

// 考核报名
export function checkBeforeExamine(recordCollectId) {
  return request({
    url: baseUrl + 'check/' + recordCollectId,
    method: 'post',
  })
}

// 撤销考核报名
export function revokeExam(recordCollectId) {
  return request({
    url: baseUrl + 'revokeExam/' + recordCollectId,
    method: 'post',
  })
}
// 自动保存/提交试卷
export function submitExamine(data) {
  return request({
    url: baseUrl + 'submitExamine',
    method: 'post',
    data: data
  })
}

