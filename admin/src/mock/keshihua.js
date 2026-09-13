import Mock from 'mockjs';

// 核心指标统计数据
Mock.mock('/api/dashboard/analysis/total', 'get', () => {
  return {
    users: Mock.Random.integer(10000, 15000),      // 用户总数
    companies: Mock.Random.integer(800, 1000),     // 公司总数
    roles: Mock.Random.integer(150, 200),          // 角色总数
    activeUsers: Mock.Random.integer(3000, 5000)   // 活跃用户
  };
});

// 省份详细数据
Mock.mock(/\/api\/dashboard\/province\/detail\/.*/, 'get', (options) => {
  const province = options.url.split('/').pop();
  return {
    code: 0,
    data: {
      name: province,
      students: Mock.Random.integer(1000, 3000),
      teachers: Mock.Random.integer(100, 300),
      departments: Mock.Random.integer(10, 20),
      colleges: Mock.Random.shuffle([
        '理工大学',
        '师范大学',
        '医科大学',
        '农业大学',
        '财经大学'
      ]).slice(0, Mock.Random.integer(2, 4))
    }
  };
});

// 省份地图数据
Mock.mock('/api/dashboard/province/map', 'get', () => {
  const provinces = [
    '北京', '上海', '广东', '江苏', '浙江', 
    '山东', '河南', '四川', '湖北', '湖南'
  ];
  
  return {
    code: 0,
    data: provinces.map(province => ({
      name: province,
      value: Mock.Random.integer(60, 100),
      itemStyle: {
        areaColor: Mock.Random.color(),
        emphasis: {
          areaColor: '#0060c9'
        }
      }
    }))
  };
});

// 用户分布数据
Mock.mock('/api/dashboard/user/distribution', 'get', () => {
  return {
    data: [
      { value: Mock.Random.integer(35, 45), name: '企业用户' },
      { value: Mock.Random.integer(25, 35), name: '个人用户' },
      { value: Mock.Random.integer(15, 25), name: '开发者' },
      { value: Mock.Random.integer(5, 15), name: '游客' }
    ]
  };
});

// 公司规模分布数据
Mock.mock('/api/dashboard/company/scale', 'get', () => {
  return {
    xAxis: ['小型', '中型', '大型', '超大型'],
    data: [
      Mock.Random.integer(100, 200),
      Mock.Random.integer(250, 350),
      Mock.Random.integer(150, 250),
      Mock.Random.integer(50, 100)
    ]
  };
});

// 系统活跃度趋势数据
Mock.mock('/api/dashboard/system/activity', 'get', () => {
  const days = [];
  const data = [];
  for (let i = 0; i < 7; i++) {
    const date = new Date();
    date.setDate(date.getDate() - i);
    days.unshift(date.getDate() + '日');
    data.unshift(Mock.Random.integer(2000, 3000));
  }
  return {
    xAxis: days,
    data: data
  };
});