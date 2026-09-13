# 数字人智能动作功能使用说明

## 功能概述

智能动作功能可以根据用户输入的内容和情感状态，自动为不同的数字人选择合适的动作。系统会分析文本内容，识别情感，并推荐最符合场景的动作。

## 主要特性

### 🎯 智能动作选择
- **内容分析**: 根据关键词匹配合适的动作
- **情感识别**: 基于情感分析结果推荐动作
- **个性化**: 为不同数字人提供专属动作映射
- **智能延迟**: 根据内容长度计算合适的执行时机

### 🎭 支持的数字人
- **马可** (110017006): 商务风格，适合正式场合
- **小满** (138805001): 功能丰富，支持数字手势
- **安安** (138801001): 亲和力强，适合客服场景
- **晓姿** (110117005): 优雅大方，适合展示介绍
- **晓娴** (110021006): 专业稳重，适合商务演示
- **沐沐** (110332017): 温和友善，适合教育场景
- **可爱** (130033001): 活泼可爱，支持特殊动作
- **晓依** (110005011): 简洁清爽，适合简单交互

### 📝 动作分类

#### 1. 问候类
- **触发词**: 你好、您好、早上好、下午好、晚上好、初次见面、很高兴、认识你
- **推荐动作**: 挥手、打招呼、欢迎、鞠躬

#### 2. 告别类
- **触发词**: 再见、拜拜、告别、下次见、回头见、明天见
- **推荐动作**: 再见、挥手告别

#### 3. 赞美/鼓励类
- **触发词**: 好棒、真棒、厉害、不错、很好、加油、继续、坚持、努力、点赞
- **推荐动作**: 点赞、鼓掌、加油、比心

#### 4. 介绍/展示类
- **触发词**: 介绍、展示、看看、这是、推荐、建议、分享、告诉你
- **推荐动作**: 有请、介绍、展示、指引

#### 5. 强调类
- **触发词**: 重要、注意、关键、必须、一定、强调、记住、特别
- **推荐动作**: 强调手势、指向

#### 6. 思考类
- **触发词**: 想想、考虑、思考、让我想想、嗯、这个问题、分析
- **推荐动作**: 思考状态、沉思

#### 7. 拒绝/否定类
- **触发词**: 不、不是、不对、不行、拒绝、不可以、不能、没有
- **推荐动作**: 摇头、拒绝手势

#### 8. 疑问/困惑类
- **触发词**: 什么、为什么、怎么、如何、不懂、不明白、困惑、疑问
- **推荐动作**: 摊手、困惑状态

#### 9. 数字类
- **触发词**: 第一、第二、第三、第四、第五、第六、第七、第八、第九、1-9
- **推荐动作**: 数字手势（小满专属）

#### 10. 特殊情感类
- **触发词**: 爱你、喜欢、亲亲、么么哒、中国、祖国、跳舞、开心
- **推荐动作**: 飞吻、比心、跳舞、爱国手势

## 使用方法

### 1. 在 DigitalHumanAvatar 组件中使用

智能动作功能已经集成到 `DigitalHumanAvatar` 组件中，会在以下情况自动触发：

- 用户通过输入框发送消息后
- AI 回复完成后
- 通过 `appCome` 方法接收到 uniapp 消息后

### 2. 手动调用

```javascript
import { getSmartActions, getActionDelay } from '@/utils/digitalHumanActions.js'
import { detectEmotion } from '@/utils/digital-human.js'

// 获取智能推荐动作
const content = "你好，很高兴见到你"
const avatarId = "138805001" // 小满
const emotion = detectEmotion(content)

const actions = getSmartActions(content, avatarId, {
  emotion,
  maxActions: 1,
  includeRandom: false
})

if (actions.length > 0) {
  const action = actions[0]
  const delay = getActionDelay(content)
  
  // 延迟执行动作
  setTimeout(() => {
    digitalHumanStore.sendActionToAvatar(action.id)
  }, delay)
}
```

### 3. 在配置页面测试

1. 打开数字人配置页面
2. 选择数字人形象
3. 在"智能动作测试"区域输入测试内容
4. 点击"测试智能动作"按钮
5. 系统会自动推荐并执行动作

## API 参考

### getSmartActions(content, avatarId, options)

智能动作选择器主函数

**参数:**
- `content` (string): 输入内容
- `avatarId` (string): 数字人ID
- `options` (object): 选项配置
  - `emotion` (object): 情感分析结果
  - `maxActions` (number): 最大动作数量，默认1
  - `includeRandom` (boolean): 是否包含随机动作，默认false

**返回值:**
- Array: 推荐动作数组

### getActionsByContent(content, avatarId, maxActions)

根据内容获取动作

**参数:**
- `content` (string): 输入内容
- `avatarId` (string): 数字人ID
- `maxActions` (number): 最大动作数量，默认1

### getActionsByEmotion(emotion, avatarId)

根据情感获取动作

**参数:**
- `emotion` (object): 情感分析结果
- `avatarId` (string): 数字人ID

### getActionDelay(content)

计算动作执行延迟

**参数:**
- `content` (string): 内容

**返回值:**
- number: 延迟时间（毫秒）

## 配置说明

### 动作映射规则

动作映射规则定义在 `digitalHumanActions.js` 中的 `contentActionMapping` 对象中。每个规则包含：

- `keywords`: 触发关键词数组
- `actions.common`: 通用动作关键词
- `actions.specific`: 特定数字人的动作ID

### 默认动作

每个数字人都有默认动作，当没有匹配到合适动作时会使用：

```javascript
const defaultActionIds = {
  110017006: ['A_RH_hello_O', 'A_RH_emphasize2_O'], // 马可
  138805001: ['A_H_listen_C', 'A_H_nod_O'], // 小满
  138801001: ['A_U_greet_O', 'A_U_understand_O'], // 安安
  // ... 其他数字人
}
```

## 测试功能

### 单个测试

```javascript
import { testSmartAction } from '@/utils/actionTestUtils.js'

const result = testSmartAction('你好，很高兴见到你', '138805001')
console.log(result)
```

### 批量测试

```javascript
import { batchTestActions } from '@/utils/actionTestUtils.js'

const results = batchTestActions('138805001')
console.log(results)
```

### 测试所有数字人

```javascript
import { testAllAvatars } from '@/utils/actionTestUtils.js'

const results = testAllAvatars('你好，很高兴见到你')
console.log(results)
```

## 注意事项

1. **数字人配置**: 确保数字人已正确配置并启动
2. **动作支持**: 不同数字人支持的动作不同，请参考具体的动作列表
3. **延迟执行**: 动作会根据内容长度自动延迟执行，避免与语音冲突
4. **错误处理**: 系统会自动处理动作执行失败的情况
5. **性能考虑**: 避免频繁调用动作执行，建议设置合理的间隔

## 扩展指南

### 添加新的动作分类

1. 在 `contentActionMapping` 中添加新的分类
2. 定义关键词和动作映射
3. 为不同数字人配置特定动作

### 添加新的数字人支持

1. 在 `actionData` 中添加新数字人的动作列表
2. 在各个动作分类中添加对应的映射
3. 设置默认动作

### 自定义动作选择逻辑

可以通过修改 `getSmartActions` 函数来自定义动作选择逻辑，例如：

- 添加时间因素（早上、下午、晚上不同动作）
- 添加用户偏好记忆
- 添加上下文感知
- 添加动作组合

## 常见问题

### Q: 为什么动作没有执行？
A: 检查数字人是否已启动，动作ID是否正确，以及是否有足够的权限执行动作。

### Q: 如何调试动作选择？
A: 使用测试工具或查看控制台日志，可以看到完整的动作选择过程。

### Q: 动作执行时机不对？
A: 可以通过 `getActionDelay` 函数调整延迟时间，或者手动设置延迟。

### Q: 如何禁用智能动作？
A: 在调用 `getSmartActions` 时传入空的 `content` 参数，或者修改组件中的调用逻辑。

## 更新日志

### v1.0.0
- 初始版本发布
- 支持8个数字人
- 实现10种动作分类
- 集成情感分析
- 提供测试工具 