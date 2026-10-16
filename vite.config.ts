import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  base: '/COINRING-LEGENDS/',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    emptyOutDir: true,
  },
  server: {
    port: 3000,
    open: true,
  },
});
