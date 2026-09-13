<script setup>
import { useXfAsr } from "@/utils/useXfAsr.js";

const { startRecording, stopRecording, recordText, resultText } = useXfAsr();
</script>

<template>
  <div class="app-container">
    <h1>识别结果：{{ resultText }}</h1>
    <button @click="startRecording">{{ recordText }}</button>
    <button @click="stopRecording">停止录音</button>
  </div>
</template>