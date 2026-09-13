# 文本过滤工具使用说明

## 概述

文本过滤工具 (`textFilter.js`) 是专门为数字人应用设计的文本清理和过滤工具，用于清理发送给数字人的文本内容，移除各种杂乱字符、格式标记和不适合语音播报的内容。

## 主要功能

### 1. 基础文本清理 (`basicTextClean`)

移除基本的格式化字符和HTML标签：

```javascript
import { basicTextClean } from '@/utils/textFilter.js'

const text = '# 标题\n\n**粗体**文本和<strong>HTML</strong>标签'
const cleaned = basicTextClean(text)
console.log(cleaned) // "标题 粗体文本和HTML标签"
```

### 2. 高级文本清理 (`advancedTextClean`)

在基础清理的基础上，还会移除URL、邮箱、电话号码等：

```javascript
import { advancedTextClean } from '@/utils/textFilter.js'

const text = '请访问 https://example.com 或发送邮件到 maintainer@example.com'
const cleaned = advancedTextClean(text)
console.log(cleaned) // "请访问 或发送邮件到"
```

### 3. 智能文本清理 (`smartTextClean`)

提供更多配置选项的智能清理：

```javascript
import { smartTextClean } from '@/utils/textFilter.js'

const text = '# 标题\n\n**重要**消息！！！😊 123'
const cleaned = smartTextClean(text, {
  removeEmoji: true,     // 移除emoji
  removeNumbers: true,   // 移除数字
  removeEnglish: false,  // 保留英文
  maxLength: 200         // 最大长度限制
})
console.log(cleaned) // "标题 重要消息"
```

### 4. 数字人专用过滤 (`filterForDigitalHuman`) ⭐推荐

专门为数字人播报优化的过滤函数：

```javascript
import { filterForDigitalHuman } from '@/utils/textFilter.js'

const text = 'System: 用户询问天气\n\n```javascript\nconst weather = "sunny";\n```\n\n今天天气很好！'
const filtered = filterForDigitalHuman(text)
console.log(filtered) // "今天天气很好"
```

### 5. 文本检查 (`checkTextForDigitalHuman`)

检查文本是否适合数字人播报：

```javascript
import { checkTextForDigitalHuman } from '@/utils/textFilter.js'

const text = '###ERROR###@#$%^&*()+=[]{}|\\:";\'<>?~`'
const result = checkTextForDigitalHuman(text)
console.log(result)
// {
//   isValid: false,
//   reason: "包含过多特殊字符",
//   suggestions: ["建议使用更自然的语言表达"],
//   cleanText: "ERROR"
// }
```

### 6. 批量处理 (`batchTextFilter`)

批量处理多个文本：

```javascript
import { batchTextFilter } from '@/utils/textFilter.js'

const textArray = [
  '你好世界！',
  '# 这是标题',
  '```代码块```',
  '正常文本'
]

const filtered = batchTextFilter(textArray)
console.log(filtered) // ["你好世界", "这是标题", "正常文本"]
```

### 7. 预设配置 (`filterWithPreset`)

使用预设的过滤配置：

```javascript
import { filterWithPreset } from '@/utils/textFilter.js'

const text = '# 标题\n\n**重要**消息！！！😊'

// 严格模式：移除所有可能影响播报的内容
const strict = filterWithPreset(text, 'strict')

// 标准模式：保留基本内容，移除格式化字符
const standard = filterWithPreset(text, 'standard')

// 宽松模式：仅移除明显的干扰内容
const loose = filterWithPreset(text, 'loose')
```

## 在项目中的集成

### 1. DigitalHumanAvatar.vue 组件

```javascript
// 在 appCome 方法中
async appCome(message) {
  // 过滤文本内容
  const filteredMessage = filterForDigitalHuman(message)
  if (!filteredMessage) {
    console.log('消息过滤后为空，跳过发送')
    return
  }
  
  // 发送过滤后的消息给数字人
  await this.digitalHumanStore.sendTextToAvatar(filteredMessage)
}
```

### 2. 配置页面

```javascript
// 在 writeText 方法中
writeText() {
  const filteredText = filterForDigitalHuman(this.textarea)
  if (!filteredText) {
    this.$message.warning('文本过滤后为空，请输入有效内容')
    return
  }
  
  // 检查文本质量
  const checkResult = checkTextForDigitalHuman(filteredText)
  if (!checkResult.isValid) {
    this.$message.warning(`文本检查失败: ${checkResult.reason}`)
  }
  
  // 发送给数字人
  avatarPlatform2.writeText(filteredText, config)
}
```

### 3. App.vue 中的消息处理

```javascript
// 处理来自uniapp的消息
const handleUniappMessage = async (event) => {
  const message = typeof event.data === 'string' ? event.data : JSON.stringify(event.data)
  
  // 过滤文本内容
  const filteredMessage = filterForDigitalHuman(message)
  if (filteredMessage) {
    digitalRef.value.appCome(filteredMessage)
  }
}
```

## 过滤规则说明

### 会被移除的内容：

1. **Markdown格式**：`# ## ###`、`**粗体**`、`*斜体*`、`~~删除线~~`
2. **HTML标签**：`<div>`、`<p>`、`<strong>` 等
3. **代码块**：```代码``` 和 `行内代码`
4. **特殊字符**：`#@$%^&*()+=[]{}|\\:";'<>?~`
5. **URL链接**：`https://example.com`、`www.example.com`
6. **邮箱地址**：`maintainer@example.com`
7. **系统提示**：`System:`、`Assistant:`、`用户:`
8. **JSON数据**：`{"key": "value"}`
9. **重复标点**：`！！！`、`？？？`、`，，，`
10. **过长数字串**：可能是ID或标识符的长数字

### 会被保留的内容：

1. **正常的中文文本**
2. **必要的标点符号**：句号、逗号、感叹号、问号（单个）
3. **数字**：正常的数字内容
4. **英文单词**：正常的英文内容
5. **emoji表情**：默认保留，可配置移除

## 测试功能

项目包含完整的测试套件 (`textFilterTest.js`)：

```javascript
// 在浏览器控制台中运行
window.textFilterTest.runCompleteTest()  // 运行完整测试
window.textFilterTest.runAllTests()      // 运行基本测试
window.textFilterTest.testPresets()      // 测试预设配置
window.textFilterTest.performanceTest()  // 性能测试
```

## 性能说明

- 单次过滤耗时：< 1ms
- 支持批量处理
- 内存占用低
- 适合实时文本处理

## 配置建议

### 对于不同场景的推荐配置：

1. **用户输入**：使用 `filterForDigitalHuman()` - 最适合数字人播报
2. **AI回复**：使用 `smartTextClean()` - 保留更多内容但清理格式
3. **系统消息**：使用 `basicTextClean()` - 基础清理即可
4. **批量处理**：使用 `batchTextFilter()` - 高效处理多个文本

### 预设配置选择：

- **strict**：严格模式，适合正式场合
- **standard**：标准模式，适合日常使用
- **loose**：宽松模式，适合保留更多原始内容

## 注意事项

1. 过滤后的文本可能会变短，需要检查是否为空
2. 某些特殊字符可能影响语音合成效果
3. 建议在发送给数字人之前都进行过滤
4. 可以根据实际需求调整过滤规则
5. 测试功能可以帮助验证过滤效果

## 更新日志

- v1.0.0：初始版本，包含基础过滤功能
- v1.1.0：增加智能过滤和预设配置
- v1.2.0：增加文本检查和批量处理功能
- v1.3.0：增加完整的测试套件和性能优化 