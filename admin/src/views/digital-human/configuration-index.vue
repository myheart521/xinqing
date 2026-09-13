<template>
  <div class="digital-human-config">
    <el-container class="main-container">
      <!-- 侧边栏控制面板 -->
      <el-aside width="320px" class="control-panel">
        <div class="panel-header">
          <h3>数字人控制面板</h3>
        </div>

        <!--        <div class="control-section">-->
        <!--          <h4>SDK 初始化</h4>-->
        <!--          <el-button @click="initSDK()" type="primary" class="control-btn">-->
        <!--            <el-icon><Connection /></el-icon>-->
        <!--            实例化SDK-->
        <!--          </el-button>-->
        <!--          <el-button @click="createRecoder()" type="primary" class="control-btn">-->
        <!--            <el-icon><Microphone /></el-icon>-->
        <!--            创建录音器-->
        <!--          </el-button>-->
        <!--        </div>-->

        <!--        <div class="control-section">-->
        <!--          <h4>事件监听</h4>-->
        <!--          <el-button @click="setSDKEvenet()" type="primary" class="control-btn">-->
        <!--            <el-icon><Bell /></el-icon>-->
        <!--            设置SDK监听事件-->
        <!--          </el-button>-->
        <!--          <el-button @click="setPlayerEvenet()" type="primary" class="control-btn">-->
        <!--            <el-icon><VideoPlay /></el-icon>-->
        <!--            设置播放器监听事件-->
        <!--          </el-button>-->
        <!--        </div>-->

        <div class="control-section">
          <h4>配置设置</h4>
          <el-button @click="showApiInfoDialog()" type="primary" class="control-btn">
            <el-icon>
              <Setting/>
            </el-icon>
            API配置
          </el-button>
          <el-button @click="showGlobalParamsDialog()" type="primary" class="control-btn">
            <el-icon>
              <Tools/>
            </el-icon>
            全局参数
          </el-button>
          <el-button @click="start()" type="success" class="control-btn">
            <el-icon>
              <VideoPlay/>
            </el-icon>
            启动
          </el-button>
        </div>

        <div class="control-section">
          <h4>文本控制</h4>
          <el-radio-group v-model="nlp" class="radio-group">
            <el-radio :label="true">开启语义理解</el-radio>
            <el-radio :label="false">关闭语义理解</el-radio>
          </el-radio-group>

          <el-input
              type="textarea"
              placeholder="请输入内容,包括符号在内，最大2000字符"
              v-model="textarea"
              maxlength="2000"
              show-word-limit
              :autosize="{ minRows: 4, maxRows: 8 }"
              class="text-input"
          />

          <el-input v-model="vc" placeholder="变声" class="control-input"/>

          <el-button @click="writeText()" type="primary" class="control-btn">
            <el-icon>
              <Document/>
            </el-icon>
            文本驱动
          </el-button>
          <el-button @click="interrupt()" type="warning" class="control-btn">
            <el-icon>
              <VideoPause/>
            </el-icon>
            打断
          </el-button>
        </div>

        <div class="control-section">
          <h4>动作控制</h4>
          <el-select
              v-model="action"
              placeholder="请选择动作"
              clearable
              class="control-input"
              :disabled="currentActionList.length === 0"
          >
            <el-option
                v-for="actionItem in currentActionList"
                :key="actionItem.id"
                :label="actionItem.name"
                :value="actionItem.id"
            >
              <span style="float: left">{{ actionItem.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ actionItem.id }}</span>
            </el-option>
          </el-select>
          <el-button @click="writeCmd()" type="primary" class="control-btn">
            <el-icon>
              <Edit/>
            </el-icon>
            执行动作
          </el-button>
        </div>

        <div class="control-section">
          <h4>录音控制</h4>
          <el-button
              @click="startRecord()"
              v-if="!recorderbutton"
              type="primary"
              class="control-btn"
          >
            <el-icon>
              <Microphone/>
            </el-icon>
            开启录音
          </el-button>
          <el-button
              @click="stopRecord()"
              v-if="recorderbutton"
              type="danger"
              class="control-btn"
          >
            <el-icon>
              <Close/>
            </el-icon>
            关闭录音
          </el-button>
        </div>

        <div class="control-section">
          <h4>连接控制</h4>
          <el-button @click="stop()" type="warning" class="control-btn">
            <el-icon>
              <Close/>
            </el-icon>
            关闭连接
          </el-button>
          <el-button @click="destroy()" type="danger" class="control-btn">
            <el-icon>
              <Delete/>
            </el-icon>
            销毁SDK
          </el-button>
        </div>

        <div class="control-section">
          <h4>配置管理</h4>
          <el-button @click="saveConfigToLocalStorage()" type="primary" class="control-btn">
            <el-icon>
              <Setting/>
            </el-icon>
            保存配置
          </el-button>
          <el-button @click="clearLocalStorage()" type="warning" class="control-btn">
            <el-icon>
              <Delete/>
            </el-icon>
            清除配置
          </el-button>
          <el-button @click="debugConfig()" type="info" class="control-btn">
            <el-icon>
              <Document/>
            </el-icon>
            调试配置
          </el-button>
        </div>
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <div class="avatar-container">
          <div class="avatar-wrapper" id="wrapper"></div>
          <div class="opacity-control">
            <span>透明度</span>
            <input type="range" id="opacityRange" min="0" max="1" step="0.1" value="1">
          </div>
        </div>
      </el-main>

      <!-- SetApiInfo 弹窗 -->
      <el-dialog
          v-model="SetApiInfodialog"
          title="初始化SDK"
          width="600px"
          :close-on-click-modal="false"
      >
        <el-form :model="form" label-width="120px">
          <el-alert
              title="此处参数均去交互平台-接口服务中获取"
              type="info"
              :closable="false"
              class="mb-4"
          />

          <el-form-item label="Appid" required>
            <el-input
                v-model="form.appid"
                placeholder="请输入Appid"
                clearable
            />
          </el-form-item>

          <el-form-item label="ApiKey" required>
            <el-input
                v-model="form.apikey"
                placeholder="请输入ApiKey"
                clearable
            />
          </el-form-item>

          <el-form-item label="ApiSecret" required>
            <el-input
                v-model="form.apisecret"
                placeholder="请输入ApiSecret"
                clearable
                show-password
            />
          </el-form-item>

          <el-form-item label="SceneId" required>
            <el-input
                v-model="form.sceneid"
                placeholder="请输入SceneId"
                clearable
            />
          </el-form-item>

          <el-form-item label="ServerUrl" required>
            <el-input
                v-model="form.serverurl"
                placeholder="请输入ServerUrl"
                clearable
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
          <el-button @click="SetApiInfodialog = false">取 消</el-button>
            <el-button type="primary" @click="handleApiInfoConfirm">
              确 定
            </el-button>
        </div>
        </template>
      </el-dialog>

      <!-- SetGlobalParams 弹窗 -->
      <el-dialog
          v-model="SetGlobalParamsdialog"
          title="设置全局变量"
          width="700px"
          :close-on-click-modal="false"
      >
        <div class="dialog-header">
          <h3>打断模式全局设置</h3>
        </div>

        <el-form :model="setglobalparamsform" label-width="140px">
          <el-form-item label="视频协议">
            <el-tooltip
                content="支持webrtc/xrtc/rtmp(控制台打印视频流地址)"
                placement="top"
            >
              <el-icon class="help-icon">
                <QuestionFilled/>
              </el-icon>
            </el-tooltip>
            <el-select
                v-model="setglobalparamsform.stream.protocol"
                placeholder="请选择视频流协议"
                class="form-select"
            >
              <el-option label="xrtc" value="xrtc"/>
              <el-option label="webrtc" value="webrtc"/>
              <el-option label="rtmp" value="rtmp"/>
            </el-select>
          </el-form-item>

          <el-form-item label="透明背景">
            <el-tooltip
                content="仅支持xrtc协议"
                placement="top"
            >
              <el-icon class="help-icon">
                <QuestionFilled/>
              </el-icon>
            </el-tooltip>
            <el-select v-model="setglobalparamsform.stream.alpha" placeholder="请选择透明度设置" class="form-select">
              <el-option label="开启透明背景" :value="1"/>
              <el-option label="关闭透明背景" :value="0"/>
            </el-select>
          </el-form-item>

          <el-form-item label="全局交互模式">
            <el-radio-group v-model="setglobalparamsform.avatar_dispatch.interactive_mode">
              <el-radio :label="0">追加模式（信息依次播报）</el-radio>
              <el-radio :label="1">打断模式（直接播报最新）</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="形象选择" required>
            <el-select
                v-model="setglobalparamsform.avatar.avatar_id"
                placeholder="请选择数字人形象"
                clearable
                class="form-select"
                @change="onAvatarChange"
            >
              <el-option
                  v-for="avatar in avatarList"
                  :key="avatar.id"
                  :label="avatar.name"
                  :value="avatar.id"
              >
                <span style="float: left">{{ avatar.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ avatar.id }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="分辨率高">
            <el-input
                v-model="setglobalparamsform.avatar.height"
                placeholder="请输入分辨率高度"
                clearable
            />
          </el-form-item>

          <el-form-item label="分辨率宽">
            <el-input
                v-model="setglobalparamsform.avatar.width"
                placeholder="请输入分辨率宽度"
                clearable
            />
          </el-form-item>

          <el-form-item label="音频采样率">
            <el-radio-group v-model="setglobalparamsform.avatar.audio_format">
              <el-radio :label="1">16K(传1)</el-radio>
              <el-radio :label="2">24K(传2，大部分情况默认24K即可)</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item
              label="形象裁剪"
              v-if="setglobalparamsform.avatar.mask_region != null"
          >
            <el-input
                v-model="setglobalparamsform.avatar.mask_region"
                placeholder="对形象进行裁剪[从左到右,从上到下,从右到左,从下到上]"
                clearable
            />
          </el-form-item>

          <el-form-item label="声音选择" required>
            <el-select
                v-model="setglobalparamsform.tts.vcn"
                placeholder="请选择语音合成声音"
                clearable
                class="form-select"
            >
              <el-option
                  v-for="voice in voiceList"
                  :key="voice.vcn"
                  :label="voice.name"
                  :value="voice.vcn"
              >
                <span style="float: left">{{ voice.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ voice.vcn }}</span>
              </el-option>
            </el-select>
          </el-form-item>



          <el-form-item label="是否开启字幕">
            <el-radio-group v-model="setglobalparamsform.subtitle.subtitle">
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="字体颜色">
            <el-color-picker v-model="setglobalparamsform.subtitle.font_color"/>
          </el-form-item>

          <el-form-item label="是否开启背景图">
            <el-radio-group v-model="setglobalparamsform.enable">
              <el-radio :label="true">开启</el-radio>
              <el-radio :label="false">关闭</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="背景图片">
            <el-radio-group v-model="setglobalparamsform.background.type">
              <el-radio label="url">URL</el-radio>
              <el-radio label="res_key">res_key(到交互平台-素材管理中获取)</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="背景数据">
            <el-input
                v-model="setglobalparamsform.background.data"
                placeholder="请输入背景数据"
                clearable
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
          <el-button @click="SetGlobalParamsdialog = false">取 消</el-button>
            <el-button type="primary" @click="handleGlobalParamsConfirm">
              确 定
            </el-button>
        </div>
        </template>
      </el-dialog>
    </el-container>
  </div>
</template>

<script>
//模块导入
import AvatarPlatform, {
  PlayerEvents,
  SDKEvents,
} from "@/utils/vm-sdk/avatar-sdk-web_3.1.1.1011/index.js";

// 导入图标
import {
  Connection,
  Microphone,
  VideoPlay,
  Setting,
  Tools,
  VideoPause,
  Close,
  Delete,
  QuestionFilled,
  Bell,
  Document,
  Edit
} from '@element-plus/icons-vue'

//动态虚拟人调节透明度
document.addEventListener("DOMContentLoaded", function () {
  const div = document.getElementById('wrapper');
  const range = document.getElementById('opacityRange');

  range.addEventListener('input', function () {
    div.style.opacity = this.value;
  });
})

let avatarPlatform2 = null;
let recorder = null;
export default {
  name: "configuration-index",
  components: {
    Connection,
    Microphone,
    VideoPlay,
    Setting,
    Tools,
    VideoPause,
    Close,
    Delete,
    QuestionFilled,
    Bell,
    Document,
    Edit
  },
  data() {
    return {
      SetApiInfodialog: false,
      SetGlobalParamsdialog: false,
      form: {
        appid: "",//到交互平台-接口服务中获取
        apikey: "",//到交互平台-接口服务中获取
        apisecret: "",//到交互平台-接口服务中获取
        sceneid: "202810917480697856",//到交互平台-接口服务中获取，即"接口服务ID"
        serverurl: "wss://avatar.cn-huadong-1.xf-yun.com/v1/interact",//接口地址，无需更改
      },
      setglobalparamsform: {
        stream: {
          protocol: "xrtc",//（必传）实时视频协议，支持webrtc/xrtc/rtmp，其中只有xrtc支持透明背景，需参数alpha传1
          fps: 25,//（非必传）视频刷新率,值越大，越流畅，取值范围0-25，默认25即可
          bitrate: 1000000,//（非必传）视频码率，值越大，越清晰，对网络要求越高，默认1000000即可
          alpha: 1,//（非必传）是否开启透明背景，0关闭1开始，需配合protocol=xrtc使用
        },
        avatar: {
          avatar_id: "110117005",//（必传）授权的形象资源id，请到交互平台-接口服务-形象列表中获取
          width: 1080,//（非必传）视频分辨率宽（不是画布的宽，调整画布大小需调整名为wrapper的div宽）
          height: 1920,//（非必传）视频分辨率高（不是画布的高，调整画布大小需调整名为wrapper的div高）
          mask_region: "[0,0,1080,1920]",//（非必传）形象裁剪参数，[从左到右，从上到下，从右到左，从下到上]
          scale: 1,//（非必传）形象缩放比例，取值范围0.1-1
          move_h: 0,//（非必传）形象左右移动
          move_v: 0,//（非必传）形象上下移动
          audio_format: 1,//（非必传）音频采样率，传1即可
        },
        tts: {
          vcn: "x4_lingxiaoying_assist",//（必传）授权的声音资源id，请到交互平台-接口服务-声音列表中获取
          speed: 50,//（非必传）语速
          pitch: 50,//（非必传）语调
          volume: 100,//（非必传）音量
        },
        avatar_dispatch: {
          interactive_mode: 1,//（非必传）0追加模式，1打断模式
        },
        subtitle: {
          subtitle: 1,//（非必传）开启字幕，2D形象支持字幕，透明背景不支持字幕，3D形象不支持字幕（3D形象多为卡通形象，2D多为真人形象）
          font_color: "#FFFFFF",//（非必传）字体颜色
          font_name: "Sanji.Suxian.Simple",//（非必传）不支持自定义字体，若不想使用默认提供的
          //字体，那么可以设置asr和nlp监听事件，去获取语音识别和语义理解的文本，自己前端贴字体。
          //支持一下字体：'Sanji.Suxian.Simple','Honglei.Runninghand.Sim','Hunyuan.Gothic.Bold',
          //'Huayuan.Gothic.Regular','mainTitle'
          position_x: 100,//（非必传）设置字幕水平位置，必须配置width、height一起使用，否则字幕不显示
          position_y: 0,//（非必传）设置字幕竖向位置，必须配置width、height一起使用，否则字幕不显示
          font_size: 10,//（非必传）设置字幕字体大小，取值范围：1-10
          width: 100,//（非必传）设置字幕宽
          height: 100,//（非必传）设置字幕高
        },
        enable: false,//demo中用来控制是否开启背景的参数，与虚拟人参数无关
        background: {
          type: "res_key",//（非必传）上传图片的类型，支持url以及res_key。（res_key请到交互平台-素材管理-背景中上传获取)
          data: "22SLM2teIw+aqR6Xsm2JbH6Ng310kDam2NiCY/RQ9n6dw47gMO+7gGUJfWWfkqD3IxsU/HMK1uJTTxxF2llcKSM4dlSdBy0Piag/DndHocqs32kTOwXUw6lkyggYQBXF0uwTv9jVFm1ZjZgSehV3kpx5RTvizZ9MqEI8lotCRvokC9HLI0pGfKtSmlKgCKL+OUoc9QI5HW3wLtYbLersumd4UCKEPk/uWAdKEh4ntSJiW2km8waGFsg/VSNFj5vaDK3LC4PxfsRvi1a2veZW7JUs/VOleE9wwgTH+A/oqPPcyksBY7aQ4TxYjvS9Qj9LtXkvOwttQMgPGwoxlqBEBhR/xLUwmecHkHzgjACFtxE=",
          //（非必传）图片的值，当type='url'时,data='http://xxx/xxx.png'，当type='res_key'时，data='res_key值'（res_key请到交互平台-素材管理-背景中上传获取)
        }
      },
      formLabelWidth: "120px",
      textarea: "",
      vc: "",
      recorderbutton: false,
      nlp: false,

      action: "A_RH_hello_O",
      volume: 100,
      // 形象列表
      avatarList: [
        {id: "110017006", name: "马可"},
        {id: "138805001", name: "小满"},
        {id: "cnrfb86h2000000004", name: "婉仪"},
        {id: "cnrn9jgi2000000005", name: "诗雅"},
        {id: "cnr5dg8n2000000003", name: "浩然"},
        {id: "cnrmkf0e2000000006", name: "梦萱"},
        {id: "138801001", name: "安安"},
        {id: "110117005", name: "晓姿"},
        {id: "110021006", name: "晓娴"},
        {id: "110332017", name: "沐沐"},
        {id: "130033001", name: "可爱"},
        {id: "110005011", name: "晓依"}
      ],
      // 声音列表
      voiceList: [
        {vcn: "x4_yezi", name: "小露"},
        {vcn: "x4_lingxiaoqi_oral", name: "聆小琪"},
        {vcn: "x4_yiting", name: "小娟-温情4.0"},
        {vcn: "x4_yuexiaoni_assist", name: "悦小妮"},
        {vcn: "x4_lingxiaoyu_assist", name: "聆小瑜"},
        {vcn: "x4_lingxiaoying_assist", name: "聆小璎-助理（新）"},
        {vcn: "x4_xiaozhong", name: "小钟"},
        {vcn: "x4_mingge", name: "天明"},
        {vcn: "x4_lingxiaoqi_assist", name: "聆小琪-助理（新）"},
        {vcn: "x4_chaoge", name: "超哥4.0"},
        {vcn: "x4_panting", name: "小娟-甜美4.0"},
        {vcn: "x4_lingxiaoyue_oral", name: "聆小玥"},
        {vcn: "x4_lingyuyan_oral", name: "聆玉言"},
        {vcn: "x4_lingyuzhao_oral", name: "聆玉昭"},
        {vcn: "x4_lingfeiyi_oral", name: "聆飞逸"},
        {vcn: "x4_lingfeizhe_oral", name: "聆飞哲"},
        {vcn: "x4_lingxiaoxuan_oral", name: "聆小璇"},
        {vcn: "x4_lingxiaoxuan_em_v2", name: "聆小璇-多情感（旧）"},
        {vcn: "yezi", name: "叶子"},
        {vcn: "x5_lingfeiyi_flow", name: "聆飞逸-极速超拟人"},
        {vcn: "x5_lingxiaoyue_flow", name: "聆小玥-极速超拟人"},
        {vcn: "x_Steve", name: "Steve"},
        {vcn: "x4_EnUs_Lindsay_assist", name: "Linda"},
        {vcn: "x4_EnUs_Catherine_profnews", name: "Catherine4.0"},
        {vcn: "x2_jajp_zhongcun", name: "日语中村樱"},
        {vcn: "x4_jajp_zhongcun_assist", name: "中村樱"},
        {vcn: "x4_EnUs_Laura_education", name: "Laura4.0"},
        {vcn: "x2_frrgm_lisa", name: "法语Lisa"},
        {vcn: "x4_EnUk_Amanda_education", name: "Amanda_education"},
        {vcn: "x4_enuk_ashleigh_assist", name: "Alice"},
        {vcn: "x2_aren_rania", name: "阿拉伯语Rania"},
        {vcn: "x4_ctncn_xiaomei_profnews", name: "广东晓梅-小美"},
        {vcn: "x4_EnUk_Lydia_edu", name: "英语Lydia"},
        {vcn: "x2_engam_ryan", name: "美式英语Ryan2.0"},
        {vcn: "x2_mariane", name: "法语Mariane"},
        {vcn: "x4_KoKr_Kyung_assist", name: "韩语Kyung"},
        {vcn: "x2_kokr_miya", name: "韩语X2_Miya"},
        {vcn: "x2_ruru_evgenii", name: "俄语Evgenii"},
        {vcn: "x2_ruru_keshu", name: "俄语Keshu"},
        {vcn: "x2_aren_toufic", name: "阿拉伯Toufic"},
        {vcn: "e622bf6_ttsclone-xfyousheng-cjvmm", name: "朗霄"},
        {vcn: "edfe421_ttsclone-xfyousheng-meqoc", name: "启铭"},
        {vcn: "eb69c11_ttsclone-xfyousheng-lnovq", name: "悦宁"},
        {vcn: "f18f328_ttsclone-xfyousheng-ddivi", name: "雅昕"},
        {vcn: "x4_yifei", name: "一菲"}
      ],
      // 动作数据
      actionData: {
        "110017006": [ // 马可
          {id: "A_LH_please_O", name: "左手向左有请"},
          {id: "A_RH_emphasize2_O", name: "右手掌强调"},
          {id: "A_RH_emphasize_O", name: "右手指竖起强调"},
          {id: "A_RH_encourage_O", name: "右手握拳加油"},
          {id: "A_RH_good_O", name: "右手点赞夸奖"},
          {id: "A_RH_hello_O", name: "右手挥手你好"},
          {id: "A_RH_please1_O", name: "右手向前有请"},
          {id: "A_RH_please_O", name: "右手向右有请"},
          {id: "A_RLH_emphasize_O", name: "双手强调"}
        ],
        "138805001": [ // 小满
          {id: "A_H_listen_C", name: "点头倾听"},
          {id: "A_H_nod_O", name: "点头"},
          {id: "A_H_shake_O", name: "摇头"},
          {id: "A_H_standby_C", name: "待机"},
          {id: "A_LH_Motioned_O", name: "请看左下"},
          {id: "A_LH_Spread_O", name: "左摊手"},
          {id: "A_RH_circle_O", name: "右手头画圈"},
          {id: "A_RH_eight_O", name: "第八"},
          {id: "A_RH_emphasize2_O", name: "右手掌强调"},
          {id: "A_RH_emphasize5_O", name: "食指强调"},
          {id: "A_RH_encourage_O", name: "右手握拳加油"},
          {id: "A_RH_five_O", name: "第五"},
          {id: "A_RH_four_O", name: "第四"},
          {id: "A_RH_Motioned_O", name: "请看右下"},
          {id: "A_RH_nine_O", name: "第九"},
          {id: "A_RH_one_O", name: "第一"},
          {id: "A_RH_seven_O", name: "第七"},
          {id: "A_RH_six_O", name: "第六"},
          {id: "A_RH_think1_O", name: "思考1"},
          {id: "A_RH_three_O", name: "第三"},
          {id: "A_RH_two_O", name: "第二"},
          {id: "A_RLH_introduced_O", name: "双手铺开介绍"},
          {id: "A_RLH_no_O", name: "双手十字拒绝"},
          {id: "A_RLH_puzzle_O", name: "双手一摊"},
          {id: "A_RLH_Spread_O", name: "连续摊手"},
          {id: "A_UL_introduced_O", name: "请看左上"},
          {id: "A_UR_introduced_O", name: "请看右上"},
          {id: "A_U_applause_O", name: "鼓掌"},
          {id: "A_U_bow1_O", name: "弯腰鞠躬"},
          {id: "A_U_bowing_O", name: "作揖"},
          {id: "A_U_bow_O", name: "鞠躬"},
          {id: "A_U_click_O", name: "前方轻点击"},
          {id: "A_U_hello_O", name: "打招呼（单手）"},
          {id: "A_U_introduced_O", name: "指自己"},
          {id: "A_U_like_O", name: "比心（单手）"},
          {id: "A_U_no_O", name: "拒绝"},
          {id: "A_U_please_O", name: "拜托"},
          {id: "A_U_puzzle_O", name: "不知道"},
          {id: "A_U_think2_O", name: "思考2"},
          {id: "A_WL_please_O", name: "左手向左身体左侧"},
          {id: "A_WR_introduced_O", name: "单手指听众(右摊手)"},
          {id: "A_WR_listen_O", name: "右手附耳倾听"},
          {id: "A_WR_please_O", name: "右手向右身体右侧"},
          {id: "A_W_behind_O", name: "手背后"},
          {id: "A_W_bye_O", name: "再见"},
          {id: "A_W_encourage_O", name: "打call"},
          {id: "A_W_expect_O", name: "期待"},
          {id: "A_W_hair_O", name: "整理头发"},
          {id: "A_W_hello_O", name: "打招呼（双手）"},
          {id: "A_W_like_O", name: "比心（双手）"},
          {id: "A_W_ok_O", name: "比OK"},
          {id: "A_W_rock_O", name: "晃脚腕"},
          {id: "A_W_shyness_O", name: "害羞"},
          {id: "A_W_slither_O", name: "右手体前滑动"},
          {id: "A_W_surname1_O", name: "和手1"},
          {id: "A_W_surname_O", name: "和手"},
          {id: "A_W_yawn_O", name: "打呵欠"}
        ],
        "138801001": [ // 安安
          {id: "A_H_look_around_O", name: "右环视"},
          {id: "A_LH_look_O", name: "请看左边"},
          {id: "A_LH_look_top_O", name: "请看左上"},
          {id: "A_LH_unfold_hand_O", name: "左摊手"},
          {id: "A_LH_wave_C", name: "左挥手"},
          {id: "A_RH_bye_C", name: "再见"},
          {id: "A_RH_finger_heart_O", name: "比心（小）"},
          {id: "A_RH_look_O", name: "请看右边-2"},
          {id: "A_RH_look_top_O", name: "请看右上"},
          {id: "A_RH_raise_hand_O", name: "右抬手"},
          {id: "A_RLH_applaud_C", name: "鼓掌"},
          {id: "A_RLH_fold_hand_O", name: "摊合手"},
          {id: "A_RLH_unfold_hand_O", name: "双摊手"},
          {id: "A_U_bow_O", name: "鞠躬"},
          {id: "A_U_emphasize_O", name: "强调"},
          {id: "A_U_finger_heart_O", name: "比心（大）"},
          {id: "A_U_greet_O", name: "打招呼"},
          {id: "A_U_guide_O", name: "指引"},
          {id: "A_U_ignorant_O", name: "不懂"},
          {id: "A_U_introduce_O", name: "自我介绍"},
          {id: "A_U_show_O", name: "show"},
          {id: "A_U_understand_O", name: "明白意图"}
        ],
        "110117005": [ // 晓姿
          {id: "A_LH_introduced_O", name: "左手向左上介绍"},
          {id: "A_LH_please_O", name: "左手向左有请"},
          {id: "A_RH_click1_O", name: "右手点击"},
          {id: "A_RH_emphasize2_O", name: "右手掌强调"},
          {id: "A_RH_hello_O", name: "右手挥手你好"},
          {id: "A_RH_introduced1_O", name: "右手向右上介绍"},
          {id: "A_RH_please1_O", name: "右手向前有请"},
          {id: "A_RH_please_O", name: "右手向右有请"},
          {id: "A_RLH_emphasize_O", name: "双手强调"},
          {id: "A_RLH_welcome_O", name: "双手打开欢迎"}
        ],
        "110021006": [ // 晓娴
          {id: "A_LH_please_O", name: "左手向左有请"},
          {id: "A_RH_bye_O", name: "右手挥手再见"},
          {id: "A_RH_emphasize2_O", name: "右手掌强调"},
          {id: "A_RH_emphasize_O", name: "右手指竖起强调"},
          {id: "A_RH_good_O", name: "右手点赞夸奖"},
          {id: "A_RH_please1_O", name: "右手向前有请"},
          {id: "A_RLH_emphasize_O", name: "双手强调"},
          {id: "A_R_introduced_O", name: "右手体侧向下滑动介绍"}
        ],
        "110332017": [ // 沐沐
          {id: "A_LH_introduced_O", name: "左手向左上介绍"},
          {id: "A_LH_please_O", name: "左手向左有请"},
          {id: "A_RH_hello_O", name: "右手挥手你好"},
          {id: "A_RH_introduced1_O", name: "右手向右上介绍"},
          {id: "A_RH_please1_O", name: "右手向前有请"},
          {id: "A_RH_please_O", name: "右手向右有请"},
          {id: "A_RLH_emphasize_O", name: "双手强调"},
          {id: "A_RLH_welcome_O", name: "双手打开欢迎"}
        ],
        "130033001": [ // 可爱
          {id: "A_kiss_O_HU", name: "飞吻"},
          {id: "A_RH_biye_online_O", name: "比耶"},
          {id: "A_RH_circle_O", name: "右手头画圈"},
          {id: "A_RH_emphasize_O", name: "右手指竖起强调"},
          {id: "A_RH_think1_O", name: "思考1"},
          {id: "A_RLH_adore_O", name: "崇拜"},
          {id: "A_RLH_applause_O", name: "鼓掌"},
          {id: "A_RLH_bye_O", name: "双手再见"},
          {id: "A_RLH_welcome_O", name: "双手打开欢迎"},
          {id: "A_U_think2_O", name: "思考2"},
          {id: "A_WR_ENTER_O", name: "右侧入场"},
          {id: "A_W_dance_O", name: "跳舞"},
          {id: "A_W_lovechina_O", name: "我爱中国"},
          {id: "A_W_See_for_you_O", name: "目及皆你"}
        ],
        "110005011": [ // 晓依
          {id: "A_LH_click_O", name: "左手点击"},
          {id: "A_LH_introduced_O", name: "左手向左上介绍"},
          {id: "A_RH_click_O", name: "右手向下指"},
          {id: "A_RH_hello_O", name: "右手挥手你好"},
          {id: "A_RH_introduced1_O", name: "右手向右上介绍"},
          {id: "A_RH_please1_O", name: "右手向前有请"}
        ]
      },
      // 当前可用的动作列表
      currentActionList: [],
      // 保存配置的定时器
      saveTimer: null,
    };
  },
  methods: {
    // 显示API信息弹窗
    showApiInfoDialog() {
      this.SetApiInfodialog = true;
    },

    // 显示全局参数弹窗
    showGlobalParamsDialog() {
      this.SetGlobalParamsdialog = true;
    },

    // 处理API信息确认
    handleApiInfoConfirm() {
      this.SetApiInfodialog = false;
      this.SetApiInfo2();
      // 保存配置到本地存储
      this.saveConfigToLocalStorage();
    },

    // 处理全局参数确认
    handleGlobalParamsConfirm() {
      this.SetGlobalParamsdialog = false;
      this.SetGlobalParams();
      // 保存配置到本地存储
      this.saveConfigToLocalStorage();
    },

    initSDK() {
      //必须先实例化SDK，再去调用其挂载的方法
      avatarPlatform2 = new AvatarPlatform();
      if (avatarPlatform2 != null) {
        // 实例化SDK后，重新从本地存储读取配置并应用
        this.loadConfigFromLocalStorage(false);
        // 如果已经设置了API信息，自动重新设置
        if (this.form.appid && this.form.apikey && this.form.apisecret) {
          this.SetApiInfo2();
        }
        // 如果已经设置了全局参数，自动重新设置
        if (this.setglobalparamsform.avatar.avatar_id) {
          this.SetGlobalParams();
        }

        this.open2("实例化SDK成功，已应用本地配置");
      }
    },
    createRecoder() {
      if (avatarPlatform2 != null) {
        recorder = avatarPlatform2.createRecorder();
        this.open2("创建录音器成功");
      } else {
        alert("请实例化SDK实例");
      }
    },
    setSDKEvenet() {
      //绑定SDK事件
      if (avatarPlatform2 != null) {
        avatarPlatform2
            .on(SDKEvents.connected, function (initResp) {
              console.log("SDKEvent.connect:initResp:", initResp);
            })
            .on(SDKEvents.stream_start, function () {
              console.log("stream_start");
            })
            .on(SDKEvents.disconnected, function (err) {
              console.log("SDKEvent.disconnected:", err);
              if (err) {
                // 因为异常 而导致的断开！ 此处可以进行 提示通知等
                console.error("ws link disconnected because of Error");
                console.error(e.code, e.message, e.name, e.stack);
              }
            })
            .on(SDKEvents.nlp, function (nlpData) {
              console.log("语义理解内容nlp:", nlpData);
            })
            .on(SDKEvents.frame_start, function (frame_start) {
              console.log(
                  "推流开始（可以看作一段文本开始播报时间点）frame_start:",
                  frame_start
              );
            })
            .on(SDKEvents.frame_stop, function (frame_stop) {
              console.log(
                  "推流结束（可以看作一段文本结束播报时间点）frame_stop:",
                  frame_stop
              );
            })
            .on(SDKEvents.error, function (error) {
              console.log("错误信息error:", error);
            })
            .on(SDKEvents.connected, function () {
              console.log("connected");
            })
            .on(SDKEvents.asr, function (asrData) {
              console.log("语音识别数据asr:", asrData);
            })
            .on(SDKEvents.tts_duration, function (ttsData) {
              console.log("语音合成用时tts：", ttsData);
            })
            .on(SDKEvents.subtitle_info, function (subtitleData) {
              console.log("subtitleData：", subtitleData);
            })
            .on(SDKEvents.action_start, function (action_start) {
              console.log(
                  "动作推流开始（可以看作动作开始时间节点）action_start:",
                  action_start
              );
            })
            .on(SDKEvents.action_stop, function (action_stop) {
              console.log(
                  "动作推流结束（可以看作动作结束时间点）action_stop：",
                  action_stop
              );
            });
        this.open2("监听SDK事件成功");
      } else {
        alert("请先实例化SDK")
      }
    },
    setPlayerEvenet() {
      if (avatarPlatform2 != null) {
        //绑定播放器事件
        const player = avatarPlatform2.createPlayer();
        player
            .on(PlayerEvents.play, function () {
              console.log("paly");
            })
            .on(PlayerEvents.playing, function () {
              console.log("playing");
            })
            .on(PlayerEvents.waiting, function () {
              console.log("waiting");
            })
            .on(PlayerEvents.stop, function () {
              console.log("stop");
            })
            .on(PlayerEvents.playNotAllowed, function () {
              console.log(
                  "playNotAllowed：触发了游览器限制自动播放策略，播放前必须与游览器产生交互（例如点击页面或者dom组件），触发该事件后调用avatarPlatform2.player.resume()方法来接触限制"
              );
              player.resume();
            });
        this.open2("监听播放器事件成功");
      } else {
        alert("请先实例化SDK")
      }
    },
    SetApiInfo2() {
      if (avatarPlatform2 == null) {
        alert("请先实例化SDK");
      } else {
        console.log("设置setApiInfo");
        const params = {
          appId: this.form.appid,
          apiKey: this.form.apikey,
          apiSecret: this.form.apisecret,
          serverUrl: this.form.serverurl,
          sceneId: this.form.sceneid,
        };
        console.log("初始化SDK信息：", params);
        //初始化SDK
        avatarPlatform2.setApiInfo(params);
        this.open2("初始化SDK成功");
      }
    },
    SetGlobalParams() {
      if (avatarPlatform2 != null) {
        let params = Object.assign({}, this.setglobalparamsform);
        console.log("this.setglobalparamsform.stream.alpha", this.setglobalparamsform.stream.alpha)
        if (this.setglobalparamsform.enable == false) {
          delete params.background;
          delete params.enable;
        }
        console.log("this.setglobalparamsform", this.setglobalparamsform)
        if (this.setglobalparamsform.stream.alpha == true) {
          console.log("设置alpha=1")
          params.stream.alpha = 1
        } else {
          console.log("设置alpha=0")
          params.stream.alpha = 0
        }
        console.log("设置的全局变量为：", params);
        avatarPlatform2.setGlobalParams(params);
        this.open2("设置全局变量成功")
      } else {
        alert("请先实例化SDK");
      }
    },
    start() {
      if (avatarPlatform2 != null) {
        // 启动前先从本地存储读取最新配置（不显示提示信息）
        this.loadConfigFromLocalStorage(false);

        avatarPlatform2
            .start({wrapper: document.querySelector("#wrapper")})
            .catch((e) => {
              console.error(e.code, e.message, e.name, e.stack);
            });
      } else {
        alert("请先实例化SDK")
      }
    },
    writeText() {
      if (avatarPlatform2 != null) {
        const text = this.textarea;
        if (text != "" && this.vc == "") {
          avatarPlatform2.writeText(text, {
            nlp: this.nlp,//是否开启语义理解
            tts: {
              volume: 100,
            },
          });
        } else if (text != "" && this.vc != "") {
          avatarPlatform2.writeText(text, {
            nlp: this.nlp,//是否开启语义理解
            tts: {
              vcn: this.vc,//变声
              volume: 100,
            },
          });
        } else {
          alert("内容不许为空");
        }
      } else {
        alert("请先实例化SDK")
      }

    },
    writeCmd() {
      if (avatarPlatform2 != null) {
        if (this.action) {
          avatarPlatform2.writeCmd("action", this.action);
          this.open2(`执行动作: ${this.currentActionList.find(item => item.id === this.action)?.name || this.action}`);
        } else {
          this.$message({
            message: "请先选择要执行的动作",
            type: "warning",
          });
        }
      } else {
        alert("请先实例化SDK")
      }
    },
    interrupt() {
      if (avatarPlatform2 != null) {
        avatarPlatform2.interrupt();
      } else {
        alert("请先实例化SDK")
      }
    },
    startRecord() {
      if (avatarPlatform2 != null) {
        avatarPlatform2.recorder.startRecord(0, () => {
          console.warn('STOPED RECORDER')
        }, {
          nlp: true,
          avatar_dispatch: {
            interactive_mode: 0//交互模式（追加或打断）
          }
        });
        //关闭录音按钮显示
        this.recorderbutton = true;
      } else {
        alert("请先实例化SDK")
      }
    },
    stopRecord() {
      if (avatarPlatform2 != null) {
        avatarPlatform2.recorder.stopRecord();
        //开启录音按钮显示
        this.recorderbutton = false;
      } else {
        alert("请先实例化SDK")
      }
    },
    stop() {
      if (avatarPlatform2 != null) {
        avatarPlatform2.stop();
      } else {
        alert("请先实例化SDK")
      }
    },
    destroy() {
      if (avatarPlatform2 != null) {
        //销毁SDK示例，内部包含stop协议，重启需重新示例化avatarPlatform实例
        avatarPlatform2.destroy();
        avatarPlatform2 = null;
      } else {
        alert("请先实例化SDK")
      }
    },
    open2(text) {
      this.$message({
        message: text,
        type: "success",
      });
    },

    // 监听形象选择变化
    onAvatarChange(avatarId) {
      if (avatarId && this.actionData[avatarId]) {
        this.currentActionList = this.actionData[avatarId];
        // 清空当前选择的动作
        this.action = "";
        this.open2(`已加载 ${this.avatarList.find(avatar => avatar.id === avatarId)?.name || avatarId} 的动作列表`);
      } else {
        this.currentActionList = [];
        this.action = "";
      }
    },

    // 保存配置到本地存储（带防抖）
    saveConfigToLocalStorage() {
      // 清除之前的定时器
      if (this.saveTimer) {
        clearTimeout(this.saveTimer);
      }

      // 设置新的定时器，延迟1秒保存
      this.saveTimer = setTimeout(() => {
        try {
          const config = {
            // API配置
            form: this.form,
            // 全局参数配置
            setglobalparamsform: this.setglobalparamsform,
            // 文本控制配置
            textarea: this.textarea,
            vc: this.vc,
            nlp: this.nlp,
            action: this.action,
            volume: this.volume,
            // 录音状态
            recorderbutton: this.recorderbutton,
            // 弹窗状态
            SetApiInfodialog: this.SetApiInfodialog,
            SetGlobalParamsdialog: this.SetGlobalParamsdialog
          };
          localStorage.setItem('digitalHumanConfig', JSON.stringify(config));
          console.log('配置已保存到本地存储');
        } catch (error) {
          console.error('保存配置失败:', error);
        }
      }, 1000);
    },

    // 从本地存储读取配置
    loadConfigFromLocalStorage(showMessage = true) {
      try {
        const savedConfig = localStorage.getItem('digitalHumanConfig');
        if (savedConfig) {
          const config = JSON.parse(savedConfig);

          // 恢复API配置
          if (config.form) {
            this.form = {...this.form, ...config.form};
          }

          // 恢复全局参数配置（深度合并）
          if (config.setglobalparamsform) {
            // 深度合并stream配置
            if (config.setglobalparamsform.stream) {
              this.setglobalparamsform.stream = {
                ...this.setglobalparamsform.stream,
                ...config.setglobalparamsform.stream
              };
            }

            // 深度合并avatar配置
            if (config.setglobalparamsform.avatar) {
              this.setglobalparamsform.avatar = {
                ...this.setglobalparamsform.avatar,
                ...config.setglobalparamsform.avatar
              };
            }

            // 深度合并tts配置
            if (config.setglobalparamsform.tts) {
              this.setglobalparamsform.tts = {
                ...this.setglobalparamsform.tts,
                ...config.setglobalparamsform.tts
              };
            }

            // 深度合并avatar_dispatch配置
            if (config.setglobalparamsform.avatar_dispatch) {
              this.setglobalparamsform.avatar_dispatch = {
                ...this.setglobalparamsform.avatar_dispatch,
                ...config.setglobalparamsform.avatar_dispatch
              };
            }

            // 深度合并subtitle配置
            if (config.setglobalparamsform.subtitle) {
              this.setglobalparamsform.subtitle = {
                ...this.setglobalparamsform.subtitle,
                ...config.setglobalparamsform.subtitle
              };
            }

            // 深度合并background配置
            if (config.setglobalparamsform.background) {
              this.setglobalparamsform.background = {
                ...this.setglobalparamsform.background,
                ...config.setglobalparamsform.background
              };
            }

            // 合并其他顶层属性
            if (config.setglobalparamsform.enable !== undefined) {
              this.setglobalparamsform.enable = config.setglobalparamsform.enable;
            }
          }

          // 恢复文本控制配置
          if (config.textarea !== undefined) this.textarea = config.textarea;
          if (config.vc !== undefined) this.vc = config.vc;
          if (config.nlp !== undefined) this.nlp = config.nlp;
          if (config.action !== undefined) this.action = config.action;
          if (config.volume !== undefined) this.volume = config.volume;

          // 恢复录音状态
          if (config.recorderbutton !== undefined) this.recorderbutton = config.recorderbutton;

          // 恢复弹窗状态（通常不需要恢复，但保留以防需要）
          // if (config.SetApiInfodialog !== undefined) this.SetApiInfodialog = config.SetApiInfodialog;
          // if (config.SetGlobalParamsdialog !== undefined) this.SetGlobalParamsdialog = config.SetGlobalParamsdialog;

          console.log('配置已从本地存储恢复');
          if (showMessage) {
            this.open2('配置已从本地存储恢复');
          }
        }
      } catch (error) {
        console.error('读取配置失败:', error);
      }
    },

    // 清除本地存储的配置
    clearLocalStorage() {
      try {
        localStorage.removeItem('digitalHumanConfig');
        this.open2('本地配置已清除');
      } catch (error) {
        console.error('清除配置失败:', error);
      }
    },

    // 调试配置
    debugConfig() {
      try {
        const savedConfig = localStorage.getItem('digitalHumanConfig');
        if (savedConfig) {
          const config = JSON.parse(savedConfig);
          console.log('当前保存的配置:', config);
          console.log('透明背景设置:', config.setglobalparamsform?.stream?.alpha);
          console.log('当前内存中的透明背景设置:', this.setglobalparamsform.stream.alpha);

          // 显示配置信息
          this.$alert(
              `当前保存的透明背景设置: ${config.setglobalparamsform?.stream?.alpha}\n` +
              `当前内存中的透明背景设置: ${this.setglobalparamsform.stream.alpha}\n` +
              `完整配置已输出到控制台`,
              '配置调试信息',
              {
                confirmButtonText: '确定',
                type: 'info'
              }
          );
        } else {
          this.$alert('没有找到保存的配置', '配置调试信息');
        }
      } catch (error) {
        console.error('调试配置失败:', error);
        this.$alert('调试配置失败: ' + error.message, '配置调试信息');
      }
    },
  },

  mounted() {
    // 页面加载时，从本地存储读取配置
    this.loadConfigFromLocalStorage();
    this.initSDK()

    // 如果已经选择了形象，自动加载对应的动作列表
    if (this.setglobalparamsform.avatar.avatar_id && this.actionData[this.setglobalparamsform.avatar.avatar_id]) {
      this.currentActionList = this.actionData[this.setglobalparamsform.avatar.avatar_id];
    }
  },

  watch: {
    // 监听文本输入变化，自动保存配置
    textarea() {
      this.saveConfigToLocalStorage();
    },
    // 监听变声设置变化
    vc() {
      this.saveConfigToLocalStorage();
    },
    // 监听动作选择变化
    action() {
      this.saveConfigToLocalStorage();
    },
    // 监听语义理解开关变化
    nlp() {
      this.saveConfigToLocalStorage();
    },
    // 监听录音状态变化
    recorderbutton() {
      this.saveConfigToLocalStorage();
    },
    // 监听API配置变化
    form: {
      handler() {
        this.saveConfigToLocalStorage();
      },
      deep: true
    },
    // 监听全局参数配置变化
    setglobalparamsform: {
      handler() {
        this.saveConfigToLocalStorage();
      },
      deep: true
    }
  },

  beforeDestroy() {
    //关闭页面时调用stop协议，确保链接断开，释放资源
    if (avatarPlatform2) {
      avatarPlatform2.stop();
    }

    // 清除保存配置的定时器
    if (this.saveTimer) {
      clearTimeout(this.saveTimer);
    }
  }
};
</script>

<style scoped>
:deep(.el-button+.el-button){
  margin-left: 0 !important;
}
.digital-human-config {
  height: 100vh;
  background: linear-gradient(135deg, #e0e7ff 0%, #f8fafc 100%);
}

.main-container {
  height: 100vh;
  box-shadow: 0 8px 32px 0 rgba(60, 60, 120, 0.08);
  border-radius: 18px;
  overflow: hidden;
}

.control-panel {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-right: 1.5px solid #e2e8f0;
  overflow-y: auto;
  padding: 0;
  box-shadow: 2px 0 16px 0 rgba(102, 126, 234, 0.08);
}

.panel-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 28px 20px 22px 20px;
  text-align: center;
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  box-shadow: 0 2px 8px 0 rgba(102, 126, 234, 0.10);
}

.panel-header h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
}

.control-section {
  padding: 22px 20px 18px 20px;
  border-bottom: 1.5px solid #f3f4f6;
  background: transparent;
}

.control-section:last-child {
  border-bottom: none;
}

.control-section h4 {
  margin: 0 0 18px 0;
  color: #4f46e5;
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  letter-spacing: 0.5px;
}

.control-btn {
  width: 100%;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 44px;
  font-size: 15px;
  border-radius: 12px;
  transition: box-shadow 0.2s, background 0.2s, transform 0.1s;
  font-weight: 500;
  letter-spacing: 0.5px;
  text-align: center;
  white-space: nowrap;
}

.control-btn:last-child {
  margin-bottom: 0;
}

.control-btn:hover {
  background: linear-gradient(90deg, #a5b4fc 0%, #c7d2fe 100%);
  color: #3730a3;
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.13);
  transform: translateY(-2px) scale(1.03);
}

/* 确保按钮内的图标和文字都居中对齐 */
.control-btn .el-icon {
  flex-shrink: 0;
}

.control-btn span {
  flex: 1;
  text-align: center;
}

.radio-group {
  margin-bottom: 18px;
}

.text-input {
  margin-bottom: 18px;
}

.control-input {
  margin-bottom: 12px;
}

.main-content {
  padding: 0;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.avatar-wrapper {
  flex: 1;
  width: 100%;
  max-width: 420px;
  min-height: 480px;
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  position: relative;
  overflow: hidden;
  border-radius: 22px;
  box-shadow: 0 8px 32px 0 rgba(102, 126, 234, 0.18);
  margin: 0 auto 24px auto;
  border: 1.5px solid #e0e7ff;
}

.opacity-control {
  background: rgba(255, 255, 255, 0.93);
  padding: 15px 24px;
  display: flex;
  align-items: center;
  gap: 18px;
  border-top: 1.5px solid #e2e8f0;
  border-radius: 0 0 22px 22px;
  box-shadow: 0 2px 8px 0 rgba(102, 126, 234, 0.06);
}

.opacity-control span {
  color: #4a5568;
  font-weight: 600;
  min-width: 60px;
  font-size: 15px;
}

.opacity-control input[type="range"] {
  flex: 1;
  height: 7px;
  border-radius: 4px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.opacity-control input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.18);
}

.opacity-control input[type="range"]::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #667eea;
  cursor: pointer;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.18);
}

/* 弹窗样式 */
.dialog-header {
  text-align: center;
  margin-bottom: 20px;
}

.dialog-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 18px;
  font-weight: 600;
}

.help-icon {
  margin-left: 8px;
  color: #667eea;
  cursor: help;
}

.form-select {
  width: 100%;
  border-radius: 8px;
}

/* 下拉框选项样式 */
:deep(.el-select-dropdown__item) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  font-size: 15px;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #e0e7ff;
}

:deep(.el-select-dropdown__item.selected) {
  background-color: #6366f1;
  color: white;
}

/* 下拉框样式 */
:deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1.5px #dcdfe6 inset;
}

:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1.5px #a5b4fc inset;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1.5px #6366f1 inset;
}

/* 动作选择下拉框样式 */
.control-input {
  margin-bottom: 12px;
}

.control-input:deep(.el-input__wrapper) {
  border-radius: 10px;
}

.control-input:deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .main-container {
    flex-direction: column;
    border-radius: 0;
    box-shadow: none;
  }
  .control-panel {
    width: 100% !important;
    min-width: 0;
    border-radius: 0;
    box-shadow: none;
  }
  .main-content {
    padding: 0;
    min-height: 320px;
  }
  .avatar-wrapper {
    min-height: 320px;
    max-width: 100%;
    border-radius: 16px;
  }
  .opacity-control {
    border-radius: 0 0 16px 16px;
    padding: 12px 10px;
  }
  .panel-header {
    border-radius: 0;
    padding: 18px 10px 12px 10px;
  }
  .control-section {
    padding: 14px 10px 10px 10px;
  }
  .control-btn {
    width: 100%;
    height: 36px;
    font-size: 13px;
    border-radius: 8px;
  }
}

/* 滚动条样式 */
.control-panel::-webkit-scrollbar {
  width: 6px;
}

.control-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.control-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.control-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #3730a3;
  font-size: 15px;
  letter-spacing: 0.5px;
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
}

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 500;
}

:deep(.el-dialog) {
  border-radius: 14px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e2e8f0;
  padding: 20px 20px 15px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #e2e8f0;
  padding: 15px 20px 20px;
}

/* 警告文本样式 */
.mb-4 {
  margin-bottom: 16px;
}
</style>