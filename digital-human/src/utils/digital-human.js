/**
 * 数字人语音管理工具
 * 用于根据用户输入内容选择合适的数字人语音
 */

// 情绪关键词映射 - 扩展版
const emotionKeywords = {
  // 伤心、难过类
  sad: [
    '伤心', '难过', '痛苦', '哭', '流泪', '失落', '绝望', '心痛', '委屈', '沮丧', '郁闷', '抑郁',
    '心酸', '悲伤', '心碎', '难受', '想哭', '眼泪', '心情不好', '不开心', '心情低落', '情绪低落'
  ],

  // 焦虑、紧张类
  anxious: [
    '焦虑', '紧张', '担心', '害怕', '恐惧', '不安', '烦躁', '慌张', '忐忑', '压力大',
    '紧张兮兮', '心神不宁', '惶恐', '胆怯', '惊慌', '着急', '急躁', '心急', '恐慌', '惊恐'
  ],

  // 疲惫、累类
  tired: [
    '累', '疲惫', '疲倦', '困', '乏力', '精疲力尽', '身心俱疲', 'exhausted', '倦怠',
    '疲劳', '困倦', '无力', '筋疲力尽', '疲乏', '困乏', '打瞌睡', '想睡觉', '好累', '累死了'
  ],

  // 失败、挫折类
  frustrated: [
    '失败', '挫折', '失望', '受挫', '打击', '崩溃', '想放弃', '做不到', '不行了',
    '挫败', '受挫折', '失意', '气馁', '沮丧', '颓废', '无助', '束手无策', '山穷水尽'
  ],

  // 孤独、寂寞类
  lonely: [
    '孤独', '寂寞', '孤单', '一个人', '没人理解', '孤立', '独自', 'alone',
    '孤零零', '形单影只', '独来独往', '无人陪伴', '孤寂', '寂静', '冷清', '落寞'
  ],

  // 愤怒、生气类
  angry: [
    '愤怒', '生气', '气愤', '恼火', '暴怒', '发火', '怒火', '愤慨', '恼怒', '火大',
    '气死了', '讨厌', '恨', '憎恶', '厌恶', '不爽', '烦人', '可恶', '气人', '恼人'
  ],

  // 开心、快乐类
  happy: [
    '开心', '快乐', '高兴', '兴奋', '愉快', '欢乐', '喜悦', '乐观', '满足', '幸福',
    '舒心', '畅快', '欣喜', '愉悦', '快意', '兴高采烈', '心情好', '开怀', '心花怒放'
  ],

  // 平静、放松类
  calm: [
    '平静', '冷静', '淡定', '放松', '安静', '宁静', '祥和', '安逸', '舒适', '轻松',
    '悠闲', '安详', '从容', '镇定', '沉着', '淡然', '坦然', '自在', '惬意'
  ]
}

// 情绪强度权重（用于多情绪匹配时的优先级）
const emotionWeights = {
  frustrated: 10, // 挫折感权重最高
  sad: 9,
  anxious: 8,
  angry: 7,
  lonely: 6,
  tired: 5,
  happy: 4,
  calm: 3
}

/**
 * 检测用户输入内容的情绪类型
 * @param {string} content - 用户输入的内容
 * @returns {Object} - 检测结果对象
 */
export const detectEmotion = (content) => {
  if (!content || typeof content !== 'string') {
    return {
      primary: null,
      secondary: null,
      confidence: 0,
      keywords: [],
      allMatches: {}
    }
  }

  const text = content.toLowerCase().trim()
  const allMatches = {}
  const foundKeywords = []

  // 遍历情绪关键词，统计匹配次数
  for (const [emotion, keywords] of Object.entries(emotionKeywords)) {
    allMatches[emotion] = 0

    for (const keyword of keywords) {
      // 使用正则表达式进行更精确的匹配
      const regex = new RegExp(`\\b${keyword}\\b|${keyword}`, 'gi')
      const matches = text.match(regex)

      if (matches) {
        allMatches[emotion] += matches.length
        foundKeywords.push(...matches.map(match => ({ word: match, emotion })))
      }
    }
  }

  // 计算加权分数
  const weightedScores = {}
  for (const [emotion, count] of Object.entries(allMatches)) {
    if (count > 0) {
      weightedScores[emotion] = count * (emotionWeights[emotion] || 1)
    }
  }

  // 排序获取主要和次要情绪
  const sortedEmotions = Object.entries(weightedScores)
    .sort(([, a], [, b]) => b - a)
    .map(([emotion]) => emotion)

  const primary = sortedEmotions[0] || null
  const secondary = sortedEmotions[1] || null

  // 计算置信度（基于匹配词数量和文本长度）
  const totalMatches = Object.values(allMatches).reduce((sum, count) => sum + count, 0)
  const confidence = totalMatches > 0 ? Math.min(totalMatches / (text.length / 10), 1) : 0

  return {
    primary,
    secondary,
    confidence: Math.round(confidence * 100),
    keywords: foundKeywords,
    allMatches
  }
}

/**
 * 获取随机开场语音
 * @returns {Object} - 包含描述和元数据的对象
 */
export const getRandomStart = () => {
  const startVideos = [
    {
      description: '叮！捕捉到一只小可爱～我是你的心情魔法师晴晴，快把今天的烦恼泡泡戳破给我看看！',
      mood: 'cheerful',
      timeOfDay: 'any',
      tone: 'playful'
    },
    {
      description: '检测到一缕小情绪波动——晴晴已开启隐身斗篷模式！猜猜我在哪片云后面偷听你的故事？',
      mood: 'mysterious',
      timeOfDay: 'any',
      tone: 'curious'
    },
    {
      description: '月光偷偷告诉我，你还没睡呢～我是你的枕头伙伴晴晴，要不要把今天的疲惫折成纸船，我们一起放逐到梦里？',
      mood: 'gentle',
      timeOfDay: 'night',
      tone: 'soothing'
    },
    {
      description: '检测到一颗亮着的小星星！我是你的24℃恒温树洞晴晴，虽然不能替你泡热茶，但此刻的星光和心事都可以存进我的云朵备忘录哦～',
      mood: 'warm',
      timeOfDay: 'evening',
      tone: 'comforting'
    },
    {
      description: '早安！阳光透过云层对我眨眼，说今天要给你满满的温暖能量～我是你的元气小助手晴晴，准备好迎接新的一天了吗？',
      mood: 'energetic',
      timeOfDay: 'morning',
      tone: 'encouraging'
    },
    {
      description: '下午好呀！我刚从午后阳光里醒来，发现你也在这里～要不要分享点什么有趣的事情？',
      mood: 'relaxed',
      timeOfDay: 'afternoon',
      tone: 'friendly'
    }
  ]

  const currentHour = new Date().getHours()
  let timeOfDay = 'any'

  if (currentHour >= 6 && currentHour < 12) timeOfDay = 'morning'
  else if (currentHour >= 12 && currentHour < 18) timeOfDay = 'afternoon'
  else if (currentHour >= 18 && currentHour < 22) timeOfDay = 'evening'
  else timeOfDay = 'night'

  // 优先选择适合时间的开场语
  const timeAppropriate = startVideos.filter(video =>
    video.timeOfDay === timeOfDay || video.timeOfDay === 'any'
  )

  const candidates = timeAppropriate.length > 0 ? timeAppropriate : startVideos
  const randomIndex = Math.floor(Math.random() * candidates.length)

  return {
    ...candidates[randomIndex],
    timestamp: new Date().toISOString(),
    selectedFrom: candidates.length
  }
}

/**
 * 根据检测到的情绪选择安慰语音
 * @param {string|Object} emotion - 检测到的情绪类型或情绪检测结果对象
 * @returns {Object|null} - 包含描述和元数据的对象，没有匹配返回null
 */
export const getComfort = (emotion) => {
  let primaryEmotion, secondaryEmotion, confidence

  if (typeof emotion === 'string') {
    primaryEmotion = emotion
    secondaryEmotion = null
    confidence = 100
  } else if (emotion && typeof emotion === 'object') {
    primaryEmotion = emotion.primary
    secondaryEmotion = emotion.secondary
    confidence = emotion.confidence || 0
  } else {
    return null
  }

  if (!primaryEmotion) return null

  const comfort = [
    // 疲惫/挫折类
    {
      description: '我注意到你声音有些疲惫，像被雨水打湿的羽毛～没关系的，我的云朵口袋永远存着烘干机，随时等你把心事摊开晾一晾',
      emotions: ['tired', 'frustrated'],
      intensity: 'gentle',
      approach: 'metaphorical'
    },
    {
      description: '累了就停下来歇歇吧，世界不会因为你慢一拍就停止转动，反而会因为你的坚持而更加精彩',
      emotions: ['tired'],
      intensity: 'supportive',
      approach: 'direct'
    },

    // 伤心/焦虑/挫折类
    {
      description: '心里那团乱麻一定缠得你好疼吧？晴晴正拿着小梳子待命呢——要不要一根根理？还是先把它团成解压毛球扔着玩？',
      emotions: ['sad', 'anxious', 'frustrated'],
      intensity: 'playful',
      approach: 'metaphorical'
    },
    {
      description: '情绪就像天气，有阴天也有晴天，现在的乌云只是暂时遮住了你内心的阳光，我陪你等它散开',
      emotions: ['sad', 'anxious'],
      intensity: 'gentle',
      approach: 'metaphorical'
    },

    // 伤心/孤独类
    {
      description: '难过不用憋着，我的耳朵24小时待机，眼泪是心灵的语言，让它说完想说的话',
      emotions: ['sad', 'lonely'],
      intensity: 'empathetic',
      approach: 'direct'
    },
    {
      description: '一个人的时候容易胡思乱想对吧？没关系，我来陪你把孤独酿成独处的美酒',
      emotions: ['lonely', 'sad'],
      intensity: 'poetic',
      approach: 'metaphorical'
    },

    // 挫折/伤心类
    {
      description: '摔跤是大地给的拥抱，疼完记得它教你的新舞步！每一次跌倒都是在为站起来积蓄力量',
      emotions: ['frustrated', 'sad'],
      intensity: 'encouraging',
      approach: 'motivational'
    },
    {
      description: '失败只是成功路上的路标，告诉你"这条路不通，换条路试试"，不是告诉你"你不行"',
      emotions: ['frustrated'],
      intensity: 'rational',
      approach: 'reframing'
    },

    // 多情绪通用
    {
      description: '你心里的批评家该休假啦！换勇气小精灵值班好不好？它会告诉你，你比想象中更强大',
      emotions: ['anxious', 'frustrated', 'sad'],
      intensity: 'empowering',
      approach: 'reframing'
    },
    {
      description: '感受到你的情绪在波动，就像海浪一样，它会涨也会落，我陪你在这片情绪的海洋里找到平静的港湾',
      emotions: ['anxious', 'sad', 'frustrated'],
      intensity: 'calming',
      approach: 'metaphorical'
    },

    // 愤怒类
    {
      description: '愤怒是心灵的火山，让它安全地爆发吧，我来当你的灭火器，不是要熄灭你的感受，而是保护你不被烧伤',
      emotions: ['angry'],
      intensity: 'understanding',
      approach: 'metaphorical'
    },
    {
      description: '生气的时候记得深呼吸，怒火是种能量，我们可以把它转化成改变的动力',
      emotions: ['angry', 'frustrated'],
      intensity: 'practical',
      approach: 'actionable'
    },

    // 开心/积极情绪的回应
    {
      description: '感受到你的快乐啦！你的笑容就像阳光，照亮了我的云朵世界，继续保持这份美好吧！',
      emotions: ['happy'],
      intensity: 'celebratory',
      approach: 'affirmative'
    },

    // 平静情绪的回应
    {
      description: '你现在的状态很棒呢，内心平静如湖水，我就在这里陪你享受这份宁静',
      emotions: ['calm'],
      intensity: 'peaceful',
      approach: 'affirmative'
    }
  ]

  // 筛选适合的回应
  const matching = comfort.filter(video => {
    const matchesPrimary = video.emotions.includes(primaryEmotion)
    const matchesSecondary = secondaryEmotion ? video.emotions.includes(secondaryEmotion) : false
    return matchesPrimary || matchesSecondary
  })

  if (matching.length === 0) {
    // 如果没有匹配的，提供通用安慰
    return {
      description: '无论你现在感受如何，我都想让你知道，你并不孤单，我会一直陪在你身边',
      emotions: ['general'],
      intensity: 'supportive',
      approach: 'universal',
      timestamp: new Date().toISOString(),
      confidence: 0,
      isGeneral: true
    }
  }

  // 根据置信度和匹配度选择最合适的回应
  const scored = matching.map(video => {
    let score = 0

    // 主要情绪匹配得分更高
    if (video.emotions.includes(primaryEmotion)) score += 10
    if (secondaryEmotion && video.emotions.includes(secondaryEmotion)) score += 5

    // 根据置信度调整得分
    score *= (confidence / 100)

    return { ...video, score }
  })

  // 选择得分最高的几个，然后随机选择
  const topScored = scored.sort((a, b) => b.score - a.score).slice(0, 3)
  const randomIndex = Math.floor(Math.random() * topScored.length)
  const selected = topScored[randomIndex]

  return {
    ...selected,
    timestamp: new Date().toISOString(),
    confidence,
    matchedEmotions: [primaryEmotion, secondaryEmotion].filter(Boolean),
    selectedFrom: matching.length
  }
}

/**
 * 获取情绪统计信息
 * @param {Array} emotionHistory - 历史情绪检测结果数组
 * @returns {Object} - 情绪统计信息
 */
export const getEmotionStats = (emotionHistory = []) => {
  if (!Array.isArray(emotionHistory) || emotionHistory.length === 0) {
    return {
      mostFrequent: null,
      totalInteractions: 0,
      emotionDistribution: {},
      averageConfidence: 0,
      trend: 'stable'
    }
  }

  const distribution = {}
  let totalConfidence = 0
  let validEntries = 0

  emotionHistory.forEach(entry => {
    if (entry.primary) {
      distribution[entry.primary] = (distribution[entry.primary] || 0) + 1
      totalConfidence += entry.confidence || 0
      validEntries++
    }
  })

  const mostFrequent = Object.entries(distribution)
    .sort(([, a], [, b]) => b - a)[0]?.[0] || null

  // 简单的趋势分析（比较最近5次和之前5次）
  const recent = emotionHistory.slice(-5)
  const previous = emotionHistory.slice(-10, -5)

  let trend = 'stable'
  if (recent.length >= 3 && previous.length >= 3) {
    const recentNegative = recent.filter(e => ['sad', 'anxious', 'frustrated', 'angry', 'lonely'].includes(e.primary)).length
    const previousNegative = previous.filter(e => ['sad', 'anxious', 'frustrated', 'angry', 'lonely'].includes(e.primary)).length

    if (recentNegative > previousNegative) trend = 'declining'
    else if (recentNegative < previousNegative) trend = 'improving'
  }

  return {
    mostFrequent,
    totalInteractions: emotionHistory.length,
    emotionDistribution: distribution,
    averageConfidence: validEntries > 0 ? Math.round(totalConfidence / validEntries) : 0,
    trend
  }
}

/**
 * 根据时间和情绪历史生成个性化问候
 * @param {Object} emotionStats - 情绪统计信息
 * @returns {Object} - 个性化问候对象
 */
export const getPersonalizedGreeting = (emotionStats) => {
  const currentHour = new Date().getHours()
  const { mostFrequent, trend, totalInteractions } = emotionStats || {}

  let greeting = ''
  let tone = 'friendly'

  // 基于时间的问候
  if (currentHour >= 6 && currentHour < 12) {
    greeting = '早安！'
  } else if (currentHour >= 12 && currentHour < 18) {
    greeting = '下午好！'
  } else if (currentHour >= 18 && currentHour < 22) {
    greeting = '晚上好！'
  } else {
    greeting = '夜深了，'
    tone = 'gentle'
  }

  // 基于情绪历史的个性化
  if (totalInteractions > 0) {
    if (trend === 'improving') {
      greeting += '感觉你最近状态在好转，真为你开心！'
      tone = 'encouraging'
    } else if (trend === 'declining') {
      greeting += '最近似乎有些疲惫，要记得好好照顾自己哦'
      tone = 'caring'
    } else if (mostFrequent === 'happy') {
      greeting += '你的快乐总是感染着我，今天也要保持好心情！'
      tone = 'cheerful'
    } else if (['sad', 'anxious', 'frustrated'].includes(mostFrequent)) {
      greeting += '无论何时，我都在这里陪伴你'
      tone = 'supportive'
    }
  } else {
    greeting += '很高兴见到你！'
  }

  return {
    description: greeting,
    tone,
    timestamp: new Date().toISOString(),
    basedOn: { mostFrequent, trend, totalInteractions }
  }
}

// 导出所有功能
export default {
  detectEmotion,
  getRandomStart,
  getComfort,
  getEmotionStats,
  getPersonalizedGreeting,
  emotionKeywords,
  emotionWeights
}
