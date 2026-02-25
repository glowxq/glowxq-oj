/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_APP_CLIENT_ID: string
  readonly VITE_APP_HTTP_TIMEOUT: number
  readonly VITE_SOCKET_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
