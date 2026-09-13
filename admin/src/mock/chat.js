// src/mock/chat.js
import Mock from 'mockjs';

// 模拟学生列表
Mock.mock('/api/students', 'get', {
  students: [
    { id: '1', name: '张三', avatar: 'https://via.placeholder.com/50' },
    { id: '2', name: '李四', avatar: 'https://via.placeholder.com/50' },
    { id: '3', name: '王五', avatar: 'https://via.placeholder.com/50' },
    { id: '4', name: '赵六', avatar: 'https://via.placeholder.com/50' },
  ],
});

// 模拟聊天记录
Mock.mock('/api/chat/:studentId', 'get', (req) => {
  const studentId = req.params.studentId;
  return {
    messages: [
      { id: 1, sender: 'teacher', content: `老师：你好，学生${studentId}` },
      { id: 2, sender: 'student', content: `学生${studentId}：你好` },
      { id: 3, sender: 'teacher', content: `老师：最近学习怎么样？` },
      { id: 4, sender: 'student', content: `学生${studentId}：还可以，谢谢关心。` },
    ],
  };
});

// 模拟发送消息
Mock.mock('/api/chat/:studentId', 'post', (req) => {
  const { message } = JSON.parse(req.body);
  console.log(`Mock: Message sent to student ${req.params.studentId}:`, message);
  return { success: true };
});
