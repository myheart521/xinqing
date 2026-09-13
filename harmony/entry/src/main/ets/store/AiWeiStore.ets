

export const setAiWei = ()=>{
  AppStorage.setOrCreate("AiWeiResult",'')
  AppStorage.setOrCreate("AiWeiHistoryMessage",[])
}

