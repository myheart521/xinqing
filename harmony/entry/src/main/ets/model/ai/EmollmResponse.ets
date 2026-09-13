// 定义接口来匹配 API 的数据结构
interface usage {
  prompt_tokens: number;
  completion_tokens: number;
  total_tokens: number;
}

export interface ParsedData {
  id: string;
  object: string;
  created: number;
  sentence_id: number;
  is_end: boolean;
  is_truncated: boolean;
  result: string;
  need_clear_history: boolean;
  finish_reason: string;
  usage: usage;
}

// 用于保存多轮对话的消息历史
@Observed
export class Message {
  role: string="";
  content: string="";
}

//下面没有使用
@Observed
export class HistoryMessages{
  messages:Message[]=[]
}

//保存的数组
@Observed
export class MessageArr{

}

// 定义HTTP请求头
export interface HttpHeaders {
  'Authorization': string;
  'Content-Type': string;
}

//定义响应的数据
// export interface MiddleData{
//   id:number|null
//   event:string|null
//   data:ParsedData
//   retry:number|null
// }

export class MiddleData{
  id:number|null=null
  event:string|null=null
  data:ParsedData|null=null
  retry:number|null=0
}