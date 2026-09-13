import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { SpinKitHelper, SpinType } from '@pura/spinkit';
import { EfAxiosConst, efAxiosParams, efClientApi } from '@yunkss/ef_axios'
import { AppUtil } from '@q/harmony-utils';


export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onCreate');
    AppStorage.setOrCreate('context', this.context)
    // SpinKitHelper 加载框 start
    SpinKitHelper.setGlobalConfig((config) => {
      config.uiContext = this.context //必须初始化上下文
      config.spinType = SpinType.spinD
      config.spinSize = 60
      config.spinColor = '#0A66F9'
      config.backgroundColor = '#CC000000'
      config.borderRadius = 10
      config.maskColor = '#22000000'
      config.autoCancel = false
    })
    //   SpinKitHelper 加载框 end
  }

  onDestroy(): void {
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    // Main window is created, set main page for this ability
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageCreate');

    windowStage.loadContent('pages/login/LoginPage', (err) => {
      if (err.code) {
        hilog.error(0x0000, 'testTag', 'Failed to load the content. Cause: %{public}s', JSON.stringify(err) ?? '');
        return;
      }
      hilog.info(0x0000, 'testTag', 'Succeeded in loading the content.');
    });
    //axios
    efAxiosParams.baseURL = 'http://localhost:8080/';
    efAxiosParams.isAllEncrypt = true;
    efAxiosParams.isLogger = false;
    EfAxiosConst.loadingTxt = '加载中';
    //全局初始化方法，在UIAbility的onCreate方法中初始化 AppUtil.init()
    AppUtil.init(this.context, windowStage);
    //全局初始化方法，在UIAbility的onCreate方法中初始化 AppUtil.init()

  }

  onWindowStageDestroy(): void {
    // Main window is destroyed, release UI related resources
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    // Ability has brought to foreground
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onForeground');
  }

  onBackground(): void {
    // Ability has back to background
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onBackground');
  }
}
