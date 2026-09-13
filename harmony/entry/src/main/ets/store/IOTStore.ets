
export function setIOTData(begin:string,end:string){
  AppStorage.setOrCreate("IOTBegin",begin)
  AppStorage.setOrCreate("IOTEnd",end)
}