/**
 * 动作测试工具
 * 用于在配置页面测试智能动作功能
 */

import { getSmartActions, getActionsByContent, getActionsByEmotion } from './digitalHumanActions.js'
import { detectEmotion } from './digital-human.js'

/**
 * 测试智能动作选择
 * @param {string} content - 测试内容
 * @param {string} avatarId - 数字人ID
 * @returns {Object} 测试结果
 */
export const testSmartAction = (content, avatarId) => {
  console.log('=== 智能动作测试 ===')
  console.log('输入内容:', content)
  console.log('数字人ID:', avatarId)
  
  // 确保content是字符串类型
  const textContent = typeof content === 'string' ? content : JSON.stringify(content)
  
  // 1. 情感分析
  const emotion = detectEmotion(textContent)
  console.log('情感分析结果:', emotion)
  
  // 2. 根据内容获取动作
  const contentActions = getActionsByContent(textContent, avatarId, 3)
  console.log('基于内容的动作推荐:', contentActions)
  
  // 3. 根据情感获取动作
  const emotionActions = getActionsByEmotion(emotion, avatarId)
  console.log('基于情感的动作推荐:', emotionActions)
  
  // 4. 智能综合推荐
  const smartActions = getSmartActions(textContent, avatarId, {
    emotion,
    maxActions: 2,
    includeRandom: false
  })
  console.log('智能综合推荐:', smartActions)
  
  return {
    content: textContent,
    avatarId,
    emotion,
    contentActions,
    emotionActions,
    smartActions
  }
}

/**
 * 批量测试不同内容的动作推荐
 * @param {string} avatarId - 数字人ID
 * @returns {Array} 测试结果数组
 */
export const batchTestActions = (avatarId) => {
  const testContents = [
    '你好，很高兴见到你',
    '再见，下次见',
    '你真棒，继续加油',
    '让我来介绍一下这个产品',
    '这个问题很重要，请注意',
    '让我想想这个问题',
    '不，这样不对',
    '我不明白你的意思',
    '第一点是...',
    '我爱你，么么哒',
    '我感觉很难受，有些消极的想法',
    '推荐一些音乐给我',
    '我想学习一些知识'
  ]
  
  const results = []
  
  testContents.forEach(content => {
    const result = testSmartAction(content, avatarId)
    results.push(result)
  })
  
  return results
}

/**
 * 测试所有数字人的动作推荐
 * @param {string} content - 测试内容
 * @returns {Object} 所有数字人的测试结果
 */
export const testAllAvatars = (content) => {
  const avatarIds = [
    '110017006', // 马可
    '138805001', // 小满
    '138801001', // 安安
    '110117005', // 晓姿
    '110021006', // 晓娴
    '110332017', // 沐沐
    '130033001', // 可爱
    '110005011', // 晓依
  ]
  
  const results = {}
  
  avatarIds.forEach(avatarId => {
    results[avatarId] = testSmartAction(content, avatarId)
  })
  
  return results
}

/**
 * 在配置页面添加测试按钮的辅助函数
 * @param {string} avatarId - 当前选中的数字人ID
 * @param {Function} executeAction - 执行动作的函数
 */
export const addTestButton = (avatarId, executeAction) => {
  // 创建测试按钮
  const testButton = document.createElement('button')
  testButton.textContent = '测试智能动作'
  testButton.style.cssText = `
    margin: 10px;
    padding: 8px 16px;
    background: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  `
  
  // 点击事件
  testButton.addEventListener('click', () => {
    const testContent = prompt('请输入测试内容:', '你好，很高兴见到你')
    if (testContent) {
      const result = testSmartAction(testContent, avatarId)
      if (result.smartActions.length > 0) {
        const action = result.smartActions[0]
        console.log(`执行推荐动作: ${action.name} (${action.id})`)
        executeAction(action.id)
      } else {
        console.log('没有找到合适的动作')
      }
    }
  })
  
  // 添加到页面
  const controlPanel = document.querySelector('.control-panel')
  if (controlPanel) {
    controlPanel.appendChild(testButton)
  }
}

export default {
  testSmartAction,
  batchTestActions,
  testAllAvatars,
  addTestButton
} 