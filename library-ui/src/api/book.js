import request from '@/utils/request'

// 获取图书列表
export function getBooks(params) {
  return request({
    url: '/books',
    method: 'get',
    params
  })
}

// 搜索图书
export function searchBooks(keyword) {
  return request({
    url: '/books/search',
    method: 'get',
    params: { keyword }
  })
}

// 获取图书详情
export function getBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

// 创建图书
export function createBook(data) {
  return request({
    url: '/books',
    method: 'post',
    data
  })
}

// 更新图书
export function updateBook(id, data) {
  return request({
    url: `/books/${id}`,
    method: 'put',
    data
  })
}

// 删除图书
export function deleteBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'delete'
  })
}

// 获取分类列表
export function getCategories() {
  return request({
    url: '/categories',
    method: 'get'
  })
} 