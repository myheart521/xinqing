// src/api/teacher.js
import axios from 'axios';

// 获取学生测评记录
export const fetchStudentRecords = async (query = '') => {
  try {
    const response = await axios.get('/student-records', {
      params: { query },
    });
    return response.data.records || [];
  } catch (error) {
    console.error('Error fetching student records:', error);
    throw error;
  }
};

// 导出学生测评记录为 CSV
export const exportStudentRecords = async (records) => {
  try {
    const headers = ['学生姓名', '学号', '测评日期', '测评分数', '测评状态'];
    const rows = records.map((record) => [
      record.studentName,
      record.studentId,
      record.testDate,
      record.testScore,
      record.testStatus,
    ]);

    const csvContent = [headers, ...rows].map((row) => row.join(',')).join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'student-records.csv';
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error('Error exporting student records:', error);
    throw error;
  }
};
