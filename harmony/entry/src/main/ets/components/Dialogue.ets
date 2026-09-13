import { DialogUtil } from '@pura/harmony-utils/src/main/ets/action/DialogUtil'

export function warnDialogue(title:string,message:string,confirm:string,onAction?:()=>void){
  DialogUtil.setDefaultConfig((config) => {
    config.alignment = DialogAlignment.Center
    config.backgroundColor = "#fbbd12"
    config.borderColor = Color.White
  });
  DialogUtil.showConfirmDialog({
    title: title,
    message: message,
    confirm: confirm,
    onAction: () => {
      onAction
    }
  })
}