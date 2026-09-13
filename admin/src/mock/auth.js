import Mock from 'mockjs'

Mock.setup({
  timeout: '200-800'
})

// 登录接口
Mock.mock(/\/api\/v1\/auth\/login/, 'post', {
  code: 200,
  data: {
    token: Mock.Random.guid(),
    username: 'mockUser',
    avatar: require('@/assets/vue.svg')
  }
})

// 注册接口
Mock.mock(/\/api\/v1\/auth\/register/, 'post', {
  code: 200,
  data: {
    uid: Mock.Random.integer(10000, 99999),
    message: '注册成功'
  }
})

// 用户信息接口
Mock.mock(/\/api\/v1\/user\/info/, 'get', {
  code: 200,
  data: {
    username: 'mockAdmin',
    roles: ['admin'],
    avatar: Mock.Random.image('100x100')
  }
})