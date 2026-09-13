<template>
  <div class="published-articles-container">
    <h2 class="page-title">已发布文章</h2>

    <!-- 选项卡 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 知识文章 -->
      <el-tab-pane label="知识文章" name="knowledge">
        <div class="filter-section">
          <el-input v-model="knowledgeSearchQuery" placeholder="搜索标题或标签" class="search-input" clearable />
          <el-button type="primary" @click="fetchKnowledgeArticles">搜索</el-button>
        </div>
        <el-table :data="filteredKnowledgeArticles" style="width: 100%" border stripe>
          <el-table-column prop="title" label="标题" width="300" />
          <el-table-column prop="tags" label="标签" width="200" />
          <el-table-column prop="publishTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="text" @click="viewArticle(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
              v-model:current-page="knowledgeCurrentPage"
              :page-size="pageSize"
              :total="knowledgeTotal"
              layout="prev, pager, next, total"
              @current-change="handleKnowledgePageChange"
          />
        </div>
      </el-tab-pane>

      <!-- 食谱文章 -->
      <el-tab-pane label="食谱文章" name="recipe">
        <div class="filter-section">
          <el-input v-model="dietSearchQuery" placeholder="搜索标题、菜品名称或标签" class="search-input" clearable />
          <el-button type="primary" @click="fetchDietArticles">搜索</el-button>
        </div>
        <el-table :data="filteredDietArticles" style="width: 100%" border stripe>
          <el-table-column prop="title" label="标题" width="300" />
          <el-table-column prop="name" label="菜品名称" width="200" />
          <el-table-column prop="tags" label="标签" width="200" />
          <el-table-column prop="publishTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="text" @click="viewArticle(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
              v-model:current-page="dietCurrentPage"
              :page-size="pageSize"
              :total="dietTotal"
              layout="prev, pager, next, total"
              @current-change="handleDietPageChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 文章详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="文章详情" width="60%" :before-close="handleDialogClose">
      <div class="article-detail">
        <h3>{{ selectedArticle?.title }}</h3>
        <p><strong>类型:</strong> {{ selectedArticle?.type === 'knowledge' ? '知识文章' : '食谱文章' }}</p>
        <p v-if="selectedArticle?.type === 'recipe'"><strong>菜品名称:</strong> {{ selectedArticle?.name }}</p>
        <p><strong>标签:</strong> {{ selectedArticle?.tags }}</p>
        <p><strong>发布时间:</strong> {{ selectedArticle?.publishTime }}</p>
        <div v-if="selectedArticle?.mainImage || selectedArticle?.coverImage" class="cover-image">
          <img :src="selectedArticle?.mainImage || selectedArticle?.coverImage" alt="封面图片" />
        </div>
        <div class="content-preview" v-html="selectedArticle?.content"></div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {getKnowledgeArticles, getDietArticles} from '@/api/teacher/article.js'; // 新增接口函数

// 选项卡
const activeTab = ref('knowledge');

// 知识文章相关数据
const knowledgeArticles = ref([]);
const knowledgeSearchQuery = ref('');
const knowledgeCurrentPage = ref(1);
const knowledgeTotal = ref(0);

// 食谱文章相关数据
const dietArticles = ref([]);
const dietSearchQuery = ref('');
const dietCurrentPage = ref(1);
const dietTotal = ref(0);

// 分页配置
const pageSize = ref(10);

// 弹窗数据
const dialogVisible = ref(false);
const selectedArticle = ref(null);

// 计算过滤后的知识文章
const filteredKnowledgeArticles = computed(() => {
  let result = knowledgeArticles.value;
  if (knowledgeSearchQuery.value) {
    const query = knowledgeSearchQuery.value.toLowerCase();
    result = result.filter(
        (article) =>
            article.title.toLowerCase().includes(query) ||
            (article.tags && article.tags.toLowerCase().includes(query))
    );
  }
  const start = (knowledgeCurrentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return result.slice(start, end);
});

// 计算过滤后的食谱文章
const filteredDietArticles = computed(() => {
  let result = dietArticles.value;
  if (dietSearchQuery.value) {
    const query = dietSearchQuery.value.toLowerCase();
    result = result.filter(
        (article) =>
            article.title.toLowerCase().includes(query) ||
            article.name.toLowerCase().includes(query) ||
            (article.tags && article.tags.toLowerCase().includes(query))
    );
  }
  const start = (dietCurrentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return result.slice(start, end);
});

// 获取知识文章
const fetchKnowledgeArticles = async () => {
  try {
    const response = await getKnowledgeArticles({
      page: knowledgeCurrentPage.value,
      size: pageSize.value,
    });
    if (response.code === 1) {
      knowledgeArticles.value = response.data.list || response.data;
      knowledgeTotal.value = response.data.total || knowledgeArticles.value.length;
      ElMessage.success('知识文章加载成功');
    } else {
      throw new Error(response.msg || '获取知识文章失败');
    }
  } catch (error) {
    ElMessage.error('加载知识文章失败: ' + (error.message || '未知错误'));
  }
};

// 获取食谱文章
const fetchDietArticles = async () => {
  try {
    const response = await getDietArticles({
      page: dietCurrentPage.value,
      size: pageSize.value,
    });
    if (response.code === 1) {
      dietArticles.value = response.data.list || response.data;
      dietTotal.value = response.data.total || dietArticles.value.length;
      ElMessage.success('食谱文章加载成功');
    } else {
      throw new Error(response.msg || '获取食谱文章失败');
    }
  } catch (error) {
    ElMessage.error('加载食谱文章失败: ' + (error.message || '未知错误'));
  }
};

// 页面切换
const handleKnowledgePageChange = (page) => {
  knowledgeCurrentPage.value = page;
  fetchKnowledgeArticles();
};

const handleDietPageChange = (page) => {
  dietCurrentPage.value = page;
  fetchDietArticles();
};

// 选项卡切换
const handleTabClick = () => {
  if (activeTab.value === 'knowledge') {
    fetchKnowledgeArticles();
  } else if (activeTab.value === 'recipe') {
    fetchDietArticles();
  }
};

// 查看文章详情
const viewArticle = (article) => {
  selectedArticle.value = {...article, type: activeTab.value};
  dialogVisible.value = true;
};

// 关闭弹窗
const handleDialogClose = () => {
  dialogVisible.value = false;
  selectedArticle.value = null;
};

// 页面加载时获取初始数据
onMounted(() => {
  fetchKnowledgeArticles(); // 默认加载知识文章
});
</script>

<style scoped>
.published-articles-container {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  min-height: 80vh;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.article-detail {
  padding: 20px;
}

.article-detail h3 {
  margin-bottom: 15px;
  color: #2196F3;
}

.article-detail p {
  margin-bottom: 10px;
  color: #666;
}

.cover-image {
  margin: 20px 0;
  text-align: center;
}

.cover-image img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-preview {
  border: 1px solid #e0e0e0;
  padding: 15px;
  border-radius: 4px;
  background: #f9f9f9;
  overflow-x: auto;
}

.content-preview :deep(img) {
  max-width: 100%;
}

.content-preview :deep(video) {
  max-width: 100%;
}
</style>
