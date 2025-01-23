import request from '@/utils/request'

// 获取仪表盘统计数据
export function getStats() {
  return request({
    url: '/api/stats',
    method: 'get'
  })
}

// 获取最近借阅记录
export function getRecentBorrows() {
  return request({
    url: '/api/borrows/recent',
    method: 'get',
    params: {
      size: 5
    }
  })
}

// 获取热门图书
export function getPopularBooks() {
  return request({
    url: '/api/books/popular',
    method: 'get',
    params: {
      size: 5
    }
  })
} 