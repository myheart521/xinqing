import { TabItem } from "../../model/index/TabItem";

export function initTabData(): TabItem[] {
  let tabDataArray: TabItem[] = [
    new TabItem(0,"跑步机"),
    new TabItem(1,"智能手表"),
    new TabItem(2,"智能体脂秤"),
  ];
  return tabDataArray;
}