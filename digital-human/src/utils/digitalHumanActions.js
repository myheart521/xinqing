/**
 * 数字人动作管理工具
 * 根据不同的数字人和输入内容自动选择合适的动作
 */

// 动作数据 - 从配置页面复制过来
const actionData = {
  110017006: [
    // 马可
    { id: 'A_LH_please_O', name: '左手向左有请' },
    { id: 'A_RH_emphasize2_O', name: '右手掌强调' },
    { id: 'A_RH_emphasize_O', name: '右手指竖起强调' },
    { id: 'A_RH_encourage_O', name: '右手握拳加油' },
    { id: 'A_RH_good_O', name: '右手点赞夸奖' },
    { id: 'A_RH_hello_O', name: '右手挥手你好' },
    { id: 'A_RH_please1_O', name: '右手向前有请' },
    { id: 'A_RH_please_O', name: '右手向右有请' },
    { id: 'A_RLH_emphasize_O', name: '双手强调' },
  ],
  138805001: [
    // 小满
    { id: 'A_H_listen_C', name: '点头倾听' },
    { id: 'A_H_nod_O', name: '点头' },
    { id: 'A_H_shake_O', name: '摇头' },
    { id: 'A_H_standby_C', name: '待机' },
    { id: 'A_LH_Motioned_O', name: '请看左下' },
    { id: 'A_LH_Spread_O', name: '左摊手' },
    { id: 'A_RH_circle_O', name: '右手头画圈' },
    { id: 'A_RH_eight_O', name: '第八' },
    { id: 'A_RH_emphasize2_O', name: '右手掌强调' },
    { id: 'A_RH_emphasize5_O', name: '食指强调' },
    { id: 'A_RH_encourage_O', name: '右手握拳加油' },
    { id: 'A_RH_five_O', name: '第五' },
    { id: 'A_RH_four_O', name: '第四' },
    { id: 'A_RH_Motioned_O', name: '请看右下' },
    { id: 'A_RH_nine_O', name: '第九' },
    { id: 'A_RH_one_O', name: '第一' },
    { id: 'A_RH_seven_O', name: '第七' },
    { id: 'A_RH_six_O', name: '第六' },
    { id: 'A_RH_think1_O', name: '思考1' },
    { id: 'A_RH_three_O', name: '第三' },
    { id: 'A_RH_two_O', name: '第二' },
    { id: 'A_RLH_introduced_O', name: '双手铺开介绍' },
    { id: 'A_RLH_no_O', name: '双手十字拒绝' },
    { id: 'A_RLH_puzzle_O', name: '双手一摊' },
    { id: 'A_RLH_Spread_O', name: '连续摊手' },
    { id: 'A_UL_introduced_O', name: '请看左上' },
    { id: 'A_UR_introduced_O', name: '请看右上' },
    { id: 'A_U_applause_O', name: '鼓掌' },
    { id: 'A_U_bow1_O', name: '弯腰鞠躬' },
    { id: 'A_U_bowing_O', name: '作揖' },
    { id: 'A_U_bow_O', name: '鞠躬' },
    { id: 'A_U_click_O', name: '前方轻点击' },
    { id: 'A_U_hello_O', name: '打招呼（单手）' },
    { id: 'A_U_introduced_O', name: '指自己' },
    { id: 'A_U_like_O', name: '比心（单手）' },
    { id: 'A_U_no_O', name: '拒绝' },
    { id: 'A_U_please_O', name: '拜托' },
    { id: 'A_U_puzzle_O', name: '不知道' },
    { id: 'A_U_think2_O', name: '思考2' },
    { id: 'A_WL_please_O', name: '左手向左身体左侧' },
    { id: 'A_WR_introduced_O', name: '单手指听众(右摊手)' },
    { id: 'A_WR_listen_O', name: '右手附耳倾听' },
    { id: 'A_WR_please_O', name: '右手向右身体右侧' },
    { id: 'A_W_behind_O', name: '手背后' },
    { id: 'A_W_bye_O', name: '再见' },
    { id: 'A_W_encourage_O', name: '打call' },
    { id: 'A_W_expect_O', name: '期待' },
    { id: 'A_W_hair_O', name: '整理头发' },
    { id: 'A_W_hello_O', name: '打招呼（双手）' },
    { id: 'A_W_like_O', name: '比心（双手）' },
    { id: 'A_W_ok_O', name: '比OK' },
    { id: 'A_W_rock_O', name: '晃脚腕' },
    { id: 'A_W_shyness_O', name: '害羞' },
    { id: 'A_W_slither_O', name: '右手体前滑动' },
    { id: 'A_W_surname1_O', name: '和手1' },
    { id: 'A_W_surname_O', name: '和手' },
    { id: 'A_W_yawn_O', name: '打呵欠' },
  ],
  138801001: [
    // 安安
    { id: 'A_H_look_around_O', name: '右环视' },
    { id: 'A_LH_look_O', name: '请看左边' },
    { id: 'A_LH_look_top_O', name: '请看左上' },
    { id: 'A_LH_unfold_hand_O', name: '左摊手' },
    { id: 'A_LH_wave_C', name: '左挥手' },
    { id: 'A_RH_bye_C', name: '再见' },
    { id: 'A_RH_finger_heart_O', name: '比心（小）' },
    { id: 'A_RH_look_O', name: '请看右边-2' },
    { id: 'A_RH_look_top_O', name: '请看右上' },
    { id: 'A_RH_raise_hand_O', name: '右抬手' },
    { id: 'A_RLH_applaud_C', name: '鼓掌' },
    { id: 'A_RLH_fold_hand_O', name: '摊合手' },
    { id: 'A_RLH_unfold_hand_O', name: '双摊手' },
    { id: 'A_U_bow_O', name: '鞠躬' },
    { id: 'A_U_emphasize_O', name: '强调' },
    { id: 'A_U_finger_heart_O', name: '比心（大）' },
    { id: 'A_U_greet_O', name: '打招呼' },
    { id: 'A_U_guide_O', name: '指引' },
    { id: 'A_U_ignorant_O', name: '不懂' },
    { id: 'A_U_introduce_O', name: '自我介绍' },
    { id: 'A_U_show_O', name: 'show' },
    { id: 'A_U_understand_O', name: '明白意图' },
  ],
  110117005: [
    // 晓姿
    { id: 'A_LH_introduced_O', name: '左手向左上介绍' },
    { id: 'A_LH_please_O', name: '左手向左有请' },
    { id: 'A_RH_click1_O', name: '右手点击' },
    { id: 'A_RH_emphasize2_O', name: '右手掌强调' },
    { id: 'A_RH_hello_O', name: '右手挥手你好' },
    { id: 'A_RH_introduced1_O', name: '右手向右上介绍' },
    { id: 'A_RH_please1_O', name: '右手向前有请' },
    { id: 'A_RH_please_O', name: '右手向右有请' },
    { id: 'A_RLH_emphasize_O', name: '双手强调' },
    { id: 'A_RLH_welcome_O', name: '双手打开欢迎' },
  ],
  110021006: [
    // 晓娴
    { id: 'A_LH_please_O', name: '左手向左有请' },
    { id: 'A_RH_bye_O', name: '右手挥手再见' },
    { id: 'A_RH_emphasize2_O', name: '右手掌强调' },
    { id: 'A_RH_emphasize_O', name: '右手指竖起强调' },
    { id: 'A_RH_good_O', name: '右手点赞夸奖' },
    { id: 'A_RH_please1_O', name: '右手向前有请' },
    { id: 'A_RLH_emphasize_O', name: '双手强调' },
    { id: 'A_R_introduced_O', name: '右手体侧向下滑动介绍' },
  ],
  110332017: [
    // 沐沐
    { id: 'A_LH_introduced_O', name: '左手向左上介绍' },
    { id: 'A_LH_please_O', name: '左手向左有请' },
    { id: 'A_RH_hello_O', name: '右手挥手你好' },
    { id: 'A_RH_introduced1_O', name: '右手向右上介绍' },
    { id: 'A_RH_please1_O', name: '右手向前有请' },
    { id: 'A_RH_please_O', name: '右手向右有请' },
    { id: 'A_RLH_emphasize_O', name: '双手强调' },
    { id: 'A_RLH_welcome_O', name: '双手打开欢迎' },
  ],
  130033001: [
    // 可爱
    { id: 'A_kiss_O_HU', name: '飞吻' },
    { id: 'A_RH_biye_online_O', name: '比耶' },
    { id: 'A_RH_circle_O', name: '右手头画圈' },
    { id: 'A_RH_emphasize_O', name: '右手指竖起强调' },
    { id: 'A_RH_think1_O', name: '思考1' },
    { id: 'A_RLH_adore_O', name: '崇拜' },
    { id: 'A_RLH_applause_O', name: '鼓掌' },
    { id: 'A_RLH_bye_O', name: '双手再见' },
    { id: 'A_RLH_welcome_O', name: '双手打开欢迎' },
    { id: 'A_U_think2_O', name: '思考2' },
    { id: 'A_WR_ENTER_O', name: '右侧入场' },
    { id: 'A_W_dance_O', name: '跳舞' },
    { id: 'A_W_lovechina_O', name: '我爱中国' },
    { id: 'A_W_See_for_you_O', name: '目及皆你' },
  ],
  110005011: [
    // 晓依
    { id: 'A_LH_click_O', name: '左手点击' },
    { id: 'A_LH_introduced_O', name: '左手向左上介绍' },
    { id: 'A_RH_click_O', name: '右手向下指' },
    { id: 'A_RH_hello_O', name: '右手挥手你好' },
    { id: 'A_RH_introduced1_O', name: '右手向右上介绍' },
    { id: 'A_RH_please1_O', name: '右手向前有请' },
  ],
}

// 内容关键词与动作的映射规则
const contentActionMapping = {
  // 问候类
  greeting: {
    keywords: ['你好', '您好', '早上好', '下午好', '晚上好', '初次见面', '很高兴', '认识你'],
    actions: {
      common: ['hello', 'greet', 'welcome', 'bow'],
      specific: {
        110017006: ['A_RH_hello_O'], // 马可
        138805001: ['A_U_hello_O', 'A_W_hello_O'], // 小满
        138801001: ['A_U_greet_O'], // 安安
        110117005: ['A_RH_hello_O'], // 晓姿
        110021006: ['A_RH_hello_O'], // 晓娴
        110332017: ['A_RH_hello_O'], // 沐沐
        130033001: ['A_RLH_welcome_O'], // 可爱
        110005011: ['A_RH_hello_O'], // 晓依
      }
    }
  },

  // 告别类
  farewell: {
    keywords: ['再见', '拜拜', '告别', '下次见', '回头见', '明天见'],
    actions: {
      common: ['bye'],
      specific: {
        110017006: ['A_RH_hello_O'], // 马可用挥手
        138805001: ['A_W_bye_O'], // 小满
        138801001: ['A_RH_bye_C'], // 安安
        110117005: ['A_RH_hello_O'], // 晓姿用挥手
        110021006: ['A_RH_bye_O'], // 晓娴
        110332017: ['A_RH_hello_O'], // 沐沐用挥手
        130033001: ['A_RLH_bye_O'], // 可爱
        110005011: ['A_RH_hello_O'], // 晓依用挥手
      }
    }
  },

  // 赞美/鼓励类
  praise: {
    keywords: ['好棒', '真棒', '厉害', '不错', '很好', '加油', '继续', '坚持', '努力', '点赞'],
    actions: {
      common: ['good', 'encourage', 'applause', 'like'],
      specific: {
        110017006: ['A_RH_good_O', 'A_RH_encourage_O'], // 马可
        138805001: ['A_RH_encourage_O', 'A_U_applause_O', 'A_W_encourage_O'], // 小满
        138801001: ['A_RLH_applaud_C', 'A_U_finger_heart_O'], // 安安
        110117005: ['A_RLH_emphasize_O'], // 晓姿
        110021006: ['A_RH_good_O'], // 晓娴
        110332017: ['A_RLH_emphasize_O'], // 沐沐
        130033001: ['A_RLH_applause_O', 'A_RH_biye_online_O'], // 可爱
        110005011: ['A_RH_hello_O'], // 晓依
      }
    }
  },

  // 介绍/展示类
  introduce: {
    keywords: ['介绍', '展示', '看看', '这是', '推荐', '建议', '分享', '告诉你'],
    actions: {
      common: ['introduced', 'please', 'show', 'guide'],
      specific: {
        110017006: ['A_RH_please1_O', 'A_RH_please_O', 'A_LH_please_O'], // 马可
        138805001: ['A_RLH_introduced_O', 'A_UL_introduced_O', 'A_UR_introduced_O'], // 小满
        138801001: ['A_U_introduce_O', 'A_U_show_O', 'A_U_guide_O'], // 安安
        110117005: ['A_RH_introduced1_O', 'A_LH_introduced_O', 'A_RH_please1_O'], // 晓姿
        110021006: ['A_RH_please1_O', 'A_LH_please_O'], // 晓娴
        110332017: ['A_RH_introduced1_O', 'A_LH_introduced_O', 'A_RH_please1_O'], // 沐沐
        130033001: ['A_RLH_welcome_O'], // 可爱
        110005011: ['A_RH_introduced1_O', 'A_LH_introduced_O', 'A_RH_please1_O'], // 晓依
      }
    }
  },

  // 强调类
  emphasize: {
    keywords: ['重要', '注意', '关键', '必须', '一定', '强调', '记住', '特别'],
    actions: {
      common: ['emphasize'],
      specific: {
        110017006: ['A_RH_emphasize_O', 'A_RH_emphasize2_O', 'A_RLH_emphasize_O'], // 马可
        138805001: ['A_RH_emphasize2_O', 'A_RH_emphasize5_O'], // 小满
        138801001: ['A_U_emphasize_O'], // 安安
        110117005: ['A_RH_emphasize2_O', 'A_RLH_emphasize_O'], // 晓姿
        110021006: ['A_RH_emphasize_O', 'A_RH_emphasize2_O', 'A_RLH_emphasize_O'], // 晓娴
        110332017: ['A_RLH_emphasize_O'], // 沐沐
        130033001: ['A_RH_emphasize_O'], // 可爱
        110005011: ['A_RH_click1_O'], // 晓依
      }
    }
  },

  // 思考类
  thinking: {
    keywords: ['想想', '考虑', '思考', '让我想想', '嗯', '这个问题', '分析'],
    actions: {
      common: ['think'],
      specific: {
        110017006: ['A_RH_emphasize2_O'], // 马可用强调手势
        138805001: ['A_RH_think1_O', 'A_U_think2_O'], // 小满
        138801001: ['A_U_ignorant_O'], // 安安
        110117005: ['A_RH_emphasize2_O'], // 晓姿
        110021006: ['A_RH_emphasize2_O'], // 晓娴
        110332017: ['A_RLH_emphasize_O'], // 沐沐
        130033001: ['A_RH_think1_O', 'A_U_think2_O'], // 可爱
        110005011: ['A_RH_click1_O'], // 晓依
      }
    }
  },

  // 拒绝/否定类
  refuse: {
    keywords: ['不', '不是', '不对', '不行', '拒绝', '不可以', '不能', '没有'],
    actions: {
      common: ['no', 'shake'],
      specific: {
        110017006: ['A_RH_emphasize2_O'], // 马可用强调
        138805001: ['A_RLH_no_O', 'A_U_no_O', 'A_H_shake_O'], // 小满
        138801001: ['A_RLH_fold_hand_O'], // 安安
        110117005: ['A_RH_emphasize2_O'], // 晓姿
        110021006: ['A_RH_emphasize2_O'], // 晓娴
        110332017: ['A_RLH_emphasize_O'], // 沐沐
        130033001: ['A_RH_emphasize_O'], // 可爱
        110005011: ['A_RH_click1_O'], // 晓依
      }
    }
  },

  // 疑问/困惑类
  question: {
    keywords: ['什么', '为什么', '怎么', '如何', '不懂', '不明白', '困惑', '疑问'],
    actions: {
      common: ['puzzle', 'ignorant'],
      specific: {
        110017006: ['A_RH_emphasize2_O'], // 马可
        138805001: ['A_RLH_puzzle_O', 'A_U_puzzle_O'], // 小满
        138801001: ['A_U_ignorant_O', 'A_RLH_unfold_hand_O'], // 安安
        110117005: ['A_RH_emphasize2_O'], // 晓姿
        110021006: ['A_RH_emphasize2_O'], // 晓娴
        110332017: ['A_RLH_emphasize_O'], // 沐沐
        130033001: ['A_RH_circle_O'], // 可爱
        110005011: ['A_RH_click1_O'], // 晓依
      }
    }
  },

  // 数字类
  numbers: {
    keywords: ['第一', '第二', '第三', '第四', '第五', '第六', '第七', '第八', '第九', '1', '2', '3', '4', '5', '6', '7', '8', '9'],
    actions: {
      common: ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine'],
      specific: {
        138805001: ['A_RH_one_O', 'A_RH_two_O', 'A_RH_three_O', 'A_RH_four_O', 'A_RH_five_O', 'A_RH_six_O', 'A_RH_seven_O', 'A_RH_eight_O', 'A_RH_nine_O'], // 小满
      }
    }
  },

  // 特殊情感类（仅限可爱形象）
  special: {
    keywords: ['爱你', '喜欢', '亲亲', '么么哒', '中国', '祖国', '跳舞', '开心'],
    actions: {
      specific: {
        130033001: ['A_kiss_O_HU', 'A_W_lovechina_O', 'A_W_dance_O', 'A_W_See_for_you_O'], // 可爱
        138805001: ['A_U_like_O', 'A_W_like_O'], // 小满
        138801001: ['A_RH_finger_heart_O', 'A_U_finger_heart_O'], // 安安
      }
    }
  }
}

/**
 * 根据内容和数字人ID获取合适的动作
 * @param {string} content - 输入内容
 * @param {string} avatarId - 数字人ID
 * @param {number} maxActions - 最大返回动作数量
 * @returns {Array} 动作数组
 */
export const getActionsByContent = (content, avatarId, maxActions = 1) => {
  if (!content || !avatarId || !actionData[avatarId]) {
    return []
  }

  // 确保content是字符串类型
  const text = String(content).toLowerCase()
  const availableActions = actionData[avatarId]
  const matchedActions = []

  // 遍历所有映射规则
  for (const [category, mapping] of Object.entries(contentActionMapping)) {
    // 检查是否匹配关键词
    const hasKeyword = mapping.keywords.some(keyword => text.includes(keyword))
    
    if (hasKeyword) {
      // 优先使用特定数字人的动作
      if (mapping.actions.specific && mapping.actions.specific[avatarId]) {
        const specificActions = mapping.actions.specific[avatarId]
        for (const actionId of specificActions) {
          const action = availableActions.find(a => a.id === actionId)
          if (action && !matchedActions.find(m => m.id === action.id)) {
            matchedActions.push({
              ...action,
              category,
              priority: 1
            })
          }
        }
      }
      
      // 如果没有特定动作，使用通用动作
      if (matchedActions.length === 0 && mapping.actions.common) {
        for (const commonAction of mapping.actions.common) {
          const action = availableActions.find(a => 
            a.id.toLowerCase().includes(commonAction) || 
            a.name.includes(commonAction)
          )
          if (action && !matchedActions.find(m => m.id === action.id)) {
            matchedActions.push({
              ...action,
              category,
              priority: 2
            })
          }
        }
      }
    }
  }

  // 如果没有匹配的动作，返回一些默认的常用动作
  if (matchedActions.length === 0) {
    const defaultActions = getDefaultActions(avatarId)
    matchedActions.push(...defaultActions)
  }

  // 按优先级排序，然后随机选择
  matchedActions.sort((a, b) => a.priority - b.priority)
  
  // 随机选择指定数量的动作
  const result = []
  const shuffled = [...matchedActions].sort(() => Math.random() - 0.5)
  
  for (let i = 0; i < Math.min(maxActions, shuffled.length); i++) {
    result.push(shuffled[i])
  }

  return result
}

/**
 * 获取默认动作
 * @param {string} avatarId - 数字人ID
 * @returns {Array} 默认动作数组
 */
const getDefaultActions = (avatarId) => {
  const availableActions = actionData[avatarId] || []
  
  // 每个数字人的默认动作
  const defaultActionIds = {
    110017006: ['A_RH_hello_O', 'A_RH_emphasize2_O'], // 马可
    138805001: ['A_H_listen_C', 'A_H_nod_O'], // 小满
    138801001: ['A_U_greet_O', 'A_U_understand_O'], // 安安
    110117005: ['A_RH_hello_O', 'A_RH_emphasize2_O'], // 晓姿
    110021006: ['A_RH_emphasize2_O', 'A_RH_good_O'], // 晓娴
    110332017: ['A_RH_hello_O', 'A_RLH_emphasize_O'], // 沐沐
    130033001: ['A_RLH_welcome_O', 'A_RH_emphasize_O'], // 可爱
    110005011: ['A_RH_hello_O', 'A_RH_click1_O'], // 晓依
  }

  const defaultIds = defaultActionIds[avatarId] || []
  return defaultIds.map(id => {
    const action = availableActions.find(a => a.id === id)
    return action ? { ...action, category: 'default', priority: 3 } : null
  }).filter(Boolean)
}

/**
 * 根据情感分析结果获取动作
 * @param {Object} emotion - 情感分析结果
 * @param {string} avatarId - 数字人ID
 * @returns {Array} 动作数组
 */
export const getActionsByEmotion = (emotion, avatarId) => {
  if (!emotion || !emotion.primary || !avatarId) {
    return []
  }

  const emotionActionMapping = {
    happy: ['encourage', 'applause', 'like', 'hello'],
    sad: ['please', 'encourage'],
    angry: ['emphasize', 'no'],
    anxious: ['please', 'encourage'],
    tired: ['encourage'],
    frustrated: ['encourage', 'please'],
    lonely: ['hello', 'encourage'],
    calm: ['hello', 'nod']
  }

  const actionKeywords = emotionActionMapping[emotion.primary] || ['hello']
  const availableActions = actionData[avatarId] || []
  const matchedActions = []

  for (const keyword of actionKeywords) {
    const action = availableActions.find(a => 
      a.id.toLowerCase().includes(keyword) || 
      a.name.includes(keyword)
    )
    if (action && !matchedActions.find(m => m.id === action.id)) {
      matchedActions.push({
        ...action,
        category: 'emotion',
        priority: 1
      })
    }
  }

  return matchedActions.length > 0 ? [matchedActions[0]] : getDefaultActions(avatarId).slice(0, 1)
}

/**
 * 获取随机动作
 * @param {string} avatarId - 数字人ID
 * @param {number} count - 动作数量
 * @returns {Array} 随机动作数组
 */
export const getRandomActions = (avatarId, count = 1) => {
  const availableActions = actionData[avatarId] || []
  if (availableActions.length === 0) return []

  const shuffled = [...availableActions].sort(() => Math.random() - 0.5)
  return shuffled.slice(0, count).map(action => ({
    ...action,
    category: 'random',
    priority: 4
  }))
}

/**
 * 获取指定数字人的所有可用动作
 * @param {string} avatarId - 数字人ID
 * @returns {Array} 所有动作数组
 */
export const getAvailableActions = (avatarId) => {
  return actionData[avatarId] || []
}

/**
 * 智能动作选择器 - 主要入口函数
 * @param {string} content - 输入内容
 * @param {string} avatarId - 数字人ID
 * @param {Object} options - 选项
 * @param {Object} options.emotion - 情感分析结果
 * @param {number} options.maxActions - 最大动作数量
 * @param {boolean} options.includeRandom - 是否包含随机动作
 * @returns {Array} 推荐动作数组
 */
export const getSmartActions = (content, avatarId, options = {}) => {
  const {
    emotion = null,
    maxActions = 1,
    includeRandom = false
  } = options

  let actions = []

  // 1. 优先根据内容匹配动作
  if (content) {
    actions = getActionsByContent(content, avatarId, maxActions)
  }

  // 2. 如果内容匹配不到，尝试根据情感匹配
  if (actions.length === 0 && emotion) {
    actions = getActionsByEmotion(emotion, avatarId)
  }

  // 3. 如果还是没有，使用默认动作
  if (actions.length === 0) {
    actions = getDefaultActions(avatarId).slice(0, maxActions)
  }

  // 4. 如果需要，添加随机动作
  if (includeRandom && actions.length < maxActions) {
    const randomActions = getRandomActions(avatarId, maxActions - actions.length)
    actions = [...actions, ...randomActions]
  }

  return actions.slice(0, maxActions)
}

/**
 * 动作执行延迟计算
 * @param {string} content - 内容
 * @returns {number} 延迟时间（毫秒）
 */
export const getActionDelay = (content) => {
  if (!content) return 1000

  // 确保content是字符串类型
  const text = String(content)
  const length = text.length
  if (length < 10) return 500
  if (length < 30) return 1000
  if (length < 50) return 1500
  return 2000
}

export default {
  getActionsByContent,
  getActionsByEmotion,
  getRandomActions,
  getAvailableActions,
  getSmartActions,
  getActionDelay
} 