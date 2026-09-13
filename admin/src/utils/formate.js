// value代表内容（value不能为空），num是限制我们多少字符进行显示省略号（num=20意思是不传默认是等于20）
export const getEllipsis = (value, num = 20) => {
    if (!value) {
        return ''
    }
    if (value.length > num) {
        return value.slice(0, num) + '...'
    }
    return value
}
// 需求：判断date是否为今年，如果是就去除年份，不是就显示全部
export const contrastDate = (date) => {
    var myDate = new Date();
    // 此时此刻的年份
    var currentMonth = myDate.getFullYear()

    // 传递参数的年份进行处理
    var index = date.indexOf("\-");
    // 拿到第一个'-'前面的内容（例如：2022）
    var transmitMonth = date.substring(0, index);

    // 对比传递过来的参数（时间）是不是今年，如果是今年就不显示年份
    if (transmitMonth == currentMonth) {
        // 拿到第一个'-'后面的内容（例如：10-14）
        return date.substring(index + 1, date.length)
    } else {
        return date
    }
}
//获取日期与时间,
export const toLocaleString = (type = 'date') => {
    var myDate = new Date();
    var date
    if (type == 'time') {
        date = moment(myDate.toLocaleString()).format('YYYY-MM-DD HH:mm');
    } else {
        date = moment(myDate.toLocaleString()).format('YYYY-MM-DD'); //获取日期与时间
    }
    return date
}



/**
 * @description 用于获取用户传递值的px值  如果用户传递了"xxpx"或者"xxrpx"，取出其数值部分，如果是"xxxrpx"还需要用过uni.upx2px进行转换
 * @param {number|string} value 用户传递值的px值
 * @param {boolean} unit
 * @returns {number|string}
 */
export function getPx(value, unit = false) {
    if (test.number(value)) {
        return unit ? `${value}px` : Number(value)
    }
    // 如果带有rpx，先取出其数值部分，再转为px值
    if (/(rpx|upx)$/.test(value)) {
        return unit ? `${uni.upx2px(parseInt(value))}px` : Number(uni.upx2px(parseInt(value)))
    }
    return unit ? `${parseInt(value)}px` : parseInt(value)
}

/**
 * 格式化时间
 * @param {string|array} time - 时间字符串或数组 [年, 月, 日, 时, 分, 秒]
 * @param {string} format - 格式化模式，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (time, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!time) return '';
  
  let date;
  
  // 处理数组类型的时间
  if (Array.isArray(time)) {
    // 解构数组，获取年月日时分秒
    const [year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0] = time;
    // 创建Date对象，注意月份要减1
    date = new Date(year, month - 1, day, hour, minute, second);
  } 
  // 处理字符串或时间戳
  else {
    date = new Date(time);
  }
  
  // 检查日期是否有效
  if (isNaN(date.getTime())) {
    console.error('无效的时间格式');
    return '';
  }
  
  const map = {
    'YYYY': date.getFullYear(),
    'MM': (date.getMonth() + 1).toString().padStart(2, '0'),
    'DD': date.getDate().toString().padStart(2, '0'),
    'HH': date.getHours().toString().padStart(2, '0'),
    'mm': date.getMinutes().toString().padStart(2, '0'),
    'ss': date.getSeconds().toString().padStart(2, '0'),
    'M': date.getMonth() + 1,
    'D': date.getDate(),
    'H': date.getHours(),
    'm': date.getMinutes(),
    's': date.getSeconds()
  };
  
  // 替换格式字符串
  return format.replace(/YYYY|MM|DD|HH|mm|ss|M|D|H|m|s/g, match => map[match]);
};

// 判断日期是否为今年，如果是则返回月日，否则返回年月日
export const formatDateByYear = (time) => {
  if (!time) return '';
  
  const currentYear = new Date().getFullYear();
  const formattedDate = formatTime(time, 'YYYY-MM-DD');
  const timeYear = parseInt(formattedDate.split('-')[0]);
  
  if (timeYear === currentYear) {
    return formatTime(time, 'MM-DD');
  } else {
    return formattedDate;
  }
};

export const formatRelativeTime = (time) => {
    if (!time) return '';

    const now = new Date();
    const date = Array.isArray(time)
        ? new Date(...time.map((v,i) => i === 1 ? v-1 : v))
        : new Date(time);

    const diff = now.getTime() - date.getTime();

    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return `${Math.floor(diff/60000)}分钟前`;
    if (diff < 86400000) return `${Math.floor(diff/3600000)}小时前`;
    if (diff < 172800000) return `昨天 ${date.getHours()}:${date.getMinutes().toString().padStart(2,'0')}`;

    if (diff < 604800000) {
        const days = Math.floor(diff / 86400000);
        if (days >= 2) return `${days}天前`;

        const weekdays = ['日','一','二','三','四','五','六'];
        return `星期${weekdays[date.getDay()]} ${date.getHours()}:${date.getMinutes().toString().padStart(2,'0')}`;
    }

    if (now.getFullYear() === date.getFullYear()) {
        return `${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes().toString().padStart(2,'0')}`;
    }

    return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}`;
};

// // 格式化数组时间
// formatTime([2025, 2, 9, 22, 19, 2]); // "2025-02-09 22:19:02"
//
// // 自定义格式
// formatTime([2025, 2, 9, 22, 19, 2], 'YYYY年MM月DD日 HH时mm分'); // "2025年02月09日 22时19分"
//
// // 判断是否为今年
// formatDateByYear([2023, 5, 15]); // 如果当前是2023年，返回 "05-15"，否则返回 "2023-05-15"
//
// // 相对时间
// formatRelativeTime([2023, 6, 10, 14, 30]); // 可能返回"3小时前"、"昨天"、"星期六"等
