import Mock from 'mockjs';

// 手动指定的用户ID列表
const userMap = {
  "user1": "张三",
  "user2": "李四",
  "user3": "王五",
  "user4": "赵六"
};

// 模拟用户登录并分配一个固定的ID
Mock.mock('/api/video/login', 'post', (req) => {
  const { userId } = JSON.parse(req.body); // 前端传入的用户ID
  const userName = userMap[userId] || "未知用户";

  return {
    success: true,
    data: {
      userId,
      userName,
      message: `用户登录成功，您的ID为：${userId}，用户名为：${userName}`,
    },
  };
});

// 模拟WebSocket信令传输
Mock.mock('/api/video/signaling', 'post', (req) => {
  const { type, data, from, target } = JSON.parse(req.body);
  console.log(`Mock: 收到信令类型 ${type}，从 ${from} 发往 ${target}`, data);

  // 模拟信令响应
  if (type === 'offer') {
    return {
      success: true,
      message: `Offer已接收，正在处理...`,
      response: {
        type: 'answer',
        data: Mock.mock({
          sdp: '@string(100, 200)', // 模拟SDP数据
        }),
        from: target,
        target: from,
      },
    };
  } else if (type === 'answer') {
    return {
      success: true,
      message: `Answer已接收，正在处理...`,
    };
  } else if (type === 'candidate') {
    return {
      success: true,
      message: `Candidate已接收，正在处理...`,
    };
  }

  return {
    success: true,
    message: `信令 ${type} 已处理完成`,
  };
});
