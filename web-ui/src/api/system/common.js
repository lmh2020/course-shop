import request from '@/utils/request'

// 上传单个文件
export function uploadFile(data) {
  return request({
    url: '/system/uploadFile',
    method: 'post',
    params: data,
  })
}
