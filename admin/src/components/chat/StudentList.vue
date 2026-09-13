<template>
  <div v-if="props.students && props.students.length > 0">
    <!-- 列表展示 -->
    <ul>
      <li
          v-for="student in props.students"
          :key="student.id"
          @click="selectStudent(student.id)"
          :class="{ selected: student.id === props.selectedId }"
      >
        <img
            :src="student.userAvatar || 'https://via.placeholder.com/30'"
            alt="学生头像"
            width="30"
            height="30"
            style="border-radius: 50%; margin-right: 10px;"
        >
        {{ student.userName || '未知姓名' }}
      </li>
    </ul>
  </div>
  <div v-else>
    <p>暂无学生数据，请稍后重试</p>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

// 父组件传递的 `students` 数据
const props = defineProps({
  students: {
    type: Array,
    default: () => [],
  },
  selectedId: {
    type: [String, Number],
    default: null,
  },
});

// 事件: 选择学生
const emit = defineEmits(['select-student']);

// 选择学生时触发
const selectStudent = (id) => {
  if (id === undefined || id === null) {
    console.error('StudentList: 无效的 studentId', id);
    return;
  }
  console.log('StudentList: 选择学生，studentId =', id);
  emit('select-student', id);
};
</script>

<style scoped>
/* 学生列表样式 */
ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

li {
  padding: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.2s;
}

li:hover {
  background-color: #f0f0f0;
}

li.selected {
  background-color: #e6f3ff;
  font-weight: bold;
}

img {
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

p {
  color: #888;
  text-align: center;
  padding: 20px;
}
</style>
