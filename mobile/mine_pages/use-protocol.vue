<script setup>
import { ref } from 'vue'
import navbar from "@/components/navbar.vue"
import TnButton from '@tuniao/tnui-vue3-uniapp/components/button/src/button.vue'
import TnIcon from '@tuniao/tnui-vue3-uniapp/components/icon/src/icon.vue'

// 协议更新日期
const updateDate = ref('2023年12月1日')

// 协议章节
const protocolSections = ref([
  {
    title: '一、总则',
    content: `
      1.1 心晴用户协议（以下简称"本协议"）是您与心晴团队（以下简称"我们"）之间关于您使用心晴应用（以下简称"本应用"）所订立的协议。
      
      1.2 本协议的内容包括协议正文、心晴隐私政策以及我们已经发布的或将来可能发布的各类规则。所有规则均为本协议不可分割的组成部分，与协议正文具有同等法律效力。
      
      1.3 您应当在使用本应用之前认真阅读全部协议内容，对于协议中以加粗字体显示的内容，您应重点阅读。如您对协议有任何疑问，可向我们咨询。
      
      1.4 您点击"同意并继续"按钮后，即视为您已阅读并同意本协议全部条款，本协议即构成对双方有约束力的法律文件。
    `
  },
  {
    title: '二、账号注册与使用',
    content: `
      2.1 您在使用本应用前需要注册账号。您承诺以真实身份注册，并保证所提供的个人资料真实、准确、完整、合法有效。
      
      2.2 您注册成功后，将获得一个账号及相应的密码，该账号和密码由您自行负责保管。您应当对以您的账号进行的所有活动和事件承担法律责任。
      
      2.3 为保障您的账号安全，我们建议您定期更改密码，且不应将账号、密码转让或出借予他人使用。如因您主动泄露或因您遭受他人攻击、诈骗等行为导致的任何损失及后果，我们不承担责任。
      
      2.4 您理解并同意，为了保障本应用的正常运营，我们有权对您的账号进行登录限制、功能限制等管理措施。
    `
  },
  {
    title: '三、用户个人信息保护',
    content: `
      3.1 保护您的个人信息是我们的重要责任，我们将按照本协议及《心晴隐私政策》的规定收集、使用、存储和共享您的个人信息。
      
      3.2 我们收集您的个人信息是为了向您提供服务及提升服务质量，为您提供更加优质、个性化、精准的服务。
      
      3.3 我们会采取合理的安全措施和技术手段存储和保护您的个人信息，防止您的信息丢失、被盗用或遭受篡改。
      
      3.4 未经您的同意，我们不会向任何第三方提供、共享您的个人信息，但以下情况除外：
        (1) 根据法律法规的规定或行政、司法机构的要求；
        (2) 为维护社会公共利益、应对突发公共卫生事件等紧急情况；
        (3) 为维护本应用的安全稳定运行所必需的；
        (4) 其他依据法律法规可以共享的情形。
    `
  },
  {
    title: '四、用户行为规范',
    content: `
      4.1 您在使用本应用过程中，必须遵守中华人民共和国相关法律法规，不得利用本应用从事违法违规行为，包括但不限于：
        (1) 反对宪法所确定的基本原则的；
        (2) 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；
        (3) 损害国家荣誉和利益的；
        (4) 煽动民族仇恨、民族歧视，破坏民族团结的；
        (5) 破坏国家宗教政策，宣扬邪教和封建迷信的；
        (6) 散布谣言，扰乱社会秩序，破坏社会稳定的；
        (7) 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；
        (8) 侮辱或者诽谤他人，侵害他人合法权益的；
        (9) 含有法律、行政法规禁止的其他内容的。
      
      4.2 您不得利用本应用进行任何可能对互联网正常运转造成不利影响的行为。
      
      4.3 您不得利用本应用从事任何损害他人合法权益的行为。
      
      4.4 如果您违反了本条约定，我们有权进行独立判断并采取相应措施，包括但不限于警告、删除内容、限制或禁止使用部分或全部功能、账号封禁直至注销等。
    `
  },
  {
    title: '五、知识产权',
    content: `
      5.1 本应用所包含的所有内容，包括但不限于文本、图形、标志、按钮图标、图像、音频、视频、数字下载、数据编辑和软件，均由我们或我们的内容提供者拥有知识产权，受中国法律法规和国际公约的保护。
      
      5.2 未经我们或相关权利人的书面许可，您不得以任何方式擅自使用、修改、复制、公开传播、改变、散布、发行或公开发表本应用的程序或内容。
      
      5.3 您在本应用上发布的内容（包括但不限于文字、图片、视频等），您保证对该等内容享有合法权利。您同意授予我们非独家的、可转授权的、不可撤销的、全球通用的、永久的、免费的许可使用权，使我们有权在全球范围内使用、复制、修改、改编、出版、翻译、据以创作衍生作品、传播、表演和展示此等内容。
    `
  },
  {
    title: '六、服务变更、中断或终止',
    content: `
      6.1 您理解并同意，本应用可能会不定期进行维护、升级等，导致服务在一定时间内无法正常使用。我们会尽量在合理的时间内事先通知，但我们不保证服务不会中断，也不保证服务中断时能事先通知您。
      
      6.2 如发生下列任何一种情形，我们有权不经通知而中断或终止向您提供服务：
        (1) 您提供的个人资料不真实；
        (2) 您违反本协议中规定的用户行为规范；
        (3) 您在使用付费服务时未按规定支付相应费用；
        (4) 您的行为违反法律法规或涉嫌违法犯罪；
        (5) 根据法律法规、行政命令或司法机关的要求；
        (6) 出于安全的原因或其他必要的情形。
      
      6.3 您可以通过注销账号的方式终止使用本应用服务，但您仍应对您在使用本应用期间的行为承担相应责任。
    `
  },
  {
    title: '七、免责声明',
    content: `
      7.1 您理解并同意，本应用服务可能会受到多种因素的影响，我们不保证服务完全满足您的需求，也不保证服务不会中断，对服务的及时性、安全性、准确性也不作担保。
      
      7.2 对于因不可抗力或我们无法控制的原因造成的服务中断或其他缺陷，我们不承担任何责任，但将尽力减少因此给您造成的损失和影响。
      
      7.3 本应用提供的心理健康服务仅供参考，不能替代专业医疗机构的诊断和治疗。如您有严重的心理健康问题，请及时就医。
      
      7.4 您理解并同意，在使用本应用过程中可能存在来自他人的侮辱、诽谤、骚扰、不作为、欺骗等风险，我们不对其他用户的行为承担责任。
    `
  },
  {
    title: '八、协议修改',
    content: `
      8.1 我们保留随时修改本协议的权利，修改后的协议条款将在本应用上公布。
      
      8.2 如您不同意修改后的协议条款，您有权停止使用本应用服务。如您继续使用本应用服务，则视为您接受修改后的协议条款。
    `
  },
  {
    title: '九、法律适用与争议解决',
    content: `
      9.1 本协议的订立、执行和解释及争议的解决均应适用中华人民共和国法律。
      
      9.2 如双方就本协议内容或其执行发生任何争议，双方应尽量友好协商解决；协商不成时，任何一方均可向我们所在地有管辖权的人民法院提起诉讼。
    `
  },
  {
    title: '十、其他',
    content: `
      10.1 本协议中的标题仅为方便阅读而设，不应影响本协议的解释。
      
      10.2 本协议的任何条款无论因何种原因完全或部分无效或不具有执行力，本协议的其余条款仍应有效并且有约束力。
      
      10.3 本协议自您点击"同意并继续"按钮或您实际使用本应用服务时生效，至您注销账号或我们终止向您提供服务时终止。
    `
  }
])

// 当前展开的章节
const expandedSection = ref(-1)

// 切换章节展开状态
const toggleSection = (index) => {
  if (expandedSection.value === index) {
    expandedSection.value = -1
  } else {
    expandedSection.value = index
  }
}

// 展开所有章节
const expandAll = () => {
  expandedSection.value = -2 // 特殊值，表示全部展开
}

// 收起所有章节
const collapseAll = () => {
  expandedSection.value = -1
}
</script>

<template>
  <view class="protocol-container">
    <!-- 导航栏 -->
    <navbar title="使用协议" ></navbar>
    
    <!-- 协议头部 -->
    <view class="protocol-header">
      <view class="protocol-title">心晴用户协议</view>
      <view class="protocol-update">更新日期：{{ updateDate }}</view>
      
      <view class="protocol-actions tn-flex tn-flex-row-center">
        <TnButton size="sm" @tap="expandAll" bg-color="#5677fc" margin="0 20rpx 0 0">
          <text class="tn-white_text">展开全部</text>
        </TnButton>
        <TnButton size="sm" @tap="collapseAll" bg-color="#aaaaaa">
          <text class="tn-white_text">收起全部</text>
        </TnButton>
      </view>
    </view>
    
    <!-- 协议内容 -->
    <view class="protocol-content">
      <view 
        v-for="(section, index) in protocolSections" 
        :key="index"
        class="protocol-section tn-shadow-sm"
      >
        <view 
          class="section-header tn-flex tn-flex-row-between tn-flex-col-center"
          @tap="toggleSection(index)"
        >
          <text class="section-title tn-text-bold">{{ section.title }}</text>
          <TnIcon 
            :name="expandedSection === index || expandedSection === -2 ? 'chevron-up' : 'chevron-down'" 
            size="40rpx"
            color="#666666"
          ></TnIcon>
        </view>
        
        <view 
          class="section-content"
          :class="{ 'section-expanded': expandedSection === index || expandedSection === -2 }"
        >
          <text>{{ section.content }}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部声明 -->
    <view class="protocol-footer">
      <text class="footer-text">如您对本协议有任何疑问，请联系我们的客服</text>
      <text class="footer-contact">客服电话：400-123-4567</text>
      <text class="footer-contact">客服邮箱：service@xinqing.com</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
.protocol-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;
}

.protocol-header {
  padding: 40rpx 30rpx;
  background-color: #ffffff;
  
  .protocol-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333333;
    text-align: center;
    margin-bottom: 20rpx;
  }
  
  .protocol-update {
    font-size: 24rpx;
    color: #888888;
    text-align: center;
    margin-bottom: 30rpx;
  }
  
  .protocol-actions {
    margin-top: 30rpx;
  }
}

.protocol-content {
  padding: 30rpx;
  
  .protocol-section {
    background-color: #ffffff;
    border-radius: 12rpx;
    margin-bottom: 30rpx;
    overflow: hidden;
    
    .section-header {
      padding: 30rpx;
      background-color: #f9f9f9;
      
      .section-title {
        font-size: 30rpx;
        color: #333333;
      }
    }
    
    .section-content {
      padding: 0 30rpx;
      max-height: 0;
      overflow: hidden;
      transition: max-height 0.3s ease, padding 0.3s ease;
      
      text {
        font-size: 28rpx;
        color: #666666;
        line-height: 1.8;
        white-space: pre-line;
      }
      
      &.section-expanded {
        padding: 30rpx;
        max-height: 3000rpx;
      }
    }
  }
}

.protocol-footer {
  padding: 40rpx 30rpx;
  text-align: center;
  
  .footer-text {
    font-size: 26rpx;
    color: #888888;
    margin-bottom: 20rpx;
    display: block;
  }
  
  .footer-contact {
    font-size: 26rpx;
    color: #5677fc;
    margin-bottom: 10rpx;
    display: block;
  }
}
</style>