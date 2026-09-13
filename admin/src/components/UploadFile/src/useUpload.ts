import * as FileApi from '@/api/infra/file';
// @ts-ignore 忽略类型检查警告
import CryptoJS from 'crypto-js';
import type { UploadRequestOptions } from 'element-plus';
import axios from 'axios';

/**
 * 获得上传 URL
 */
export const getUploadUrl = (): string => {
  return `${import.meta.env.VITE_BASE_URL}${import.meta.env.VITE_API_URL}/infra/file/upload`;
};

export const useUpload = () => {
  // 后端上传地址
  const uploadUrl = getUploadUrl();
  // 是否使用前端直连上传
  const isClientUpload = UPLOAD_TYPE.CLIENT === import.meta.env.VITE_UPLOAD_TYPE;
  // 重写 ElUpload 上传方法
  const httpRequest = async (options: UploadRequestOptions) => {
    // 模式一：前端上传
    if (isClientUpload) {
      // 1.1 生成文件名称
      const fileName = await generateFileName(options.file);
      // 1.2 获取文件预签名地址
      const presignedInfo = await FileApi.getFilePresignedUrl(fileName);
      // 1.3 上传文件（不能使用 ElUpload 的 ajaxUpload 方法的原因：其使用的是 FormData 上传，Minio 不支持）
      return axios
        .put(presignedInfo.uploadUrl, options.file, {
          headers: {
            'Content-Type': options.file.type,
          },
        })
        .then(() => {
          // 1.4. 记录文件信息到后端（异步）
          createFile(presignedInfo, fileName, options.file);
          // 通知成功，数据格式保持与后端上传的返回结果一致
          return { data: presignedInfo.url };
        });
    } else {
      // 模式二：后端上传
      // 重写 el-upload httpRequest 文件上传成功会走成功的钩子，失败走失败的钩子
      return new Promise((resolve, reject) => {
        FileApi.updateFile({ file: options.file })
          .then((res) => {
            if (res.code === 0) {
              resolve(res);
            } else {
              reject(res);
            }
          })
          .catch((res) => {
            reject(res);
          });
      });
    }
  };

  return {
    uploadUrl,
    httpRequest,
  };
};

/**
 * 创建文件信息
 * @param vo 文件预签名信息
 * @param name 文件名称
 * @param file 文件
 */
function createFile(vo: FileApi.FilePresignedUrlRespVO, name: string, file: File) {
  const fileVo = {
    configId: vo.configId,
    url: vo.url,
    path: name,
    name: file.name,
    type: file.type,
    size: file.size,
  };
  FileApi.createFile(fileVo);
  return fileVo;
}

/**
 * 生成文件名称（使用算法 SHA256）
 * @param file 要上传的文件
 */
async function generateFileName(file: File) {
  try {
    if (!file.type) {
      throw new Error('文件类型不可识别');
    }

    const data = await file.arrayBuffer();
    const wordArray = CryptoJS.lib.WordArray.create(new Uint8Array(data));

    // 计算 SHA256（包含完整文件内容）
    const sha256 = CryptoJS.SHA256(wordArray).toString(CryptoJS.enc.Hex);

    // 严格获取扩展名
    const extMatch = file.name.match(/\.([^.]+)$/);
    const ext = extMatch ? `.${extMatch[1]}` : '';

    return `${sha256}${ext}`;
  } catch (error) {
    console.error('文件哈希生成失败:', error);
    throw new Error('文件处理失败，请检查文件完整性');
  }
}

/**
 * 上传类型
 */
enum UPLOAD_TYPE {
  // 客户端直接上传（只支持 S3 服务）
  CLIENT = 'client',
  // 客户端发送到后端上传
  SERVER = 'server',
}