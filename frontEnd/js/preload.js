// preload.js
const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  sairDoApp: () => ipcRenderer.send('sair-do-app')
});
