// 登录模块
export namespace ICaptcha {
  export interface Info {
    bigImageBase64: string;
    bigWidth: number;
    bigHeight: number;
    smallImageBase64: string;
    smallWidth: number;
    smallHeight: number;
    requestId: string;
    posY: number;
    secretKey: string;
  }
  export interface VerifyImageParams {
    requestId: string;
    moveEncrypted: string;
  }
}
