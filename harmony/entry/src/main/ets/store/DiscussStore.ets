export function setDiscussItem(range:string,content:string,discussName:string){
  AppStorage.setOrCreate("range",range)
  AppStorage.setOrCreate("content",content)
  AppStorage.setOrCreate("discussName",discussName)
}