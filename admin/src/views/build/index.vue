<template>
  <div class="editor-page">

    <QuillEditor ref="quillEditorRef" />
<!--    <div class="editor-actions">-->
<!--      <el-button type="primary" @click="handleGetContent">获取内容</el-button>-->
<!--      <el-button @click="handleResetEditor">重置编辑器</el-button>-->
<!--    </div>-->
  </div>
</template>

<script setup>
import { ref } from 'vue';
import QuillEditor from '@/components/Editor/src/quilleditor.vue'; // 确保路径正确

const quillEditorRef = ref(null);

const handleGetContent = () => {
  if (quillEditorRef.value) {
    const content = quillEditorRef.value.getEditorContent();
    console.log(content);
    alert('编辑器内容:\n' + content);
  }
};

const handleResetEditor = () => {
  if (quillEditorRef.value) {
    quillEditorRef.value.resetEditor();
  }
};
</script>

<style scoped>
.editor-page {
  padding: 10px;
}

.editor-actions {
  margin-top: 0px;
}

.editor-actions .el-button {
  margin-right: 10px;
}

</style>
