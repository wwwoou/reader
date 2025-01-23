import request from '@/utils/request'

// 获取借阅列表
export function getBorrows(params) {
  return request({
    url: '/borrows',
    method: 'get',
    params
  })
}

// 获取借阅记录
export function getBorrowRecords(params) {
  return request({
    url: '/borrows/records',
    method: 'get',
    params
  })
}

// 创建借阅
export function createBorrow(data) {
  return request({
    url: '/borrows',
    method: 'post',
    data
  })
}

// 归还图书
export function returnBook(id, data) {
  return request({
    url: `/borrows/${id}/return`,
    method: 'post',
    data
  })
}

// 续借图书
export function renewBook(id, data) {
  return request({
    url: `/borrows/${id}/renew`,
    method: 'post',
    data
  })
} 