<template>
  <div class="editor-container">
    <!-- 富文本编辑器 -->
    <div class="editor">
      <!-- 封面图片预览 -->
      <div class="cover-preview" v-if="coverImage">
        <img :src="coverImage" alt="封面图片" class="cover-image" />
      </div>
      <div class="header">
        <div class="media-toolbar btns-full flex jc">
          <button type="button" class="but cir" title="上传本地图片" data-toggle="tooltip" @click="handleImageUpload">
            <i class="fa fa-camera"></i>
          </button>
          <button type="button" class="but cir" title="粘贴图片链接" data-toggle="tooltip" @click="showImageUrlInput = !showImageUrlInput">
            <i class="fa fa-link"></i>
          </button>
          <button type="button" class="but cir" title="上传视频" data-toggle="tooltip" @click="handleVideoUpload">
            <i class="fa fa-video-camera"></i>
          </button>
          <input
              type="file"
              ref="imageInput"
              style="display: none"
              accept="image/*"
              @change="onImageSelected"
          />
          <input
              type="file"
              ref="videoInput"
              style="display: none"
              accept="video/*"
              @change="onVideoSelected"
          />
        </div>
        <!-- 图片链接输入框 -->
        <div class="image-url-wrapper" v-if="showImageUrlInput">
          <input
              type="text"
              v-model="imageUrl"
              placeholder="粘贴图片链接"
              class="image-url-input"
              @keyup.enter="setImageFromUrl"
          />
          <button class="confirm-btn" @click="setImageFromUrl">确认</button>
        </div>
      </div>
      <div class="content">
        <div class="title-wrapper">
          <input type="text" placeholder="请输入标题" v-model="postTitle" class="title-input" />
          <i class="line-form-line"></i>
        </div>
        <!-- 添加菜品名称输入框，仅在食谱类型时显示 -->
        <div class="title-wrapper" v-if="articleType === 'recipe'">
          <input type="text" placeholder="请输入主要菜品名称" v-model="recipeName" class="title-input" />
          <i class="line-form-line"></i>
        </div>
        <QuillEditor
            v-model:content="editorContent"
            :options="editorOptions"
            contentType="html"
            @ready="onEditorReady"
            @update:content="onContentUpdate"
        />
      </div>
    </div>

    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="category">
        <h3>文章参数</h3>
        <select v-model="articleType" class="category-select">
          <option value="knowledge">知识文章</option>
          <option value="recipe">食谱文章</option>
        </select>
        <input type="text" placeholder="输入文章标签（用逗号分隔）" v-model="tags" class="tags-input" />
        <input type="text" placeholder="输入标签颜色（可选）" v-model="color" class="tags-input" />
        <textarea
            placeholder="请输入文章描述"
            v-model="descriptions"
            class="description-input"
        ></textarea>
      </div>
      <div class="actions">
        <button class="save-draft" @click="saveContent">保存草稿</button>
        <button class="publish" @click="publishContent">发布文章</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits } from "vue";
import { QuillEditor } from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css";
import "@fortawesome/fontawesome-free/css/all.min.css";
import { ElMessage } from "element-plus";
import { uploadImage, uploadVideo, publishArticle, publishDiet } from '@/api/teacher/article.js';

// 定义 props 和 emits
const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
});
const emit = defineEmits(["update:modelValue"]);

// 数据绑定
const editorContent = ref(props.modelValue);
const postTitle = ref("");
const recipeName = ref(""); // 新增：菜品名称，仅用于食谱
const descriptions = ref("");
const tags = ref("");
const color = ref("");
const coverImage = ref("");
const articleType = ref("knowledge");
const imageUrl = ref("");
const showImageUrlInput = ref(false);

// 保存 Quill 实例
const quillInstance = ref(null);

// 编辑器配置
const editorOptions = {
  theme: "snow",
  placeholder: "请输入内容...",
  modules: {
    toolbar: [
      ["bold", "italic", "underline", "strike"],
      [{ list: "ordered" }, { list: "bullet" }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ color: [] }, { background: [] }],
      [{ align: [] }],
      ["link", "image", "video"],
      ["clean"],
    ],
  },
};

// 编辑器就绪时保存实例并打印日志
const onEditorReady = (quill) => {
  quillInstance.value = quill;
  console.log("编辑器已就绪:", new Date().toLocaleTimeString());
};

// 监听内容更新
const onContentUpdate = () => {
  emit("update:modelValue", editorContent.value);
};

// 监听父组件传入的值变化
watch(() => props.modelValue, (newVal) => {
  editorContent.value = newVal;
});

// 文件输入引用
const imageInput = ref(null);
const videoInput = ref(null);

// 等待 Quill 实例就绪的函数
const waitForQuill = () => {
  return new Promise((resolve) => {
    const checkQuill = () => {
      if (quillInstance.value) {
        resolve(quillInstance.value);
      } else {
        console.log("等待编辑器初始化...");
        setTimeout(checkQuill, 100);
      }
    };
    checkQuill();
  });
};

// 处理本地图片上传按钮点击
const handleImageUpload = () => {
  imageInput.value.click();
};

// 处理视频上传按钮点击
const handleVideoUpload = () => {
  videoInput.value.click();
};

// 处理本地图片选择
const onImageSelected = async (event) => {
  const file = event.target.files[0];
  if (file) {
    try {
      const response = await uploadImage(file);
      console.log("本地图片上传响应:", response);

      let imageUrl;
      if (typeof response === "string") {
        imageUrl = response;
      } else if (response && response.code === 1 && response.data) {
        imageUrl = response.data;
      } else {
        throw new Error("返回数据格式无效");
      }

      if (imageUrl) {
        coverImage.value = imageUrl;
        ElMessage.success("本地图片上传成功");
        console.log("本地图片设置成功:", imageUrl);
      }
    } catch (error) {
      ElMessage.error("本地图片上传失败: " + (error.message || "未知错误"));
    }
  }
  event.target.value = "";
};

// 处理粘贴图片链接
const setImageFromUrl = () => {
  if (imageUrl.value) {
    const urlPattern = /^(https?:\/\/.*\.(?:png|jpg|jpeg|gif|webp))/i;
    if (urlPattern.test(imageUrl.value)) {
      coverImage.value = imageUrl.value;
      ElMessage.success("图片链接设置成功");
      console.log("图片链接设置成功:", imageUrl.value);
      imageUrl.value = "";
      showImageUrlInput.value = false;
    } else {
      ElMessage.error("无效的图片链接，请确保是有效的图片 URL");
    }
  } else {
    ElMessage.error("请输入图片链接");
  }
};

// 处理视频选择
const onVideoSelected = async (event) => {
  const file = event.target.files[0];
  if (file) {
    try {
      const response = await uploadVideo(file);
      console.log("视频上传响应:", response);

      let videoUrl;
      if (typeof response === "string") {
        videoUrl = response;
      } else if (response && response.code === 1 && response.data) {
        videoUrl = response.data;
      } else {
        throw new Error("返回数据格式无效");
      }

      if (videoUrl) {
        const quill = await waitForQuill();
        const range = quill.getSelection() || { index: quill.getLength() };
        quill.insertEmbed(range.index, "video", videoUrl);
        ElMessage.success("视频上传成功");
      }
    } catch (error) {
      ElMessage.error("视频上传失败: " + (error.message || "未知错误"));
    }
  }
  event.target.value = "";
};

// 保存草稿
const saveContent = () => {
  if (!postTitle.value || !editorContent.value || !descriptions.value) {
    ElMessage.error("标题、描述和内容不能为空");
    return;
  }
  const draft = {
    title: postTitle.value,
    name: recipeName.value, // 保存菜品名称
    descriptions: descriptions.value,
    content: editorContent.value,
    tags: tags.value,
    color: color.value,
    coverImage: coverImage.value,
    articleType: articleType.value,
  };
  localStorage.setItem("articleDraft", JSON.stringify(draft));
  ElMessage.success("草稿保存成功");
};

// 发布文章
const publishContent = async () => {
  if (!postTitle.value || !editorContent.value || !descriptions.value) {
    ElMessage.error("标题、描述和内容不能为空");
    return;
  }

  try {
    if (articleType.value === "knowledge") {
      const contentData = {
        title: postTitle.value,
        descriptions: descriptions.value,
        content: editorContent.value,
        coverImage: coverImage.value || "",
        tags: tags.value || "",
        color: color.value || "",
      };
      console.log("发布知识文章参数:", contentData);
      await publishArticle(contentData);
      ElMessage.success("知识文章发布成功");
    } else if (articleType.value === "recipe") {
      if (!recipeName.value) {
        ElMessage.error("食谱文章需要填写主要菜品名称");
        return;
      }
      if (!coverImage.value) {
        ElMessage.error("食谱文章需要上传封面图片");
        return;
      }
      if (!tags.value) {
        ElMessage.error("食谱文章需要填写标签");
        return;
      }
      const dietData = {
        title: postTitle.value,
        name: recipeName.value,
        mainImage: coverImage.value,
        content: editorContent.value,
        tags: tags.value,
      };
      console.log("发布食谱参数:", dietData);
      await publishDiet(dietData);
      ElMessage.success("食谱文章发布成功");
    }
  } catch (error) {
    ElMessage.error("文章发布失败: " + (error.message || "未知错误"));
  }
};
</script>

<style scoped>
.editor-container {
  display: flex;
  gap: 24px;
  height: 100vh;
  background: #f8f9fa;
  border-radius: 16px;
  padding: 24px;
  margin: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.editor {
  flex: 3;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 40px);
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.header {
  border-bottom: 1px solid #eef2f6;
  padding: 16px;
  background: #fff;
}

.media-toolbar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.but.cir {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2px solid #e8eef5;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.but.cir:hover {
  transform: translateY(-2px);
  border-color: #6b8cfe;
  box-shadow: 0 4px 12px rgba(107, 140, 254, 0.2);
}

.but.cir i {
  font-size: 18px;
  color: #64748b;
  transition: color 0.3s;
}

.but.cir:hover i {
  color: #6b8cfe;
}

.title-wrapper {
  position: relative;
  margin-bottom: 28px;
  padding: 0 12px;
}

.title-input {
  width: 100%;
  padding: 8px 0;
  font-size: 28px;
  font-weight: 500;
  border: none;
  outline: none;
  background: transparent;
  transition: all 0.3s;
  color: #1e293b;
}

.title-input::placeholder {
  color: #94a3b8;
}

.line-form-line {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #e8eef5;
  transition: all 0.3s;
}

.title-wrapper:focus-within .line-form-line {
  background: #6b8cfe;
  transform: scaleX(1.02);
}

.sidebar {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.category {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
}

.category h3 {
  font-size: 18px;
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 20px;
}

.category-select,
.tags-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e8eef5;
  border-radius: 12px;
  margin-bottom: 16px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
  background: #f8fafc;
  color: #1e293b;
}

.category-select:focus,
.tags-input:focus {
  border-color: #6b8cfe;
  background: #fff;
  box-shadow: 0 2px 8px rgba(107, 140, 254, 0.1);
}

.description-input {
  width: 100%;
  height: 120px;
  padding: 12px;
  font-size: 14px;
  border: 2px solid #e8eef5;
  border-radius: 12px;
  outline: none;
  resize: none;
  transition: all 0.3s;
  background: #f8fafc;
  color: #1e293b;
}

.description-input:focus {
  border-color: #6b8cfe;
  background: #fff;
  box-shadow: 0 2px 8px rgba(107, 140, 254, 0.1);
}

.actions {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.actions button {
  padding: 14px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.save-draft {
  background: #f1f5f9;
  color: #1e293b;
  border: 2px solid #e8eef5 !important;
}

.save-draft:hover {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.publish {
  background: #6b8cfe;
  color: #fff;
}

.publish:hover {
  background: #4b6fef;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(107, 140, 254, 0.2);
}

.cover-preview {
  margin: 0 24px 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 16px;
  text-align: center;
}

.cover-image {
  max-width: 60%;
  max-height: 200px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.image-url-wrapper {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 12px;
}

.image-url-input {
  flex: 1;
  padding: 10px 16px;
  border: 2px solid #e8eef5;
  border-radius: 10px;
  outline: none;
  font-size: 14px;
  transition: all 0.3s;
  color: #1e293b;
}

.image-url-input:focus {
  border-color: #6b8cfe;
  box-shadow: 0 2px 8px rgba(107, 140, 254, 0.1);
}

.confirm-btn {
  padding: 10px 20px;
  background: #6b8cfe;
  color: #fff;
  border: none;
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.confirm-btn:hover {
  background: #4b6fef;
  transform: translateY(-1px);
}

/* 添加响应式布局支持 */
@media (max-width: 1200px) {
  .editor-container {
    flex-direction: column;
  }
  
  .sidebar {
    min-width: 100%;
  }
}
</style>
