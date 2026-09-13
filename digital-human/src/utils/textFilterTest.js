/**
 * 文本过滤功能测试
 * 展示各种文本过滤场景和效果
 */

import { 
  filterForDigitalHuman, 
  checkTextForDigitalHuman, 
  smartTextClean,
  filterWithPreset,
  batchTextFilter
} from './textFilter.js'

/**
 * 测试用例数据
 */
const testCases = [
  {
    name: '包含Markdown格式的文本',
    input: '# 这是标题\n\n**粗体文本**和*斜体文本*\n\n- 列表项1\n- 列表项2\n\n```javascript\nconst hello = "world";\n```',
    expected: '这是标题 粗体文本和斜体文本 列表项1 列表项2'
  },
  {
    name: '包含HTML标签的文本',
    input: '<div>这是一个<strong>重要</strong>的消息</div><p>请注意查看</p>',
    expected: '这是一个重要的消息 请注意查看'
  },
  {
    name: '包含特殊字符的文本',
    input: '你好！！！这是一个#测试@消息$%^&*()+=[]{}|\\:";\'<>?~`',
    expected: '你好 这是一个测试消息'
  },
  {
    name: '包含URL和邮箱的文本',
    input: '请访问 https://example.com 或发送邮件到 maintainer@example.com 联系我们',
    expected: '请访问 或发送邮件到 联系我们'
  },
  {
    name: '包含代码的文本',
    input: '这是一段代码：```function test() { return "hello"; }``` 请查看',
    expected: '这是一段代码 请查看'
  },
  {
    name: '包含系统提示的文本',
    input: 'System: 用户询问关于天气的问题\nAssistant: 今天天气很好，适合外出',
    expected: '今天天气很好 适合外出'
  },
  {
    name: '包含JSON数据的文本',
    input: '用户信息：{"name": "张三", "age": 25} 请处理',
    expected: '用户信息 请处理'
  },
  {
    name: '正常的中文文本',
    input: '你好，很高兴见到你！今天天气不错，适合出门散步。',
    expected: '你好 很高兴见到你 今天天气不错 适合出门散步'
  },
  {
    name: '包含重复标点的文本',
    input: '太好了！！！真的吗？？？当然，，，没问题。。。',
    expected: '太好了 真的吗 当然 没问题'
  },
  {
    name: '空文本和特殊情况',
    input: '',
    expected: ''
  }
]

/**
 * 运行单个测试用例
 */
const runSingleTest = (testCase) => {
  console.log(`\n=== 测试: ${testCase.name} ===`)
  console.log('输入:', testCase.input)
  
  const result = filterForDigitalHuman(testCase.input)
  console.log('输出:', result)
  
  const checkResult = checkTextForDigitalHuman(testCase.input)
  console.log('检查结果:', checkResult.isValid ? '✅ 通过' : '❌ 失败')
  if (!checkResult.isValid) {
    console.log('失败原因:', checkResult.reason)
    console.log('建议:', checkResult.suggestions)
  }
  
  console.log('---')
  
  return {
    ...testCase,
    result,
    checkResult
  }
}

/**
 * 运行所有测试用例
 */
export const runAllTests = () => {
  console.log('🧪 开始运行文本过滤测试...\n')
  
  const results = testCases.map(runSingleTest)
  
  // 统计结果
  const passCount = results.filter(r => r.checkResult.isValid).length
  const totalCount = results.length
  
  console.log(`\n📊 测试结果统计:`)
  console.log(`总测试数: ${totalCount}`)
  console.log(`通过数: ${passCount}`)
  console.log(`失败数: ${totalCount - passCount}`)
  console.log(`通过率: ${((passCount / totalCount) * 100).toFixed(1)}%`)
  
  return results
}

/**
 * 测试不同预设配置
 */
export const testPresets = () => {
  console.log('\n🎛️ 测试不同预设配置...\n')
  
  const testText = '# 标题\n\n**重要**消息！！！请访问 https://example.com 查看详情。\n\n😊 谢谢！'
  
  const presets = ['strict', 'standard', 'loose']
  
  presets.forEach(preset => {
    console.log(`--- ${preset.toUpperCase()} 模式 ---`)
    console.log('输入:', testText)
    
    const result = filterWithPreset(testText, preset)
    console.log('输出:', result)
    console.log('长度:', result.length)
    console.log('')
  })
}

/**
 * 测试批量处理
 */
export const testBatchProcessing = () => {
  console.log('\n📦 测试批量处理...\n')
  
  const textArray = [
    '你好世界！',
    '# 这是标题',
    '```code here```',
    '正常的文本内容',
    '',
    '包含URL: https://example.com',
    '**粗体**文本'
  ]
  
  console.log('输入数组:', textArray)
  
  const results = batchTextFilter(textArray)
  console.log('输出数组:', results)
  console.log('过滤前数量:', textArray.length)
  console.log('过滤后数量:', results.length)
}

/**
 * 性能测试
 */
export const performanceTest = () => {
  console.log('\n⚡ 性能测试...\n')
  
  const testText = '# 标题\n\n这是一个包含**粗体**和*斜体*的测试文本。\n\n```javascript\nconst test = "hello";\n```\n\n请访问 https://example.com 了解更多信息。'
  
  const iterations = 1000
  
  console.log(`测试文本长度: ${testText.length}`)
  console.log(`测试次数: ${iterations}`)
  
  const startTime = performance.now()
  
  for (let i = 0; i < iterations; i++) {
    filterForDigitalHuman(testText)
  }
  
  const endTime = performance.now()
  const totalTime = endTime - startTime
  const avgTime = totalTime / iterations
  
  console.log(`总耗时: ${totalTime.toFixed(2)}ms`)
  console.log(`平均耗时: ${avgTime.toFixed(4)}ms`)
  console.log(`处理速度: ${(iterations / (totalTime / 1000)).toFixed(0)} 次/秒`)
}

/**
 * 实际使用示例
 */
export const usageExamples = () => {
  console.log('\n💡 实际使用示例...\n')
  
  // 示例1: 基本过滤
  console.log('--- 示例1: 基本过滤 ---')
  const userInput = '用户说: # 你好！请帮我查询**天气**信息 https://weather.com'
  const filtered = filterForDigitalHuman(userInput)
  console.log('用户输入:', userInput)
  console.log('过滤结果:', filtered)
  console.log('')
  
  // 示例2: 智能过滤
  console.log('--- 示例2: 智能过滤 ---')
  const aiResponse = 'AI助手回复：\n\n根据您的询问，我为您整理了以下信息：\n\n1. **天气情况**：晴天\n2. **温度**：25°C\n\n```json\n{"weather": "sunny", "temp": 25}\n```\n\n希望对您有帮助！'
  const smartFiltered = smartTextClean(aiResponse, {
    removeEmoji: false,
    maxLength: 200
  })
  console.log('AI回复:', aiResponse)
  console.log('智能过滤:', smartFiltered)
  console.log('')
  
  // 示例3: 文本检查
  console.log('--- 示例3: 文本检查 ---')
  const problematicText = '###ERROR###@#$%^&*()+=[]{}|\\:";\'<>?~`'
  const checkResult = checkTextForDigitalHuman(problematicText)
  console.log('问题文本:', problematicText)
  console.log('检查结果:', checkResult)
  console.log('')
}

/**
 * 运行所有测试
 */
export const runCompleteTest = () => {
  console.log('🚀 开始完整测试套件...\n')
  
  runAllTests()
  testPresets()
  testBatchProcessing()
  performanceTest()
  usageExamples()
  
  console.log('\n✅ 所有测试完成！')
}

// 如果在浏览器环境中，添加到全局对象
if (typeof window !== 'undefined') {
  window.textFilterTest = {
    runAllTests,
    testPresets,
    testBatchProcessing,
    performanceTest,
    usageExamples,
    runCompleteTest
  }
  
  console.log('文本过滤测试已加载，可以使用以下命令:')
  console.log('- window.textFilterTest.runCompleteTest() // 运行完整测试')
  console.log('- window.textFilterTest.runAllTests() // 运行基本测试')
  console.log('- window.textFilterTest.testPresets() // 测试预设配置')
  console.log('- window.textFilterTest.performanceTest() // 性能测试')
}

export default {
  runAllTests,
  testPresets,
  testBatchProcessing,
  performanceTest,
  usageExamples,
  runCompleteTest
} 