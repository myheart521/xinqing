<template>
  <div class="tool-prompt-helper" v-if="showPrompt">
    <div class="prompt-card">
      <div class="prompt-header">
        <div class="prompt-title">
          <svg class="icon" viewBox="0 0 1024 1024" fill="currentColor">
            <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm176 259.9c0 8.8-7.2 16-16 16H368c-8.8 0-16-7.2-16-16V296c0-8.8 7.2-16 16-16h304c8.8 0 16 7.2 16 16v27.9zM496 384c0 8.8-7.2 16-16 16H368c-8.8 0-16-7.2-16-16v-27.9c0-8.8 7.2-16 16-16h112c8.8 0 16 7.2 16 16V384zm144 0c0 8.8-7.2 16-16 16H560c-8.8 0-16-7.2-16-16v-27.9c0-8.8 7.2-16 16-16h64c8.8 0 16 7.2 16 16V384zm80 0c0 8.8-7.2 16-16 16h-48c-8.8 0-16-7.2-16-16v-27.9c0-8.8 7.2-16 16-16h48c8.8 0 16 7.2 16 16V384z"/>
          </svg>
          智能工具助手
        </div>
        <button @click="closePrompt" class="close-btn">
          <svg viewBox="0 0 1024 1024" fill="currentColor">
            <path d="M563.8 512l262.5-312.9c4.4-5.2.7-13.1-6.1-13.1h-79.8c-4.7 0-9.2 2.1-12.3 5.7L511.6 449.8 295.1 191.7c-3.1-3.6-7.6-5.7-12.3-5.7H203c-6.8 0-10.5 7.9-6.1 13.1L459.4 512 196.9 824.9A7.95 7.95 0 0 0 203 838h79.8c4.7 0 9.2-2.1 12.3-5.7L511.6 574.2l216.5 258.1c3.1 3.6 7.6 5.7 12.3 5.7h79.8c6.8 0 10.5-7.9 6.1-13.1L563.8 512z"/>
          </svg>
        </button>
      </div>
      
      <div class="prompt-content">
        <div class="tool-info" v-if="currentTool">
          <h4>{{ currentTool.name }}</h4>
          <p class="tool-description">{{ currentTool.description }}</p>
          
          <div class="parameters-section" v-if="currentTool.parameters.length > 0">
            <h5>需要的参数：</h5>
            <div class="parameter-list">
              <div 
                v-for="param in currentTool.parameters" 
                :key="param.name"
                class="parameter-item"
                :class="{ 'required': param.required }"
              >
                <div class="param-header">
                  <span class="param-name">{{ param.name }}</span>
                  <span class="param-type">{{ param.type }}</span>
                  <span v-if="param.required" class="required-badge">必填</span>
                </div>
                <p class="param-description">{{ param.description }}</p>
                <div class="param-example" v-if="param.example">
                  <span class="example-label">示例：</span>
                  <code>{{ param.example }}</code>
                </div>
              </div>
            </div>
          </div>
          
          <div class="quick-actions">
            <button 
              @click="generateExample" 
              class="action-btn primary"
            >
              生成示例问题
            </button>
            <button 
              @click="insertTemplate" 
              class="action-btn secondary"
            >
              插入模板
            </button>
          </div>
        </div>
        
        <div class="suggestions-section" v-if="suggestions.length > 0">
          <h5>相关建议：</h5>
          <div class="suggestion-list">
            <div 
              v-for="suggestion in suggestions" 
              :key="suggestion.id"
              class="suggestion-item"
              @click="selectSuggestion(suggestion)"
            >
              <div class="suggestion-content">
                <h6>{{ suggestion.title }}</h6>
                <p>{{ suggestion.description }}</p>
              </div>
              <svg class="arrow-icon" viewBox="0 0 1024 1024" fill="currentColor">
                <path d="M765.7 486.8L314.9 134.7A7.97 7.97 0 0 0 302 141v46.2c0 3.3 1.6 6.4 4.3 8.2l308.2 246.2c2.5 2 2.5 5.5 0 7.5L306.3 695.3c-2.7 1.8-4.3 4.9-4.3 8.2V750c0 6.8 7.9 10.5 13.1 6.1l450.8-352.1a31.96 31.96 0 0 0 0-50.2z"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'ToolPromptHelper',
  props: {
    userInput: {
      type: String,
      default: ''
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'insert-text', 'select-suggestion'],
  setup(props, { emit }) {
    // 定义所有可用的工具
    const tools = ref([
      {
        id: 'getStudentInfo',
        name: '获取学生信息',
        description: '根据输入的学号获取学生发布的详细信息',
        keywords: ['学生信息', '学号', '查询学生', '学生详情'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请查询学号为 [学号] 的学生信息'
      },
      {
        id: 'getStudentActivityInfo',
        name: '获取学生活动信息',
        description: '根据学号获取学生参与活动的详细信息，可以指明查询几条，默认最近2个',
        keywords: ['学生活动', '参与活动', '活动记录', '活动详情'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          },
          {
            name: '查询活动条数',
            type: 'Integer',
            required: false,
            description: '查询活动的条数，默认最近2条',
            example: '5'
          }
        ],
        template: '请查询学号为 [学号] 的学生最近 [条数] 条活动记录'
      },
      {
        id: 'getStudentDynamicInfo',
        name: '获取学生博客动态',
        description: '根据学号获取学生发布的博客动态的详细信息，可以指明查询几条，默认最近2条',
        keywords: ['博客', '动态', '发布内容', '学生博客'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          },
          {
            name: '查询博客条数',
            type: 'Integer',
            required: false,
            description: '查询博客动态的条数，默认最近2条',
            example: '3'
          }
        ],
        template: '请查询学号为 [学号] 的学生最近 [条数] 条博客动态'
      },
      {
        id: 'getStudentCommentInfo',
        name: '获取学生评论信息',
        description: '根据学号获取学生发布的评论，可以指明查询几个，默认是5个',
        keywords: ['评论', '学生评论', '评论记录'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          },
          {
            name: '评论条数',
            type: 'Integer',
            required: false,
            description: '评论条数，默认为5条',
            example: '10'
          }
        ],
        template: '请查询学号为 [学号] 的学生最近 [条数] 条评论'
      },
      {
        id: 'getStudentMood',
        name: '获取学生心理状态',
        description: '根据学号获取学生心理状态分析',
        keywords: ['心理状态', '心理分析', '情绪', '心理健康'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请分析学号为 [学号] 的学生心理状态'
      },
      {
        id: 'saveFileToLocal',
        name: '生成文件',
        description: '生成文件的工具，可以选择文件的格式',
        keywords: ['生成文件', '导出文件', '保存文件', '文件格式'],
        parameters: [
          {
            name: '数据内容',
            type: 'String',
            required: true,
            description: '需要保存到文件中的数据',
            example: '学生信息统计报告...'
          },
          {
            name: '文件格式',
            type: 'FileType',
            required: false,
            description: '文件保存的格式，默认格式为TXT',
            example: 'PDF, EXCEL, TXT'
          },
          {
            name: '文件名',
            type: 'String',
            required: false,
            description: '文件名，默认为null',
            example: '学生报告_2024'
          },
          {
            name: '登录用户ID',
            type: 'Long',
            required: true,
            description: '登录用户的id',
            example: '123'
          }
        ],
        template: '请将 [数据内容] 生成为 [格式] 格式的文件，文件名为 [文件名]'
      },
      {
        id: 'operateBrowser',
        name: '操作浏览器',
        description: '操作浏览器执行各种任务',
        keywords: ['浏览器操作', '自动化', '浏览器', '操作'],
        parameters: [
          {
            name: '操作内容',
            type: 'String',
            required: true,
            description: '需要操作的内容',
            example: '打开百度搜索页面'
          }
        ],
        template: '请执行浏览器操作：[操作内容]'
      },
      {
        id: 'publishArticle',
        name: '发布文章',
        description: '发布知识文章到平台',
        keywords: ['发布文章', '文章发布', '知识分享', '内容发布'],
        parameters: [
          {
            name: '标题',
            type: 'String',
            required: true,
            description: '文章标题',
            example: '学生心理健康指导'
          },
          {
            name: '描述',
            type: 'String',
            required: true,
            description: '文章描述（简短）',
            example: '关于学生心理健康的指导建议'
          },
          {
            name: '标签',
            type: 'String',
            required: true,
            description: '文章标签',
            example: '心理健康,学生指导'
          },
          {
            name: '内容',
            type: 'String',
            required: true,
            description: '使用HTML富文本内容',
            example: '<h1>心理健康指导</h1><p>内容...</p>'
          }
        ],
        template: '请发布一篇标题为"[标题]"的文章，描述："[描述]"，标签："[标签]"'
      },
      {
        id: 'exportStudentTestResult',
        name: '导出学生测评结果',
        description: '根据学号导出学生的测评结果记录',
        keywords: ['导出测评', '测评结果', '学生测评', '心理测评'],
        parameters: [
          {
            name: '登录用户ID',
            type: 'Long',
            required: true,
            description: '登录用户id',
            example: '123'
          },
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          },
          {
            name: '查询条数',
            type: 'Integer',
            required: false,
            description: '查询几条数据，如果没传默认为10条',
            example: '15'
          }
        ],
        template: '请导出学号为 [学号] 的学生最近 [条数] 条测评结果'
      },
      {
        id: 'exportStudentList',
        name: '导出学生列表',
        description: '导出学生列表',
        keywords: ['导出学生', '学生列表', '学生名单'],
        parameters: [
          {
            name: '登录用户ID',
            type: 'Long',
            required: true,
            description: '登录用户id',
            example: '123'
          },
          {
            name: '查询条数',
            type: 'Integer',
            required: false,
            description: '查询几条数据，如果没传默认为10条',
            example: '20'
          }
        ],
        template: '请导出最近 [条数] 条学生列表'
      },
      {
        id: 'getStudentSportRecord',
        name: '获取学生运动记录',
        description: '获取学生最近的运动记录',
        keywords: ['运动记录', '体育活动', '健身记录', '运动数据'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请查询学号为 [学号] 的学生运动记录'
      },
      {
        id: 'getStudentCircle',
        name: '获取学生关注圈子',
        description: '获取学生关注的圈子',
        keywords: ['关注圈子', '社交圈子', '兴趣圈', '圈子'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请查询学号为 [学号] 的学生关注的圈子'
      },
      {
        id: 'getStudentAttention',
        name: '获取学生关注用户',
        description: '获取学生关注的用户',
        keywords: ['关注用户', '关注列表', '好友关注'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请查询学号为 [学号] 的学生关注的用户'
      },
      {
        id: 'getStudentLike',
        name: '获取学生点赞文章',
        description: '获取用户点赞的文章',
        keywords: ['点赞文章', '喜欢的文章', '收藏文章'],
        parameters: [
          {
            name: '学生学号',
            type: 'String',
            required: true,
            description: '学生的学号',
            example: '2021001'
          }
        ],
        template: '请查询学号为 [学号] 的学生点赞的文章'
      },
      {
        id: 'crawlerWeb',
        name: '爬取网页数据',
        description: '爬取网页数据链接数据',
        keywords: ['爬取网页', '网页数据', '网站信息', '爬虫'],
        parameters: [
          {
            name: '网址',
            type: 'String',
            required: true,
            description: '要爬取的网址',
            example: 'https://example.com'
          }
        ],
        template: '请爬取网址 [网址] 的内容'
      },
      {
        id: 'baiDuMCP',
        name: '百度地图工具',
        description: '该工具是一个地图的智能体，具有以下功能，提供全场景覆盖的地理信息服务，包括地理编码、逆地理编码、IP定位、天气查询、骑行路径规划、步行路径规划、驾车路径规划、公交路径规划、距离测量、关键词搜索、周边搜索、详情搜索等',
        keywords: ['地图', '导航', '位置', '路径规划', '地理信息', '天气', '搜索', '定位'],
        parameters: [
          {
            name: '问题描述',
            type: 'String',
            required: true,
            description: '地图相关的问题描述',
            example: '从北京到上海的驾车路线规划'
          }
        ],
        template: '地图查询：[问题描述]'
      }
    ])

    // 计算当前匹配的工具
    const currentTool = computed(() => {
      if (!props.userInput) return null
      
      const input = props.userInput.toLowerCase()
      
      // 查找匹配的工具
      for (const tool of tools.value) {
        const matchesKeyword = tool.keywords.some(keyword => 
          input.includes(keyword.toLowerCase())
        )
        if (matchesKeyword) {
          return tool
        }
      }
      
      return null
    })

    // 计算建议
    const suggestions = computed(() => {
      if (!props.userInput || props.userInput.length < 2) return []
      
      const input = props.userInput.toLowerCase()
      
      return tools.value
        .filter(tool => {
          return tool.keywords.some(keyword => 
            keyword.toLowerCase().includes(input) || 
            input.includes(keyword.toLowerCase())
          )
        })
        .slice(0, 3)
        .map(tool => ({
          id: tool.id,
          title: tool.name,
          description: tool.description,
          tool: tool
        }))
    })

    // 是否显示提示
    const showPrompt = computed(() => {
      return props.visible && (currentTool.value || suggestions.value.length > 0)
    })

    // 关闭提示
    const closePrompt = () => {
      emit('close')
    }

    // 生成示例问题
    const generateExample = () => {
      if (!currentTool.value) return
      
      let example = currentTool.value.template
      currentTool.value.parameters.forEach(param => {
        const placeholder = `[${param.name}]`
        if (example.includes(placeholder)) {
          example = example.replace(placeholder, param.example || `请输入${param.name}`)
        }
      })
      
      emit('insert-text', example)
    }

    // 插入模板
    const insertTemplate = () => {
      if (!currentTool.value) return
      emit('insert-text', currentTool.value.template)
    }

    // 选择建议
    const selectSuggestion = (suggestion) => {
      emit('select-suggestion', suggestion)
    }

    return {
      tools,
      currentTool,
      suggestions,
      showPrompt,
      closePrompt,
      generateExample,
      insertTemplate,
      selectSuggestion
    }
  }
}
</script>

<style scoped>
.tool-prompt-helper {
  position: absolute;
  bottom: 100%;
  left: 0;
  right: 0;
  margin-bottom: 8px;
  z-index: 1000;
}

.prompt-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #e5e7eb;
  max-height: 500px;
  overflow-y: auto;
}

.prompt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px 12px;
  border-bottom: 1px solid #f3f4f6;
}

.prompt-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.prompt-title .icon {
  width: 20px;
  height: 20px;
  color: #3b82f6;
}

.close-btn {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f3f4f6;
}

.close-btn svg {
  width: 16px;
  height: 16px;
  color: #6b7280;
}

.prompt-content {
  padding: 0 20px 20px;
}

.tool-info h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.tool-description {
  margin: 0 0 16px 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
}

.parameters-section h5,
.suggestions-section h5 {
  margin: 16px 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.parameter-list {
  space-y: 12px;
}

.parameter-item {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 3px solid #e5e7eb;
  margin-bottom: 8px;
}

.parameter-item.required {
  border-left-color: #ef4444;
  background: #fef2f2;
}

.param-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.param-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.param-type {
  font-size: 12px;
  color: #6b7280;
  background: #e5e7eb;
  padding: 2px 6px;
  border-radius: 4px;
}

.required-badge {
  font-size: 11px;
  color: #ef4444;
  background: #fee2e2;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.param-description {
  margin: 4px 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
}

.param-example {
  margin-top: 6px;
  font-size: 12px;
}

.example-label {
  color: #6b7280;
  margin-right: 4px;
}

.param-example code {
  background: #f3f4f6;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', monospace;
  color: #1f2937;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.action-btn.primary {
  background: #3b82f6;
  color: white;
}

.action-btn.primary:hover {
  background: #2563eb;
}

.action-btn.secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.action-btn.secondary:hover {
  background: #e5e7eb;
}

.suggestion-list {
  space-y: 8px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e5e7eb;
  margin-bottom: 8px;
}

.suggestion-item:hover {
  background: #f3f4f6;
  border-color: #3b82f6;
}

.suggestion-content h6 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.suggestion-content p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  color: #9ca3af;
  transition: color 0.2s;
}

.suggestion-item:hover .arrow-icon {
  color: #3b82f6;
}
</style> 