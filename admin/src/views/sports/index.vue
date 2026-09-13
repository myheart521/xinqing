<template>
  <div class="sport-manager" style="padding: 20px; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);">
    <!-- Banner部分 -->
    <el-card class="box-card" style="margin-bottom: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 8px;">
      <template #header>
        <div class="clearfix">
          <span>Banner 管理</span>
          <el-button @click="openDialog('add', {}, 'banner')" size="small" type="primary"
                     style="float: right; background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%); border: none; border-radius: 4px;">
            添加 Banner
          </el-button>
        </div>
      </template>
      <el-table :data="banners" style="width: 100%" stripe hover>
        <el-table-column prop="imageUrl" label="图片" width="180">
          <template #default="scope">
            <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl || '/default-image.png'" alt="image"
                 class="banner-img" @error="onImageError"/>
          </template>
        </el-table-column>
        <el-table-column prop="link" label="链接" width="200"/>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button @click="openDialog('edit', scope.row, 'banner')" size="mini" type="primary">编辑</el-button>
            <el-button @click="deleteItem(scope.row.id, 'banner')" size="mini" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Swiper部分 -->
    <el-card class="box-card" style="margin-bottom: 20px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 8px;">
      <template #header>
        <div class="clearfix">
          <span>Swiper 管理</span>
          <el-button @click="openDialog('add', {}, 'swiper')" size="small" type="primary" style="float: right">
            添加 Swiper
          </el-button>
        </div>
      </template>
      <el-table :data="swiperList" style="width: 100%">
        <el-table-column prop="url" label="图片" width="180">
          <template #default="scope">
            <img v-if="scope.row.url" :src="scope.row.url || '/default-image.png'" alt="image" class="swiper-img"
                 @error="onImageError"/>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="200"/>
        <el-table-column prop="text" label="描述" width="200"/>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button @click="openDialog('edit', scope.row, 'swiper')" size="mini" type="primary">编辑</el-button>
            <el-button @click="deleteItem(scope.row.id, 'swiper')" size="mini" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="30%" @close="resetForm">
      <el-form :model="currentItem" ref="formRef" :rules="formRules">
        <el-form-item v-if="currentType === 'banner'" label="图片" :label-width="formLabelWidth" prop="imageUrl">
          <el-upload
              :http-request="handleImageUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div v-if="currentItem.imageUrl" class="image-preview">
              <img :src="currentItem.imageUrl" alt="预览" style="max-width: 100px; max-height: 100px;"/>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="currentType === 'banner'" label="跳转链接" :label-width="formLabelWidth" prop="link">
          <el-input v-model="currentItem.link" autocomplete="off" placeholder="请输入 http:// 或 https:// 开头的 URL"/>
        </el-form-item>
        <el-form-item v-if="currentType === 'swiper'" label="图片" :label-width="formLabelWidth" prop="url">
          <el-upload
              :http-request="handleImageUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div v-if="currentItem.url" class="image-preview">
              <img :src="currentItem.url" alt="预览" style="max-width: 100px; max-height: 100px;"/>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="currentType === 'swiper'" label="名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="currentItem.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item v-if="currentType === 'swiper'" label="描述" :label-width="formLabelWidth" prop="text">
          <el-input v-model="currentItem.text" autocomplete="off"/>
        </el-form-item>
        <el-form-item v-if="currentType === 'swiper'" label="分类" :label-width="formLabelWidth" prop="swiperLink">
          <el-input v-model="currentItem.link" autocomplete="off" placeholder="请输入分类（可选）"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
              type="primary"
              @click="saveItem"
              :disabled="currentType === 'banner' ? !currentItem.imageUrl || !currentItem.link || !currentItem.createId : !currentItem.url || !currentItem.name || !currentItem.text"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted, reactive, toRefs, nextTick} from 'vue';
import {
  getBanners,
  publishBanner,
  deleteBanner,
  getCards,
  publishCard,
  deleteCard,
  uploadImage
} from '@/api/teacher/article.js';
import {ElMessage, ElMessageBox} from 'element-plus';
import {debounce} from 'lodash';
import {requestBaseUrl} from "@/utils/URL.js";

// 获取用户 ID
const getUserId = () => {
  const userId = localStorage.getItem('userId');
  return userId ? Number(userId) : null;
};

export default {
  name: 'SportManager',
  setup() {
    const formState = reactive({
      banners: [],
      swiperList: [],
      hotArticles: [],
      dialogVisible: false,
      dialogTitle: '',
      currentItem: {},
      formLabelWidth: '100px',
      currentType: '',
      loading: false
    });

    const formRef = ref(null);

    // 表单验证规则
    const formRules = {
      imageUrl: [
        {required: true, message: '请上传图片', trigger: 'change'},
        {type: 'string', message: '图片 URL 必须是字符串', trigger: 'change'}
      ],
      link: [
        {required: true, message: '请输入跳转链接', trigger: 'blur'},
        {type: 'string', message: '链接必须是字符串', trigger: 'blur'}
      ],
      url: [
        {required: true, message: '请上传图片', trigger: 'change'},
        {type: 'string', message: '图片 URL 必须是字符串', trigger: 'change'}
      ],
      name: [
        {required: true, message: '请输入名称', trigger: 'blur'},
        {type: 'string', message: '名称必须是字符串', trigger: 'blur'}
      ],
      text: [
        {required: true, message: '请输入描述', trigger: 'blur'},
        {type: 'string', message: '描述必须是字符串', trigger: 'blur'}
      ],
      swiperLink: [
        {type: 'string', message: '分类必须是字符串', trigger: 'blur'}
      ]
    };

    // 图片基地址
    // const IMAGE_BASE_URL = 'http://localhost/';
    const IMAGE_BASE_URL = requestBaseUrl;


    // 防抖版本的 fetchData
    const fetchData = debounce(async () => {
      formState.loading = true;
      try {
        const [bannerResponse, swiperResponse, articleResponse] = await Promise.all([
          getBanners(),
          getCards(),
          Promise.resolve({data: []})
        ]);

        console.log('Banners:', JSON.stringify(bannerResponse, null, 2));
        console.log('Cards:', JSON.stringify(swiperResponse, null, 2));
        console.log('Articles:', JSON.stringify(articleResponse, null, 2));

        formState.banners = (bannerResponse.data || []).map(item => ({
          id: item.id,
          imageUrl: item.imageUrl?.startsWith('http') ? item.imageUrl : `${IMAGE_BASE_URL}${item.imageUrl || ''}`,
          link: Array.isArray(item.link) ? item.link[0] || '' : item.link || '',
          createId: item.createId
        }));

        formState.swiperList = (swiperResponse.data || []).map(item => ({
          id: item.id,
          url: item.url?.startsWith('http') ? item.url : `${IMAGE_BASE_URL}${item.url || ''}`,
          name: item.name || '',
          text: item.text || '',
          link: item.link || '',
          createId: item.createId || null
        }));

        formState.hotArticles = (articleResponse.data || []).map(item => ({
          id: item.id,
          cover_image: item.cover_image ? (item.cover_image.startsWith('http') ? item.cover_image : `${IMAGE_BASE_URL}${item.cover_image}`) : null,
          title: item.title,
          tags: item.tags || []
        }));
      } catch (error) {
        console.error('fetchData 出错:', error);
        ElMessage.error('获取数据失败：' + error.message);
      } finally {
        formState.loading = false;
      }
    }, 300);

    // 处理图片加载失败
    const onImageError = (event) => {
      const img = event.target;
      if (img.src !== '/default-image.png') {
        console.warn('图片加载失败:', img.src);
        img.src = '/assets/placeholder.svg';
      }
    };

    // 上传前的检查
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/');
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isImage) {
        ElMessage.error('只能上传图片文件！');
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB！');
      }
      return isImage && isLt2M;
    };

    // 处理图片上传
    const handleImageUpload = async (options) => {
      try {
        const imageUrl = await uploadImage(options.file);
        console.log('上传图片返回:', imageUrl);
        if (imageUrl && typeof imageUrl === 'string' && imageUrl.startsWith('http')) {
          if (formState.currentType === 'banner') {
            formState.currentItem.imageUrl = imageUrl;
          } else if (formState.currentType === 'swiper') {
            formState.currentItem.url = imageUrl;
          }
          options.onSuccess({code: 1, data: imageUrl});
        } else {
          throw new Error('无效的图片 URL');
        }
      } catch (error) {
        console.error('上传失败:', error);
        options.onError(error);
        ElMessage.error('图片上传失败');
      }
    };

    // 上传成功回调
    const handleUploadSuccess = (response) => {
      if (response.code === 1 && response.data) {
        console.log('上传成功响应:', response);
        ElMessage.success('图片上传成功');
      } else {
        console.warn('上传响应异常:', response);
        ElMessage.error('图片上传异常');
      }
    };

    // 上传失败回调
    const handleUploadError = (error) => {
      console.error('上传出错:', error);
      ElMessage.error('图片上传失败');
    };

    // 打开弹窗
    const openDialog = async (type, item = {}, category = 'banner') => {
      console.log('打开弹窗:', {type, category, item});

      formState.dialogTitle = type === 'edit'
          ? `编辑 ${category === 'banner' ? 'Banner' : 'Swiper'}`
          : `添加 ${category === 'banner' ? 'Banner' : 'Swiper'}`;

      const userId = getUserId();

      if (!userId) {
        ElMessage.error('请先登录');
        return;
      }

      formState.currentItem = {};

      if (category === 'banner') {
        formState.currentItem = type === 'edit'
            ? {
              id: item.id,
              imageUrl: item.imageUrl || '',
              link: Array.isArray(item.link) ? item.link[0] || '' : item.link || '',
              createId: item.createId || userId
            }
            : {imageUrl: '', link: '', createId: userId};
      } else if (category === 'swiper') {
        formState.currentItem = type === 'edit'
            ? {
              id: item.id,
              url: item.url || '',
              name: item.name || '',
              text: item.text || '',
              link: item.link || '',
              createId: item.createId || userId
            }
            : {url: '', name: '', text: '', link: '', createId: userId};
      }

      formState.currentType = category;
      formState.dialogVisible = true;

      nextTick(() => {
        if (formRef.value) {
          formRef.value.clearValidate();
        }
      });

      console.log('当前类别:', formState.currentType);
      console.log('当前表单数据:', formState.currentItem);
    };

    // 重置表单
    const resetForm = () => {
      console.log('重置表单, 当前类型:', formState.currentType);
      formState.currentItem = {};
      formState.currentType = '';
      if (formRef.value) {
        formRef.value.resetFields();
      }
    };

    // 保存数据
    const saveItem = async () => {
      if (!formRef.value) return;
      try {
        console.log('保存数据:', {
          currentType: formState.currentType,
          currentItem: JSON.stringify(formState.currentItem, null, 2)
        });
        await formRef.value.validate();
        const {id} = formState.currentItem;

        if (formState.currentType === 'banner') {
          const link = Array.isArray(formState.currentItem.link)
              ? formState.currentItem.link[0] || ''
              : formState.currentItem.link || '';
          const bannerData = {
            imageUrl: formState.currentItem.imageUrl || '',
            link: link,
            createId: Number(formState.currentItem.createId)
          };
          console.log('发送的 Banner 数据:', JSON.stringify(bannerData, null, 2));
          if (!bannerData.imageUrl || !bannerData.link || !bannerData.createId) {
            ElMessage.error('请填写所有必填字段');
            return;
          }
          if (isNaN(bannerData.createId)) {
            ElMessage.error('无效的用户 ID');
            return;
          }
          if (id) {
            await updateBanner(id, bannerData);
            ElMessage.success('Banner 编辑成功');
          } else {
            await publishBanner(bannerData);
            ElMessage.success('Banner 添加成功');
          }
        } else if (formState.currentType === 'swiper') {
          const swiperData = {
            url: formState.currentItem.url || '',
            name: formState.currentItem.name || '',
            text: formState.currentItem.text || '',
            link: formState.currentItem.link || '',
            createId: Number(formState.currentItem.createId)
          };
          console.log('发送的 Swiper 数据:', JSON.stringify(swiperData, null, 2));
          if (!swiperData.url || !swiperData.name || !swiperData.text || !swiperData.createId) {
            ElMessage.error('请填写所有必填字段（图片、名称、描述）');
            return;
          }
          if (isNaN(swiperData.createId)) {
            ElMessage.error('无效的用户 ID');
            return;
          }
          if (id) {
            await updateSwiper(id, swiperData);
            ElMessage.success('Swiper 编辑成功');
          } else {
            // 确保 JSON 格式
            console.log('发送的 Swiper 数据:', JSON.stringify(swiperData, null, 2));
            const response = await publishCard(swiperData);
            console.log('publishCard 响应:', response);
            ElMessage.success('Swiper 添加成功');
          }
        }
        formState.dialogVisible = false;
        fetchData();
      } catch (error) {
        console.error('保存失败:', error);
        if (error.response?.data?.msg === '有必要参数为空') {
          ElMessage.error('保存失败：请检查是否填写了所有必填字段');
        } else if (error.response?.status === 400) {
          ElMessage.error('保存失败：请求数据格式错误');
        } else {
          ElMessage.error('保存失败：' + (error.message || '未知错误'));
        }
      }
    };

    // 删除数据
    const deleteItem = async (id, type) => {
      console.log('删除ID:', id, '类型:', type);
      try {
        if (!id || isNaN(id)) {
          ElMessage.error('无法删除：无效的ID');
          return;
        }
        await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        if (type === 'banner') {
          await deleteBanner(id);
          ElMessage.success('Banner 删除成功');
        } else if (type === 'swiper') {
          await deleteCard(id);
          ElMessage.success('Swiper 删除成功');
        }
        fetchData();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败：' + (error.message || '未知错误'));
        }
      }
    };

    // 查看文章详情
    const viewArticle = (id) => {
      console.log('查看文章:', id);
    };

    // 更新接口（实际实现需替换模拟函数）
    const updateBanner = async (id, data) => {
      console.log('Update Banner:', id, data);
      return true;
    };
    const updateSwiper = async (id, data) => {
      console.log('Update Swiper:', id, data);
      return true;
    };

    onMounted(() => {
      fetchData();
    });

    return {
      ...toRefs(formState),
      formRef,
      formRules,
      fetchData,
      openDialog,
      saveItem,
      deleteItem,
      viewArticle,
      onImageError,
      beforeUpload,
      handleImageUpload,
      handleUploadSuccess,
      handleUploadError,
      resetForm
    };
  }
};
</script>

<style scoped>
.sport-manager {
  margin: 20px;
}

.box-card {
  transition: transform 0.3s ease;
}

.box-card:hover {
  transform: translateY(-2px);
}

.banner-img,
.swiper-img,
.article-img {
  width: 100px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  transition: transform 0.3s ease;
}

.banner-img:hover,
.swiper-img:hover,
.article-img:hover {
  transform: scale(1.05);
}

.el-button {
  transition: all 0.3s ease;
}

.el-button--primary {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  border: none;
}

.el-button--primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.el-button--danger {
  background: linear-gradient(135deg, #eb3941 0%, #f15e64 100%);
  border: none;
}

.el-button--danger:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

.el-dialog {
  border-radius: 8px;
}

.el-dialog__header {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  border-radius: 8px 8px 0 0;
}

.el-dialog__body {
  padding: 30px 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  border: none;
  color: #666;
}

.image-preview {
  margin-top: 10px;
}
</style>
