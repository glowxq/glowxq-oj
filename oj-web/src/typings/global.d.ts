declare namespace Menu {
  interface MenuOptions {
    id: string;
    pid: string;
    path: string;
    name: string;
    sort: number;
    menuTypeCd: string;
    component?: string | (() => Promise<unknown>);
    redirect?: string;
    permissions?: string;
    meta: MetaProps;
    children?: MenuOptions[];
  }

  interface MetaProps {
    icon: string;
    title: string;
    isLink?: string;
    isHidden: string;
    isFull: string;
    isAffix: string;
    isKeepAlive: string;
    useDataScope: string;
  }
}

declare namespace View {
  interface DefaultParams {
    // Dialog Title
    title: string;
    // Dialog 中数据信息
    row: { [key: string]: any };
    // Dialog中调用接口API
    api: ((params: any) => Promise<any>) | undefined;
    // 刷新列表数据使用
    getTableList?: (() => Promise<any>) | undefined;
    // 是否是新增
    isAdd?: boolean;
    // 其他附加信息
    [key: string]: any;
  }
}

declare namespace File {
  type ImageMimeType =
    | 'image/apng'
    | 'image/bmp'
    | 'image/gif'
    | 'image/jpeg'
    | 'image/pjpeg'
    | 'image/png'
    | 'image/svg+xml'
    | 'image/tiff'
    | 'image/webp'
    | 'image/x-icon';

  type ExcelMimeType = 'application/vnd.ms-excel' | 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

  type ExcelFileType = '.xlsx' | '.xls';
}
